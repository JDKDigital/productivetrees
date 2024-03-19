package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ProductiveDroppyFruitBlock extends ProductiveDanglerFruitBlock
{
    private final Supplier<Block> fruit;

    public ProductiveDroppyFruitBlock(Properties properties, TreeObject treeObject, Supplier<Block> fruit) {
        super(properties, treeObject);
        this.fruit = fruit;
    }

    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        super.randomTick(state, level, blockPos, random);
        if (getAge(state) == getMaxAge() && level.getBlockState(blockPos.below()).canBeReplaced()) {
            // Drop onto ground
            int n = 0;
            var pointer = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            while (n++ < 30 && level.getBlockState(pointer.below()).canBeReplaced()) {
                pointer.move(Direction.DOWN);
            }
            var stateBelow = level.getBlockState(pointer.below());
            if (!stateBelow.is(this.fruit.get()) && stateBelow.isFaceSturdy(level, pointer.below(), Direction.UP)) {
                level.setBlockAndUpdate(blockPos.below(), this.fruit.get().defaultBlockState());
                level.setBlock(blockPos, this.getStateForAge(state, 0), 2);
            }
        }
    }
}
