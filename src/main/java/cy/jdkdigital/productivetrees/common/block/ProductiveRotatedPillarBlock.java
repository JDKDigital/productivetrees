package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveRotatedPillarBlock extends RotatedPillarBlock
{
    public ProductiveRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        Identifier name = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return TreeUtil.isTranslucentTree(name.getPath()) && neighborState.getBlock() instanceof ProductiveRotatedPillarBlock;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        int[] info = ProductiveTrees.FLAMMABILITY.get(state.getBlock());
        return info != null ? info[0] : 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        int[] info = ProductiveTrees.FLAMMABILITY.get(state.getBlock());
        return info != null ? info[1] : 0;
    }
}
