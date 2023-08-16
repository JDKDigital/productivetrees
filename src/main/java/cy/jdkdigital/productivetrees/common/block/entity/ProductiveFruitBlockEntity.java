package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveFruitBlockEntity extends BlockEntity
{
    public ProductiveFruitBlockEntity(TreeObject treeObject, BlockPos blockPos, BlockState blockState) {
        super(treeObject.getFruitBlockEntity().get(), blockPos, blockState);
    }
}
