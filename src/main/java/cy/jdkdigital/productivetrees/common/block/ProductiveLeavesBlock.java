package cy.jdkdigital.productivetrees.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
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

    public static final MapCodec<ProductiveLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            Identifier.CODEC.fieldOf("tree").forGetter(block -> block.treeObject.getId())
    ).apply(instance, (properties, treeId) -> new ProductiveLeavesBlock(properties, TreeFinder.trees.get(treeId))));

    protected final TreeObject treeObject;

    public ProductiveLeavesBlock(Properties properties, TreeObject treeObject) {
        super(0.0F, properties);
        this.treeObject = treeObject;
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // falling-leaf particles are emitted from animateTick using the tree's configured leaf colour
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (CONNECTED_LEAF_TREES.contains(treeObject.getId().getPath())) {
            // connected-leaf trees decay gradually off the random tick (like vanilla), tracing connected
            // leaves back to a log instead of using the orthogonal distance; no instant cascade
            decayIfDetached(state, level, pos);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    // drop the leaf if no log is reachable through connected leaves; each detached frond leaf decays on its
    // own random tick, so the canopy comes down gradually rather than collapsing at once
    private void decayIfDetached(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.getValue(PERSISTENT) || logReachableThroughLeaves(level, pos)) {
            return;
        }
        dropResources(state, level, pos);
        level.removeBlock(pos, false);
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
        boolean petals = treeObject.hasBlossomPetals();
        // vanilla emits falling leaves at a 1% per-tick chance and cherry-style blossoms at 10%
        int chance = petals ? 10 : 100;
        if ((petals || treeObject.hasFallingLeaves()) && rand.nextInt(chance) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                int leaf = TextColor.parseColor(treeObject.getLeafColor()).result().get().getValue();
                if (petals) {
                    // blossom trees drift coloured petals instead of leaves
                    var particle = ClientRegistration.PETAL_PARTICLES.get();
                    particle.setColor(leaf);
                    ParticleUtils.spawnParticleBelow(level, pos, rand, particle);
                } else {
                    ParticleUtils.spawnParticleBelow(level, pos, rand, ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, leaf));
                }
            }
        }
    }

    public TreeObject getTree() {
        return treeObject;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        int[] info = ProductiveTrees.FLAMMABILITY.get(state.getBlock());
        return info != null ? info[0] : 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        int[] info = ProductiveTrees.FLAMMABILITY.get(state.getBlock());
        return info != null ? info[1] : 0;
    }
}
