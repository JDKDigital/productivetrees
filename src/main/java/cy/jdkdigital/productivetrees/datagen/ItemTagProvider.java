package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, ProductiveTrees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);

        copy(ModTags.POLLINATABLE, ModTags.POLLINATABLE_ITEM);
        copy(cy.jdkdigital.productivebees.init.ModTags.HIVES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.HIVES);
        copy(cy.jdkdigital.productivebees.init.ModTags.BOXES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.BOXES);

        // Serene seasons compat
        copy(BlockTags.create(new ResourceLocation("sereneseasons:spring_crops")), ItemTags.create(new ResourceLocation("sereneseasons:spring_crops")));
        copy(BlockTags.create(new ResourceLocation("sereneseasons:summer_crops")), ItemTags.create(new ResourceLocation("sereneseasons:summer_crops")));
        copy(BlockTags.create(new ResourceLocation("sereneseasons:autumn_crops")), ItemTags.create(new ResourceLocation("sereneseasons:autumn_crops")));
        copy(BlockTags.create(new ResourceLocation("sereneseasons:winter_crops")), ItemTags.create(new ResourceLocation("sereneseasons:winter_crops")));

        // Diet compat
        var dietFruitsTag = tag(ItemTags.create(new ResourceLocation("diet:fruits")));
        var dietProteinsTag = tag(ItemTags.create(new ResourceLocation("diet:proteins")));
        var dietIngredientsTag = tag(ItemTags.create(new ResourceLocation("diet:ingredients")));

        // Create compat
        var moddedStrippedLogs = tag(ItemTags.create(new ResourceLocation("create:modded_stripped_logs")));
        var moddedStrippedWood = tag(ItemTags.create(new ResourceLocation("create:modded_stripped_wood")));
        TreeFinder.trees.forEach((id, treeObject) -> {
            moddedStrippedLogs.add(treeObject.getStrippedLogBlock().get().asItem());
            moddedStrippedWood.add(treeObject.getStrippedWoodBlock().get().asItem());
        });

        tag(ModTags.SAWDUST).add(TreeRegistrator.SAWDUST.get());
        tag(ModTags.DUSTS_WOOD).addTag(ModTags.SAWDUST);
        tag(ModTags.DUSTS).addTag(ModTags.DUSTS_WOOD);
        tag(ModTags.CINNAMON).add(TreeRegistrator.CINNAMON.get());
        tag(ModTags.MAPLE_SYRUP).add(TreeRegistrator.MAPLE_SYRUP.get());
        tag(ModTags.DATE_PALM_JUICE).add(TreeRegistrator.DATE_PALM_JUICE.get());
        tag(ModTags.CORK).add(TreeRegistrator.CORK.get());
        tag(ModTags.RUBBER).add(TreeRegistrator.RUBBER.get());
        tag(Tags.Items.DYES_YELLOW).add(TreeRegistrator.FUSTIC.get());
        tag(Tags.Items.DYES_PURPLE).add(TreeRegistrator.HAEMATOXYLIN.get());
        tag(Tags.Items.DYES_BLUE).add(TreeRegistrator.HAEMATOXYLIN.get());
        tag(Tags.Items.DYES_RED).add(TreeRegistrator.DRACAENA_SAP.get(), TreeRegistrator.HAEMATOXYLIN.get());

        TreeRegistrator.BERRIES.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(new ResourceLocation("forge", "berries/" + tagName(cropConfig.name())));
            var tagKeyFruit = ItemTags.create(new ResourceLocation("forge", "fruits/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(new ResourceLocation("forge", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.BERRIES).addTag(tagKey);
            dietFruitsTag.addTag(tagKey);
            tag(tagKeyFruit).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.FRUITS).addTag(tagKeyFruit);
            tag(tagKeyCrop).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.FRUITS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(new ResourceLocation("forge", "fruits/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(new ResourceLocation("forge", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.FRUITS).addTag(tagKey);
            dietFruitsTag.addTag(tagKey);
            tag(tagKeyCrop).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.NUTS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(new ResourceLocation("forge", "nuts/" + tagName(cropConfig.name())));
            var tagKeyCrop = ItemTags.create(new ResourceLocation("forge", "crops/" + tagName(cropConfig.name())));
            tag(tagKey).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.NUTS).addTag(tagKey);
            dietProteinsTag.addTag(tagKey);
            tag(tagKeyCrop).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.CROPS).addTag(tagKeyCrop);
        });
        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig ->  {
            var tagKey = ItemTags.create(new ResourceLocation("forge", "nuts/" + tagName(cropConfig.name())));
            tag(tagKey).add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name())));
            tag(ModTags.NUTS).addTag(tagKey);
            dietProteinsTag.addTag(tagKey);
        });

        tag(ModTags.FRUITS_APPLE).add(
                Items.APPLE,
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "golden_delicious_apple")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "granny_smith_apple")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "beliy_naliv_apple"))
        );
        tag(ModTags.FRUITS_CRABAPPLE).add(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sweet_crabapple")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "prairie_crabapple")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "flowering_crabapple"))
        );
        tag(ModTags.FRUITS_CHERRY).add(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "black_cherry")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sour_cherry")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sparkling_cherry")),
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "wild_cherry"))
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
            var blockTagKey = BlockTags.create(new ResourceLocation("forge", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            var tagKey = ItemTags.create(new ResourceLocation("forge", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            copy(blockTagKey, tagKey);
        });
    }

    private String tagName(String cropName) {
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
