package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.common.block.ProductiveStandingSignBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveWallSignBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ProductiveSignBlockEntity extends SignBlockEntity
{
    private final Block block;

    public ProductiveSignBlockEntity(Block block, BlockPos pos, BlockState state) {
        super(pos, state);
        this.block = block;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        if (block instanceof ProductiveStandingSignBlock signBlock) {
            return signBlock.getBlockEntity().get();
        }
        if (block instanceof ProductiveWallSignBlock signBlock) {
            return signBlock.getBlockEntity().get();
        }
        return BlockEntityType.SIGN;
    }
}
