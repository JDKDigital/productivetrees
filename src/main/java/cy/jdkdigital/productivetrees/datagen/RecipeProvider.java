package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.SawmillRecipeBuilder;
import cy.jdkdigital.productivetrees.datagen.recipe.TreePollinationRecipeBuilder;
import cy.jdkdigital.productivetrees.registry.*;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(gen, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', Ingredient.of(ModTags.SAWDUST))
                .define('W', DataComponentIngredient.of(false, PotionContents.createItemStack(Items.POTION, Potions.WATER)))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawdust_to_paper_water_bottle"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', Ingredient.of(ModTags.SAWDUST))
                .define('W', Items.WATER_BUCKET)
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawdust_to_paper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BLUE_DYE, 2)
                .unlockedBy(getHasName(TreeRegistrator.HAEMATOXYLIN.get()), has(TreeRegistrator.HAEMATOXYLIN.get()))
                .pattern("##")
                .define('#', Ingredient.of(TreeRegistrator.HAEMATOXYLIN.get()))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "blue_dye_from_haematoxylin"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
                .unlockedBy(getHasName(TreeRegistrator.HAEMATOXYLIN.get()), has(TreeRegistrator.HAEMATOXYLIN.get()))
                .pattern("#").pattern("#")
                .define('#', Ingredient.of(TreeRegistrator.HAEMATOXYLIN.get()))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "purple_dye_from_haematoxylin"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SUGAR, 3)
                .unlockedBy("has_maple_syrup", has(ModTags.MAPLE_SYRUP))
                .requires(ModTags.MAPLE_SYRUP)
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sugar_from_maple_syrup"));

        TreeFinder.trees.forEach((id, treeObject) -> {
            var planks = TreeUtil.getBlock(treeObject.getId(), "_planks");
            planksFromLogs(pRecipeOutput, TreeUtil.getBlock(treeObject.getId(), "_planks"), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, id.getPath() + "_logs")));
            woodFromLogs(pRecipeOutput, TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_log"));
            if (!ProductiveTrees.isMinimal) {
                shapedVariant(pRecipeOutput, BlockFamily.Variant.STAIRS, TreeUtil.getBlock(treeObject.getId(), "_stairs"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.SLAB, TreeUtil.getBlock(treeObject.getId(), "_slab"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.PRESSURE_PLATE, TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.BUTTON, TreeUtil.getBlock(treeObject.getId(), "_button"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.FENCE, TreeUtil.getBlock(treeObject.getId(), "_fence"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.FENCE_GATE, TreeUtil.getBlock(treeObject.getId(), "_fence_gate"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.DOOR, TreeUtil.getBlock(treeObject.getId(), "_door"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.TRAPDOOR, TreeUtil.getBlock(treeObject.getId(), "_trapdoor"), planks);
                shapedVariant(pRecipeOutput, BlockFamily.Variant.SIGN, TreeUtil.getBlock(treeObject.getId(), "_sign"), planks);
                hangingSign(pRecipeOutput, TreeUtil.getBlock(treeObject.getId(), "_hanging_sign"), planks);
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TreeUtil.getBlock(treeObject.getId(), "_bookshelf")).define('#', planks).define('X', Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has(planks)).save(pRecipeOutput, treeObject.getId().withPath(p -> "bookshelves/" + p + "_bookshelf"));
            }
            buildSawmillRecipe(treeObject, pRecipeOutput);
        });
        buildCrateRecipes(pRecipeOutput);

        buildTreeBreedingRecipes(pRecipeOutput);

        // vanilla wood sawmill processing
        buildVanillaSawmillRecipes(pRecipeOutput);
        buildCompatSawmillRecipes(pRecipeOutput);

        // Nut toasting
        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig -> {
            var roastedNut = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name()));
            var rawNut = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "")));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNut), RecipeCategory.FOOD, roastedNut, 0.1F, 120)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_smelting"));
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNut), RecipeCategory.FOOD, roastedNut, 0.1F, 20)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_smoking"));

            var roastedNutCrate = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
            var rawNutCrate = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "") + "_crate"));
            if (rawNutCrate != null) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, roastedNutCrate, 0.9F, 1080)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_crate_smelting"));
                SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, roastedNutCrate, 0.9F, 180)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_crate_smoking"));
            }
        });
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(TreeRegistrator.RUBBER.get()), RecipeCategory.FOOD, TreeRegistrator.CURED_RUBBER.get(), 0.1F, 120)
                .unlockedBy(getHasName(TreeRegistrator.RUBBER.get()), has(TreeRegistrator.RUBBER.get()))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "cured_rubber"));
    }

    private static void planksFromLogs(RecipeOutput consumer, ItemLike result, TagKey<Item> pLogs) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .requires(Ingredient.of(pLogs))
                .group("planks")
                .unlockedBy("has_logs", has(pLogs))
                .save(consumer, prefixedRecipeId(result, "planks/"));
    }

    protected static void woodFromLogs(RecipeOutput consumer, ItemLike pWood, ItemLike pLog) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pWood, 3)
                .define('#', pLog).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_log", has(pLog))
                .save(consumer, prefixedRecipeId(pWood, "wood/"));
    }

    private static void shapedVariant(RecipeOutput consumer, BlockFamily.Variant variant, ItemLike result, ItemLike plank) {
        var builder = SHAPE_BUILDERS.get(variant).apply(result, plank);
        builder.group(variant.name().toLowerCase());
        builder.unlockedBy(getHasName(plank), has(plank));
        builder.save(consumer, prefixedRecipeId(result, variant.name().toLowerCase() + "/"));
    }

    protected static void hangingSign(RecipeOutput consumer, ItemLike result, ItemLike plank) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result, 6).group("hanging_sign").define('#', plank).define('X', Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(plank)).save(consumer, prefixedRecipeId(result, "hanging_sign/"));
    }

    private static ResourceLocation prefixedRecipeId(ItemLike item, String prefix) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).withPath(path ->  prefix + path);
    }

    private void buildSawmillRecipe(WoodObject treeObject, RecipeOutput consumer) {
        String name = treeObject.getId().getPath();
        SawmillRecipeBuilder.tree(treeObject, ItemTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, treeObject.getId().getPath() + "_logs")), TreeUtil.getBlock(treeObject.getId(), "_planks")).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/" + name + "_planks_from_log"));
    }

    private void buildVanillaSawmillRecipes(RecipeOutput consumer) {
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.OAK_LOGS), new ItemStack(Items.OAK_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/oak_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.SPRUCE_LOGS), new ItemStack(Items.SPRUCE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/spruce_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.ACACIA_LOGS), new ItemStack(Items.ACACIA_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/acacia_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.BIRCH_LOGS), new ItemStack(Items.BIRCH_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/birch_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.JUNGLE_LOGS), new ItemStack(Items.JUNGLE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/jungle_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.CHERRY_LOGS), new ItemStack(Items.CHERRY_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/cherry_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.MANGROVE_LOGS), new ItemStack(Items.MANGROVE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/mangrove_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.DARK_OAK_LOGS), new ItemStack(Items.DARK_OAK_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/dark_oak_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/crimson_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill/warped_planks_from_log"));
    }

    private void buildCompatSawmillRecipes(RecipeOutput consumer) {
        // TODO BOP, RU, BYG etc.

    }

    private void buildCrateRecipes(RecipeOutput consumer) {
        TreeRegistrator.CRATED_CROPS.forEach(crate -> {
            var cropName = crate.getPath().replace("_crate", "");
            var crateItem = BuiltInRegistries.ITEM.get(crate);
            var cropItem = BuiltInRegistries.ITEM.get(crate.withPath(p -> cropName));

            var cropTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", ItemTagProvider.tagName(cropName)));
            if (TreeRegistrator.FRUITS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "fruits/" + ItemTagProvider.tagName(cropName)));
            } else if (TreeRegistrator.BERRIES.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "berries/" + ItemTagProvider.tagName(cropName)));
            } else if (TreeRegistrator.NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0 || TreeRegistrator.ROASTED_NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuts/" + ItemTagProvider.tagName(cropName)));
            }

            if (cropName.equals("red_delicious_apple")) {
                cropItem = Items.APPLE;
                cropTag = null;
            }

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .requires(crateItem)
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "crates/" + crate.getPath() + "_unpack"));
            var rBuilder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, crateItem)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .pattern("###")
                    .pattern("#R#")
                    .pattern("###")
                    .define('R', cropItem);
            if (cropTag != null) {
                rBuilder.define('#', cropTag);
            } else {
                rBuilder.define('#', cropItem);
            }
            rBuilder.save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "crates/" + crate.getPath()));
        });
    }

    private void buildTreeBreedingRecipes(RecipeOutput consumer) {
        treeBreeding(consumer, "silver_lime", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 0.55f);
        treeBreeding(consumer, "cacao", Blocks.JUNGLE_LEAVES, Blocks.CHERRY_LEAVES, 0.35f);
        treeBreeding(consumer, "walnut", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding(consumer, "sweet_chestnut", "walnut", RecipeProvider.getLeafIngredient("wild_cherry", "silver_lime"), 0.1f);
        treeBreeding(consumer, "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), Ingredient.of(Blocks.BIRCH_LEAVES), 0.1f);
        treeBreeding(consumer, "sugar_maple", "european_larch", "red_maple", 0.05f);
        treeBreeding(consumer, "citron", "silver_lime", "sour_cherry", 0.05f);
        treeBreeding(consumer, "plum", "citron", "wild_cherry", 0.05f);
        treeBreeding(consumer, "bull_pine", "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), 0.1f);
        treeBreeding(consumer, "sequoia", "european_larch", "bull_pine", 0.05f);
        treeBreeding(consumer, "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), Ingredient.of(Blocks.JUNGLE_LEAVES), 0.40f);
        treeBreeding(consumer, "ipe", "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding(consumer, "aquilaria", "teak", "ipe", 0.1f);
        treeBreeding(consumer, "kapok", "teak", Ingredient.of(Blocks.JUNGLE_LEAVES), 0.1f);
        treeBreeding(consumer, "ceylon_ebony", "kapok", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding(consumer, "purple_crepe_myrtle", "ceylon_ebony", Ingredient.of(Blocks.CHERRY_LEAVES), 0.05f);
        treeBreeding(consumer, "zebrano", "white_poplar", "ceylon_ebony", 0.05f);
        treeBreeding(consumer, "yellow_meranti", "ceylon_ebony", "kapok", 0.1f);
        treeBreeding(consumer, "mahogany", "yellow_meranti", "kapok", 0.1f);
        treeBreeding(consumer, "padauk", "red_maple", Ingredient.of(Blocks.JUNGLE_LEAVES), 0.05f);
        treeBreeding(consumer, "dogwood", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.05f);
        treeBreeding(consumer, "balsa", "teak", Ingredient.of(Blocks.ACACIA_LEAVES), 0.1f);
        treeBreeding(consumer, "cocobolo", "balsa", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding(consumer, "wenge", "balsa", "cocobolo", 0.1f);
        treeBreeding(consumer, "socotra_dragon", "wenge", "cocobolo", 0.1f);
        treeBreeding(consumer, "grandidiers_baobab", "balsa", "wenge", 0.1f);
        treeBreeding(consumer, "blue_mahoe", "teak", "balsa", 0.05f);
        treeBreeding(consumer, "white_willow", "silver_lime", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES), 0.05f);
        treeBreeding(consumer, "greenheart", "mahogany", "kapok", 0.1f);
        treeBreeding(consumer, "papaya", "wild_cherry", "cacao", 0.05f);
        treeBreeding(consumer, "date_palm", "papaya", "cacao", 0.05f);
        treeBreeding(consumer, "asai_palm", "date_palm", "black_cherry", 0.05f);
        treeBreeding(consumer, "persimmon", "ceylon_ebony", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").getItems()[0].getItem()), 0.05f);
        treeBreeding(consumer, "myrtle_ebony", "ceylon_ebony", "persimmon", 0.05f);
        treeBreeding(consumer, "pomegranate", "holly", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").getItems()[0].getItem()), 0.05f);
        treeBreeding(consumer, "white_poplar", "white_willow", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, getLeafIngredient("silver_lime").getItems()[0].getItem()), 0.05f);
        treeBreeding(consumer, "red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), Ingredient.of(Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding(consumer, "granny_smith_apple", "red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding(consumer, "golden_delicious_apple", "red_delicious_apple", "granny_smith_apple", 0.1f);
        treeBreeding(consumer, "beliy_naliv_apple", "golden_delicious_apple", "granny_smith_apple", 0.1f);
        treeBreeding(consumer, "sweet_crabapple", "red_delicious_apple", "sugar_maple", 0.1f);
        treeBreeding(consumer, "flowering_crabapple", "sweet_crabapple", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding(consumer, "prairie_crabapple", "red_delicious_apple", Ingredient.of(Blocks.BIRCH_LEAVES), 0.1f);
        treeBreeding(consumer, "blackthorn", "plum", "red_delicious_apple", 0.1f);
        treeBreeding(consumer, "cherry_plum", "plum", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding(consumer, "peach", "plum", "sweet_chestnut", 0.1f);
        treeBreeding(consumer, "nectarine", "plum", "peach", 0.1f);
        treeBreeding(consumer, "apricot", "plum", "peach", 0.1f);
        treeBreeding(consumer, "almond", "plum", "walnut", 0.1f);
        treeBreeding(consumer, "wild_cherry", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding(consumer, "sour_cherry", "white_willow", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding(consumer, "black_cherry", "ceylon_ebony", "sour_cherry", 0.1f);
        treeBreeding(consumer, "orange", "mandarin", "pomelo", 0.1f);
        treeBreeding(consumer, "mandarin", "pomelo", "wild_cherry", 0.1f);
        treeBreeding(consumer, "tangerine", "mandarin", "kumquat", 0.1f);
        treeBreeding(consumer, "satsuma", "mandarin", "kumquat", 0.1f);
        treeBreeding(consumer, "lime", "pomelo", "key_lime", 0.1f);
        treeBreeding(consumer, "key_lime", "citron", "wild_cherry", 0.1f);
        treeBreeding(consumer, "finger_lime", "citron", "key_lime", 0.1f);
        treeBreeding(consumer, "pomelo", "citron", "wild_cherry", 0.1f);
        treeBreeding(consumer, "grapefruit", "pomelo", "orange", 0.1f);
        treeBreeding(consumer, "kumquat", "mandarin", "wild_cherry", 0.1f);
        treeBreeding(consumer, "lemon", "pomelo", "citron", 0.1f);
        treeBreeding(consumer, "buddhas_hand", "mandarin", "citron", 0.1f);
        treeBreeding(consumer, "banana", "balsa", "cacao", 0.1f);
        treeBreeding(consumer, "red_banana", "banana", "kapok", 0.1f);
        treeBreeding(consumer, "plantain", "banana", "teak", 0.1f);
        treeBreeding(consumer, "butternut", "walnut", "wild_cherry", 0.1f);
        treeBreeding(consumer, "rowan", "aspen", "alder", 0.1f);
        treeBreeding(consumer, "western_hemlock", "bull_pine", "silver_fir", 3);
        treeBreeding(consumer, "ash", "silver_lime", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding(consumer, "alder", "beech", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding(consumer, "beech", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 0.50f);
        treeBreeding(consumer, "aspen", "beech", "alder", 0.1f);
        treeBreeding(consumer, "yew", "european_larch", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding(consumer, "lawson_cypress", "bull_pine", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding(consumer, "cork_oak", "lawson_cypress", Blocks.OAK_LEAVES, 0.1f);
        treeBreeding(consumer, "douglas_fir", "silver_fir", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding(consumer, "hazel", "aspen", "beech", 0.1f);
        treeBreeding(consumer, "sycamore_fig", "ash", "sugar_maple", 0.1f);
        treeBreeding(consumer, "breadfruit", "sycamore_fig", "sugar_maple", 0.1f);
        treeBreeding(consumer, "cempedak", "sycamore_fig", "breadfruit", 0.1f);
        treeBreeding(consumer, "jackfruit", "cempedak", "breadfruit", 0.1f);
        treeBreeding(consumer, "whitebeam", "ash", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding(consumer, "hawthorn", "rowan", "beech", 0.1f);
        treeBreeding(consumer, "pecan", "beech", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding(consumer, "sugar_apple", "pecan", "wild_cherry", 0.1f);
        treeBreeding(consumer, "soursop", "sugar_apple", "banana", 0.1f);
        treeBreeding(consumer, "elm", "ash", "silver_lime", 0.1f);
        treeBreeding(consumer, "elderberry", "aspen", "alder", 0.1f);
        treeBreeding(consumer, "holly", "alder", "rowan", 0.1f);
        treeBreeding(consumer, "hornbeam", "ash", "whitebeam", 0.1f);
        treeBreeding(consumer, "great_sallow", "white_willow", "aspen", 0.1f);
        treeBreeding(consumer, "silver_fir", "balsam_fir", "bull_pine", 0.1f);
        treeBreeding(consumer, "cedar", "silver_fir", "european_larch", 0.1f);
        treeBreeding(consumer, "olive", "alder", "wild_cherry", 0.1f);
        treeBreeding(consumer, "red_maple", "silver_lime", "european_larch", 0.1f);
        treeBreeding(consumer, "balsam_fir", "alder", "european_larch", 0.1f);
        treeBreeding(consumer, "loblolly_pine", "bull_pine", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding(consumer, "sweetgum", "european_larch", "sugar_maple", 0.1f);
        treeBreeding(consumer, "rubber_tree", "sweetgum", "loblolly_pine", 0.1f);
        treeBreeding(consumer, "black_locust", "balsa", "silver_lime", 0.1f);
        treeBreeding(consumer, "sand_pear", "red_delicious_apple", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding(consumer, "cultivated_pear", "red_delicious_apple", "sand_pear", 0.1f);
        treeBreeding(consumer, "osage_orange", "kapok", "old_fustic", 0.1f);
        treeBreeding(consumer, "old_fustic", "red_maple", "mahogany", 0.1f);
        treeBreeding(consumer, "brazilwood", "teak", "mahogany", 0.1f);
        treeBreeding(consumer, "sandalwood", "brazilwood", "mahogany", 0.1f);
        treeBreeding(consumer, "logwood", "kapok", "rosewood", 0.1f);
        treeBreeding(consumer, "rosewood", "mahogany", "teak", 0.1f);
        treeBreeding(consumer, "purpleheart", "brazilwood", "kapok", 0.1f);
        treeBreeding(consumer, "iroko", "balsa", "teak", 0.1f);
        treeBreeding(consumer, "ginkgo", "wenge", "silver_lime", 0.1f);
        treeBreeding(consumer, "brazil_nut", "beech", "cacao", 0.1f);
        treeBreeding(consumer, "rose_gum", "balsa", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding(consumer, "swamp_gum", "yellow_meranti", "rose_gum", 0.1f);
        treeBreeding(consumer, "boxwood", "holly", "alder", 0.1f);
        treeBreeding(consumer, "coffea", "black_cherry", "cacao", 0.1f);
        treeBreeding(consumer, "clove", "coffea", "teak", 0.1f);
        treeBreeding(consumer, "monkey_puzzle", "western_hemlock", Blocks.JUNGLE_LEAVES, 0.1f);
        treeBreeding(consumer, "rainbow_gum", "balsa", "rose_gum", 0.1f);
        treeBreeding(consumer, "pink_ivory", "brazilwood", "rose_gum", 0.1f);
        treeBreeding(consumer, "juniper", "elderberry", "silver_fir", 0.1f);
        treeBreeding(consumer, "cinnamon", "rosewood", "teak", 0.1f);
        treeBreeding(consumer, "coconut", "brazil_nut", "balsa", 0.1f);
        treeBreeding(consumer, "cashew", "teak", Blocks.MANGROVE_LEAVES, 0.1f);
        treeBreeding(consumer, "pistachio", "almond", "cashew", 0.1f);
        treeBreeding(consumer, "avocado", "wenge", Blocks.OAK_LEAVES, 0.1f);
        treeBreeding(consumer, "nutmeg", "teak", "clove", 0.1f);
        treeBreeding(consumer, "allspice", "teak", "clove", 0.1f);
        treeBreeding(consumer, "star_anise", "clove", "allspice", 0.1f);
        treeBreeding(consumer, "mango", "orange", Blocks.MANGROVE_LEAVES, 0.1f);
        treeBreeding(consumer, "star_fruit", "mango", "star_anise", 0.1f);
        treeBreeding(consumer, "candlenut", "ginkgo", "hazel", 0.1f);
        treeBreeding(consumer, "copoazu", "cacao", "candlenut", 0.1f);
        treeBreeding(consumer, "carob", "sweet_chestnut", "copoazu", 0.1f);
        treeBreeding(consumer, "pandanus", "walnut", "coconut", 0.1f);
        treeBreeding(consumer, "salak", "pandanus", "coconut", 0.1f);

        treeBreeding(consumer, "purple_spiral", "blue_yonder", "firecracker", 0.05f);
        treeBreeding(consumer, "cave_dweller", "black_ember", "soul_tree", 0.05f);
        treeBreeding(consumer, "foggy_blast", "cave_dweller", "soul_tree", 0.05f);
        treeBreeding(consumer, "night_fuchsia", "purple_spiral", "sparkle_cherry", 0.05f);
        treeBreeding(consumer, "time_traveller", "blue_yonder", "rippling_willow", 0.05f);
        treeBreeding(consumer, "sparkle_cherry", "firecracker", "soul_tree", 0.05f);
        treeBreeding(consumer, "slimy_delight", "rippling_willow", "soul_tree", 0.05f);
        treeBreeding(consumer, "thunder_bolt", "firecracker", "flickering_sun", 0.05f);
        treeBreeding(consumer, "rippling_willow", "blue_yonder", "flickering_sun", 0.05f);
        treeBreeding(consumer, "water_wonder", "blue_yonder", "soul_tree", 0.05f);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, String leafA, String leafB, float chance) {
        treeBreeding(consumer, name, leafA, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, leafB + "_leaves")), chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, String leafA, Block leafB, float chance) {
        treeBreeding(consumer, name, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, leafA + "_leaves")), leafB, chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, String leafA, Ingredient leafB, float chance) {
        treeBreeding(consumer, name, Ingredient.of(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, leafA + "_leaves"))), leafB, new ItemStack(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, name + "_sapling"))), chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, Ingredient leafA, Ingredient leafB, float chance) {
        treeBreeding(consumer, name, leafA, leafB, new ItemStack(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, name + "_sapling"))), chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, Block leafA, Block leafB, float chance) {
        treeBreeding(consumer, name, leafA, leafB, name, chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, Block leafA, Block leafB, String saplingName, float chance) {
        treeBreeding(consumer, name, leafA, leafB, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, saplingName + "_sapling")), chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, Block leafA, Block leafB, Block result, float chance) {
        treeBreeding(consumer, name, Ingredient.of(leafA), Ingredient.of(leafB), new ItemStack(result), chance);
    }

    public static void treeBreeding(RecipeOutput consumer, String name, Ingredient leafA, Ingredient leafB, ItemStack result, float chance) {
        if (leafA.isEmpty()) {
            throw new RuntimeException("Empty leafA for tree " + name);
        }
        if (leafB.isEmpty()) {
            throw new RuntimeException("Empty leafB for tree " + name);
        }
        if (result.isEmpty()) {
            throw new RuntimeException("Empty result for tree " + name);
        }
        TreePollinationRecipeBuilder.direct(leafA, leafB, result, chance).save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "pollination/" + name));
    }

    private static Ingredient getLeafIngredient(String... treeNames) {
        var leaves = Arrays.stream(treeNames).map(s -> {
            return new ItemStack(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, s + "_leaves")));
        });
        return Ingredient.of(leaves);
    }
}
