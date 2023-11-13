package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.BotanyPotCropRecipeBuilder;
import cy.jdkdigital.productivetrees.datagen.recipe.SingleConditionalRecipe;
import cy.jdkdigital.productivetrees.datagen.recipe.TreePollinationRecipeBuilder;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.woodcutter.registry.ModRecipeSerializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen) {
        super(gen);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.registerWood()) {
                RecipeProvider.planksFromLogs(consumer, treeObject.getPlankBlock().get(), treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get());
                woodFromLogs(consumer, treeObject.getWoodBlock().get(), treeObject.getLogBlock().get());
                shaped(consumer, BlockFamily.Variant.STAIRS, treeObject.getStairsBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.SLAB, treeObject.getSlabBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.PRESSURE_PLATE, treeObject.getPressurePlateBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.BUTTON, treeObject.getButtonBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.FENCE, treeObject.getFenceBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.FENCE_GATE, treeObject.getFenceGateBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.DOOR, treeObject.getDoorBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.TRAPDOOR, treeObject.getTrapdoorBlock().get(), treeObject.getPlankBlock().get());
                shaped(consumer, BlockFamily.Variant.SIGN, treeObject.getSignBlock().get(), treeObject.getPlankBlock().get());
                hangingSign(consumer, treeObject.getHangingSignBlock().get(), treeObject.getPlankBlock().get());
                buildCorailWoodcutterRecipes(treeObject, consumer);
                buildHiveRecipe(ProductiveTrees.MODID, treeObject, consumer);
                buildBoxRecipe(ProductiveTrees.MODID, treeObject, consumer);
                // TODO thermal sawmill support
            }
            buildBotanyPotsRecipes(treeObject, consumer);
        });
        TreeFinder.woods.forEach((id, woodObject) -> {
            RecipeProvider.planksFromLogs(consumer, woodObject.getPlankBlock().get(), woodObject.getLogBlock().get(), woodObject.getWoodBlock().get(), woodObject.getStrippedLogBlock().get(), woodObject.getStrippedWoodBlock().get());
            woodFromLogs(consumer, woodObject.getWoodBlock().get(), woodObject.getLogBlock().get());
            shaped(consumer, BlockFamily.Variant.STAIRS, woodObject.getStairsBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.SLAB, woodObject.getSlabBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.PRESSURE_PLATE, woodObject.getPressurePlateBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.BUTTON, woodObject.getButtonBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.FENCE, woodObject.getFenceBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.FENCE_GATE, woodObject.getFenceGateBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.DOOR, woodObject.getDoorBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.TRAPDOOR, woodObject.getTrapdoorBlock().get(), woodObject.getPlankBlock().get());
            shaped(consumer, BlockFamily.Variant.SIGN, woodObject.getSignBlock().get(), woodObject.getPlankBlock().get());
            hangingSign(consumer, woodObject.getWallSignBlock().get(), woodObject.getPlankBlock().get());
            buildCorailWoodcutterRecipes(woodObject, consumer);
            if (woodObject.getHiveStyle() != null) {
                buildHiveRecipe(ProductiveTrees.MODID, woodObject, consumer);
                buildBoxRecipe(ProductiveTrees.MODID, woodObject, consumer);
            }
        });

        buildTreeBreedingRecipes(consumer);
    }

    private static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike... ingredients) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .requires(Ingredient.of(ingredients))
                .group("planks")
                .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(ingredients).build()))
                .save(consumer);
    }

    private static void shaped(Consumer<FinishedRecipe> consumer, BlockFamily.Variant variant, ItemLike result, ItemLike plank) {
        var builder = SHAPE_BUILDERS.get(variant).apply(result, plank);
        builder.group(variant.getName());
        builder.unlockedBy(getHasName(plank), has(plank));
        builder.save(consumer);
    }

    protected static void hangingSign(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike plank) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result, 6).group("hanging_sign").define('#', plank).define('X', Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(plank)).save(consumer);
    }

    private void buildCorailWoodcutterRecipes(WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        String name = treeObject.getId().getPath();
        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getPlankBlock().get(), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_planks_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getButtonBlock().get(), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getFenceBlock().get(), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getFenceGateBlock().get(), 1)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_gate_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), Blocks.LADDER, 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_ladder_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getPressurePlateBlock().get(), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getSlabBlock().get(), 8)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), treeObject.getStairsBlock().get(), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), Items.STICK, 8)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_sticks_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()), Items.BOWL, 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getLogBlock().get(), treeObject.getWoodBlock().get(), treeObject.getStrippedLogBlock().get(), treeObject.getStrippedWoodBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_bowl_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), Items.BOWL, 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_bowl_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), Items.STICK, 2)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_sticks_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), Blocks.LADDER, 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_ladder_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), treeObject.getButtonBlock().get(), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), treeObject.getFenceBlock().get(), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), treeObject.getPressurePlateBlock().get(), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), treeObject.getSlabBlock().get(), 2)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(treeObject.getPlankBlock().get()), treeObject.getStairsBlock().get(), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(treeObject.getPlankBlock().get()).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_planks"));
    }

    private static SingleItemRecipeBuilder woodcutter(Ingredient ingredient, ItemLike output, int count) {
        return new SingleItemRecipeBuilder(RecipeCategory.BUILDING_BLOCKS, ModRecipeSerializers.WOODCUTTING, ingredient, output, count);
    }

    private void buildBotanyPotsRecipes(TreeObject treeObject, Consumer<FinishedRecipe> consumer) {
        String name = treeObject.getId().getPath();
        List<HarvestEntry> drops = new ArrayList<>()
        {{
            if (treeObject.registerWood()) { // TODO use whatever wood is used in the feature
                add(new HarvestEntry(1.0F, new ItemStack(treeObject.getLogBlock().get()), 2, 4));
            }
            add(new HarvestEntry(0.1F, new ItemStack(Items.STICK), 1, 2));
            add(new HarvestEntry(0.15F, new ItemStack(treeObject.getSaplingBlock().get()), 1, 1));
        }};
        if (treeObject.hasFruit()) {
            ItemStack fruit = treeObject.getFruit().getItem();
            if (!fruit.isEmpty()) {
                drops.add(new HarvestEntry(treeObject.getFruit().growthSpeed(), new ItemStack(fruit.getItem()), 1, fruit.getCount()));
            } else {
                ProductiveTrees.LOGGER.warn("Empty fruit item for tree " + treeObject.getId());
            }
        }

        SingleConditionalRecipe.builder().addCondition(modLoaded("botanytrees")).setRecipe(
            BotanyPotCropRecipeBuilder.direct(Ingredient.of(treeObject.getSaplingBlock().get()), Set.of("dirt"), 2400, drops, treeObject.getSaplingBlock().get().defaultBlockState(), 0)::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "botanytrees/" + name));
    }

    private void buildTreeBreedingRecipes(Consumer<FinishedRecipe> consumer) {
        treeBreeding(consumer, "silver_lime", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 15);
        treeBreeding(consumer, "walnut", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "sweet_chestnut", "walnut", RecipeProvider.getLeafIngredient("wild_cherry", "silver_lime"), 10);
        treeBreeding(consumer, "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), Ingredient.of(Blocks.BIRCH_LEAVES), 10);
        treeBreeding(consumer, "sugar_maple", "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), 5);
        treeBreeding(consumer, "citron", "silver_lime", "sour_cherry", 5);
        treeBreeding(consumer, "plum", "citron", "wild_cherry", 5);
        treeBreeding(consumer, "bull_pine", "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), 10);
        treeBreeding(consumer, "sequoia", "european_larch", "bull_pine", 5);
        treeBreeding(consumer, "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), Ingredient.of(Blocks.JUNGLE_LEAVES), 10);
        treeBreeding(consumer, "ipe", "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "kapok", "teak", Ingredient.of(Blocks.JUNGLE_LEAVES), 10);
        treeBreeding(consumer, "myrtle_ebony", "kapok", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "zebrano", "white_poplar", "myrtle_ebony", 5);
        treeBreeding(consumer, "yellow_meranti", "myrtle_ebony", "kapok", 10);
        treeBreeding(consumer, "mahogany", "yellow_meranti", "kapok", 10);
        treeBreeding(consumer, "padauk", Ingredient.of(Blocks.ACACIA_LEAVES), Ingredient.of(Blocks.JUNGLE_LEAVES), 5);
        treeBreeding(consumer, "balsa", "teak", Ingredient.of(Blocks.ACACIA_LEAVES), 10);
        treeBreeding(consumer, "cocobolo", "balsa", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "wenge", "balsa", "cocobolo", 10);
        treeBreeding(consumer, "grandidiers_baobab", "balsa", "wenge", 10);
        treeBreeding(consumer, "blue_mahoe", "teak", "balsa", 5);
        treeBreeding(consumer, "white_willow", "silver_lime", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES), 5);
        treeBreeding(consumer, "cogwood", "mahogany", "kapok", 10);
        treeBreeding(consumer, "papaya", "wild_cherry", Ingredient.of(Blocks.JUNGLE_LEAVES), 5);
        treeBreeding(consumer, "date_palm", "papaya", Ingredient.of(Blocks.JUNGLE_LEAVES), 5);
        treeBreeding(consumer, "white_poplar", "white_willow", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, getLeafIngredient("silver_lime").getItems()[0].getItem()), 5);
        treeBreeding(consumer, "orchard_apple", Ingredient.of(Blocks.CHERRY_LEAVES), Ingredient.of(Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "sweet_crabapple", "orchard_apple", "sugar_maple", 10);
        treeBreeding(consumer, "flowering_crabapple", "orchard_apple", "sweet_crabapple", 10);
        treeBreeding(consumer, "prarie_crabapple", "orchard_apple", Ingredient.of(Blocks.BIRCH_LEAVES), 10);
        treeBreeding(consumer, "blackthorn", "plum", "orchard_apple", 10);
        treeBreeding(consumer, "cherry_plum", "plum", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "peach", "plum", "sweet_chestnut", 10);
        treeBreeding(consumer, "nectarine", "plum", "peach", 10);
        treeBreeding(consumer, "apricot", "plum", "peach", 10);
        treeBreeding(consumer, "almond", "plum", "walnut", 10);
        treeBreeding(consumer, "wild_cherry", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "sour_cherry", "white_willow", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "black_cherry", "myrtle_ebony", "sour_cherry", 10);
        treeBreeding(consumer, "orange", "mandarin", "pomelo", 10);
        treeBreeding(consumer, "mandarin", "pomelo", "wild_cherry", 10);
        treeBreeding(consumer, "tangerine", "mandarin", "kumquat", 10);
        treeBreeding(consumer, "satsuma", "mandarin", "kumquat", 10);
        treeBreeding(consumer, "lime", "pomelo", "key_lime", 10);
        treeBreeding(consumer, "key_lime", "citron", "wild_cherry", 10);
        treeBreeding(consumer, "finger_lime", "citron", "key_lime", 10);
        treeBreeding(consumer, "pomelo", "citron", "wild_cherry", 10);
        treeBreeding(consumer, "grapefruit", "pomelo", "orange", 10);
        treeBreeding(consumer, "kumquat", "mandarin", "wild_cherry", 10);
        treeBreeding(consumer, "lemon", "pomelo", "citron", 10);
        treeBreeding(consumer, "buddhas_hand", "mandarin", "citron", 10);
        treeBreeding(consumer, "banana", "balsa", Ingredient.of(Blocks.JUNGLE_LEAVES), 10);
        treeBreeding(consumer, "red_banana", "banana", "kapok", 10);
        treeBreeding(consumer, "plantain", "banana", "teak", 10);
        treeBreeding(consumer, "butternut", "walnut", "wild_cherry", 10);
        treeBreeding(consumer, "rowan", "aspen", "alder", 10);
        treeBreeding(consumer, "western_hemlock", "bull_pine", "silver_fir", 3);
        treeBreeding(consumer, "ash", "silver_lime", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "alder", "beech", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "beech", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "copper_beech", "beech", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "aspen", "beech", "alder", 10);
        treeBreeding(consumer, "yew", "european_larch", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "lawson_cypress", "bull_pine", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "douglas_fir", "silver_fir", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "hazel", "aspen", "beech", 10);
        treeBreeding(consumer, "sycamore_fig", "ash", "sugar_maple", 10);
        treeBreeding(consumer, "breadfruit", "sycamore_fig", "sugar_maple", 10);
        treeBreeding(consumer, "cheese_plant", "sycamore_fig", "breadfruit", 10);
        treeBreeding(consumer, "whitebeam", "ash", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "hawthorn", "rowan", "beech", 10);
        treeBreeding(consumer, "pecan", "beech", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "elm", "ash", "bull_pine", 10);
        treeBreeding(consumer, "elderberry", "aspen", "alder", 10);
        treeBreeding(consumer, "holly", "alder", "rowan", 10);
        treeBreeding(consumer, "hornbeam", "ash", "european_larch", 10);
        treeBreeding(consumer, "great_sallow", "white_willow", "aspen", 10);
        treeBreeding(consumer, "silver_fir", "balsam_fir", "bull_pine", 10);
        treeBreeding(consumer, "cedar", "silver_fir", "european_larch", 10);
        treeBreeding(consumer, "olive", "alder", "wild_cherry", 10);
        treeBreeding(consumer, "red_maple", "silver_lime", "sugar_maple", 10);
        treeBreeding(consumer, "balsam_fir", "alder", "european_larch", 10);
        treeBreeding(consumer, "loblolly_pine", "bull_pine", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "sweetgum", "european_larch", "sugar_maple", 10);
        treeBreeding(consumer, "black_locust", "balsa", "silver_lime", 10);
        treeBreeding(consumer, "sand_pear", "orchard_apple", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "cultivated_pear", "orchard_apple", "sand_pear", 10);
        treeBreeding(consumer, "osange_orange", "kapok", "old_fustic", 10);
        treeBreeding(consumer, "old_fustic", "teak", "mahogany", 10);
        treeBreeding(consumer, "brazilwood", "teak", "mahogany", 10);
        treeBreeding(consumer, "logwood", "kapok", "rosewood", 10);
        treeBreeding(consumer, "rosewood", "mahogany", "teak", 10);
        treeBreeding(consumer, "purpleheart", "brazilwood", "kapok", 10);
        treeBreeding(consumer, "iroko", "balsa", "teak", 10);
        treeBreeding(consumer, "ginkgo", "wenge", "silver_lime", 10);
        treeBreeding(consumer, "brazil_nut", "beech", Blocks.JUNGLE_LEAVES, 10);
        treeBreeding(consumer, "rose_gum", "balsa", "sweetgum", 10); // TODO sweetgum has nothing to do with rose gum
        treeBreeding(consumer, "swamp_gum", "yellow_meranti", "rose_gum", 10);
        treeBreeding(consumer, "boxwood", "holly", "alder", 10);
        treeBreeding(consumer, "coffea", "black_cherry", Blocks.JUNGLE_LEAVES, 10);
        treeBreeding(consumer, "clove", "coffea", "teak", 10);
        treeBreeding(consumer, "monkey_puzzle", "western_hemlock", Blocks.JUNGLE_LEAVES, 10);
        treeBreeding(consumer, "rainbow_gum", "balsa", "rose_gum", 10);
        treeBreeding(consumer, "pink_ivory", "brazilwood", "rose_gum", 10);
        treeBreeding(consumer, "redcurrant", "elderberry", "wild_cherry", 10);
        treeBreeding(consumer, "blackcurrant", "black_cherry", "redcurrant", 10);
        treeBreeding(consumer, "raspberry", "elderberry", "wild_cherry", 10);
        treeBreeding(consumer, "blackberry", "black_cherry", "raspberry", 10);
        treeBreeding(consumer, "blueberry", "blackberry", "raspberry", 10);
        treeBreeding(consumer, "cranberry", "blackberry", "cherry_plum", 10);
        treeBreeding(consumer, "juniper", "raspberry", "silver_fir", 10);
        treeBreeding(consumer, "gooseberry", "raspberry", "lime", 10);
        treeBreeding(consumer, "golden_raspberry", "raspberry", "orange", 10);
        treeBreeding(consumer, "miracle_berry", "golden_raspberry", "sugar_maple", 10);
        treeBreeding(consumer, "cinnamon", "rosewood", "teak", 10);
        treeBreeding(consumer, "coconut", "brazil_nut", "balsa", 10);
        treeBreeding(consumer, "cashew", "teak", Blocks.MANGROVE_LEAVES, 10);
        treeBreeding(consumer, "pistachio", "almond", "cashew", 10);
        treeBreeding(consumer, "avocado", "wenge", Blocks.OAK_LEAVES, 10);
        treeBreeding(consumer, "nutmeg", "teak", "clove", 10);
        treeBreeding(consumer, "allspice", "teak", "clove", 10);
        treeBreeding(consumer, "chilli_pepper", "ginkgo", "hazel", 10);
        treeBreeding(consumer, "star_anise", "clove", "allspice", 10);
        treeBreeding(consumer, "mango", "orange", Blocks.MANGROVE_LEAVES, 10);
        treeBreeding(consumer, "star_fruit", "mango", "star_anise", 10);
        treeBreeding(consumer, "candlenut", "ginkgo", "hazel", 10);
        treeBreeding(consumer, "akebia", "candlenut", Blocks.JUNGLE_LEAVES, 10);
        treeBreeding(consumer, "copoazu", "akebia", "cashew", 10);
        treeBreeding(consumer, "carob", "sweet_chestnut", "copoazu", 10);
        treeBreeding(consumer, "ysabella_purpurea", "purpleheart", "rosewood", 5);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, String leafA, String leafB, int chance) {
        treeBreeding(consumer, name, leafA, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, leafB + "_leaves")), chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, String leafA, Block leafB, int chance) {
        treeBreeding(consumer, name, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, leafA + "_leaves")), leafB, chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, String leafA, Ingredient leafB, int chance) {
        treeBreeding(consumer, name, Ingredient.of(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, leafA + "_leaves"))), leafB, Ingredient.of(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, name + "_sapling"))), chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, Ingredient leafA, Ingredient leafB, int chance) {
        treeBreeding(consumer, name, leafA, leafB, Ingredient.of(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, name + "_sapling"))), chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, Block leafA, Block leafB, int chance) {
        treeBreeding(consumer, name, leafA, leafB, name, chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, Block leafA, Block leafB, String saplingName, int chance) {
        treeBreeding(consumer, name, leafA, leafB, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, saplingName + "_sapling")), chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, Block leafA, Block leafB, Block result, int chance) {
        treeBreeding(consumer, name, Ingredient.of(leafA), Ingredient.of(leafB), Ingredient.of(result), chance);
    }

    public static void treeBreeding(Consumer<FinishedRecipe> consumer, String name, Ingredient leafA, Ingredient leafB, Ingredient result, int chance) {
        if (leafA.isEmpty()) {
            throw new RuntimeException("Empty leafA for tree " + name);
        }
        if (leafB.isEmpty()) {
            throw new RuntimeException("Empty leafB for tree " + name);
        }
        if (result.isEmpty()) {
            throw new RuntimeException("Empty result for tree " + name);
        }
        TreePollinationRecipeBuilder.direct(leafA, leafB, result, chance).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "pollination/" + name));
    }

    private static Ingredient getLeafIngredient(String... treeNames) {
        var leaves = Arrays.stream(treeNames).map(s -> {
            return new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, s + "_leaves")));
        });
        return Ingredient.of(leaves);
    }

    public void buildHiveRecipe(String modid, WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        Block hive = treeObject.getHiveBlock().get();
        SingleConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .setRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                                .define('W', Ingredient.of(treeObject.getPlankBlock().get()))
                                .define('H', Ingredient.of(ModTags.Forge.HIVES))
                                .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                                .define('F', Ingredient.of(ModTags.Forge.CAMPFIRES))
                                .define('S', Ingredient.of(Tags.Items.SHEARS))
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                ::save
                )
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "hives/" + ForgeRegistries.BLOCKS.getKey(hive).getPath().replace("advanced_", "")));

        buildHiveResetRecipes(modid, hive, new ResourceLocation(ProductiveTrees.MODID, "hives/" + ForgeRegistries.BLOCKS.getKey(hive).getPath().replace("advanced_", "") + "_clear"), consumer);
    }

    public void buildBoxRecipe(String modid, WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        Block box = treeObject.getExpansionBoxBlock().get();
        SingleConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .setRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                                .define('W', treeObject.getPlankBlock().get())
                                .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                ::save
                )
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "expansion_boxes/" + ForgeRegistries.BLOCKS.getKey(box).getPath()));
    }

    private void buildHiveResetRecipes(String modid, Block hive, ResourceLocation location, Consumer<FinishedRecipe> consumer) {
        SingleConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                ).setRecipe(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, hive).group("hives")
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                .requires(hive)
                                ::save
                ).build(consumer, location);
    }
}
