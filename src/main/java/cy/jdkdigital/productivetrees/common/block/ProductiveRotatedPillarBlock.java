package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ProductiveRotatedPillarBlock extends RotatedPillarBlock
{
    public ProductiveRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return TreeUtil.isTranslucentTree(name.getPath()) && neighborState.getBlock() instanceof ProductiveRotatedPillarBlock;
    }
}
