package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ProductiveLogBlock extends ProductiveRotatedPillarBlock
{
    public ProductiveLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (ToolActions.AXE_STRIP == toolAction) {
            var key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            var block = ForgeRegistries.BLOCKS.getValue(key.withPath(p -> p.replace("_log", "_stripped_log")));
            return block.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @Override
    public float getSpeedFactor() {
        return TreeUtil.getTree(this).getId().getPath().equals("black_ember") ? 1.1f : super.getSpeedFactor();
    }
}
