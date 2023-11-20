package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.common.block.entity.ProductiveHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Supplier;

public class ProductiveCeilingHangingSignBlock extends CeilingHangingSignBlock
{
    private final Supplier<BlockEntityType<ProductiveHangingSignBlockEntity>> blockEntity;

    public ProductiveCeilingHangingSignBlock(Properties properties, WoodType woodType, Supplier<BlockEntityType<ProductiveHangingSignBlockEntity>> blockEntity) {
        super(properties, woodType);
        this.blockEntity = blockEntity;
    }

    public Supplier<BlockEntityType<ProductiveHangingSignBlockEntity>> getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProductiveHangingSignBlockEntity(this, pos, state);
    }
}
