package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProductiveBranchedWoodBlock extends ProductiveWoodBlock
{
    private static final VoxelShape SHAPE = box(5D, 0.0D, 5D, 11D, 16D, 11D);

    public ProductiveBranchedWoodBlock(Properties properties, WoodObject treeObject) {
        super(properties, treeObject);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
