package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivelib.datagen.recipe.SingleConditionalRecipe;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.*;
import cy.jdkdigital.productivetrees.registry.*;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.woodcutter.registry.ModRecipeSerializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    String[] RESIN_TREES = new String[]{"bull_pine", "douglas_fir", "cedar", "loblolly_pine", "sweetgum"};

    public RecipeProvider(PackOutput gen) {
        super(gen);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', Ingredient.of(ModTags.SAWDUST))
                .define('W', StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
                .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawdust_to_paper_water_bottle"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 2)
                .unlockedBy("has_sawdust", has(ModTags.SAWDUST))
                .pattern("###").pattern("#W#").pattern("###")
                .define('#', Ingredient.of(ModTags.SAWDUST))
                .define('W', Items.WATER_BUCKET)
                .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawdust_to_paper"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SUGAR, 3)
                .unlockedBy("has_maple_syrup", has(ModTags.MAPLE_SYRUP))
                .requires(ModTags.MAPLE_SYRUP)
                .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sugar_from_maple_syrup"));

        Block THERMAL_SAWMILL = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thermal:sawmill"));
        Block THERMAL_INSOLATOR = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thermal:insolator"));
        Block THERMAL_EXTRACTOR = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thermal:tree_extractor"));

        TreeFinder.trees.forEach((id, treeObject) -> {
            var planks = TreeUtil.getBlock(treeObject.getId(), "_planks");
            planksFromLogs(consumer, TreeUtil.getBlock(treeObject.getId(), "_planks"), ItemTags.create(new ResourceLocation(ProductiveTrees.MODID, id.getPath() + "_logs")));
            woodFromLogs(consumer, TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_log"));
            shapedVariant(consumer, BlockFamily.Variant.STAIRS, TreeUtil.getBlock(treeObject.getId(), "_stairs"), planks);
            shapedVariant(consumer, BlockFamily.Variant.SLAB, TreeUtil.getBlock(treeObject.getId(), "_slab"), planks);
            shapedVariant(consumer, BlockFamily.Variant.PRESSURE_PLATE, TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), planks);
            shapedVariant(consumer, BlockFamily.Variant.BUTTON, TreeUtil.getBlock(treeObject.getId(), "_button"), planks);
            shapedVariant(consumer, BlockFamily.Variant.FENCE, TreeUtil.getBlock(treeObject.getId(), "_fence"), planks);
            shapedVariant(consumer, BlockFamily.Variant.FENCE_GATE, TreeUtil.getBlock(treeObject.getId(), "_fence_gate"), planks);
            shapedVariant(consumer, BlockFamily.Variant.DOOR, TreeUtil.getBlock(treeObject.getId(), "_door"), planks);
            shapedVariant(consumer, BlockFamily.Variant.TRAPDOOR, TreeUtil.getBlock(treeObject.getId(), "_trapdoor"), planks);
            shapedVariant(consumer, BlockFamily.Variant.SIGN, TreeUtil.getBlock(treeObject.getId(), "_sign"), planks);
            hangingSign(consumer, TreeUtil.getBlock(treeObject.getId(), "_hanging_sign"), planks);
            buildSawmillRecipe(treeObject, consumer);
            buildCorailWoodcutterRecipes(treeObject, consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TreeUtil.getBlock(treeObject.getId(), "_bookshelf")).define('#', planks).define('X', Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has(planks)).save(consumer, treeObject.getId().withPath(p -> "bookshelves/" + p + "_bookshelf"));
            // arboreal extractor (for resin, sap and latex), insolator (logs, saplings and fruit)
            if (THERMAL_SAWMILL != null) {
                SingleConditionalRecipe.builder()
                        .addCondition(new ModLoadedCondition("thermal"))
                        .setRecipe(
                                ThermalSawmilRecipeBuilder.tree(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood"), TreeUtil.getBlock(treeObject.getId(), "_planks"))
                                        .unlockedBy("has_log", has(TreeUtil.getBlock(treeObject.getId(), "_log")))
                                        .unlockedBy("has_sawmill", has(THERMAL_SAWMILL))
                                        ::save
                        )
                        .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "thermal/sawmill/" + id.getPath()));
            }
            if (THERMAL_INSOLATOR != null) {
                SingleConditionalRecipe.builder()
                        .addCondition(new ModLoadedCondition("thermal"))
                        .setRecipe(
                                ThermalInsulatorRecipeBuilder.tree(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_sapling"), treeObject.getFruit().getItem().copy(), treeObject.getFruit().growthSpeed())
                                        .unlockedBy("has_sapling", has(TreeUtil.getBlock(treeObject.getId(), "_planks")))
                                        .unlockedBy("has_insolator", has(THERMAL_INSOLATOR))
                                        ::save
                        )
                        .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "thermal/insolator/" + id.getPath()));
            }
            if (THERMAL_EXTRACTOR != null) {
                String fluidName = "thermal:sap"; // resin, latex
                if (Arrays.stream(RESIN_TREES).anyMatch(s -> s.equals(id.getPath()))) {
                    fluidName = "thermal:resin";
                }
                if (id.getPath().equals("rubber_tree")) {
                    fluidName = "thermal:latex";
                }
                if (id.getPath().equals("sugar_maple")) {
                    fluidName = "productivetrees:maple_sap";
                }

                FluidStack fluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName)), 50);
                SingleConditionalRecipe.builder()
                        .addCondition(new ModLoadedCondition("thermal"))
                        .setRecipe(
                                ThermalExtractorRecipeBuilder.direct(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_leaves"), fluid)
                                        .unlockedBy("has_log", has(TreeUtil.getBlock(treeObject.getId(), "_log")))
                                        .unlockedBy("has_tree_extractor", has(THERMAL_EXTRACTOR))
                                        ::save
                        )
                        .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "thermal/tree_extractor/" + id.getPath()));
            }
            if (treeObject.getStyle().hiveStyle() != null) {
                buildHiveRecipe(ProductiveTrees.MODID, treeObject, consumer);
                buildBoxRecipe(ProductiveTrees.MODID, treeObject, consumer);
            }
            buildBotanyPotsRecipe(treeObject, consumer);
        });
        buildCrateRecipes(consumer);

        buildTreeBreedingRecipes(consumer);

        // vanilla wood sawmill processing
        buildVanillaSawmillRecipes(consumer);

        // Treetap recipes
        SingleConditionalRecipe.builder().addCondition(modLoaded("treetap"))
                .setRecipe(TreetapRecipeBuilder.direct(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, "socotra_dragon_log")), new ItemStack(TreeRegistrator.DRACAENA_SAP.get()), new ItemStack(Items.GLASS_BOTTLE), "#9d0300", 1200)::save)
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "treetap/dracaena_sap"));
        SingleConditionalRecipe.builder().addCondition(modLoaded("treetap"))
                .setRecipe(TreetapRecipeBuilder.direct(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sandalwood_log")), new ItemStack(TreeRegistrator.SANDALWOOD_OIL.get()), new ItemStack(Items.GLASS_BOTTLE), "#f1eda6", 2400)::save)
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "treetap/sandalwood_oil"));
        SingleConditionalRecipe.builder().addCondition(modLoaded("treetap"))
                .setRecipe(TreetapRecipeBuilder.direct(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, "sugar_maple_log")), new ItemStack(TreeRegistrator.MAPLE_SAP_BUCKET.get()), new FluidStack(TreeRegistrator.MAPLE_SAP.get(), 1000), 2400)::save)
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "treetap/maple_sap"));
        SingleConditionalRecipe.builder().addCondition(modLoaded("treetap"))
                .setRecipe(TreetapRecipeBuilder.direct(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, "date_palm_log")), new ItemStack(TreeRegistrator.DATE_PALM_JUICE.get()), new ItemStack(Items.GLASS_BOTTLE), "#cd7408", 2400)::save)
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "treetap/date_palm_juice"));

        // Nut toasting
        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig -> {
            var roastedNut = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name()));
            var rawNut = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "")));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNut), RecipeCategory.FOOD, roastedNut, 0.1F, 120)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_smelting"));
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNut), RecipeCategory.FOOD, roastedNut, 0.1F, 20)
                    .unlockedBy(getHasName(rawNut), has(rawNut))
                    .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_smoking"));

            var roastedNutCrate = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name() + "_crate"));
            var rawNutCrate = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, cropConfig.name().replace("roasted_", "") + "_crate"));
            if (rawNutCrate != null) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, roastedNutCrate, 0.9F, 1080)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_crate_smelting"));
                SimpleCookingRecipeBuilder.smoking(Ingredient.of(rawNutCrate), RecipeCategory.FOOD, roastedNutCrate, 0.9F, 180)
                        .unlockedBy(getHasName(rawNutCrate), has(rawNutCrate))
                        .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "roasting/" + cropConfig.name() + "_crate_smoking"));
            }
        });
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(TreeRegistrator.RUBBER.get()), RecipeCategory.FOOD, TreeRegistrator.CURED_RUBBER.get(), 0.1F, 120)
                .unlockedBy(getHasName(TreeRegistrator.RUBBER.get()), has(TreeRegistrator.RUBBER.get()))
                .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "cured_rubber"));
    }

    private static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike result, TagKey<Item> pLogs) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .requires(Ingredient.of(pLogs))
                .group("planks")
                .unlockedBy("has_logs", has(pLogs))
                .save(consumer, prefixedRecipeId(result, "planks/"));
    }

    protected static void woodFromLogs(Consumer<FinishedRecipe> consumer, ItemLike pWood, ItemLike pLog) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pWood, 3)
                .define('#', pLog).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_log", has(pLog))
                .save(consumer, prefixedRecipeId(pWood, "wood/"));
    }

    private static void shapedVariant(Consumer<FinishedRecipe> consumer, BlockFamily.Variant variant, ItemLike result, ItemLike plank) {
        var builder = SHAPE_BUILDERS.get(variant).apply(result, plank);
        builder.group(variant.getName());
        builder.unlockedBy(getHasName(plank), has(plank));
        builder.save(consumer, prefixedRecipeId(result, variant.getName() + "/"));
    }

    protected static void hangingSign(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike plank) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result, 6).group("hanging_sign").define('#', plank).define('X', Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(plank)).save(consumer, prefixedRecipeId(result, "hanging_sign/"));
    }

    private static ResourceLocation prefixedRecipeId(ItemLike item, String prefix) {
        return ForgeRegistries.ITEMS.getKey(item.asItem()).withPath(path ->  prefix + path);
    }

    private void buildSawmillRecipe(WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        String name = treeObject.getId().getPath();
        SawmillRecipeBuilder.tree(treeObject, ItemTags.create(new ResourceLocation(ProductiveTrees.MODID, treeObject.getId().getPath() + "_logs")), TreeUtil.getBlock(treeObject.getId(), "_planks")).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/" + name + "_planks_from_log"));
    }

    private void buildVanillaSawmillRecipes(Consumer<FinishedRecipe> consumer) {
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.OAK_LOGS), new ItemStack(Items.OAK_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/oak_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.SPRUCE_LOGS), new ItemStack(Items.SPRUCE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/spruce_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.ACACIA_LOGS), new ItemStack(Items.ACACIA_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/acacia_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.BIRCH_LOGS), new ItemStack(Items.BIRCH_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/birch_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.JUNGLE_LOGS), new ItemStack(Items.JUNGLE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/jungle_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.CHERRY_LOGS), new ItemStack(Items.CHERRY_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/cherry_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.MANGROVE_LOGS), new ItemStack(Items.MANGROVE_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/mangrove_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.DARK_OAK_LOGS), new ItemStack(Items.DARK_OAK_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/dark_oak_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/crimson_planks_from_log"));
        SawmillRecipeBuilder.direct(Ingredient.of(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY).save(consumer, new ResourceLocation(ProductiveTrees.MODID, "sawmill/warped_planks_from_log"));
    }

    private void buildCrateRecipes(Consumer<FinishedRecipe> consumer) {
        TreeRegistrator.CRATED_CROPS.forEach(crate -> {
            var cropName = crate.getPath().replace("_crate", "");
            var crateItem = ForgeRegistries.ITEMS.getValue(crate);
            var cropItem = ForgeRegistries.ITEMS.getValue(crate.withPath(p -> cropName));

            var cropTag = ItemTags.create(new ResourceLocation("forge", "fruits/" + ItemTagProvider.tagName(cropName)));
            if (TreeRegistrator.BERRIES.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(new ResourceLocation("forge", "berries/" + ItemTagProvider.tagName(cropName)));
            } else if (TreeRegistrator.NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0 || TreeRegistrator.ROASTED_NUTS.stream().filter(cropConfig -> cropConfig.name().equals(cropName)).toList().size() > 0) {
                cropTag = ItemTags.create(new ResourceLocation("forge", "nuts/" + ItemTagProvider.tagName(cropName)));
            }

            if (cropName.equals("red_delicious_apple")) {
                cropItem = Items.APPLE;
                cropTag = null;
            }

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .requires(crateItem)
                    .save(consumer, new ResourceLocation(ProductiveTrees.MODID, "crates/" + crate.getPath() + "_unpack"));
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
            rBuilder.save(consumer, new ResourceLocation(ProductiveTrees.MODID, "crates/" + crate.getPath()));
        });
    }

    private void buildCorailWoodcutterRecipes(WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        String name = treeObject.getId().getPath();
        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_planks"), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_planks_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_button"), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_fence"), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_fence_gate"), 1)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_gate_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_slab"), 8)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_stairs"), 4)
                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_logs"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_button"), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_fence"), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_slab"), 2)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_planks"));

        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_stairs"), 1)
                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
                        ::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_planks"));
    }

    private static SingleItemRecipeBuilder woodcutter(Ingredient ingredient, ItemLike output, int count) {
        return new SingleItemRecipeBuilder(RecipeCategory.BUILDING_BLOCKS, ModRecipeSerializers.WOODCUTTING, ingredient, output, count);
    }

    private void buildBotanyPotsRecipe(TreeObject treeObject, Consumer<FinishedRecipe> consumer) {
        String name = treeObject.getId().getPath();
        List<HarvestEntry> drops = new ArrayList<>()
        {{
            add(new HarvestEntry(1.0F, new ItemStack(TreeUtil.getBlock(treeObject.getId(), "_log")), 2, 4));
            add(new HarvestEntry(0.1F, new ItemStack(Items.STICK), 1, 2));
            add(new HarvestEntry(0.15F, new ItemStack(TreeUtil.getBlock(treeObject.getId(), "_sapling")), 1, 1));
        }};
        if (treeObject.hasFruit()) {
            ItemStack fruit = treeObject.getFruit().getItem();
            if (!fruit.isEmpty()) {
                drops.add(new HarvestEntry(treeObject.getFruit().growthSpeed(), new ItemStack(fruit.getItem()), 1, fruit.getCount()));
            }
        }

        SingleConditionalRecipe.builder().addCondition(modLoaded("botanytrees")).setRecipe(
            BotanyPotCropRecipeBuilder.direct(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_sapling")), Set.of("dirt"), 2400, drops, TreeUtil.getBlock(treeObject.getId(), "_sapling").defaultBlockState(), 0)::save
        ).build(consumer, new ResourceLocation(ProductiveTrees.MODID, "botanytrees/" + name));
    }

    private void buildTreeBreedingRecipes(Consumer<FinishedRecipe> consumer) {
        treeBreeding(consumer, "silver_lime", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 15);
        treeBreeding(consumer, "cacao", Blocks.JUNGLE_LEAVES, Blocks.CHERRY_LEAVES, 15);
        treeBreeding(consumer, "walnut", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "sweet_chestnut", "walnut", RecipeProvider.getLeafIngredient("wild_cherry", "silver_lime"), 10);
        treeBreeding(consumer, "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), Ingredient.of(Blocks.BIRCH_LEAVES), 10);
        treeBreeding(consumer, "sugar_maple", "european_larch", "red_maple", 5);
        treeBreeding(consumer, "citron", "silver_lime", "sour_cherry", 5);
        treeBreeding(consumer, "plum", "citron", "wild_cherry", 5);
        treeBreeding(consumer, "bull_pine", "european_larch", Ingredient.of(Blocks.SPRUCE_LEAVES), 10);
        treeBreeding(consumer, "sequoia", "european_larch", "bull_pine", 5);
        treeBreeding(consumer, "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), Ingredient.of(Blocks.JUNGLE_LEAVES), 10);
        treeBreeding(consumer, "ipe", "teak", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "aquilaria", "teak", "ipe", 10);
        treeBreeding(consumer, "kapok", "teak", Ingredient.of(Blocks.JUNGLE_LEAVES), 10);
        treeBreeding(consumer, "ceylon_ebony", "kapok", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "purple_crepe_myrtle", "ceylon_ebony", Ingredient.of(Blocks.CHERRY_LEAVES), 5);
        treeBreeding(consumer, "zebrano", "white_poplar", "ceylon_ebony", 5);
        treeBreeding(consumer, "yellow_meranti", "ceylon_ebony", "kapok", 10);
        treeBreeding(consumer, "mahogany", "yellow_meranti", "kapok", 10);
        treeBreeding(consumer, "padauk", "red_maple", Ingredient.of(Blocks.JUNGLE_LEAVES), 5);
        treeBreeding(consumer, "dogwood", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 5);
        treeBreeding(consumer, "balsa", "teak", Ingredient.of(Blocks.ACACIA_LEAVES), 10);
        treeBreeding(consumer, "cocobolo", "balsa", Ingredient.of(Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "wenge", "balsa", "cocobolo", 10);
        treeBreeding(consumer, "socotra_dragon", "wenge", "cocobolo", 10);
        treeBreeding(consumer, "grandidiers_baobab", "balsa", "wenge", 10);
        treeBreeding(consumer, "blue_mahoe", "teak", "balsa", 5);
        treeBreeding(consumer, "white_willow", "silver_lime", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES), 5);
        treeBreeding(consumer, "greenheart", "mahogany", "kapok", 10);
        treeBreeding(consumer, "papaya", "wild_cherry", "cacao", 5);
        treeBreeding(consumer, "date_palm", "papaya", "cacao", 5);
        treeBreeding(consumer, "asai_palm", "date_palm", "black_cherry", 5);
        treeBreeding(consumer, "persimmon", "ceylon_ebony", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").getItems()[0].getItem()), 5);
        treeBreeding(consumer, "myrtle_ebony", "ceylon_ebony", "persimmon", 5);
        treeBreeding(consumer, "pomegranate", "holly", Ingredient.of(getLeafIngredient("purple_crepe_myrtle", "moonlight_magic_crepe_myrtle", "red_crepe_myrtle", "tuscarora_crepe_myrtle").getItems()[0].getItem()), 5);
        treeBreeding(consumer, "white_poplar", "white_willow", Ingredient.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, getLeafIngredient("silver_lime").getItems()[0].getItem()), 5);
        treeBreeding(consumer, "red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), Ingredient.of(Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES), 10);
        treeBreeding(consumer, "granny_smith_apple", "red_delicious_apple", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "golden_delicious_apple", "red_delicious_apple", "granny_smith_apple", 10);
        treeBreeding(consumer, "beliy_naliv_apple", "golden_delicious_apple", "granny_smith_apple", 10);
        treeBreeding(consumer, "sweet_crabapple", "red_delicious_apple", "sugar_maple", 10);
        treeBreeding(consumer, "flowering_crabapple", "sweet_crabapple", Blocks.FLOWERING_AZALEA_LEAVES, 10);
        treeBreeding(consumer, "prairie_crabapple", "red_delicious_apple", Ingredient.of(Blocks.BIRCH_LEAVES), 10);
        treeBreeding(consumer, "blackthorn", "plum", "red_delicious_apple", 10);
        treeBreeding(consumer, "cherry_plum", "plum", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "peach", "plum", "sweet_chestnut", 10);
        treeBreeding(consumer, "nectarine", "plum", "peach", 10);
        treeBreeding(consumer, "apricot", "plum", "peach", 10);
        treeBreeding(consumer, "almond", "plum", "walnut", 10);
        treeBreeding(consumer, "wild_cherry", "silver_lime", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "sour_cherry", "white_willow", Ingredient.of(Blocks.CHERRY_LEAVES), 10);
        treeBreeding(consumer, "black_cherry", "ceylon_ebony", "sour_cherry", 10);
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
        treeBreeding(consumer, "banana", "balsa", "cacao", 10);
        treeBreeding(consumer, "red_banana", "banana", "kapok", 10);
        treeBreeding(consumer, "plantain", "banana", "teak", 10);
        treeBreeding(consumer, "butternut", "walnut", "wild_cherry", 10);
        treeBreeding(consumer, "rowan", "aspen", "alder", 10);
        treeBreeding(consumer, "western_hemlock", "bull_pine", "silver_fir", 3);
        treeBreeding(consumer, "ash", "silver_lime", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "alder", "beech", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "beech", Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "aspen", "beech", "alder", 10);
        treeBreeding(consumer, "yew", "european_larch", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "lawson_cypress", "bull_pine", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "cork_oak", "lawson_cypress", Blocks.OAK_LEAVES, 10);
        treeBreeding(consumer, "douglas_fir", "silver_fir", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "hazel", "aspen", "beech", 10);
        treeBreeding(consumer, "sycamore_fig", "ash", "sugar_maple", 10);
        treeBreeding(consumer, "breadfruit", "sycamore_fig", "sugar_maple", 10);
        treeBreeding(consumer, "cempedak", "sycamore_fig", "breadfruit", 10);
        treeBreeding(consumer, "jackfruit", "cempedak", "breadfruit", 10);
        treeBreeding(consumer, "whitebeam", "ash", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "hawthorn", "rowan", "beech", 10);
        treeBreeding(consumer, "pecan", "beech", Blocks.BIRCH_LEAVES, 10);
        treeBreeding(consumer, "sugar_apple", "pecan", "wild_cherry", 10);
        treeBreeding(consumer, "soursop", "sugar_apple", "banana", 10);
        treeBreeding(consumer, "elm", "ash", "silver_lime", 10);
        treeBreeding(consumer, "elderberry", "aspen", "alder", 10);
        treeBreeding(consumer, "holly", "alder", "rowan", 10);
        treeBreeding(consumer, "hornbeam", "ash", "whitebeam", 10);
        treeBreeding(consumer, "great_sallow", "white_willow", "aspen", 10);
        treeBreeding(consumer, "silver_fir", "balsam_fir", "bull_pine", 10);
        treeBreeding(consumer, "cedar", "silver_fir", "european_larch", 10);
        treeBreeding(consumer, "olive", "alder", "wild_cherry", 10);
        treeBreeding(consumer, "red_maple", "silver_lime", "european_larch", 10);
        treeBreeding(consumer, "balsam_fir", "alder", "european_larch", 10);
        treeBreeding(consumer, "loblolly_pine", "bull_pine", Blocks.SPRUCE_LEAVES, 10);
        treeBreeding(consumer, "sweetgum", "european_larch", "sugar_maple", 10);
        treeBreeding(consumer, "rubber_tree", "sweetgum", "loblolly_pine", 10);
        treeBreeding(consumer, "black_locust", "balsa", "silver_lime", 10);
        treeBreeding(consumer, "sand_pear", "red_delicious_apple", Blocks.FLOWERING_AZALEA_LEAVES, 10);
        treeBreeding(consumer, "cultivated_pear", "red_delicious_apple", "sand_pear", 10);
        treeBreeding(consumer, "osange_orange", "kapok", "old_fustic", 10);
        treeBreeding(consumer, "old_fustic", "red_maple", "mahogany", 10);
        treeBreeding(consumer, "brazilwood", "teak", "mahogany", 10);
        treeBreeding(consumer, "sandalwood", "brazilwood", "mahogany", 10);
        treeBreeding(consumer, "logwood", "kapok", "rosewood", 10);
        treeBreeding(consumer, "rosewood", "mahogany", "teak", 10);
        treeBreeding(consumer, "purpleheart", "brazilwood", "kapok", 10);
        treeBreeding(consumer, "iroko", "balsa", "teak", 10);
        treeBreeding(consumer, "ginkgo", "wenge", "silver_lime", 10);
        treeBreeding(consumer, "brazil_nut", "beech", "cacao", 10);
        treeBreeding(consumer, "rose_gum", "balsa", Blocks.FLOWERING_AZALEA_LEAVES, 10);
        treeBreeding(consumer, "swamp_gum", "yellow_meranti", "rose_gum", 10);
        treeBreeding(consumer, "boxwood", "holly", "alder", 10);
        treeBreeding(consumer, "coffea", "black_cherry", "cacao", 10);
        treeBreeding(consumer, "clove", "coffea", "teak", 10);
        treeBreeding(consumer, "monkey_puzzle", "western_hemlock", Blocks.JUNGLE_LEAVES, 10);
        treeBreeding(consumer, "rainbow_gum", "balsa", "rose_gum", 10);
        treeBreeding(consumer, "pink_ivory", "brazilwood", "rose_gum", 10);
        treeBreeding(consumer, "juniper", "elderberry", "silver_fir", 10);
        treeBreeding(consumer, "cinnamon", "rosewood", "teak", 10);
        treeBreeding(consumer, "coconut", "brazil_nut", "balsa", 10);
        treeBreeding(consumer, "cashew", "teak", Blocks.MANGROVE_LEAVES, 10);
        treeBreeding(consumer, "pistachio", "almond", "cashew", 10);
        treeBreeding(consumer, "avocado", "wenge", Blocks.OAK_LEAVES, 10);
        treeBreeding(consumer, "nutmeg", "teak", "clove", 10);
        treeBreeding(consumer, "allspice", "teak", "clove", 10);
        treeBreeding(consumer, "star_anise", "clove", "allspice", 10);
        treeBreeding(consumer, "mango", "orange", Blocks.MANGROVE_LEAVES, 10);
        treeBreeding(consumer, "star_fruit", "mango", "star_anise", 10);
        treeBreeding(consumer, "candlenut", "ginkgo", "hazel", 10);
        treeBreeding(consumer, "copoazu", "cacao", "candlenut", 10);
        treeBreeding(consumer, "carob", "sweet_chestnut", "copoazu", 10);
        treeBreeding(consumer, "pandanus", "walnut", "coconut", 10);
        treeBreeding(consumer, "salak", "pandanus", "coconut", 10);

        treeBreeding(consumer, "purple_spiral", "blue_yonder", "firecracker", 5);
        treeBreeding(consumer, "cave_dweller", "black_ember", "soul_tree", 5);
        treeBreeding(consumer, "foggy_blast", "cave_dweller", "soul_tree", 5);
        treeBreeding(consumer, "night_fuchsia", "purple_spiral", "sparkle_cherry", 5);
        treeBreeding(consumer, "time_traveller", "blue_yonder", "rippling_willow", 5);
        treeBreeding(consumer, "sparkle_cherry", "firecracker", "soul_tree", 5);
        treeBreeding(consumer, "slimy_delight", "rippling_willow", "soul_tree", 5);
        treeBreeding(consumer, "thunder_bolt", "firecracker", "flickering_sun", 5);
        treeBreeding(consumer, "rippling_willow", "blue_yonder", "flickering_sun", 5);
        treeBreeding(consumer, "water_wonder", "blue_yonder", "soul_tree", 5);
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
        Block hive = ForgeRegistries.BLOCKS.getValue(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive"));
        SingleConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .setRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                                .define('W', Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")))
                                .define('H', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Forge.HIVES))
                                .define('C', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Forge.HONEYCOMBS))
                                .define('F', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Forge.CAMPFIRES))
                                .define('S', Ingredient.of(Tags.Items.SHEARS))
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                ::save
                )
                .build(consumer, new ResourceLocation(ProductiveTrees.MODID, "hives/" + ForgeRegistries.BLOCKS.getKey(hive).getPath().replace("advanced_", "")));

        buildHiveResetRecipes(modid, hive, new ResourceLocation(ProductiveTrees.MODID, "hives/" + ForgeRegistries.BLOCKS.getKey(hive).getPath().replace("advanced_", "") + "_clear"), consumer);
    }

    public void buildBoxRecipe(String modid, WoodObject treeObject, Consumer<FinishedRecipe> consumer) {
        Block box = ForgeRegistries.BLOCKS.getValue(treeObject.getId().withPath(p -> "expansion_box_" + p));
        SingleConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .setRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                                .define('W', TreeUtil.getBlock(treeObject.getId(), "_planks"))
                                .define('C', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Forge.HONEYCOMBS))
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
