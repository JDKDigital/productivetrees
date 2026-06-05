package cy.jdkdigital.productivetrees.client.color;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PollinatedLeavesTintSource implements BlockTintSource
{
    public static final PollinatedLeavesTintSource INSTANCE = new PollinatedLeavesTintSource();

    private PollinatedLeavesTintSource() {}

    @Override
    public int color(BlockState state) {
        return FoliageColor.FOLIAGE_DEFAULT;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
            int colorA = TreeUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafA(), level, pos);
            int colorB = TreeUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafB(), level, pos);
            return ColorUtil.blend(colorA, colorB, 0.5f);
        }
        return FoliageColor.FOLIAGE_DEFAULT;
    }
}
