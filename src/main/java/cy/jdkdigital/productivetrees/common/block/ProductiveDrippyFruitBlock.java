package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class ProductiveDrippyFruitBlock extends ProductiveFruitBlock
{
    public ProductiveDrippyFruitBlock(Properties properties, TreeObject treeObject) {
        super(properties, treeObject);
    }

    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        super.randomTick(state, level, blockPos, random);
        if (getAge(state) == getMaxAge()) {
            var puddle = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, "brown_amber_puddle"));
            if (puddle == null) {
                return;
            }
            // Drip onto ground
            int n = 0;
            var pointer = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            while (n++ < 20 && level.getBlockState(pointer.below()).canBeReplaced() && !level.getBlockState(pointer.below()).is(puddle)) {
                pointer.move(Direction.DOWN);
            }
            var stateBelow = level.getBlockState(pointer.below());
            if (stateBelow.is(puddle)) {
                int layers = Math.min(BlockStateProperties.MAX_LEVEL_8, stateBelow.getValue(BlockStateProperties.LAYERS) + 1);
                if (layers > 3) {
                    for (Direction dir: Direction.values()) {
                        if (dir.getAxis().isHorizontal()) {
                            if (level.getBlockState(pointer.below().relative(dir)).canBeReplaced() && level.getBlockState(pointer.below(2).relative(dir)).isFaceSturdy(level, pointer.below(2).relative(dir), Direction.UP)) {
                                level.setBlockAndUpdate(pointer.below().relative(dir), puddle.defaultBlockState());
                            }
                        }
                    }
                    level.setBlockAndUpdate(pointer.below(), puddle.defaultBlockState().setValue(BlockStateProperties.LAYERS, 1));
                } else {
                    level.setBlockAndUpdate(pointer.below(), puddle.defaultBlockState().setValue(BlockStateProperties.LAYERS, layers));
                }
            } else if (stateBelow.isFaceSturdy(level, pointer.below(), Direction.UP)) {
                level.setBlockAndUpdate(pointer, puddle.defaultBlockState());
            }

            level.setBlock(blockPos, this.getStateForAge(state, 0), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return InteractionResult.SUCCESS;
    }
}
