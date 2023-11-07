package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveWoodBlock extends RotatedPillarBlock
{
    private final WoodObject treeObject;

    public ProductiveWoodBlock(Properties properties, WoodObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    public BlockState getStrippedState(BlockState blockState) {
        return treeObject.getStrippedWoodBlock().get().defaultBlockState().setValue(AXIS, blockState.getValue(AXIS));
    }
}
