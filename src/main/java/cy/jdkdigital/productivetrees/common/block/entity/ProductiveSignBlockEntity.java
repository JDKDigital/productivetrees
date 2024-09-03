package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ProductiveSignBlockEntity extends SignBlockEntity
{
    public ProductiveSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return TreeRegistrator.SIGN_BE.get();
    }

    @Override
    public boolean isValidBlockState(BlockState blockState) {
        return getType().isValid(blockState);
    }
}
