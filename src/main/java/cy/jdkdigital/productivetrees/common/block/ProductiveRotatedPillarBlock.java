package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProductiveRotatedPillarBlock extends RotatedPillarBlock
{
    protected final WoodObject treeObject;

    public ProductiveRotatedPillarBlock(Properties properties, WoodObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        String name = treeObject.getId().getPath();
        return TreeUtil.isTranslucentTree(name) && neighborState.getBlock() instanceof ProductiveRotatedPillarBlock;
    }

    public WoodObject getTree() {
        return treeObject;
    }
}
