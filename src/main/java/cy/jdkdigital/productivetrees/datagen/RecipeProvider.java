package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.SawmillRecipeBuilder;
import cy.jdkdigital.productivetrees.datagen.recipe.TreePollinationRecipeBuilder;
import cy.jdkdigital.productivetrees.registry.*;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    public RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', tag(ModTags.SAWDUST))
                .define('W', DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION))
                .save(this.output, recipeKey("sawdust_to_paper_water_bottle"));
        shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', tag(ModTags.SAWDUST))
                .define('W', Items.WATER_BUCKET)
                .save(this.output, recipeKey("sawdust_to_paper"));
        shaped(RecipeCategory.MISC, Items.BLUE_DYE, 2)
                .unlockedBy(getHasName(TreeRegistrator.HAEMATOXYLIN.get()), has(TreeRegistrator.HAEMATOXYLIN.get()))
                .pattern("##")
                .define('#', Ingredient.of(TreeRegistrator.HAEMATOXYLIN.get()))
                .save(this.output, recipeKey("blue_dye_from_haematoxylin"));
        shaped(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
                .unlockedBy(getHasName(TreeRegistrator.HAEMATOXYLIN.get()), has(TreeRegistrator.HAEMATOXYLIN.get()))
                .pattern("#").pattern("#")
                .define('#', Ingredient.of(TreeRegistrator.HAEMATOXYLIN.get()))
                .save(this.output, recipeKey("purple_dye_from_haematoxylin"));

        shapeless(RecipeCategory.MISC, Items.SUGAR, 3)
                .unlockedBy("has_maple_syrup", has(ModTags.MAPLE_SYRUP))
                .requires(ModTags.MAPLE_SYRUP)
                .save(this.output, recipeKey("sugar_from_maple_syrup"));

        TreeFinder.trees.forEach((id, treeObject) -> {
            var planks = TreeUtil.getBlock(treeObject.getId(), "_planks");
            planksFromLogs(TreeUtil.getBlock(treeObject.getId(), "_planks"), ItemTags.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, id.getPath() + "_logs")));
            woodFromLog(TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_log"));
            if (!ProductiveTrees.isMinimal) {
                shapedVariant(BlockFamily.Variant.STAIRS, TreeUtil.getBlock(treeObject.getId(), "_stairs"), planks);
                shapedVariant(BlockFamily.Variant.SLAB, TreeUtil.getBlock(treeObject.getId(), "_slab"), planks);
                shapedVariant(BlockFamily.Variant.PRESSURE_PLATE, TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), planks);
                shapedVariant(BlockFamily.Variant.BUTTON, TreeUtil.getBlock(treeObject.getId(), "_button"), planks);
                shapedVariant(BlockFamily.Variant.FENCE, TreeUtil.getBlock(treeObject.getId(), "_fence"), planks);
                shapedVariant(BlockFamily.Variant.FENCE_GATE, TreeUtil.getBlock(treeObject.getId(), "_fence_gate"), planks);
                shapedVariant(BlockFamily.Variant.DOOR, TreeUtil.getBlock(treeObject.getId(), "_door"), planks);
                shapedVariant(BlockFamily.Variant.TRAPDOOR, TreeUtil.getBlock(treeObject.getId(), "_trapdoor"), planks);
                shapedVariant(BlockFamily.Variant.SIGN, TreeUtil.getBlock(treeObject.getId(), "_sign"), planks);
                hangingSignRecipe(TreeUtil.getBlock(treeObject.getId(), "_hanging_sign"), planks);
                shaped(RecipeCategory.BUILDING_BLOCKS, TreeUtil.getBlock(treeObject.getId(), "_bookshelf")).define('#', planks).define('X', Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has(planks)).save(this.output, ResourceKey.create(Registries.RECIPE, treeObject.getId().withPath(p -> "bookshelves/" + p + "_bookshelf")));
            }
            buildSawmillRecipe(treeObject);
        });
        buildCrateRecipes();

        buildTreeBreedingRecipes();

        buildVanillaSawmillRecipes();

        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig -> {
            var roastedNut = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name()));
            var rawNut = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "")));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNut), RecipeCategory.FOOD, CookingBookCategory.FOOD, roastedNut, 0.1F, 120)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(this.output, recipeKey("roasting/" + cropConfig.name() + "_smelting"));
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNut), RecipeCategory.FOOD, roastedNut, 0.1F, 20)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(this.output, recipeKey("roasting/" + cropConfig.name() + "_smoking"));

            var roastedNutCrate = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
            var rawNutCrate = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "") + "_crate"));
            if (rawNutCrate != null) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, CookingBookCategory.FOOD, roastedNutCrate, 0.9F, 1080)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(this.output, recipeKey("roasting/" + cropConfig.name() + "_crate_smelting"));
                SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, roastedNutCrate, 0.9F, 180)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(this.output, recipeKey("roasting/" + cropConfig.name() + "_crate_smoking"));
            }
        });
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(TreeRegistrator.RUBBER.get()), RecipeCategory.FOOD, CookingBookCategory.FOOD, TreeRegistrator.CURED_RUBBER.get(), 0.1F, 120)
                .unlockedBy(getHasName(TreeRegistrator.RUBBER.get()), has(TreeRegistrator.RUBBER.get()))
                .save(this.output, recipeKey("cured_rubber"));
    }

    private void planksFromLogs(ItemLike result, TagKey<Item> pLogs) {
        shapeless(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .requires(tag(pLogs))
                .group("planks")
                .unlockedBy("has_logs", has(pLogs))
                .save(this.output, prefixedRecipeKey(result, "planks/"));
    }

    private void woodFromLog(ItemLike pWood, ItemLike pLog) {
        shaped(RecipeCategory.BUILDING_BLOCKS, pWood, 3)
                .define('#', pLog).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_log", has(pLog))
                .save(this.output, prefixedRecipeKey(pWood, "wood/"));
    }

    private void shapedVariant(BlockFamily.Variant variant, ItemLike result, ItemLike plank) {
        Ingredient base = Ingredient.of(plank);
        RecipeBuilder builder = switch (variant) {
            case STAIRS -> stairBuilder(result, base);
            case SLAB -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, result, base);
            case PRESSURE_PLATE -> pressurePlateBuilder(RecipeCategory.REDSTONE, result, base);
            case BUTTON -> buttonBuilder(result, base);
            case FENCE -> fenceBuilder(result, base);
            case FENCE_GATE -> fenceGateBuilder(result, base);
            case DOOR -> doorBuilder(result, base);
            case TRAPDOOR -> trapdoorBuilder(result, base);
            case SIGN -> signBuilder(result, base);
            default -> throw new IllegalArgumentException("Unsupported variant " + variant);
        };
        builder.group(variant.name().toLowerCase());
        builder.unlockedBy(getHasName(plank), has(plank));
        builder.save(this.output, prefixedRecipeKey(result, variant.name().toLowerCase() + "/"));
    }

    private void hangingSignRecipe(ItemLike result, ItemLike plank) {
        shaped(RecipeCategory.DECORATIONS, result, 6).group("hanging_sign").define('#', plank).define('X', Items.IRON_CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(plank)).save(this.output, prefixedRecipeKey(result, "hanging_sign/"));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, path));
    }

    private static ResourceKey<Recipe<?>> prefixedRecipeKey(ItemLike item, String prefix) {
        return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(item.asItem()).withPath(path -> prefix + path));
    }

    private void buildSawmillRecipe(WoodObject treeObject) {
        String name = treeObject.getId().getPath();
        SawmillRecipeBuilder.tree(treeObject, tag(ItemTags.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, treeObject.getId().getPath() + "_logs"))), TreeUtil.getBlock(treeObject.getId(), "_planks")).save(this.output, recipeKey("sawmill/" + name + "_planks_from_log"));
    }

    private void buildVanillaSawmillRecipes() {
        SawmillRecipeBuilder.direct(tag(ItemTags.OAK_LOGS), new ItemStackTemplate(Items.OAK_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/oak_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.SPRUCE_LOGS), new ItemStackTemplate(Items.SPRUCE_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/spruce_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.ACACIA_LOGS), new ItemStackTemplate(Items.ACACIA_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/acacia_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.BIRCH_LOGS), new ItemStackTemplate(Items.BIRCH_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/birch_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.JUNGLE_LOGS), new ItemStackTemplate(Items.JUNGLE_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/jungle_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.CHERRY_LOGS), new ItemStackTemplate(Items.CHERRY_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/cherry_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.MANGROVE_LOGS), new ItemStackTemplate(Items.MANGROVE_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/mangrove_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.DARK_OAK_LOGS), new ItemStackTemplate(Items.DARK_OAK_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/dark_oak_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.CRIMSON_STEMS), new ItemStackTemplate(Items.CRIMSON_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/crimson_planks_from_log"));
        SawmillRecipeBuilder.direct(tag(ItemTags.WARPED_STEMS), new ItemStackTemplate(Items.WARPED_PLANKS, 6), new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)).save(this.output, recipeKey("sawmill/warped_planks_from_log"));
    }

    private void buildCrateRecipes() {
        TreeRegistrator.CRATED_CROPS.forEach(crate -> {
            var cropName = crate.getPath().replace("_crate", "");
            var crateItem = BuiltInRegistries.ITEM.getValue(crate);
            var cropItem = BuiltInRegistries.ITEM.getValue(crate.withPath(p -> cropName));

            var cropTag = ItemTags.create(Identifier.fromNamespaceAndPath("c", ItemTagProvider.tagName(cropName)));
            if (TreeRegistrator.FRUITS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(Identifier.fromNamespaceAndPath("c", "fruits/" + ItemTagProvider.tagName(cropName)));
            } else if (TreeRegistrator.BERRIES.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(Identifier.fromNamespaceAndPath("c", "berries/" + ItemTagProvider.tagName(cropName)));
            } else if (TreeRegistrator.NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0 || TreeRegistrator.ROASTED_NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(Identifier.fromNamespaceAndPath("c", "nuts/" + ItemTagProvider.tagName(cropName)));
            }

            if (cropName.equals("red_delicious_apple")) {
                cropItem = Items.APPLE;
                cropTag = null;
            }

            shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .requires(crateItem)
                    .save(this.output, recipeKey("crates/" + crate.getPath() + "_unpack"));
            var rBuilder = shaped(RecipeCategory.MISC, crateItem)
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
            rBuilder.save(this.output, recipeKey("crates/" + crate.getPath()));
        });
    }

    private void buildTreeBreedingRecipes() {
        treeBreeding("silver_lime", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 0.55f);
        treeBreeding("cacao", Blocks.JUNGLE_LEAVES, Blocks.CHERRY_LEAVES, 0.35f);
        treeBreeding("walnut", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding("sweet_chestnut", "walnut", getLeafIngredient("wild_cherry", "silver_lime"), 0.1f);
        treeBreeding("european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), Ingredient.of(Blocks.BIRCH_LEAVES), 0.1f);
        treeBreeding("sugar_maple", "european_larch", "red_maple", 0.05f);
        treeBreeding("citron", "silver_lime", "sour_cherry", 0.05f);
        treeBreeding("plum", "citron", "wild_cherry", 0.05f);
        treeBreeding("bull_pine", "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), 0.1f);
        treeBreeding("sequoia", "european_larch", "bull_pine", 0.05f);
        treeBreeding("teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), Ingredient.of(Blocks.JUNGLE_LEAVES), 0.40f);
        treeBreeding("ipe", "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding("aquilaria", "teak", "ipe", 0.1f);
        treeBreeding("kapok", "teak", Ingredient.of(Blocks.JUNGLE_LEAVES), 0.1f);
        treeBreeding("ceylon_ebony", "kapok", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding("purple_crepe_myrtle", "ceylon_ebony", Ingredient.of(Blocks.CHERRY_LEAVES), 0.05f);
        treeBreeding("zebrano", "white_poplar", "ceylon_ebony", 0.05f);
        treeBreeding("yellow_meranti", "ceylon_ebony", "kapok", 0.1f);
        treeBreeding("mahogany", "yellow_meranti", "kapok", 0.1f);
        treeBreeding("padauk", "red_maple", Ingredient.of(Blocks.JUNGLE_LEAVES), 0.05f);
        treeBreeding("dogwood", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.05f);
        treeBreeding("balsa", "teak", Ingredient.of(Blocks.ACACIA_LEAVES), 0.1f);
        treeBreeding("cocobolo", "balsa", Ingredient.of(Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding("wenge", "balsa", "cocobolo", 0.1f);
        treeBreeding("socotra_dragon", "wenge", "cocobolo", 0.1f);
        treeBreeding("grandidiers_baobab", "balsa", "wenge", 0.1f);
        treeBreeding("blue_mahoe", "teak", "balsa", 0.05f);
        treeBreeding("white_willow", "silver_lime", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES), 0.05f);
        treeBreeding("greenheart", "mahogany", "kapok", 0.1f);
        treeBreeding("papaya", "wild_cherry", "cacao", 0.05f);
        treeBreeding("date_palm", "papaya", "cacao", 0.05f);
        treeBreeding("asai_palm", "date_palm", "black_cherry", 0.05f);
        treeBreeding("persimmon", "ceylon_ebony", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").items().toList().getFirst().value()), 0.05f);
        treeBreeding("myrtle_ebony", "ceylon_ebony", "persimmon", 0.05f);
        treeBreeding("pomegranate", "holly", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").items().toList().getFirst().value()), 0.05f);
        treeBreeding("white_poplar", "white_willow", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, getLeafIngredient("silver_lime").items().toList().getFirst().value()), 0.05f);
        treeBreeding("red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), Ingredient.of(Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES), 0.1f);
        treeBreeding("granny_smith_apple", "red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding("golden_delicious_apple", "red_delicious_apple", "granny_smith_apple", 0.1f);
        treeBreeding("beliy_naliv_apple", "golden_delicious_apple", "granny_smith_apple", 0.1f);
        treeBreeding("sweet_crabapple", "red_delicious_apple", "sugar_maple", 0.1f);
        treeBreeding("flowering_crabapple", "sweet_crabapple", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding("prairie_crabapple", "red_delicious_apple", Ingredient.of(Blocks.BIRCH_LEAVES), 0.1f);
        treeBreeding("blackthorn", "plum", "red_delicious_apple", 0.1f);
        treeBreeding("cherry_plum", "plum", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding("peach", "plum", "sweet_chestnut", 0.1f);
        treeBreeding("nectarine", "plum", "peach", 0.1f);
        treeBreeding("apricot", "plum", "peach", 0.1f);
        treeBreeding("almond", "plum", "walnut", 0.1f);
        treeBreeding("wild_cherry", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding("sour_cherry", "white_willow", Ingredient.of(Blocks.CHERRY_LEAVES), 0.1f);
        treeBreeding("black_cherry", "ceylon_ebony", "sour_cherry", 0.1f);
        treeBreeding("orange", "mandarin", "pomelo", 0.1f);
        treeBreeding("mandarin", "pomelo", "wild_cherry", 0.1f);
        treeBreeding("tangerine", "mandarin", "kumquat", 0.1f);
        treeBreeding("satsuma", "mandarin", "kumquat", 0.1f);
        treeBreeding("lime", "pomelo", "key_lime", 0.1f);
        treeBreeding("key_lime", "citron", "wild_cherry", 0.1f);
        treeBreeding("finger_lime", "citron", "key_lime", 0.1f);
        treeBreeding("pomelo", "citron", "wild_cherry", 0.1f);
        treeBreeding("grapefruit", "pomelo", "orange", 0.1f);
        treeBreeding("kumquat", "mandarin", "wild_cherry", 0.1f);
        treeBreeding("lemon", "pomelo", "citron", 0.1f);
        treeBreeding("buddhas_hand", "mandarin", "citron", 0.1f);
        treeBreeding("banana", "balsa", "cacao", 0.1f);
        treeBreeding("red_banana", "banana", "kapok", 0.1f);
        treeBreeding("plantain", "banana", "teak", 0.1f);
        treeBreeding("butternut", "walnut", "wild_cherry", 0.1f);
        treeBreeding("rowan", "aspen", "alder", 0.1f);
        treeBreeding("western_hemlock", "bull_pine", "silver_fir", 3);
        treeBreeding("ash", "silver_lime", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding("alder", "beech", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding("beech", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 0.50f);
        treeBreeding("aspen", "beech", "alder", 0.1f);
        treeBreeding("yew", "european_larch", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding("lawson_cypress", "bull_pine", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding("cork_oak", "lawson_cypress", Blocks.OAK_LEAVES, 0.1f);
        treeBreeding("douglas_fir", "silver_fir", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding("hazel", "aspen", "beech", 0.1f);
        treeBreeding("sycamore_fig", "ash", "sugar_maple", 0.1f);
        treeBreeding("breadfruit", "sycamore_fig", "sugar_maple", 0.1f);
        treeBreeding("cempedak", "sycamore_fig", "breadfruit", 0.1f);
        treeBreeding("jackfruit", "cempedak", "breadfruit", 0.1f);
        treeBreeding("whitebeam", "ash", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding("hawthorn", "rowan", "beech", 0.1f);
        treeBreeding("pecan", "beech", Blocks.BIRCH_LEAVES, 0.1f);
        treeBreeding("sugar_apple", "pecan", "wild_cherry", 0.1f);
        treeBreeding("soursop", "sugar_apple", "banana", 0.1f);
        treeBreeding("elm", "ash", "silver_lime", 0.1f);
        treeBreeding("elderberry", "aspen", "alder", 0.1f);
        treeBreeding("holly", "alder", "rowan", 0.1f);
        treeBreeding("hornbeam", "ash", "whitebeam", 0.1f);
        treeBreeding("great_sallow", "white_willow", "aspen", 0.1f);
        treeBreeding("silver_fir", "balsam_fir", "bull_pine", 0.1f);
        treeBreeding("cedar", "silver_fir", "european_larch", 0.1f);
        treeBreeding("olive", "alder", "wild_cherry", 0.1f);
        treeBreeding("red_maple", "silver_lime", "european_larch", 0.1f);
        treeBreeding("balsam_fir", "alder", "european_larch", 0.1f);
        treeBreeding("loblolly_pine", "bull_pine", Blocks.SPRUCE_LEAVES, 0.1f);
        treeBreeding("sweetgum", "european_larch", "sugar_maple", 0.1f);
        treeBreeding("rubber_tree", "sweetgum", "loblolly_pine", 0.1f);
        treeBreeding("black_locust", "balsa", "silver_lime", 0.1f);
        treeBreeding("sand_pear", "red_delicious_apple", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding("cultivated_pear", "red_delicious_apple", "sand_pear", 0.1f);
        treeBreeding("osage_orange", "kapok", "old_fustic", 0.1f);
        treeBreeding("old_fustic", "red_maple", "mahogany", 0.1f);
        treeBreeding("brazilwood", "teak", "mahogany", 0.1f);
        treeBreeding("sandalwood", "brazilwood", "mahogany", 0.1f);
        treeBreeding("logwood", "kapok", "rosewood", 0.1f);
        treeBreeding("rosewood", "mahogany", "teak", 0.1f);
        treeBreeding("purpleheart", "brazilwood", "kapok", 0.1f);
        treeBreeding("iroko", "balsa", "teak", 0.1f);
        treeBreeding("ginkgo", "wenge", "silver_lime", 0.1f);
        treeBreeding("brazil_nut", "beech", "cacao", 0.1f);
        treeBreeding("rose_gum", "balsa", Blocks.FLOWERING_AZALEA_LEAVES, 0.1f);
        treeBreeding("swamp_gum", "yellow_meranti", "rose_gum", 0.1f);
        treeBreeding("boxwood", "holly", "alder", 0.1f);
        treeBreeding("coffea", "black_cherry", "cacao", 0.1f);
        treeBreeding("clove", "coffea", "teak", 0.1f);
        treeBreeding("monkey_puzzle", "western_hemlock", Blocks.JUNGLE_LEAVES, 0.1f);
        treeBreeding("rainbow_gum", "balsa", "rose_gum", 0.1f);
        treeBreeding("pink_ivory", "brazilwood", "rose_gum", 0.1f);
        treeBreeding("juniper", "elderberry", "silver_fir", 0.1f);
        treeBreeding("cinnamon", "rosewood", "teak", 0.1f);
        treeBreeding("coconut", "brazil_nut", "balsa", 0.1f);
        treeBreeding("cashew", "teak", Blocks.MANGROVE_LEAVES, 0.1f);
        treeBreeding("pistachio", "almond", "cashew", 0.1f);
        treeBreeding("avocado", "wenge", Blocks.OAK_LEAVES, 0.1f);
        treeBreeding("nutmeg", "teak", "clove", 0.1f);
        treeBreeding("allspice", "teak", "clove", 0.1f);
        treeBreeding("star_anise", "clove", "allspice", 0.1f);
        treeBreeding("mango", "orange", Blocks.MANGROVE_LEAVES, 0.1f);
        treeBreeding("star_fruit", "mango", "star_anise", 0.1f);
        treeBreeding("candlenut", "ginkgo", "hazel", 0.1f);
        treeBreeding("copoazu", "cacao", "candlenut", 0.1f);
        treeBreeding("carob", "sweet_chestnut", "copoazu", 0.1f);
        treeBreeding("pandanus", "walnut", "coconut", 0.1f);
        treeBreeding("salak", "pandanus", "coconut", 0.1f);

        treeBreeding("purple_spiral", "blue_yonder", "firecracker", 0.05f);
        treeBreeding("cave_dweller", "black_ember", "soul_tree", 0.05f);
        treeBreeding("foggy_blast", "cave_dweller", "soul_tree", 0.05f);
        treeBreeding("night_fuchsia", "purple_spiral", "sparkle_cherry", 0.05f);
        treeBreeding("time_traveller", "blue_yonder", "rippling_willow", 0.05f);
        treeBreeding("sparkle_cherry", "firecracker", "soul_tree", 0.05f);
        treeBreeding("slimy_delight", "rippling_willow", "soul_tree", 0.05f);
        treeBreeding("thunder_bolt", "firecracker", "flickering_sun", 0.05f);
        treeBreeding("rippling_willow", "blue_yonder", "flickering_sun", 0.05f);
        treeBreeding("water_wonder", "blue_yonder", "soul_tree", 0.05f);
    }

    public void treeBreeding(String name, String leafA, String leafB, float chance) {
        treeBreeding(name, leafA, BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, leafB + "_leaves")), chance);
    }

    public void treeBreeding(String name, String leafA, Block leafB, float chance) {
        treeBreeding(name, BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, leafA + "_leaves")), leafB, chance);
    }

    public void treeBreeding(String name, String leafA, Ingredient leafB, float chance) {
        treeBreeding(name, Ingredient.of(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, leafA + "_leaves"))), leafB, new ItemStackTemplate(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, name + "_sapling")).asItem()), chance);
    }

    public void treeBreeding(String name, Ingredient leafA, Ingredient leafB, float chance) {
        treeBreeding(name, leafA, leafB, new ItemStackTemplate(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, name + "_sapling")).asItem()), chance);
    }

    public void treeBreeding(String name, Block leafA, Block leafB, float chance) {
        treeBreeding(name, leafA, leafB, name, chance);
    }

    public void treeBreeding(String name, Block leafA, Block leafB, String saplingName, float chance) {
        treeBreeding(name, leafA, leafB, BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, saplingName + "_sapling")), chance);
    }

    public void treeBreeding(String name, Block leafA, Block leafB, Block result, float chance) {
        treeBreeding(name, Ingredient.of(leafA), Ingredient.of(leafB), new ItemStackTemplate(result.asItem()), chance);
    }

    public void treeBreeding(String name, Ingredient leafA, Ingredient leafB, ItemStackTemplate result, float chance) {
        if (leafA.isEmpty()) {
            throw new RuntimeException("Empty leafA for tree " + name);
        }
        if (leafB.isEmpty()) {
            throw new RuntimeException("Empty leafB for tree " + name);
        }
        if (result.item().value() == Items.AIR) {
            throw new RuntimeException("Empty result for tree " + name);
        }
        TreePollinationRecipeBuilder.direct(leafA, leafB, result, chance).save(this.output, recipeKey("pollination/" + name));
    }

    private static Ingredient getLeafIngredient(String... treeNames) {
        var leaves = Arrays.stream(treeNames).map(s -> {
            return (ItemLike) BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, s + "_leaves"));
        });
        return Ingredient.of(leaves);
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Productive Trees Recipes";
        }
    }
}
