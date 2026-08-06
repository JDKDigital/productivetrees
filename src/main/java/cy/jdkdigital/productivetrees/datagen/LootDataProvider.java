package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivelib.loot.condition.OptionalCopyBlockState;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.loot.OptionalBlockStatePropertyCondition;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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

public class LootDataProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public LootDataProvider(PackOutput output, List<LootTableProvider.SubProviderEntry> providers, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.subProviders = providers;
        this.registries = registries;
    }

    @Override
    public String getName() {
        return "Productive Trees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> this.run(pOutput, provider));
    }

    private CompletableFuture<?> run(CachedOutput pOutput, HolderLookup.Provider pProvider) {
        final Map<Identifier, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().apply(pProvider).generate((resourceKey, builder) -> {
                builder.setRandomSequence(resourceKey.identifier());
                if (map.put(resourceKey.identifier(), builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceKey.identifier());
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(pOutput, pProvider, LootTable.DIRECT_CODEC, entry.getValue(), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
        private static final float[] JUNGLE_LEAVES_SAPLING_CHANGES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
        private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
        // higher drop odds than sticks/saplings — bay leaves are the harvest
        private static final float[] BAY_LEAF_CHANCES = new float[]{0.1F, 0.125F, 0.16666667F, 0.25F, 0.5F};

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

            dropSelf(TreeRegistrator.SAWMILL.get());
            dropSelf(TreeRegistrator.STRIPPER.get());
            dropSelf(TreeRegistrator.WOOD_WORKER.get());
            dropSelf(TreeRegistrator.POLLEN_SIFTER.get());
            dropSelf(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get());
            dropSelf(TreeRegistrator.COCONUT_SPROUT.get());

            TreeFinder.trees.forEach((id, treeObject) -> {
                var saplingChance = treeObject.getStyle().saplingStyle().equals("jungle") ? JUNGLE_LEAVES_SAPLING_CHANGES : NORMAL_LEAVES_SAPLING_CHANCES;
                if (id.getPath().equals("bay_leaf")) {
                    add(TreeUtil.getBlock(id, "_leaves"), leaf -> createHarvestLeavesDrops(leaf, TreeUtil.getBlock(id, "_sapling"), TreeRegistrator.BAY_LEAF.get(), BAY_LEAF_CHANCES, saplingChance));
                } else {
                    add(TreeUtil.getBlock(id, "_leaves"), leaf -> createOptionalLeavesDrops(leaf, TreeUtil.getBlock(id, "_sapling"), saplingChance));
                }
                if (treeObject.hasFruit()) {
                    var fruitItem = treeObject.getFruit().getItemType();
                    if (fruitItem.equals(Items.AIR)) {
                        add(TreeUtil.getBlock(id, "_fruit"), leaf -> createFruitlessFruitDrops(TreeUtil.getBlock(id, "_sapling"), saplingChance));
                    } else {
                        add(TreeUtil.getBlock(id, "_fruit"), leaf -> createFruitLeavesDrops(leaf, TreeUtil.getBlock(id, "_sapling"), fruitItem, saplingChance));
                    }
                }
                dropSelf(TreeUtil.getBlock(id, "_sapling"));
                dropSelf(TreeUtil.getBlock(id, "_log"));
                dropSelf(TreeUtil.getBlock(id, "_planks"));
                dropSelf(TreeUtil.getBlock(id, "_wood"));
                dropSelf(TreeUtil.getBlock(id, "_stripped_log"));
                dropSelf(TreeUtil.getBlock(id, "_stripped_wood"));
                if (!ProductiveTrees.isMinimal) {
                    // These decorative/functional blocks are skipped in minimal mode, but their loot tables ship in
                    // every build. Using productivelib's OptionalLootItem lets the tables still PARSE when the block
                    // and its item are unregistered (a vanilla item entry hard-fails on the missing item); full-mode
                    // drop behaviour is unchanged.
                    optionalDropSelf(TreeUtil.getBlock(id, "_slab"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_stairs"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_fence"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_fence_gate"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_pressure_plate"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_button"));
                    optionalDropDoor(TreeUtil.getBlock(id, "_door"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_trapdoor"));
                    optionalBookshelf(TreeUtil.getBlock(id, "_bookshelf"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_sign"));
                    optionalDropOther(TreeUtil.getBlock(id, "_wall_sign"), TreeUtil.getBlock(id, "_sign"));
                    optionalDropSelf(TreeUtil.getBlock(id, "_hanging_sign"));
                    optionalDropOther(TreeUtil.getBlock(id, "_wall_hanging_sign"), TreeUtil.getBlock(id, "_hanging_sign"));
                }
            });

            TreeRegistrator.CRATED_CROPS.forEach(cratePath -> {
                dropSelf(BuiltInRegistries.BLOCK.getValue(cratePath));
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

        protected @NotNull LootTable.Builder createOptionalLeavesDrops(Block block, Block sapling, float... dropChances) {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return createSilkTouchOrShearsDispatchTable(block,
                    LootItem.lootTableItem(sapling)
                            .when(ExplosionCondition.survivesExplosion())
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), dropChances))
            )
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .when(SHEARS_OR_SILK.invert())
                                    .add(LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyExplosionDecay.explosionDecay()).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))));
        }

        protected @NotNull LootTable.Builder createSilkTouchOrShearsDispatchTable(Block block, LootPoolEntryContainer.Builder<?> builder) {
            return createSelfDispatchTable(block, SHEARS_OR_SILK, builder);
        }

        // the normal leaf drops plus a harvest-item pool at its own rate
        protected @NotNull LootTable.Builder createHarvestLeavesDrops(Block block, Block sapling, Item harvest, float[] harvestChances, float... saplingChances) {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return createOptionalLeavesDrops(block, sapling, saplingChances)
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .when(SHEARS_OR_SILK.invert())
                                    .add(LootItem.lootTableItem(harvest).apply(ApplyExplosionDecay.explosionDecay()).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), harvestChances))));
        }

        protected @NotNull LootTable.Builder createFruitLeavesDrops(Block block, Block sapling, Item fruit, float... dropChances) {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return LootTable.lootTable()
                    .withPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                    LootItem.lootTableItem(sapling).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), dropChances)).apply(ApplyExplosionDecay.explosionDecay())
                            )
                    ).withPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                    LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyExplosionDecay.explosionDecay()).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))
                            )
                    ).withPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                    LootItem.lootTableItem(fruit).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ProductiveFruitBlock.getAgeProperty(), ProductiveFruitBlock.getMaxAge()))).apply(ApplyExplosionDecay.explosionDecay())
                            )
                    );
        }

        protected @NotNull LootTable.Builder createFruitlessFruitDrops(Block sapling, float... dropChances) {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return LootTable.lootTable()
                    .withPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                    LootItem.lootTableItem(sapling).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), dropChances)).apply(ApplyExplosionDecay.explosionDecay())
                            )
                    ).withPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                    LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyExplosionDecay.explosionDecay()).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))
                            )
                    );
        }

        protected @NotNull LootTable.Builder createSelfDispatchTable(Block block, LootItemCondition.Builder conditions, LootPoolEntryContainer.Builder<?> alternative) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(conditions).otherwise(alternative)));
        }

        protected static LootTable.Builder genOptionalBlockDrop(Block block) {
            LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(block).when(ExplosionCondition.survivesExplosion());

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(builder));
        }

        // The blocks these serve only exist outside minimal mode, but their tables ship in every build and must
        // still parse when the block/item is absent. OptionalLootItem (productivelib) tolerates a missing item at
        // load where a vanilla LootItem hard-fails; full-mode drop behaviour is preserved.
        private void optionalDropSelf(Block block) {
            this.add(block, LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(OptionalLootItem.lootTableItem(block).when(ExplosionCondition.survivesExplosion()))));
        }

        private void optionalDropOther(Block block, Block drop) {
            this.add(block, LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(OptionalLootItem.lootTableItem(drop).when(ExplosionCondition.survivesExplosion()))));
        }

        private void optionalDropDoor(Block block) {
            // Tolerant block-state condition: vanilla LootItemBlockStatePropertyCondition names the block strictly and
            // would drop the whole table on load when minimal mode leaves the door unregistered.
            this.add(block, LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(OptionalLootItem.lootTableItem(block)
                                    .when(OptionalBlockStatePropertyCondition.of(block,
                                            StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
        }

        private void optionalBookshelf(Block block) {
            this.add(block, LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(OptionalLootItem.lootTableItem(block)
                                    .when(this.hasSilkTouch())
                                    .otherwise(LootItem.lootTableItem(Items.BOOK)
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
                                            .apply(ApplyExplosionDecay.explosionDecay())))));
        }
    }
}
