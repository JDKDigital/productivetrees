package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class ProductiveDrippyFruitBlock extends ProductiveFruitBlock
{
    private final Supplier<Block> puddle;

    public ProductiveDrippyFruitBlock(Properties properties, TreeObject treeObject, Supplier<Block> puddle) {
        super(properties, treeObject);
        this.puddle = puddle;
    }

    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        super.randomTick(state, level, blockPos, random);
        if (getAge(state) == getMaxAge()) {
            var puddle = this.puddle.get().defaultBlockState();
            // Drip onto ground
            int n = 0;
            var pointer = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            while (n++ < 20 && level.getBlockState(pointer.below()).canBeReplaced() && !level.getBlockState(pointer.below()).is(puddle.getBlock())) {
                pointer.move(Direction.DOWN);
            }
            var stateBelow = level.getBlockState(pointer.below());
            if (stateBelow.is(puddle.getBlock())) {
                int layers = Math.min(BlockStateProperties.MAX_LEVEL_8, stateBelow.getValue(BlockStateProperties.LAYERS) + 1);
                if (layers > 3) {
                    for (Direction dir: Direction.values()) {
                        if (dir.getAxis().isHorizontal()) {
                            if (level.getBlockState(pointer.below().relative(dir)).canBeReplaced() && level.getBlockState(pointer.below(2).relative(dir)).isFaceSturdy(level, pointer.below(2).relative(dir), Direction.UP)) {
                                level.setBlockAndUpdate(pointer.below().relative(dir), puddle);
                            }
                        }
                    }
                    level.setBlockAndUpdate(pointer.below(), puddle.setValue(BlockStateProperties.LAYERS, 1));
                } else {
                    level.setBlockAndUpdate(pointer.below(), puddle.setValue(BlockStateProperties.LAYERS, layers));
                }
            } else if (stateBelow.isFaceSturdy(level, pointer.below(), Direction.UP)) {
                level.setBlockAndUpdate(pointer, puddle);
            }

            level.setBlock(blockPos, this.getStateForAge(state, 0), 2);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return InteractionResult.SUCCESS;
    }
}
