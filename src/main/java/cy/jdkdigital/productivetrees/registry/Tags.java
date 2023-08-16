package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Tags
{
    public static final TagKey<Block> DIRT_OR_FARMLAND = TagKey.create(Registries.BLOCK, new ResourceLocation(ProductiveTrees.MODID, "dirt_or_farmland"));

    public static final TagKey<Item> NUTS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts"));
    public static final TagKey<Item> ALMOND = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/almond"));
    public static final TagKey<Item> ACORN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/acorn"));
    public static final TagKey<Item> BEECHNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/beechnut"));
    public static final TagKey<Item> BRAZIL_NUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/brazil_nut"));
    public static final TagKey<Item> BUTTERNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/butternut"));
    public static final TagKey<Item> CANDLE_NUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/candle_nut"));
    public static final TagKey<Item> CASHEW = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/cashew"));
    public static final TagKey<Item> CHESTNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/chestnut"));
    public static final TagKey<Item> COFFEE_BEAN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/coffee_bean"));
    public static final TagKey<Item> GINKGO_NUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/ginkgo_nut"));
    public static final TagKey<Item> HAZELNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/hazelnut"));
    public static final TagKey<Item> PECAN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/pecan"));
    public static final TagKey<Item> PISTACHIO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/pistachio"));
    public static final TagKey<Item> WALNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/walnut"));

    public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries"));
    public static final TagKey<Item> BLACKBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/blackberry"));
    public static final TagKey<Item> BLACKCURRANT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/blackcurrant"));
    public static final TagKey<Item> BLUEBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/blueberry"));
    public static final TagKey<Item> REDCURRANT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/redcurrant"));
    public static final TagKey<Item> CRANBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/cranberry"));
    public static final TagKey<Item> ELDERBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/elderberry"));
    public static final TagKey<Item> GOOSEBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/gooseberry"));
    public static final TagKey<Item> RASPBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/raspberry"));
    public static final TagKey<Item> JUNIPER = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/juniper"));
    public static final TagKey<Item> GOLDEN_RASPBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/golden_raspberry"));
    public static final TagKey<Item> SLOE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/sloe"));
    public static final TagKey<Item> HAW = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/haw"));

    public static final TagKey<Item> FRUITS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits"));
    public static final TagKey<Item> APRICOT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/apricot"));
    public static final TagKey<Item> BLACK_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/black_cherry"));
    public static final TagKey<Item> CHERRY_PLUM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/cherry_plum"));
    public static final TagKey<Item> OLIVE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/olive"));
    public static final TagKey<Item> OSANGE_ORANGE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/osange_orange"));
    public static final TagKey<Item> KUMQUAT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/kumquat"));
    public static final TagKey<Item> WILD_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/wild_cherry"));
    public static final TagKey<Item> SOUR_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/sour_cherry"));
    public static final TagKey<Item> DATE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/date"));
    public static final TagKey<Item> PLUM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/plum"));
    public static final TagKey<Item> AVOCADO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/avocado"));
    public static final TagKey<Item> CRABAPPLE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/crabapple"));
    public static final TagKey<Item> FIG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/fig"));
    public static final TagKey<Item> GRAPEFRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/grapefruit"));
    public static final TagKey<Item> NECTARINE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/nectarine"));
    public static final TagKey<Item> PEACH = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/peach"));
    public static final TagKey<Item> PEAR = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/pear"));
    public static final TagKey<Item> POMELO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/pomelo"));
    public static final TagKey<Item> SAND_PEAR = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/sand_pear"));
    public static final TagKey<Item> SATSUMA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/satsuma"));
    public static final TagKey<Item> STAR_FRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/star_fruit"));
    public static final TagKey<Item> TANGERINE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/tangerine"));
    public static final TagKey<Item> BANANA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/banana"));
    public static final TagKey<Item> COCONUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/coconut"));
    public static final TagKey<Item> MANGO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/mango"));
    public static final TagKey<Item> PLANTAIN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/plantain"));
    public static final TagKey<Item> RED_BANANA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/red_banana"));
    public static final TagKey<Item> PAPAYA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/papaya"));
    public static final TagKey<Item> LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/lime"));
    public static final TagKey<Item> KEY_LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/key_lime"));
    public static final TagKey<Item> FINGER_LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/finger_lime"));
    public static final TagKey<Item> CITRON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/citron"));
    public static final TagKey<Item> LEMON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/lemon"));
    public static final TagKey<Item> ORANGE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/orange"));
    public static final TagKey<Item> MANDARIN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/mandarin"));
    public static final TagKey<Item> BUDDHAS_HAND = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/buddhas_hand"));
}
