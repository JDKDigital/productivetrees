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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) != 0) {
            // a larger mega tier, when the tree also has a smaller mega (e.g. yew, sequoia); check it before the
            // smaller tiers below so a full large patch grows the big variant rather than a sub-patch of it.
            // the large patch is the next size up from the mega: radius (megaConfiguration - 1), so a 2x2 mega
            // pairs with a 3x3 large patch and a 5x5 mega pairs with a 9x9 large patch.
            if (!treeObject.getLargeMegaFeature().equals(TreeRegistrator.NULL_FEATURE)
                    && tryGrowMegaPatch(level, pos, random, treeObject.getMegaConfiguration() - 1, treeObject.getLargeMegaFeature())) {
                return;
            }
            // odd-sized mega patches (3x3, 5x5, ...), since the vanilla TreeGrower only handles 1x1 and 2x2
            int config = treeObject.getMegaConfiguration();
            if (config >= 3 && config % 2 == 1 && !treeObject.getMegaFeature().equals(TreeRegistrator.NULL_FEATURE)
                    && tryGrowMegaPatch(level, pos, random, config / 2, treeObject.getMegaFeature())) {
                return;
            }
            // 2x2 megas ourselves: vanilla clears the four saplings invisibly (client ghosts) and assumes a 2x2 trunk,
            // so a 1x1-trunk mega like bull_pine would strand phantom saplings
            if (config == 2 && !treeObject.getMegaFeature().equals(TreeRegistrator.NULL_FEATURE)
                    && tryGrow2x2Patch(level, pos, random, treeObject.getMegaFeature())) {
                return;
            }
        }
        super.advanceTree(level, pos, state, random);
    }

    private boolean tryGrowMegaPatch(ServerLevel level, BlockPos pos, RandomSource random, int radius, ResourceKey<ConfiguredFeature<?, ?>> feature) {
        Block sapling = level.getBlockState(pos).getBlock();
        // pos can be any sapling in the patch, so test every centre whose patch still contains pos
        for (int ox = -radius; ox <= radius; ++ox) {
            for (int oz = -radius; oz <= radius; ++oz) {
                BlockPos center = pos.offset(ox, 0, oz);
                if (isFullPatch(level, center, sapling, radius)) {
                    return placeMegaPatch(level, center, random, radius, feature);
                }
            }
        }
        return false;
    }

    private boolean isFullPatch(ServerLevel level, BlockPos center, Block sapling, int radius) {
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                if (!level.getBlockState(center.offset(dx, 0, dz)).is(sapling)) {
                    return false;
                }
            }
        }
        return true;
    }

    // a 2x2 patch keyed off its lower (min x,z) corner, where the 2x2 trunk placers build from
    private boolean tryGrow2x2Patch(ServerLevel level, BlockPos pos, RandomSource random, ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        Block sapling = level.getBlockState(pos).getBlock();
        for (int ox = -1; ox <= 0; ++ox) {
            for (int oz = -1; oz <= 0; ++oz) {
                BlockPos corner = pos.offset(ox, 0, oz);
                if (level.getBlockState(corner).is(sapling)
                        && level.getBlockState(corner.offset(1, 0, 0)).is(sapling)
                        && level.getBlockState(corner.offset(0, 0, 1)).is(sapling)
                        && level.getBlockState(corner.offset(1, 0, 1)).is(sapling)) {
                    return place2x2Patch(level, corner, random, featureKey);
                }
            }
        }
        return false;
    }

    private boolean place2x2Patch(ServerLevel level, BlockPos corner, RandomSource random, ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        var holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(featureKey);
        if (holder.isEmpty()) {
            return false;
        }
        ConfiguredFeature<?, ?> feature = holder.get().value();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos[] cells = {corner, corner.offset(1, 0, 0), corner.offset(0, 0, 1), corner.offset(1, 0, 1)};
        BlockState[] saved = new BlockState[cells.length];
        for (int i = 0; i < cells.length; ++i) {
            saved[i] = level.getBlockState(cells[i]);
            level.setBlock(cells[i], air, Block.UPDATE_ALL);
        }
        if (feature.place(level, generator, random, corner)) {
            // clear this tree's saplings in a margin so a narrow trunk leaves none stranded
            for (int dx = -2; dx <= 3; ++dx) {
                for (int dz = -2; dz <= 3; ++dz) {
                    BlockPos leftover = corner.offset(dx, 0, dz);
                    if (level.getBlockState(leftover).is(this)) {
                        level.setBlock(leftover, air, Block.UPDATE_ALL);
                    }
                }
            }
            return true;
        }
        for (int i = 0; i < cells.length; ++i) {
            level.setBlock(cells[i], saved[i], Block.UPDATE_ALL);
        }
        return false;
    }

    private boolean placeMegaPatch(ServerLevel level, BlockPos center, RandomSource random, int radius, ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        var holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(featureKey);
        if (holder.isEmpty()) {
            return false;
        }
        ConfiguredFeature<?, ?> feature = holder.get().value();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        BlockState air = Blocks.AIR.defaultBlockState();
        int width = 2 * radius + 1;
        BlockPos[] cells = new BlockPos[width * width];
        BlockState[] saved = new BlockState[width * width];
        int i = 0;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                BlockPos cell = center.offset(dx, 0, dz);
                cells[i] = cell;
                saved[i] = level.getBlockState(cell);
                level.setBlock(cell, air, Block.UPDATE_ALL);
                ++i;
            }
        }
        if (feature.place(level, generator, random, center)) {
            // a wide or off-centre mega trunk, or a patch planted larger than needed, may leave saplings around the
            // trunk (e.g. the unfilled corners of a 5x5); clear this tree's saplings in a generous margin so none
            // survive to grow into stray 1x1 trees beside the mega
            int sweep = radius + 2;
            for (int dx = -sweep; dx <= sweep; ++dx) {
                for (int dz = -sweep; dz <= sweep; ++dz) {
                    BlockPos leftover = center.offset(dx, 0, dz);
                    if (level.getBlockState(leftover).is(this)) {
                        level.setBlock(leftover, air, Block.UPDATE_ALL);
                    }
                }
            }
            return true;
        }
        for (int j = 0; j < cells.length; ++j) {
            level.setBlock(cells[j], saved[j], Block.UPDATE_ALL);
        }
        return false;
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
                configurations += switch (saplingBlock.treeObject.getMegaConfiguration()) {
                    case 5 -> "5x5 ";
                    case 3 -> "3x3 ";
                    default -> "2x2 ";
                };
            }
            if (!saplingBlock.treeObject.getLargeMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                // the large patch is (megaConfiguration - 1) radius wide, matching the growth check above
                int largeWidth = 2 * (saplingBlock.treeObject.getMegaConfiguration() - 1) + 1;
                configurations += largeWidth + "x" + largeWidth + " ";
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
