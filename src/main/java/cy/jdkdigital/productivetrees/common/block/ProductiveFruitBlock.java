package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;

public class ProductiveFruitBlock extends ProductiveLeavesBlock
{
    public ProductiveFruitBlock(Properties properties, TreeObject treeObject) {
        super(properties, treeObject);
        this.registerDefaultState(this.stateDefinition.any().setValue(getAgeProperty(), 0).setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(getAgeProperty());
    }

    @Override
    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int p_49229_, int p_49230_) {
        super.triggerEvent(blockState, level, blockPos, p_49229_, p_49230_);
        BlockEntity blockentity = level.getBlockEntity(blockPos);
        return blockentity != null && blockentity.triggerEvent(p_49229_, p_49230_);
    }

    public static IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_7;
    }

    public static int getMaxAge() {
        return BlockStateProperties.MAX_AGE_7;
    }

    protected int getAge(BlockState blockState) {
        return blockState.getValue(getAgeProperty());
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(treeObject.getFruit().getItem().getItem());
    }

    public BlockState getStateForAge(BlockState currentState, int age) {
        return currentState.setValue(getAgeProperty(), age);
    }

    public static boolean isMaxAge(BlockState blockState) {
        return blockState.getValue(getAgeProperty()) >= getMaxAge();
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return !isMaxAge(blockState);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        super.randomTick(state, level, blockPos, random);
        int light = level.getRawBrightness(blockPos, 0);
        if (light >= treeObject.getGrowthConditions().minLight() && light <= treeObject.getGrowthConditions().maxLight()) {
            int i = this.getAge(state);
            if (i < getMaxAge()) {
                if (ForgeHooks.onCropsGrowPre(level, blockPos, state, random.nextInt((int)(25.0F / treeObject.getFruit().growthSpeed()) + 1) == 0)) {
                    level.setBlock(blockPos, this.getStateForAge(state, i + 1), 2);
                    ForgeHooks.onCropsGrowPost(level, blockPos, state);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (isMaxAge(state)) {
            popResource(level, pos.relative(hitResult.getDirection()), treeObject.getFruit().getItem());
            level.setBlock(pos, this.getStateForAge(state, 0), 2);
            return InteractionResult.SUCCESS;
        }

        var heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.BONE_MEAL)) {
            int i = this.getAge(state);
            level.setBlock(pos, this.getStateForAge(state, i + 1), 2);
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
