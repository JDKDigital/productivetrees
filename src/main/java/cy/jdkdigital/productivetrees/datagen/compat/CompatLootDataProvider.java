package cy.jdkdigital.productivetrees.datagen.compat;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.datagen.BlockLootProvider;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivelib.loot.condition.OptionalCopyBlockState;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompatLootDataProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public CompatLootDataProvider(PackOutput output, List<LootTableProvider.SubProviderEntry> providers, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.subProviders = providers;
        this.registries = registries;
    }

    @Override
    public String getName() {
        return "Productive Trees Compat Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> this.run(pOutput, provider));
    }

    private CompletableFuture<?> run(CachedOutput pOutput, HolderLookup.Provider pProvider) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().apply(pProvider).generate((resourceKey, builder) -> {
                builder.setRandomSequence(resourceKey.location());
                if (map.put(resourceKey.location(), builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceKey.location());
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(pOutput, pProvider, LootTable.DIRECT_CODEC, entry.getValue(), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private static final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();
        private static final LootItemCondition.Builder SHEARS_DIG = CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_DIG);
        private static LootItemCondition.Builder SILK_TOUCH;
        private static LootItemCondition.Builder SHEARS_OR_SILK;

        private final List<Block> knownBlocks = new ArrayList<>();

        public LootProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            SILK_TOUCH = this.hasSilkTouch();
            SHEARS_OR_SILK = SHEARS_DIG.or(SILK_TOUCH);

            TreeFinder.trees.forEach((id, treeObject) -> {
                if (treeObject.getStyle().hiveStyle() != null) {
                    Block hive = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive"));
                    Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, LootProvider::genHiveDrop);
                    this.add(hive, hiveFunc.apply(hive));
                    Block box = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p ->  "expansion_box_" + p));
                    Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, BlockLootProvider.LootProvider::genExpansionDrop);
                    this.add(box, expansionFunc.apply(box));
                }
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

        public static LootTable.Builder genHiveDrop(Block hive) {
            LootPoolEntryContainer.Builder<?> hiveNoHoney = OptionalLootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion())
                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES));

            LootPoolEntryContainer.Builder<?> hiveHoney;
            if (hive.defaultBlockState().hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                hiveHoney = OptionalLootItem.lootTableItem(hive).when(SILK_TOUCH)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES))
                        .apply(OptionalCopyBlockState.copyState(hive).copy(BeehiveBlock.HONEY_LEVEL));
            } else {
                hiveHoney = OptionalLootItem.lootTableItem(hive).when(SILK_TOUCH)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES));
            }

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(hiveHoney.otherwise(hiveNoHoney)));
        }
    }
}
