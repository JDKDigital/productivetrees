package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TreeRegistrator
{
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

    static final FoodProperties FRUIT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> AVOCADO = ProductiveTrees.ITEMS.register("avocado", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> BANANA = ProductiveTrees.ITEMS.register("banana", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> CRABAPPLE = ProductiveTrees.ITEMS.register("crabapple", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
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
    public static final RegistryObject<Item> AKEBIA = ProductiveTrees.ITEMS.register("akebia", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));
    public static final RegistryObject<Item> COPOAZU = ProductiveTrees.ITEMS.register("copoazu", () -> new Item(new Item.Properties().food(FRUIT_FOOD)));

    static final FoodProperties BIG_FRUIT_FOOD = (new FoodProperties.Builder()).alwaysEat().fast().nutrition(5).saturationMod(0.3F).build();
    public static final RegistryObject<Item> COCONUT = ProductiveTrees.ITEMS.register("coconut", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> MANGO = ProductiveTrees.ITEMS.register("mango", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> PLANTAIN = ProductiveTrees.ITEMS.register("plantain", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> PAPAYA = ProductiveTrees.ITEMS.register("papaya", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> BREADFRUIT = ProductiveTrees.ITEMS.register("breadfruit", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));
    public static final RegistryObject<Item> MONSTERA_DELICIOSA = ProductiveTrees.ITEMS.register("monstera_deliciosa", () -> new Item(new Item.Properties().food(BIG_FRUIT_FOOD)));

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
    public static final RegistryObject<Item> CHILLI = ProductiveTrees.ITEMS.register("chilli", () -> new Item(new Item.Properties()));
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
