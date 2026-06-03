package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

public class ProductiveLogBlock extends ProductiveRotatedPillarBlock
{
    public ProductiveLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP)) {
            var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            var block = BuiltInRegistries.BLOCK.get(key.withPath(p -> p.replace("_log", "_stripped_log")));
            return block.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public float getSpeedFactor() {
        return TreeUtil.getTree(this).getId().getPath().equals("black_ember") ? 1.1f : super.getSpeedFactor();
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        // cacao logs hold cocoa pods like jungle logs do
        if (plant.is(Blocks.COCOA) && TreeUtil.getTree(this).getId().getPath().equals("cacao")) {
            return TriState.TRUE;
        }
        return super.canSustainPlant(state, level, soilPosition, facing, plant);
    }
}
