package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, ProductiveTrees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (!TreeUtil.isSpecialTree(id)) {
                tag(ItemTags.SAPLINGS).add(TreeUtil.getBlock(id, "_sapling").asItem());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            tag(ItemTags.LEAVES).add(TreeUtil.getBlock(id, "_leaves").asItem());
            copy(BlockTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, id.getPath() + "_logs")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, id.getPath() + "_logs")));
        });
        copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);

        tag(ModTags.STRIPPER_TOOLS).addOptionalTag(ItemTags.AXES)
                .addOptional(ResourceLocation.parse("allthemodium:alloy_axe"))
                .addOptional(ResourceLocation.parse("allthemodium:unobtainium_axe"))
                .addOptional(ResourceLocation.parse("allthemodium:vibranium_axe"))
                .addOptional(ResourceLocation.parse("allthemodium:allthemodium_axe"));
        copy(ModTags.POLLINATABLE, ModTags.POLLINATABLE_ITEM);
        copy(cy.jdkdigital.productivebees.init.ModTags.HIVES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.HIVES);
        copy(cy.jdkdigital.productivebees.init.ModTags.BOXES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.BOXES);

        // Diet compat
        var dietFruitsTag = tag(ItemTags.create(ResourceLocation.parse("diet:fruits")));
        var dietProteinsTag = tag(ItemTags.create(ResourceLocation.parse("diet:proteins")));
        var dietIngredientsTag = tag(ItemTags.create(ResourceLocation.parse("diet:ingredients")));

        // Create compat
        var moddedStrippedLogs = tag(ItemTags.create(ResourceLocation.parse("create:modded_stripped_logs")));
        var moddedStrippedWood = tag(ItemTags.create(ResourceLocation.parse("create:modded_stripped_wood")));
        TreeFinder.trees.forEach((id, treeObject) -> {
            moddedStrippedLogs.add(TreeUtil.getBlock(id, "_stripped_log").asItem());
            moddedStrippedWood.add(TreeUtil.getBlock(id, "_stripped_wood").asItem());
        });

        tag(ModTags.SAWDUST).add(TreeRegistrator.SAWDUST.get());
        tag(ModTags.DUSTS_WOOD).add(TreeRegistrator.SAWDUST.get());
        tag(ModTags.DUSTS).addTag(ModTags.DUSTS_WOOD);
        tag(ModTags.COFFEE_BEANS).add(TreeRegistrator.COFFEE_BEAN.get());
        tag(ModTags.CLOVE).add(TreeRegistrator.CLOVE.get());
        tag(ModTags.CINNAMON).add(TreeRegistrator.CINNAMON.get());
        tag(ModTags.NUTMEG).add(TreeRegistrator.NUTMEG.get());
        tag(ModTags.STAR_ANISE).add(TreeRegistrator.STAR_ANISE.get());
        tag(ModTags.ROASTED_COFFEE_BEANS).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "roasted_coffee_bean")));
        tag(ModTags.MAPLE_SYRUP).add(TreeRegistrator.MAPLE_SYRUP.get());
        tag(ModTags.DATE_PALM_JUICE).add(TreeRegistrator.DATE_PALM_JUICE.get());
        tag(ModTags.CORK).add(TreeRegistrator.CORK.get());
        tag(ModTags.RUBBER).add(TreeRegistrator.CURED_RUBBER.get());
        tag(Tags.Items.DYES_YELLOW).add(TreeRegistrator.FUSTIC.get());
        tag(Tags.Items.DYES_RED).add(TreeRegistrator.DRACAENA_SAP.get(), TreeRegistrator.HAEMATOXYLIN.get());

        TreeRegistrator.BERRIES.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "berries/" + tagName(cropConfig.name())));
            var tagKeyFruit = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "fruits/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.BERRIES).addTag(tagKey);
            dietFruitsTag.addTag(tagKey);
            tag(tagKeyFruit).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.FRUITS).addTag(tagKeyFruit);
            tag(tagKeyCrop).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.FRUITS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "fruits/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.FRUITS).addTag(tagKey);
            dietFruitsTag.addTag(tagKey);
            tag(tagKeyCrop).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.NUTS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuts/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.NUTS).addTag(tagKey);
            dietProteinsTag.addTag(tagKey);
            tag(tagKeyCrop).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuts/" + tagName(cropConfig.name())));
            tag(tagKey).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.NUTS).addTag(tagKey);
            dietProteinsTag.addTag(tagKey);
        });

        tag(ModTags.FRUITS_APPLE).add(
                Items.APPLE,
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "golden_delicious_apple")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "granny_smith_apple")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "beliy_naliv_apple"))
        );
        tag(ModTags.FRUITS_CRABAPPLE).add(
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sweet_crabapple")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "prairie_crabapple")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "flowering_crabapple"))
        );
        tag(ModTags.FRUITS_CHERRY).add(
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "black_cherry")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sour_cherry")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sparkling_cherry")),
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "wild_cherry"))
        );
        tag(ModTags.FRUITS).addTags(ModTags.FRUITS_APPLE, ModTags.FRUITS_CRABAPPLE, ModTags.FRUITS_CHERRY);

        dietIngredientsTag.add(
                TreeRegistrator.ALLSPICE.get(),
                TreeRegistrator.CAROB.get(),
                TreeRegistrator.COFFEE_BEAN.get(),
                TreeRegistrator.CLOVE.get(),
                TreeRegistrator.CINNAMON.get(),
                TreeRegistrator.NUTMEG.get(),
                TreeRegistrator.STAR_ANISE.get()
        );

        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        TreeRegistrator.CRATED_CROPS.forEach(cratePath ->  {
            var blockTagKey = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            copy(blockTagKey, tagKey);
        });
    }

    public static String tagName(String cropName) {
        if (cropName.equals("star_fruit")) {
            return "starfruit";
        }
        if (cropName.equals("juniper_berry")) {
            return "juniperberry";
        }
        return cropName;
    }

    @Override
    public String getName() {
        return "Productive Trees Item Tags Provider";
    }
}
