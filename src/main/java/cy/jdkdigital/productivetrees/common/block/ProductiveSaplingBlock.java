package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class ProductiveSaplingBlock extends SaplingBlock
{
    private final TreeObject treeObject;

    public ProductiveSaplingBlock(TreeGrower grower, Properties properties, TreeObject treeObject) {
        super(grower, properties);
        this.treeObject = treeObject;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(treeObject.getSoil());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return treeObject.canForceGrowth();
    }

    public TreeObject getTree() {
        return treeObject;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
        if (pStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveSaplingBlock saplingBlock) {
            pTootipComponents.add(Component.translatable("block." + ProductiveTrees.MODID + "." + saplingBlock.treeObject.getId().getPath() + ".latin").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
            super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
            String configurations = "";
            if (!saplingBlock.treeObject.getFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                configurations += "1x1 ";
            }
            if (!saplingBlock.treeObject.getMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                configurations += "2x2 ";
            }
            if (!configurations.isEmpty()) {
                pTootipComponents.add(Component.translatable(ProductiveTrees.MODID + ".sapling.configurations", configurations).withStyle(ChatFormatting.GOLD));
            }
        } else {
            super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        if (random.nextInt(7) == 0 && canGrowAtPos(level, pos)) {
            this.advanceTree(level, pos, state, random);
        }
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        if (super.isBonemealSuccess(level, random, pos, state) && level instanceof ServerLevel serverLevel && canGrowAtPos(serverLevel, pos)) {
            return true;
        }
        return false;
    }

    private boolean canGrowAtPos(ServerLevel level, BlockPos pos) {
        int lightLevel = level.getMaxLocalRawBrightness(pos.above());
        if (lightLevel >= treeObject.getGrowthConditions().minLight() && lightLevel <= treeObject.getGrowthConditions().maxLight()) {
            if (!treeObject.getGrowthConditions().fluid().equals(Fluids.EMPTY) && !level.getFluidState(pos).is(treeObject.getGrowthConditions().fluid().getFluid())) {
                return false;
            }
            var biome = level.getBiome(pos);
            return treeObject.getGrowthConditions().biome() == null || treeObject.getGrowthConditions().biome().isEmpty() || treeObject.getGrowthConditions().biome().get().contains(biome);
        }
        return true;
    }
}
