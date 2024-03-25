package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveLeavesBlock extends LeavesBlock
{
    protected final TreeObject treeObject;

    public ProductiveLeavesBlock(Properties properties, TreeObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        if (treeObject.hasFallingLeaves() && rand.nextInt(10) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                var particle = ClientRegistration.PETAL_PARTICLES.get();
                particle.setColor(ColorUtil.getCacheColor(TextColor.parseColor(treeObject.getLeafColor()).getValue()));
                ParticleUtils.spawnParticleBelow(level, pos, rand, particle);
            }
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.setBlock(pPos, updateDistance(pState, pLevel, pPos), 3);
    }

    private static BlockState updateDistance(BlockState pState, LevelAccessor pLevel, BlockPos pPos) {
        int i = 7;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                for (var z = -1; z <= 1; z++) {
                    if (z == 0 && x == 0 && y == 0) continue;
                    mutableBlockPos.setWithOffset(pPos, x, y, z);
                    i = Math.min(i, getOptionalDistanceAt(pLevel.getBlockState(mutableBlockPos)).orElse(7) + 1);
                    if (i == 1) {
                        break;
                    }
                }
            }
        }

        return pState.setValue(DISTANCE, i);
    }

    public TreeObject getTree() {
        return treeObject;
    }
}
