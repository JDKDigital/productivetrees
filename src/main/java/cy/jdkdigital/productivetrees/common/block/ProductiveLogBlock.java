package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveLogBlock extends RotatedPillarBlock
{
    private final TreeObject treeObject;

    public ProductiveLogBlock(Properties properties, TreeObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    public BlockState getStrippedState(BlockState blockState) {
        return treeObject.getStrippedLogBlock().get().defaultBlockState().setValue(AXIS, blockState.getValue(AXIS));
    }
}
