package cy.jdkdigital.productivetrees.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivelib.common.block.CapabilityContainerBlock;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.WoodWorkerBlockEntity;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WoodWorker extends CapabilityContainerBlock
{
    public static final MapCodec<WoodWorker> CODEC = simpleCodec(WoodWorker::new);

    public WoodWorker(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof StripperBlockEntity blockEntity) {
            if (!pLevel.isClientSide()) {
                openGui((ServerPlayer) pPlayer, blockEntity);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, TreeRegistrator.WOOD_WORKER_BLOCK_ENTITY.get(), WoodWorkerBlockEntity ::tick);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodWorkerBlockEntity(pos, state);
    }

    public void openGui(ServerPlayer player, StripperBlockEntity blockEntity) {
        player.openMenu(blockEntity, packetBuffer -> packetBuffer.writeBlockPos(blockEntity.getBlockPos()));
    }
}
