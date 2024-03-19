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
    public static final TagKey<Item> POLLINATABLE_ITEM = TagKey.create(Registries.ITEM, new ResourceLocation(ProductiveTrees.MODID, "pollinatable"));

    public static final TagKey<Item> DUSTS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "dusts"));
    public static final TagKey<Item> DUSTS_WOOD = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "dusts/wood"));
    public static final TagKey<Item> SAWDUST = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sawdust"));

    public static final TagKey<Item> CINNAMON = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "cinnamon"));
    public static final TagKey<Item> MAPLE_SYRUP = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "maple_syrup"));
    public static final TagKey<Item> DATE_PALM_JUICE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "date_palm_juice"));
    public static final TagKey<Item> CORK = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "cork"));
    public static final TagKey<Item> RUBBER = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rubber"));

    public static final TagKey<Item> CROPS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "crops"));
    public static final TagKey<Item> NUTS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "nuts"));
    public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "berries"));
    public static final TagKey<Item> FRUITS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits"));
    public static final TagKey<Item> FRUITS_APPLE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/apple"));
    public static final TagKey<Item> FRUITS_CRABAPPLE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/crabapple"));
    public static final TagKey<Item> FRUITS_CHERRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "fruits/cherry"));
}
