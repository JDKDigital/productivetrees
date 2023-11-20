package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.common.block.ProductiveCeilingHangingSignBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveWallHangingSignBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ProductiveHangingSignBlockEntity extends HangingSignBlockEntity
{
    private final Block block;

    public ProductiveHangingSignBlockEntity(Block block, BlockPos pos, BlockState state) {
        super(pos, state);
        this.block = block;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        if (block instanceof ProductiveCeilingHangingSignBlock signBlock) {
            return signBlock.getBlockEntity().get();
        }
        if (block instanceof ProductiveWallHangingSignBlock signBlock) {
            return signBlock.getBlockEntity().get();
        }
        return BlockEntityType.HANGING_SIGN;
    }
}
