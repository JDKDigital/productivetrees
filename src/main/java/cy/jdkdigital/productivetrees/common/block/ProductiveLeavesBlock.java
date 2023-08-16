package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveLeavesBlock extends LeavesBlock
{
    private final TreeObject treeObject;

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
                var particle = ProductiveTrees.PETAL_PARTICLES.get();
                particle.setColor(ColorUtil.getCacheColor(TextColor.parseColor(treeObject.getLeafColor()).getValue()));
                ParticleUtils.spawnParticleBelow(level, pos, rand, particle);
            }
        }
    }

    public TreeObject getTree() {
        return treeObject;
    }
}
