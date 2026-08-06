package cy.jdkdigital.productivetrees.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PollinatedLeaves extends LeavesBlock implements EntityBlock
{
    public static final MapCodec<PollinatedLeaves> CODEC = simpleCodec(PollinatedLeaves::new);

    public PollinatedLeaves(Properties properties) {
        super(0.0F, properties);
    }

    @Override
    public MapCodec<PollinatedLeaves> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
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
        if (pLevel.isClientSide() && pLevel.getRandom().nextBoolean() && pLevel.getRandom().nextBoolean() && pLevel.getRandom().nextBoolean()) {
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
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // the pollination result is stored on the block entity, so drop it directly instead of from a loot table
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PollinatedLeavesBlockEntity blockEntity && !blockEntity.getResult().isEmpty()) {
            return List.of(blockEntity.getResult().copy());
        }
        return super.getDrops(state, params);
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.getBlockEntity(pPos) instanceof PollinatedLeavesBlockEntity blockEntity) {
            return blockEntity.getResult();
        }
        return ItemStack.EMPTY;
    }
}
