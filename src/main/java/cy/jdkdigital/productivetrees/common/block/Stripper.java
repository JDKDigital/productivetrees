package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivebees.common.block.CapabilityContainerBlock;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Stripper extends CapabilityContainerBlock
{
    public Stripper(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StripperBlockEntity tileEntity) {
            openGui((ServerPlayer) player, tileEntity);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, TreeRegistrator.STRIPPER_BLOCK_ENTITY.get(), StripperBlockEntity ::tick);
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
        return new StripperBlockEntity(pos, state);
    }

    public void openGui(ServerPlayer player, StripperBlockEntity tileEntity) {
        NetworkHooks.openScreen(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}
