package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProductiveDanglerFruitBlock extends ProductiveFruitBlock
{
    protected static final VoxelShape SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public ProductiveDanglerFruitBlock(Properties properties, TreeObject treeObject) {
        super(properties, treeObject);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x/2d, vec3.y, vec3.z/2d);
    }
}
