package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.ColoredParticleType;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.*;
import cy.jdkdigital.productivetrees.common.fluid.MapleSap;
import cy.jdkdigital.productivetrees.common.fluid.type.MapleSapType;
import cy.jdkdigital.productivetrees.common.item.PollenItem;
import cy.jdkdigital.productivetrees.common.item.SieveUpgradeItem;
import cy.jdkdigital.productivetrees.feature.foliageplacers.TaperedFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.CenteredUpwardsBranchingTrunkPlacer;
import cy.jdkdigital.productivetrees.inventory.SawmillContainer;
import cy.jdkdigital.productivetrees.inventory.StripperContainer;
import cy.jdkdigital.productivetrees.inventory.WoodWorkerContainer;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TreeRegistrator
{
    public static void init() {}

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
                .icon(() -> new ItemStack(Items.OAK_SAPLING))
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
    public static final RegistryObject<Item> MAPLE_SYRUP = registerItem("maple_syrup"); // TODO move to cuisine mod?
    public static final RegistryObject<Item> SANDALWOOD_OIL = registerItem("sandalwood_oil"); // TODO move to cuisine mod?

    public static final RegistryObject<RecipeSerializer<?>> TREE_POLLINATION = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_pollination", () -> new TreePollinationRecipe.Serializer<>(TreePollinationRecipe::new));
    public static final RegistryObject<RecipeType<TreePollinationRecipe>> TREE_POLLINATION_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_pollination", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> TREE_FRUITING = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_fruiting", () -> new TreeFruitingRecipe.Serializer<>(TreeFruitingRecipe::new));
    public static final RegistryObject<RecipeType<TreeFruitingRecipe>> TREE_FRUITING_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_fruiting", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> LOG_STRIPPING = ProductiveTrees.RECIPE_SERIALIZERS.register("log_stripping", () -> new LogStrippingRecipe.Serializer<>(LogStrippingRecipe::new));
    public static final RegistryObject<RecipeType<LogStrippingRecipe>> LOG_STRIPPING_TYPE = ProductiveTrees.RECIPE_TYPES.register("log_stripping", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> SAW_MILLLING = ProductiveTrees.RECIPE_SERIALIZERS.register("sawmill", () -> new SawmillRecipe.Serializer<>(SawmillRecipe::new));
    public static final RegistryObject<RecipeType<SawmillRecipe>> SAW_MILLLING_TYPE = ProductiveTrees.RECIPE_TYPES.register("sawmill", () -> new RecipeType<>() {});
    public static final RegistryObject<ColoredParticleType> PETAL_PARTICLES = ProductiveTrees.PARTICLE_TYPES.register("petals", ColoredParticleType::new);

    public static final RegistryObject<TrunkPlacerType<CenteredUpwardsBranchingTrunkPlacer>> CENTERED_UPWARDS_TRUNK_PLACER = ProductiveTrees.TRUNK_PLACERS.register("centered_upwards_branching_trunk_placer", () -> new TrunkPlacerType<>(CenteredUpwardsBranchingTrunkPlacer.CODEC));
    public static final RegistryObject<FoliagePlacerType<TaperedFoliagePlacer>> TAPERED_FOLIAGE_PLACER = ProductiveTrees.FOLIAGE_PLACERS.register("tapered_foliage_placer", () -> new FoliagePlacerType<>(TaperedFoliagePlacer.CODEC));

    // Fruiting items
    static final FoodProperties BERRY_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.1F).build();
    public static final RegistryObject<Item> ELDERBERRY = registerItem("elderberry", BERRY_FOOD);
    public static final RegistryObject<Item> JUNIPER = registerItem("juniper", BERRY_FOOD);
    public static final RegistryObject<Item> SLOE = registerItem("sloe", BERRY_FOOD);
    public static final RegistryObject<Item> HAW = registerItem("haw", BERRY_FOOD);
    public static final RegistryObject<Item> ASAI_BERRY = registerItem("asai_berry", BERRY_FOOD);

    static final FoodProperties SMALL_FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> APRICOT = registerItem("apricot", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> BLACK_CHERRY = registerItem("black_cherry", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> CHERRY_PLUM = registerItem("cherry_plum", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> DATE = registerItem("date", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> FIG = registerItem("fig", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> KUMQUAT = registerItem("kumquat", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> OLIVE = registerItem("olive", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> OSANGE_ORANGE = registerItem("osange_orange", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> PLUM = registerItem("plum", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> SOUR_CHERRY = registerItem("sour_cherry", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> SNAKE_FRUIT = registerItem("snake_fruit", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> SPARKLING_CHERRY = registerItem("sparkling_cherry", SMALL_FRUIT_FOOD);
    public static final RegistryObject<Item> WILD_CHERRY = registerItem("wild_cherry", SMALL_FRUIT_FOOD);

    static final FoodProperties FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> GOLDEN_DELICIOUS = registerItem("golden_delicious_apple", FRUIT_FOOD);
    public static final RegistryObject<Item> GRANNY_SMITH = registerItem("granny_smith_apple", FRUIT_FOOD);
    public static final RegistryObject<Item> BELIY_NALIV = registerItem("beliy_naliv_apple", FRUIT_FOOD);
    public static final RegistryObject<Item> AVOCADO = registerItem("avocado", FRUIT_FOOD);
    public static final RegistryObject<Item> BANANA = registerItem("banana", FRUIT_FOOD);
    public static final RegistryObject<Item> SWEET_CRABAPPLE = registerItem("sweet_crabapple", FRUIT_FOOD);
    public static final RegistryObject<Item> PRAIRIE_CRABAPPLE = registerItem("prairie_crabapple", FRUIT_FOOD);
    public static final RegistryObject<Item> FLOWERING_CRABAPPLE = registerItem("flowering_crabapple", FRUIT_FOOD);
    public static final RegistryObject<Item> GRAPEFRUIT = registerItem("grapefruit", FRUIT_FOOD);
    public static final RegistryObject<Item> NECTARINE = registerItem("nectarine", FRUIT_FOOD);
    public static final RegistryObject<Item> PEACH = registerItem("peach", FRUIT_FOOD);
    public static final RegistryObject<Item> PEAR = registerItem("pear", FRUIT_FOOD);
    public static final RegistryObject<Item> PERSIMMON = registerItem("persimmon", FRUIT_FOOD);
    public static final RegistryObject<Item> POMELO = registerItem("pomelo", FRUIT_FOOD);
    public static final RegistryObject<Item> POMEGRANATE = registerItem("pomegranate", FRUIT_FOOD);
    public static final RegistryObject<Item> RED_BANANA = registerItem("red_banana", FRUIT_FOOD);
    public static final RegistryObject<Item> SAND_PEAR = registerItem("sand_pear", FRUIT_FOOD);
    public static final RegistryObject<Item> SATSUMA = registerItem("satsuma", FRUIT_FOOD);
    public static final RegistryObject<Item> STAR_FRUIT = registerItem("star_fruit", FRUIT_FOOD);
    public static final RegistryObject<Item> TANGERINE = registerItem("tangerine", FRUIT_FOOD);
    public static final RegistryObject<Item> SWEETSOP = registerItem("sweetsop", FRUIT_FOOD);

    static final FoodProperties BIG_FRUIT_FOOD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F).build();
    public static final RegistryObject<Item> COCONUT = registerItem("coconut", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> MANGO = registerItem("mango", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> PLANTAIN = registerItem("plantain", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> PAPAYA = registerItem("papaya", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> BREADFRUIT = registerItem("breadfruit", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> AKEBIA = registerItem("akebia", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> COPOAZU = registerItem("copoazu", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> CEMPEDAK = registerItem("cempedak", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> JACKFRUIT = registerItem("jackfruit", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> HALA_FRUIT = registerItem("hala_fruit", BIG_FRUIT_FOOD);
    public static final RegistryObject<Item> SOURSOP = registerItem("soursop", BIG_FRUIT_FOOD);

    static final FoodProperties CITRUS_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0F).build();
    public static final RegistryObject<Item> LIME = registerItem("lime", CITRUS_FOOD);
    public static final RegistryObject<Item> KEY_LIME = registerItem("key_lime", CITRUS_FOOD);
    public static final RegistryObject<Item> FINGER_LIME = registerItem("finger_lime", CITRUS_FOOD);
    public static final RegistryObject<Item> CITRON = registerItem("citron", CITRUS_FOOD);
    public static final RegistryObject<Item> LEMON = registerItem("lemon", CITRUS_FOOD);

    static final FoodProperties BIG_CITRUS_FOOD = (new FoodProperties.Builder()).nutrition(3).saturationMod(0F).build();
    public static final RegistryObject<Item> ORANGE = registerItem("orange", BIG_CITRUS_FOOD);
    public static final RegistryObject<Item> MANDARIN = registerItem("mandarin", BIG_CITRUS_FOOD);
    public static final RegistryObject<Item> BUDDHAS_HAND = registerItem("buddhas_hand", BIG_CITRUS_FOOD);

    static final FoodProperties NUT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.1F).build();
    public static final RegistryObject<Item> ALMOND = registerItem("almond", NUT_FOOD);
    public static final RegistryObject<Item> ACORN = registerItem("acorn", NUT_FOOD);
    public static final RegistryObject<Item> BEECHNUT = registerItem("beechnut", NUT_FOOD);
    public static final RegistryObject<Item> BRAZIL_NUT = registerItem("brazil_nut", NUT_FOOD);
    public static final RegistryObject<Item> BUTTERNUT = registerItem("butternut", NUT_FOOD);
    public static final RegistryObject<Item> CANDLENUT = registerItem("candlenut", NUT_FOOD);
    public static final RegistryObject<Item> CASHEW = registerItem("cashew", NUT_FOOD);
    public static final RegistryObject<Item> CHESTNUT = registerItem("chestnut", NUT_FOOD);
    public static final RegistryObject<Item> GINKGO_NUT = registerItem("ginkgo_nut", NUT_FOOD);
    public static final RegistryObject<Item> HAZELNUT = registerItem("hazelnut", NUT_FOOD);
    public static final RegistryObject<Item> PECAN = registerItem("pecan", NUT_FOOD);
    public static final RegistryObject<Item> PISTACHIO = registerItem("pistachio", NUT_FOOD);
    public static final RegistryObject<Item> WALNUT = registerItem("walnut", NUT_FOOD);

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
