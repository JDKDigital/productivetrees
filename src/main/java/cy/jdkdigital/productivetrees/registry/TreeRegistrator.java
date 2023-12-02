package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.ColoredParticleType;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.*;
import cy.jdkdigital.productivetrees.common.item.PollenItem;
import cy.jdkdigital.productivetrees.common.item.SieveUpgradeItem;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
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
                blockStates.addAll(treeObject.getHiveBlock().get().getStateDefinition().getPossibleStates());
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
    public static final RegistryObject<Block> STRIPPER = ProductiveTrees.BLOCKS.register("stripper", () -> new Stripper(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()));
    public static final RegistryObject<BlockEntityType<StripperBlockEntity>> STRIPPER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("stripper", () -> BlockEntityType.Builder.of(StripperBlockEntity::new, STRIPPER.get()).build(null));
    public static final RegistryObject<Block> SAWMILL = ProductiveTrees.BLOCKS.register("sawmill", () -> new Sawmill(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()));
    public static final RegistryObject<BlockEntityType<SawmillBlockEntity>> SAWMILL_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("sawmill", () -> BlockEntityType.Builder.of(SawmillBlockEntity::new, SAWMILL.get()).build(null));
    public static final RegistryObject<Block> WOOD_WORKER = ProductiveTrees.BLOCKS.register("wood_worker", () -> new WoodWorker(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).noOcclusion()));
    public static final RegistryObject<BlockEntityType<WoodWorkerBlockEntity>> WOOD_WORKER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("wood_worker", () -> BlockEntityType.Builder.of(WoodWorkerBlockEntity::new, WOOD_WORKER.get()).build(null));
    public static final RegistryObject<Block> ENTITY_SPAWNER = ProductiveTrees.BLOCKS.register("entity_spawner", () -> new EntitySpawner(BlockBehaviour.Properties.copy(Blocks.AIR)));
    public static final RegistryObject<BlockEntityType<EntitySpawnerBlockEntity>> ENTITY_SPAWNER_BLOCK_ENTITY = ProductiveTrees.BLOCK_ENTITIES.register("entity_spawner", () -> BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new, ENTITY_SPAWNER.get()).build(null));
    public static final RegistryObject<MenuType<StripperContainer>> STRIPPER_MENU = ProductiveTrees.CONTAINER_TYPES.register("stripper", () ->
            IForgeMenuType.create(StripperContainer::new)
    );
    public static final RegistryObject<MenuType<SawmillContainer>> SAWMILL_MENU = ProductiveTrees.CONTAINER_TYPES.register("sawmill", () ->
            IForgeMenuType.create(SawmillContainer::new)
    );
    public static final RegistryObject<MenuType<WoodWorkerContainer>> WOOD_WORKER_MENU = ProductiveTrees.CONTAINER_TYPES.register("wood_worker", () ->
            IForgeMenuType.create(WoodWorkerContainer::new)
    );
    public static final RegistryObject<Item> UPGRADE_POLLEN_SIEVE = ProductiveTrees.ITEMS.register("upgrade_pollen_sieve", () -> new SieveUpgradeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> POLLEN = ProductiveTrees.ITEMS.register("pollen", () -> new PollenItem(new Item.Properties()));
    public static final RegistryObject<Item> STRIPPER_ITEM = ProductiveTrees.ITEMS.register("stripper", () -> new BlockItem(STRIPPER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SAWMILL_ITEM = ProductiveTrees.ITEMS.register("sawmill", () -> new BlockItem(SAWMILL.get(), new Item.Properties()));
    public static final RegistryObject<Item> WOOD_WORKER_ITEM = ProductiveTrees.ITEMS.register("wood_worker", () -> new BlockItem(WOOD_WORKER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SAWDUST = ProductiveTrees.ITEMS.register("sawdust", () -> new Item(new Item.Properties()));

    public static final RegistryObject<RecipeSerializer<?>> TREE_POLLINATION = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_pollination", () -> new TreePollinationRecipe.Serializer<>(TreePollinationRecipe::new));
    public static final RegistryObject<RecipeType<TreePollinationRecipe>> TREE_POLLINATION_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_pollination", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> TREE_FRUITING = ProductiveTrees.RECIPE_SERIALIZERS.register("tree_fruiting", () -> new TreeFruitingRecipe.Serializer<>(TreeFruitingRecipe::new));
    public static final RegistryObject<RecipeType<TreeFruitingRecipe>> TREE_FRUITING_TYPE = ProductiveTrees.RECIPE_TYPES.register("tree_fruiting", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> LOG_STRIPPING = ProductiveTrees.RECIPE_SERIALIZERS.register("log_stripping", () -> new LogStrippingRecipe.Serializer<>(LogStrippingRecipe::new));
    public static final RegistryObject<RecipeType<LogStrippingRecipe>> LOG_STRIPPING_TYPE = ProductiveTrees.RECIPE_TYPES.register("log_stripping", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<?>> SAW_MILLLING = ProductiveTrees.RECIPE_SERIALIZERS.register("sawmill", () -> new SawmillRecipe.Serializer<>(SawmillRecipe::new));
    public static final RegistryObject<RecipeType<SawmillRecipe>> SAW_MILLLING_TYPE = ProductiveTrees.RECIPE_TYPES.register("sawmill", () -> new RecipeType<>() {});
    public static final RegistryObject<ColoredParticleType> PETAL_PARTICLES = ProductiveTrees.PARTICLE_TYPES.register("petals", ColoredParticleType::new);

    // Fruiting items
    static final FoodProperties BERRY_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.1F).build();
    public static final RegistryObject<Item> BLACKBERRY = ProductiveTrees.ITEMS.register("blackberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> BLACKCURRANT = ProductiveTrees.ITEMS.register("blackcurrant", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> BLUEBERRY = ProductiveTrees.ITEMS.register("blueberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> REDCURRANT = ProductiveTrees.ITEMS.register("redcurrant", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> CRANBERRY = ProductiveTrees.ITEMS.register("cranberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> ELDERBERRY = ProductiveTrees.ITEMS.register("elderberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> GOOSEBERRY = ProductiveTrees.ITEMS.register("gooseberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> RASPBERRY = ProductiveTrees.ITEMS.register("raspberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> JUNIPER = ProductiveTrees.ITEMS.register("juniper", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> GOLDEN_RASPBERRY = ProductiveTrees.ITEMS.register("golden_raspberry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> SLOE = ProductiveTrees.ITEMS.register("sloe", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> HAW = ProductiveTrees.ITEMS.register("haw", () -> new Item(new Item.Properties().food(BERRY_FOOD)));
    public static final RegistryObject<Item> MIRACLE_BERRY = ProductiveTrees.ITEMS.register("miracle_berry", () -> new Item(new Item.Properties().food(BERRY_FOOD)));

    static final FoodProperties SMALL_FRUIT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> APRICOT = ProductiveTrees.ITEMS.register("apricot", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> BLACK_CHERRY = ProductiveTrees.ITEMS.register("black_cherry", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> CHERRY_PLUM = ProductiveTrees.ITEMS.register("cherry_plum", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> OLIVE = ProductiveTrees.ITEMS.register("olive", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> OSANGE_ORANGE = ProductiveTrees.ITEMS.register("osange_orange", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> KUMQUAT = ProductiveTrees.ITEMS.register("kumquat", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> WILD_CHERRY = ProductiveTrees.ITEMS.register("wild_cherry", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> SOUR_CHERRY = ProductiveTrees.ITEMS.register("sour_cherry", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> DATE = ProductiveTrees.ITEMS.register("date", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> PLUM = ProductiveTrees.ITEMS.register("plum", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));
    public static final RegistryObject<Item> SPARKLING_CHERRY = ProductiveTrees.ITEMS.register("sparkling_cherry", () -> new Item(new Item.Properties().food(SMALL_FRUIT_FOOD)));

    static final FoodProperties FRUIT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> GOLDEN_DELICIOUS = ProductiveTrees.ITEMS.register("golden_delicious_apple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> GRANNY_SMITH = ProductiveTrees.ITEMS.register("granny_smith_apple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> BELIY_NALIV = ProductiveTrees.ITEMS.register("beliy_naliv_apple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> AVOCADO = ProductiveTrees.ITEMS.register("avocado", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> BANANA = ProductiveTrees.ITEMS.register("banana", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> SWEET_CRABAPPLE = ProductiveTrees.ITEMS.register("sweet_crabapple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> PRAIRIE_CRABAPPLE = ProductiveTrees.ITEMS.register("prairie_crabapple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> FLOWERING_CRABAPPLE = ProductiveTrees.ITEMS.register("flowering_crabapple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> FIG = ProductiveTrees.ITEMS.register("fig", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> GRAPEFRUIT = ProductiveTrees.ITEMS.register("grapefruit", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> NECTARINE = ProductiveTrees.ITEMS.register("nectarine", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> PEACH = ProductiveTrees.ITEMS.register("peach", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> PEAR = ProductiveTrees.ITEMS.register("pear", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> POMELO = ProductiveTrees.ITEMS.register("pomelo", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> RED_BANANA = ProductiveTrees.ITEMS.register("red_banana", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> SAND_PEAR = ProductiveTrees.ITEMS.register("sand_pear", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> SATSUMA = ProductiveTrees.ITEMS.register("satsuma", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> STAR_FRUIT = ProductiveTrees.ITEMS.register("star_fruit", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> TANGERINE = ProductiveTrees.ITEMS.register("tangerine", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> PERSIMMON = ProductiveTrees.ITEMS.register("persimmon", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> POMEGRANATE = ProductiveTrees.ITEMS.register("pomegranate", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));

    static final FoodProperties BIG_FRUIT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(5).saturationMod(0.3F).build();
    public static final RegistryObject<Item> COCONUT = ProductiveTrees.ITEMS.register("coconut", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> MANGO = ProductiveTrees.ITEMS.register("mango", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> PLANTAIN = ProductiveTrees.ITEMS.register("plantain", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> PAPAYA = ProductiveTrees.ITEMS.register("papaya", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> BREADFRUIT = ProductiveTrees.ITEMS.register("breadfruit", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> AKEBIA = ProductiveTrees.ITEMS.register("akebia", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> COPOAZU = ProductiveTrees.ITEMS.register("copoazu", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> CEMPEDAK = ProductiveTrees.ITEMS.register("cempedak", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> JACKFRUIT = ProductiveTrees.ITEMS.register("jackfruit", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));

    static final FoodProperties CITRUS_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(2).saturationMod(0.2F).build();
    public static final RegistryObject<Item> LIME = ProductiveTrees.ITEMS.register("lime", () -> new Item(new Item.Properties().food(CITRUS_FOOD)));
    public static final RegistryObject<Item> KEY_LIME = ProductiveTrees.ITEMS.register("key_lime", () -> new Item(new Item.Properties().food(CITRUS_FOOD)));
    public static final RegistryObject<Item> FINGER_LIME = ProductiveTrees.ITEMS.register("finger_lime", () -> new Item(new Item.Properties().food(CITRUS_FOOD)));
    public static final RegistryObject<Item> CITRON = ProductiveTrees.ITEMS.register("citron", () -> new Item(new Item.Properties().food(CITRUS_FOOD)));
    public static final RegistryObject<Item> LEMON = ProductiveTrees.ITEMS.register("lemon", () -> new Item(new Item.Properties().food(CITRUS_FOOD)));

    static final FoodProperties BIG_CITRUS_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(3).saturationMod(0.2F).build();
    public static final RegistryObject<Item> ORANGE = ProductiveTrees.ITEMS.register("orange", () -> new Item(new Item.Properties().food(BIG_CITRUS_FOOD)));
    public static final RegistryObject<Item> MANDARIN = ProductiveTrees.ITEMS.register("mandarin", () -> new Item(new Item.Properties().food(BIG_CITRUS_FOOD)));
    public static final RegistryObject<Item> BUDDHAS_HAND = ProductiveTrees.ITEMS.register("buddhas_hand", () -> new Item(new Item.Properties().food(BIG_CITRUS_FOOD)));

    static final FoodProperties NUT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(1).saturationMod(0.2F).build();
    public static final RegistryObject<Item> ALMOND = ProductiveTrees.ITEMS.register("almond", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> ACORN = ProductiveTrees.ITEMS.register("acorn", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> BEECHNUT = ProductiveTrees.ITEMS.register("beechnut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> BRAZIL_NUT = ProductiveTrees.ITEMS.register("brazil_nut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> BUTTERNUT = ProductiveTrees.ITEMS.register("butternut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> CANDLENUT = ProductiveTrees.ITEMS.register("candlenut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> CASHEW = ProductiveTrees.ITEMS.register("cashew", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> CHESTNUT = ProductiveTrees.ITEMS.register("chestnut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> COFFEE_BEAN = ProductiveTrees.ITEMS.register("coffee_bean", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> GINKGO_NUT = ProductiveTrees.ITEMS.register("ginkgo_nut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> HAZELNUT = ProductiveTrees.ITEMS.register("hazelnut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> PECAN = ProductiveTrees.ITEMS.register("pecan", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> PISTACHIO = ProductiveTrees.ITEMS.register("pistachio", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> WALNUT = ProductiveTrees.ITEMS.register("walnut", () -> new Item(new Item.Properties().food(NUT_FOOD)));
    public static final RegistryObject<Item> CAROB = ProductiveTrees.ITEMS.register("carob", () -> new Item(new Item.Properties().food(NUT_FOOD)));

    // Spice
    public static final RegistryObject<Item> ALLSPICE = ProductiveTrees.ITEMS.register("allspice", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLOVE = ProductiveTrees.ITEMS.register("clove", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CINNAMON = ProductiveTrees.ITEMS.register("cinnamon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NUTMEG = ProductiveTrees.ITEMS.register("nutmeg", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STAR_ANISE = ProductiveTrees.ITEMS.register("star_anise", () -> new Item(new Item.Properties()));

    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
        return ProductiveTrees.BLOCK_ENTITIES.register(id, supplier);
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
}
