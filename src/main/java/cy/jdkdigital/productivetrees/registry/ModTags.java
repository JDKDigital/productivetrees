package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static final TagKey<Block> DIRT_OR_FARMLAND = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "dirt_or_farmland"));
    public static final TagKey<Block> POLLINATABLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "pollinatable"));
    public static final TagKey<Item> POLLINATABLE_ITEM = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "pollinatable"));
    public static final TagKey<Item> STRIPPER_TOOLS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "stripper_tools"));

    public static final TagKey<Item> DUSTS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts"));
    public static final TagKey<Item> DUSTS_WOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/wood"));
    public static final TagKey<Item> SAWDUST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sawdust"));

    public static final TagKey<Item> CINNAMON = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "cinnamon"));
    public static final TagKey<Item> NUTMEG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nutmeg"));
    public static final TagKey<Item> CLOVE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "clove"));
    public static final TagKey<Item> STAR_ANISE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "star_anise"));
    public static final TagKey<Item> COFFEE_BEANS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "coffee_beans"));
    public static final TagKey<Item> ROASTED_COFFEE_BEANS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "roasted_coffee_beans"));
    public static final TagKey<Item> MAPLE_SYRUP = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "maple_syrup"));
    public static final TagKey<Item> DATE_PALM_JUICE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "date_palm_juice"));
    public static final TagKey<Item> CORK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "cork"));
    public static final TagKey<Item> RUBBER = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rubber"));

    public static final TagKey<Item> CROPS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops"));
    public static final TagKey<Item> NUTS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "nuts"));
    public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "berries"));
    public static final TagKey<Item> FRUITS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "fruits"));
    public static final TagKey<Item> FRUITS_APPLE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "fruits/apples"));
    public static final TagKey<Item> FRUITS_CRABAPPLE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "fruits/crabapples"));
    public static final TagKey<Item> FRUITS_CHERRY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "fruits/cherries"));
}
