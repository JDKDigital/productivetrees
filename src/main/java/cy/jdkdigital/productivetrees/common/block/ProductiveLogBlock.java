package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveLogBlock extends RotatedPillarBlock
{
    private final WoodObject treeObject;

    public ProductiveLogBlock(Properties properties, WoodObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    public BlockState getStrippedState(BlockState blockState) {
        return treeObject.getStrippedLogBlock().get().defaultBlockState().setValue(AXIS, blockState.getValue(AXIS));
    }
}
