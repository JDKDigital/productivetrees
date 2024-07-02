package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PollinatedLeaves extends LeavesBlock implements EntityBlock
{
    public PollinatedLeaves(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PollinatedLeavesBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }


    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pLevel.isClientSide && pLevel.random.nextBoolean() && pLevel.random.nextBoolean() && pLevel.random.nextBoolean()) {
            var hasSpyglass = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.SPYGLASS) || Minecraft.getInstance().player.getItemInHand(InteractionHand.OFF_HAND).is(Items.SPYGLASS);
            if (hasSpyglass) {
                ParticleUtils.spawnParticleInBlock(pLevel, pPos, 15, ParticleTypes.HAPPY_VILLAGER);
            }
        }
    }

    @Override
    protected boolean decaying(BlockState blockState) {
        return false;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.getBlockEntity(pPos) instanceof PollinatedLeavesBlockEntity blockEntity) {
            return blockEntity.getResult();
        }
        return ItemStack.EMPTY;
    }
}
