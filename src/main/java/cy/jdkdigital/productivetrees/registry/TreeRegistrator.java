package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.*;
import cy.jdkdigital.productivetrees.common.feature.EntityPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafReplacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.TrunkVineDecorator;
import cy.jdkdigital.productivetrees.common.fluid.MapleSap;
import cy.jdkdigital.productivetrees.common.fluid.type.MapleSapType;
import cy.jdkdigital.productivetrees.common.item.PollenItem;
import cy.jdkdigital.productivetrees.common.item.SieveUpgradeItem;
import cy.jdkdigital.productivetrees.feature.foliageplacers.TaperedFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.CenteredUpwardsBranchingTrunkPlacer;
import cy.jdkdigital.productivetrees.inventory.PollenSifterContainer;
import cy.jdkdigital.productivetrees.inventory.SawmillContainer;
import cy.jdkdigital.productivetrees.inventory.StripperContainer;
import cy.jdkdigital.productivetrees.inventory.WoodWorkerContainer;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.util.CropConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TreeRegistrator
{
    public static final RegistryObject<PoiType> ADVANCED_HIVES = ProductiveTrees.POI_TYPES.register("advanced_beehive", () -> {
        Set<BlockState> blockStates = new HashSet<>();
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.getStyle().hiveStyle() != null) {
                try {
                    blockStates.addAll(treeObject.getHiveBlock().get().getStateDefinition().getPossibleStates());
                } catch (NullPointerException e) {
                    throw new RuntimeException(treeObject.getId() + " failed hive");
                }
            }
        });
        return new PoiType(blockStates, 1, 1);
    });
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(ProductiveTrees.MODID, ProductiveTrees.MODID));
    public static final RegistryObject<CreativeModeTab> TAB = ProductiveTrees.CREATIVE_MODE_TABS.register(ProductiveTrees.MODID, () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sawmill"))))
                .title(Component.translatable("itemGroup." + ProductiveTrees.MODID))
                .build();
    });
    public static final RegistryObject<Block> POLLINATED_LEAVES = ProductiveTrees.BLOCKS.register("pollinated_leaves", () -> new PollinatedLeaves(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<BlockEntityType<PollinatedLeavesBlockEntity>> POLLINATED_LEAVES_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("pollinated_leaves", () -> BlockEntityType.Builder.of(PollinatedLeavesBlockEntity::new, POLLINATED_LEAVES.get()).build(null));
    public static final RegistryObject<Block> STRIPPER = registerBlock("stripper", () -> new Stripper(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()), true);
    public static final RegistryObject<BlockEntityType<StripperBlockEntity>> STRIPPER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("stripper", () -> BlockEntityType.Builder.of(StripperBlockEntity::new, STRIPPER.get()).build(null));
    public static final RegistryObject<Block> SAWMILL = registerBlock("sawmill", () -> new Sawmill(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()), true);
    public static final RegistryObject<BlockEntityType<SawmillBlockEntity>> SAWMILL_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("sawmill", () -> BlockEntityType.Builder.of(SawmillBlockEntity::new, SAWMILL.get()).build(null));
    public static final RegistryObject<Block> WOOD_WORKER = registerBlock("wood_worker", () -> new WoodWorker(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()), true);
    public static final RegistryObject<BlockEntityType<WoodWorkerBlockEntity>> WOOD_WORKER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("wood_worker", () -> BlockEntityType.Builder.of(WoodWorkerBlockEntity::new, WOOD_WORKER.get()).build(null));
    public static final RegistryObject<Block> POLLEN_SIFTER = registerBlock("pollen_sifter", () -> new PollenSifter(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()), true);
    public static final RegistryObject<BlockEntityType<PollenSifterBlockEntity>> POLLEN_SIFTER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("pollen_sifter", () -> BlockEntityType.Builder.of(PollenSifterBlockEntity::new, POLLEN_SIFTER.get()).build(null));
    public static final RegistryObject<Block> ENTITY_SPAWNER = ProductiveTrees.BLOCKS.register("entity_spawner", () -> new EntitySpawner(BlockBehaviour.Properties.copy(Blocks.AIR)));
    public static final RegistryObject<BlockEntityType<EntitySpawnerBlockEntity>> ENTITY_SPAWNER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("entity_spawner", () -> BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new, ENTITY_SPAWNER.get()).build(null));
    public static final RegistryObject<Block> TIME_TRAVELLER_DISPLAY = registerBlock("time_traveller_display", () -> new TimeTravellerDisplay(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)), true);
    public static final RegistryObject<BlockEntityType<TimeTravellerDisplayBlockEntity>> TIME_TRAVELLER_DISPLAY_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("time_traveller_display", () -> BlockEntityType.Builder.of(TimeTravellerDisplayBlockEntity::new, TIME_TRAVELLER_DISPLAY.get()).build(null));

    public static final RegistryObject<MenuType<StripperContainer>> STRIPPER_MENU = ProductiveTrees.CONTAINER_TYPES.register("stripper", () ->
            IForgeMenuType.create(StripperContainer::new)
    );
    public static final RegistryObject<MenuType<SawmillContainer>> SAWMILL_MENU = ProductiveTrees.CONTAINER_TYPES.register("sawmill", () ->
            IForgeMenuType.create(SawmillContainer::new)
    );
    public static final RegistryObject<MenuType<WoodWorkerContainer>> WOOD_WORKER_MENU = ProductiveTrees.CONTAINER_TYPES.register("wood_worker", () ->
            IForgeMenuType.create(WoodWorkerContainer::new)
    );
    public static final RegistryObject<MenuType<PollenSifterContainer>> POLLEN_SIFTER_MENU = ProductiveTrees.CONTAINER_TYPES.register("pollen_sifter", () ->
            IForgeMenuType.create(PollenSifterContainer::new)
    );

    public static final RegistryObject<FluidType> MAPLE_SAP_TYPE = ProductiveTrees.FLUID_TYPES.register("maple_sap", MapleSapType::new);
    public static final RegistryObject<FlowingFluid> MAPLE_SAP = ProductiveTrees.FLUIDS.register("maple_sap", MapleSap.Source::new);
    public static final RegistryObject<FlowingFluid> MAPLE_SAP_FLOWING = ProductiveTrees.FLUIDS.register("flowing_maple_sap", MapleSap.Flowing::new);

    public static final RegistryObject<Item> MAPLE_SAP_BUCKET = registerItem("maple_sap_bucket", () -> new BucketItem(MAPLE_SAP, new Item.Properties().craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> UPGRADE_POLLEN_SIEVE = registerItem("upgrade_pollen_sieve", () -> new SieveUpgradeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> POLLEN = registerItem("pollen", () -> new PollenItem(new Item.Properties()));
    public static final RegistryObject<Item> SAWDUST = registerItem("sawdust");
    public static final RegistryObject<Item> FUSTIC = registerItem("fustic");
    public static final RegistryObject<Item> HAEMATOXYLIN = registerItem("haematoxylin");
    public static final RegistryObject<Item> DRACAENA_SAP = registerItem("dracaena_sap");
    public static final RegistryObject<Item> RUBBER = registerItem("rubber");
    public static final RegistryObject<Item> MAPLE_SYRUP = registerItem("maple_syrup", () -> new Item(new Item.Properties().food(Foods.HONEY_BOTTLE).craftRemainder(Items.GLASS_BOTTLE)));
    public static final RegistryObject<Item> SANDALWOOD_OIL = registerItem("sandalwood_oil"); // TODO turn into potion?

    public static final RegistryObject<RecipeSerializer<?>> TREE_POLLINATION = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_pollination", () -> new TreePollinationRecipe.Serializer<>(TreePollinationRecipe::new));
    public static final RegistryObject<RecipeType<TreePollinationRecipe>> TREE_POLLINATION_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_pollination", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> TREE_FRUITING = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_fruiting", () -> new TreeFruitingRecipe.Serializer<>(TreeFruitingRecipe::new));
    public static final RegistryObject<RecipeType<TreeFruitingRecipe>> TREE_FRUITING_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_fruiting", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> LOG_STRIPPING = ProductiveTrees.RECIPE_SERIALIZERS.register("log_stripping", () -> new LogStrippingRecipe.Serializer<>(LogStrippingRecipe::new));
    public static final RegistryObject<RecipeType<LogStrippingRecipe>> LOG_STRIPPING_TYPE = ProductiveTrees.RECIPE_TYPES.register("log_stripping", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> SAW_MILLLING = ProductiveTrees.RECIPE_SERIALIZERS.register("sawmill", () -> new SawmillRecipe.Serializer<>(SawmillRecipe::new));
    public static final RegistryObject<RecipeType<SawmillRecipe>> SAW_MILLLING_TYPE = ProductiveTrees.RECIPE_TYPES.register("sawmill", () -> new RecipeType<>() {});

    public static final ResourceKey<ConfiguredFeature<?, ?>> NULL_FEATURE = FeatureUtils.createKey(ProductiveTrees.MODID + ":null");
    public static final RegistryObject<TrunkPlacerType<CenteredUpwardsBranchingTrunkPlacer>> CENTERED_UPWARDS_TRUNK_PLACER = ProductiveTrees.TRUNK_PLACERS.register("centered_upwards_branching_trunk_placer", () -> new TrunkPlacerType<>(CenteredUpwardsBranchingTrunkPlacer.CODEC));
    public static final RegistryObject<FoliagePlacerType<TaperedFoliagePlacer>> TAPERED_FOLIAGE_PLACER = ProductiveTrees.FOLIAGE_PLACERS.register("tapered_foliage_placer", () -> new FoliagePlacerType<>(TaperedFoliagePlacer.CODEC));
    public static final RegistryObject<TreeDecoratorType<FruitLeafReplacerDecorator>> FRUIT_LEAF_REPLACER = ProductiveTrees.TREE_DECORATORS.register("fruit_leaf_replacer", () -> new TreeDecoratorType<>(FruitLeafReplacerDecorator.CODEC));
    public static final RegistryObject<TreeDecoratorType<FruitLeafPlacerDecorator>> FRUIT_LEAF_PLACER = ProductiveTrees.TREE_DECORATORS.register("fruit_leaf_placer", () -> new TreeDecoratorType<>(FruitLeafPlacerDecorator.CODEC));
    public static final RegistryObject<TreeDecoratorType<EntityPlacerDecorator>> ENTITY_PLACER = ProductiveTrees.TREE_DECORATORS.register("entity_placer", () -> new TreeDecoratorType<>(EntityPlacerDecorator.CODEC));
    public static final RegistryObject<TreeDecoratorType<TrunkVineDecorator>> TRUNK_VINE = ProductiveTrees.TREE_DECORATORS.register("trunk_vine", () -> new TreeDecoratorType<>(TrunkVineDecorator.CODEC));

    // Fruiting items
    static final FoodProperties BERRY_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.1F).build();
    static final FoodProperties SMALL_FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    static final FoodProperties FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    static final FoodProperties BIG_FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F).build();
    static final FoodProperties CITRUS_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0F).build();
    static final FoodProperties BIG_CITRUS_FOOD = (new FoodProperties.Builder()).nutrition(3).saturationMod(0F).build();
    static final FoodProperties NUT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.1F).build();

    public static List<CropConfig> BERRIES = new ArrayList<>()
    {{
        add(new CropConfig("elderberry", BERRY_FOOD));
        add(new CropConfig("juniper", BERRY_FOOD));
        add(new CropConfig("sloe", BERRY_FOOD));
        add(new CropConfig("haw", BERRY_FOOD));
        add(new CropConfig("asai_berry", BERRY_FOOD));
    }};

    public static List<CropConfig> FRUITS = new ArrayList<>()
    {{
        add(new CropConfig("apricot", SMALL_FRUIT_FOOD));
        add(new CropConfig("black_cherry", SMALL_FRUIT_FOOD));
        add(new CropConfig("cherry_plum", SMALL_FRUIT_FOOD));
        add(new CropConfig("date", SMALL_FRUIT_FOOD));
        add(new CropConfig("fig", SMALL_FRUIT_FOOD));
        add(new CropConfig("kumquat", SMALL_FRUIT_FOOD));
        add(new CropConfig("olive", SMALL_FRUIT_FOOD));
        add(new CropConfig("osange_orange", SMALL_FRUIT_FOOD));
        add(new CropConfig("plum", SMALL_FRUIT_FOOD));
        add(new CropConfig("sour_cherry", SMALL_FRUIT_FOOD));
        add(new CropConfig("sparkling_cherry", SMALL_FRUIT_FOOD));
        add(new CropConfig("wild_cherry", SMALL_FRUIT_FOOD));

        add(new CropConfig("golden_delicious_apple", FRUIT_FOOD));
        add(new CropConfig("granny_smith_apple", FRUIT_FOOD));
        add(new CropConfig("beliy_naliv_apple", FRUIT_FOOD));
        add(new CropConfig("avocado", FRUIT_FOOD));
        add(new CropConfig("banana", FRUIT_FOOD));
        add(new CropConfig("red_banana", FRUIT_FOOD));
        add(new CropConfig("sweet_crabapple", FRUIT_FOOD));
        add(new CropConfig("prairie_crabapple", FRUIT_FOOD));
        add(new CropConfig("flowering_crabapple", FRUIT_FOOD));
        add(new CropConfig("grapefruit", FRUIT_FOOD));
        add(new CropConfig("nectarine", FRUIT_FOOD));
        add(new CropConfig("peach", FRUIT_FOOD));
        add(new CropConfig("pear", FRUIT_FOOD));
        add(new CropConfig("persimmon", FRUIT_FOOD));
        add(new CropConfig("pomelo", FRUIT_FOOD));
        add(new CropConfig("pomegranate", FRUIT_FOOD));
        add(new CropConfig("sand_pear", FRUIT_FOOD));
        add(new CropConfig("satsuma", FRUIT_FOOD));
        add(new CropConfig("snake_fruit", FRUIT_FOOD));
        add(new CropConfig("star_fruit", FRUIT_FOOD));
        add(new CropConfig("tangerine", FRUIT_FOOD));
        add(new CropConfig("sweetsop", FRUIT_FOOD));

        add(new CropConfig("coconut", BIG_FRUIT_FOOD));
        add(new CropConfig("mango", BIG_FRUIT_FOOD));
        add(new CropConfig("plantain", BIG_FRUIT_FOOD));
        add(new CropConfig("papaya", BIG_FRUIT_FOOD));
        add(new CropConfig("breadfruit", BIG_FRUIT_FOOD));
        add(new CropConfig("copoazu", BIG_FRUIT_FOOD));
        add(new CropConfig("cempedak", BIG_FRUIT_FOOD));
        add(new CropConfig("jackfruit", BIG_FRUIT_FOOD));
        add(new CropConfig("hala_fruit", BIG_FRUIT_FOOD));
        add(new CropConfig("soursop", BIG_FRUIT_FOOD));

        add(new CropConfig("lime", CITRUS_FOOD));
        add(new CropConfig("key_lime", CITRUS_FOOD));
        add(new CropConfig("finger_lime", CITRUS_FOOD));
        add(new CropConfig("citron", CITRUS_FOOD));
        add(new CropConfig("lemon", CITRUS_FOOD));

        add(new CropConfig("orange", BIG_CITRUS_FOOD));
        add(new CropConfig("mandarin", BIG_CITRUS_FOOD));
        add(new CropConfig("buddhas_hand", BIG_CITRUS_FOOD));
    }};

    public static List<CropConfig> NUTS = new ArrayList<>()
    {{
        add(new CropConfig("almond", NUT_FOOD));
        add(new CropConfig("acorn", NUT_FOOD));
        add(new CropConfig("beechnut", NUT_FOOD));
        add(new CropConfig("brazil_nut", NUT_FOOD));
        add(new CropConfig("butternut", NUT_FOOD));
        add(new CropConfig("candlenut", NUT_FOOD));
        add(new CropConfig("cashew", NUT_FOOD));
        add(new CropConfig("chestnut", NUT_FOOD));
        add(new CropConfig("ginkgo_nut", NUT_FOOD));
        add(new CropConfig("hazelnut", NUT_FOOD));
        add(new CropConfig("pecan", NUT_FOOD));
        add(new CropConfig("pistachio", NUT_FOOD));
        add(new CropConfig("walnut", NUT_FOOD));
    }};

    public static List<ResourceLocation> CRATED_CROPS = new ArrayList<>();

    public static void init() {
        BERRIES.forEach(cropConfig -> {
            registerItem(cropConfig.name(), cropConfig.food());
            CRATED_CROPS.add(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
        });
        FRUITS.forEach(cropConfig -> {
            registerItem(cropConfig.name(), cropConfig.food());
            CRATED_CROPS.add(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
        });
        NUTS.forEach(cropConfig -> {
            registerItem(cropConfig.name(), cropConfig.food());
            CRATED_CROPS.add(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
        });
        CRATED_CROPS.forEach(cropName -> {
            registerBlock(cropName.getPath(), () -> new Block(BlockBehaviour.Properties.copy(Blocks.BARREL)), true);
        });
    }

    // Inedible produce
    public static final RegistryObject<Item> COFFEE_BEAN = registerItem("coffee_bean");
    public static final RegistryObject<Item> CAROB = registerItem("carob");
    public static final RegistryObject<Item> ALLSPICE = registerItem("allspice");
    public static final RegistryObject<Item> CLOVE = registerItem("clove");
    public static final RegistryObject<Item> CINNAMON = registerItem("cinnamon");
    public static final RegistryObject<Item> NUTMEG = registerItem("nutmeg");
    public static final RegistryObject<Item> STAR_ANISE = registerItem("star_anise");
    public static final RegistryObject<Item> CORK = registerItem("cork");
    public static final RegistryObject<Item> PLANET_PEACH = registerItem("planet_peach");

    public static RegistryObject<Item> registerItem(String name) {
        return registerItem(name, () -> new Item(new Item.Properties()));
    }

    public static RegistryObject<Item> registerItem(String name, FoodProperties food) {
        return registerItem(name, () -> new Item(new Item.Properties().food(food)));
    }

    public static RegistryObject<Item> registerItem(String name, Supplier<Item> supplier) {
        return ProductiveTrees.ITEMS.register(name, supplier);
    }
    
    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier, boolean hasItem) {
        var block = ProductiveTrees.BLOCKS.register(name, supplier);
        if (hasItem) {
            registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
        return block;
    }

    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
        return ProductiveTrees.BLOCK_ENTITIES.register(id, supplier);
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
}
