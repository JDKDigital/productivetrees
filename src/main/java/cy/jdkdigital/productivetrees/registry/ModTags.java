package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static final TagKey<Block> DIRT_OR_FARMLAND = TagKey.create(Registries.BLOCK, new ResourceLocation(ProductiveTrees.MODID, "dirt_or_farmland"));
    public static final TagKey<Block> POLLINATABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(ProductiveTrees.MODID, "pollinatable"));

    public static final TagKey<Item> DUSTS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "dusts"));
    public static final TagKey<Item> DUSTS_WOOD = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "dusts/wood"));
    public static final TagKey<Item> SAWDUST = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sawdust"));

    public static final TagKey<Item> CINNAMON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "cinnamon"));
    public static final TagKey<Item> CORK = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "cork"));

    public static final TagKey<Item> NUTS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts"));
    public static final TagKey<Item> NUTS_ALMOND = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/almond"));
    public static final TagKey<Item> NUTS_ACORN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/acorn"));
    public static final TagKey<Item> NUTS_BEECHNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/beechnut"));
    public static final TagKey<Item> NUTS_BRAZIL_NUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/brazil_nut"));
    public static final TagKey<Item> NUTS_BUTTERNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/butternut"));
    public static final TagKey<Item> NUTS_CANDLENUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/candlenut"));
    public static final TagKey<Item> NUTS_CASHEW = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/cashew"));
    public static final TagKey<Item> NUTS_CHESTNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/chestnut"));
    public static final TagKey<Item> NUTS_COFFEE_BEAN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/coffee_bean"));
    public static final TagKey<Item> NUTS_GINKGO_NUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/ginkgo_nut"));
    public static final TagKey<Item> NUTS_HAZELNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/hazelnut"));
    public static final TagKey<Item> NUTS_PECAN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/pecan"));
    public static final TagKey<Item> NUTS_PISTACHIO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/pistachio"));
    public static final TagKey<Item> NUTS_WALNUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/walnut"));
    public static final TagKey<Item> NUTS_CAROB = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts/carob"));

    public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries"));
    public static final TagKey<Item> BERRIES_ELDERBERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/elderberry"));
    public static final TagKey<Item> BERRIES_JUNIPER = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/juniper"));
    public static final TagKey<Item> BERRIES_SLOE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/sloe"));
    public static final TagKey<Item> BERRIES_HAW = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/haw"));
    public static final TagKey<Item> BERRIES_ASAI_BERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries/asai_berry"));

    public static final TagKey<Item> FRUITS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits"));
    public static final TagKey<Item> FRUITS_APPLE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/apple"));
    public static final TagKey<Item> FRUITS_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/cherry"));
    public static final TagKey<Item> FRUITS_APRICOT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/apricot"));
    public static final TagKey<Item> FRUITS_BLACK_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/black_cherry"));
    public static final TagKey<Item> FRUITS_CHERRY_PLUM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/cherry_plum"));
    public static final TagKey<Item> FRUITS_OLIVE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/olive"));
    public static final TagKey<Item> FRUITS_OSANGE_ORANGE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/osange_orange"));
    public static final TagKey<Item> FRUITS_KUMQUAT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/kumquat"));
    public static final TagKey<Item> FRUITS_WILD_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/wild_cherry"));
    public static final TagKey<Item> FRUITS_SOUR_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/sour_cherry"));
    public static final TagKey<Item> FRUITS_DATE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/date"));
    public static final TagKey<Item> FRUITS_PLUM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/plum"));
    public static final TagKey<Item> FRUITS_SNAKE_FRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/snake_fruit"));
    public static final TagKey<Item> FRUITS_AVOCADO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/avocado"));
    public static final TagKey<Item> FRUITS_CRABAPPLE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/crabapple"));
    public static final TagKey<Item> FRUITS_FIG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/fig"));
    public static final TagKey<Item> FRUITS_GRAPEFRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/grapefruit"));
    public static final TagKey<Item> FRUITS_NECTARINE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/nectarine"));
    public static final TagKey<Item> FRUITS_PEACH = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/peach"));
    public static final TagKey<Item> FRUITS_PERSIMMON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/persimmon"));
    public static final TagKey<Item> FRUITS_POMEGRANATE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/pomegranate"));
    public static final TagKey<Item> FRUITS_SWEETSOP = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/sweetsop"));
    public static final TagKey<Item> FRUITS_PEAR = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/pear"));
    public static final TagKey<Item> FRUITS_POMELO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/pomelo"));
    public static final TagKey<Item> FRUITS_SAND_PEAR = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/sand_pear"));
    public static final TagKey<Item> FRUITS_SATSUMA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/satsuma"));
    public static final TagKey<Item> FRUITS_STAR_FRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/star_fruit"));
    public static final TagKey<Item> FRUITS_TANGERINE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/tangerine"));
    public static final TagKey<Item> FRUITS_AKEBIA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/akebia"));
    public static final TagKey<Item> FRUITS_COPOAZU = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/copoazu"));
    public static final TagKey<Item> FRUITS_CEMPEDAK = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/cempedak"));
    public static final TagKey<Item> FRUITS_JACKFRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/jackfruit"));
    public static final TagKey<Item> FRUITS_HALA_FRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/hala_fruit"));
    public static final TagKey<Item> FRUITS_SOURSOP = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/soursop"));
    public static final TagKey<Item> FRUITS_BANANA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/banana"));
    public static final TagKey<Item> FRUITS_COCONUT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/coconut"));
    public static final TagKey<Item> FRUITS_MANGO = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/mango"));
    public static final TagKey<Item> FRUITS_PLANTAIN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/plantain"));
    public static final TagKey<Item> FRUITS_RED_BANANA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/red_banana"));
    public static final TagKey<Item> FRUITS_PAPAYA = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/papaya"));
    public static final TagKey<Item> FRUITS_BREADFRUIT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/breadfruit"));
    public static final TagKey<Item> FRUITS_LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/lime"));
    public static final TagKey<Item> FRUITS_KEY_LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/key_lime"));
    public static final TagKey<Item> FRUITS_FINGER_LIME = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/finger_lime"));
    public static final TagKey<Item> FRUITS_CITRON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/citron"));
    public static final TagKey<Item> FRUITS_LEMON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/lemon"));
    public static final TagKey<Item> FRUITS_ORANGE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/orange"));
    public static final TagKey<Item> FRUITS_MANDARIN = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/mandarin"));
    public static final TagKey<Item> FRUITS_BUDDHAS_HAND = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/buddhas_hand"));
}
