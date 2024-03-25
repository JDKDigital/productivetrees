package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.datagen.BlockLootProvider;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LootDataProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;

    public LootDataProvider(PackOutput output, List<LootTableProvider.SubProviderEntry> providers) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        this.subProviders = providers;
    }

    @Override
    public String getName() {
        return "Productive Trees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().get().generate((resourceLocation, builder) -> {
                builder.setRandomSequence(resourceLocation);
                if (map.put(resourceLocation, builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceLocation);
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(cache, LootDataType.TABLE.parser().toJsonTree(entry.getValue()), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
        private static final float[] JUNGLE_LEAVES_SAPLING_CHANGES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
        private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

        private static final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();
        private static final LootItemCondition.Builder SHEARS_DIG = CanToolPerformAction.canToolPerformAction(ToolActions.SHEARS_DIG);
        private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
        private static final LootItemCondition.Builder SHEARS_OR_SILK = SHEARS_DIG.or(SILK_TOUCH);

        private final List<Block> knownBlocks = new ArrayList<>();

        public LootProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelf(TreeRegistrator.SAWMILL.get());
            dropSelf(TreeRegistrator.STRIPPER.get());
            dropSelf(TreeRegistrator.WOOD_WORKER.get());
            dropSelf(TreeRegistrator.POLLEN_SIFTER.get());
            dropSelf(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get());
            dropSelf(TreeRegistrator.COCONUT_SPROUT.get());

            TreeFinder.trees.forEach((id, treeObject) -> {
                var saplingChance = treeObject.getStyle().saplingStyle().equals("jungle") ? JUNGLE_LEAVES_SAPLING_CHANGES : NORMAL_LEAVES_SAPLING_CHANCES;
                add(TreeUtil.getBlock(id, "_leaves"), leaf -> createOptionalLeavesDrops(leaf, TreeUtil.getBlock(id, "_sapling"), saplingChance));
                dropSelf(TreeUtil.getBlock(id, "_sapling"));
                dropSelf(TreeUtil.getBlock(id, "_log"));
                dropSelf(TreeUtil.getBlock(id, "_planks"));
                dropSelf(TreeUtil.getBlock(id, "_wood"));
                dropSelf(TreeUtil.getBlock(id, "_stripped_log"));
                dropSelf(TreeUtil.getBlock(id, "_stripped_wood"));
                dropSelf(TreeUtil.getBlock(id, "_slab"));
                dropSelf(TreeUtil.getBlock(id, "_stairs"));
                dropSelf(TreeUtil.getBlock(id, "_fence"));
                dropSelf(TreeUtil.getBlock(id, "_fence_gate"));
                dropSelf(TreeUtil.getBlock(id, "_pressure_plate"));
                dropSelf(TreeUtil.getBlock(id, "_button"));
                dropDoor(TreeUtil.getBlock(id, "_door"));
                dropSelf(TreeUtil.getBlock(id, "_trapdoor"));
                dropSelf(TreeUtil.getBlock(id, "_bookshelf"));
                dropSelf(TreeUtil.getBlock(id, "_sign"));
                dropOther(TreeUtil.getBlock(id, "_wall_sign"), TreeUtil.getBlock(id, "_sign"));
                dropSelf(TreeUtil.getBlock(id, "_hanging_sign"));
                dropOther(TreeUtil.getBlock(id, "_wall_hanging_sign"), TreeUtil.getBlock(id, "_hanging_sign"));
                if (treeObject.getStyle().hiveStyle() != null) {
                    Block hive = ForgeRegistries.BLOCKS.getValue(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive"));
                    Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, BlockLootProvider::genHiveDrop);
                    this.add(hive, hiveFunc.apply(hive));
                    Block box = ForgeRegistries.BLOCKS.getValue(treeObject.getId().withPath(p ->  "expansion_box_" + p));
                    Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, BlockLootProvider::genExpansionDrop);
                    this.add(box, expansionFunc.apply(box));
                }
            });

            TreeRegistrator.CRATED_CROPS.forEach(cratePath -> {
                dropSelf(ForgeRegistries.BLOCKS.getValue(cratePath));
            });
        }

        @Override
        protected void add(Block block, LootTable.Builder builder) {
            super.add(block, builder);
            knownBlocks.add(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return knownBlocks;
        }

        protected void add(Block block, Function<Block, LootTable.Builder> builderFunction) {
            this.add(block, builderFunction.apply(block));
        }

        public void dropSelf(@NotNull Block block) {
            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(block, LootProvider::genOptionalBlockDrop);
            this.add(block, func.apply(block));
        }

        public void dropDoor(@NotNull Block block) {
            this.add(block, this::createDoorTable);
        }

        protected static @NotNull LootTable.Builder createOptionalLeavesDrops(Block block, Block sapling, float... dropChances) {
            return createSilkTouchOrShearsDispatchTable(block,
                    OptionalLootItem.lootTableItem(sapling)
                            .when(ExplosionCondition.survivesExplosion())
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, dropChances))
            )
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .when(SHEARS_OR_SILK.invert())
                                    .add(OptionalLootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyExplosionDecay.explosionDecay()).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))));
        }

        protected static @NotNull LootTable.Builder createSilkTouchOrShearsDispatchTable(Block block, LootPoolEntryContainer.Builder<?> builder) {
            return createSelfDropDispatchTable(block, SHEARS_DIG.or(SILK_TOUCH), builder);
        }

        protected static @NotNull LootTable.Builder createSelfDropDispatchTable(Block block, LootItemCondition.Builder conditions, LootPoolEntryContainer.Builder<?> alternative) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(OptionalLootItem.lootTableItem(block).when(conditions).otherwise(alternative)));
        }

        protected static LootTable.Builder genOptionalBlockDrop(Block block) {
            LootPoolEntryContainer.Builder<?> builder = OptionalLootItem.lootTableItem(block).when(ExplosionCondition.survivesExplosion());

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(builder));
        }
    }
}
