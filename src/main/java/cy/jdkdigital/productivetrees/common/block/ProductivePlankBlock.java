package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ProductivePlankBlock extends Block
{
    private final String name;

    public ProductivePlankBlock(Properties properties, String name) {
        super(properties);
        this.name = name;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return TreeUtil.isTranslucentTree(name) && neighborState.getBlock() instanceof ProductivePlankBlock;
    }

    @Override
    public float getSpeedFactor() {
        return name.equals("black_ember") ? 1.1f : super.getSpeedFactor();
    }
}
