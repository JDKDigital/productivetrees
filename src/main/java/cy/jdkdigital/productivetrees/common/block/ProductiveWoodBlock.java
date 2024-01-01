package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class ProductiveWoodBlock extends ProductiveRotatedPillarBlock
{
    public ProductiveWoodBlock(Properties properties, WoodObject treeObject) {
        super(properties, treeObject);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (ToolActions.AXE_STRIP == toolAction) {
            return treeObject.getStrippedWoodBlock().get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
