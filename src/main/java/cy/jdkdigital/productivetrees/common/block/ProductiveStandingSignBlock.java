package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.common.block.entity.ProductiveSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Supplier;

public class ProductiveStandingSignBlock extends StandingSignBlock
{
    private final Supplier<BlockEntityType<ProductiveSignBlockEntity>> blockEntity;

    public ProductiveStandingSignBlock(Properties properties, WoodType woodType, Supplier<BlockEntityType<ProductiveSignBlockEntity>> blockEntity) {
        super(properties, woodType);
        this.blockEntity = blockEntity;
    }

    public Supplier<BlockEntityType<ProductiveSignBlockEntity>> getBlockEntity() {
        return blockEntity;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProductiveSignBlockEntity(this, pos, state);
    }
}
