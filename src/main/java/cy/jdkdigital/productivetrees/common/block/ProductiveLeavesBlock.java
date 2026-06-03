package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductiveLeavesBlock extends LeavesBlock
{
    // trees whose leaves reach past vanilla's 6-block decay range (diagonal palm fronds, hanging willow strands);
    // they're kept alive by tracing connected leaves (26 directions) back to a log instead of the orthogonal distance
    public static final Set<String> CONNECTED_LEAF_TREES = Set.of(
            "asai_palm", "coconut", "date_palm", "banana", "red_banana", "plantain", "pandanus", "flickering_sun",
            "white_willow", "rippling_willow", "water_wonder");
    // how many connected-leaf steps a leaf may sit from a log before it decays
    private static final int LEAF_REACH = 10;

    protected final TreeObject treeObject;

    public ProductiveLeavesBlock(Properties properties, TreeObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (CONNECTED_LEAF_TREES.contains(treeObject.getId().getPath())) {
            decayIfDetached(state, level, pos); // background cleanup
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (CONNECTED_LEAF_TREES.contains(treeObject.getId().getPath())) {
            decayIfDetached(state, level, pos); // a neighbour changed (e.g. trunk cut)
            return;
        }
        super.tick(state, level, pos, random);
    }

    // drop the leaf if no log is reachable through connected leaves, then nudge its leaf neighbours so the rest of the detached frond follows
    private void decayIfDetached(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.getValue(PERSISTENT) || logReachableThroughLeaves(level, pos)) {
            return;
        }
        dropResources(state, level, pos);
        level.removeBlock(pos, false);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState neighbour = level.getBlockState(cursor);
                    if (neighbour.getBlock() instanceof LeavesBlock) {
                        level.scheduleTick(cursor.immutable(), neighbour.getBlock(), 2);
                    }
                }
            }
        }
    }

    // flood out from the leaf through connected leaves in all 26 directions, looking for a log within LEAF_REACH steps
    private boolean logReachableThroughLeaves(LevelReader level, BlockPos origin) {
        Set<Long> visited = new HashSet<>();
        visited.add(origin.asLong());
        List<BlockPos> frontier = new ArrayList<>();
        frontier.add(origin);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int step = 0; step < LEAF_REACH && !frontier.isEmpty(); ++step) {
            List<BlockPos> next = new ArrayList<>();
            for (BlockPos p : frontier) {
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dy = -1; dy <= 1; ++dy) {
                        for (int dz = -1; dz <= 1; ++dz) {
                            if (dx == 0 && dy == 0 && dz == 0) {
                                continue;
                            }
                            cursor.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                            if (!visited.add(cursor.asLong())) {
                                continue;
                            }
                            BlockState neighbour = level.getBlockState(cursor);
                            if (neighbour.is(BlockTags.LOGS)) {
                                return true;
                            }
                            if (neighbour.getBlock() instanceof LeavesBlock) {
                                next.add(cursor.immutable());
                            }
                        }
                    }
                }
            }
            frontier = next;
        }
        return false;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        if (treeObject.hasFallingLeaves() && rand.nextInt(10) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                var particle = ClientRegistration.PETAL_PARTICLES.get();
                particle.setColor(TextColor.parseColor(treeObject.getLeafColor()).result().get().getValue());
                ParticleUtils.spawnParticleBelow(level, pos, rand, particle);
            }
        }
    }

    public TreeObject getTree() {
        return treeObject;
    }
}
