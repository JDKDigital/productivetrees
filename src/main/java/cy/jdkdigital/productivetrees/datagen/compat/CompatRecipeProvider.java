package cy.jdkdigital.productivetrees.datagen.compat;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.TreetapRecipeBuilder;
import cy.jdkdigital.productivetrees.registry.*;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;

public class CompatRecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    String[] RESIN_TREES = new String[]{"bull_pine", "douglas_fir", "cedar", "loblolly_pine", "sweetgum"};

    public CompatRecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(gen, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        Block THERMAL_SAWMILL = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("thermal:sawmill"));
        Block THERMAL_INSOLATOR = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("thermal:insolator"));
        Block THERMAL_EXTRACTOR = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("thermal:tree_extractor"));

        TreeFinder.trees.forEach((id, treeObject) -> {
            buildCorailWoodcutterRecipes(treeObject, pRecipeOutput);
            buildMekanismWoodcutterRecipes(treeObject, pRecipeOutput);

            // arboreal extractor (for resin, sap and latex), insolator (logs, saplings and fruit)
//            if (THERMAL_SAWMILL != null) {
//                ThermalSawmilRecipeBuilder.tree(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood"), TreeUtil.getBlock(treeObject.getId(), "_planks"))
//                        .unlockedBy("has_log", has(TreeUtil.getBlock(treeObject.getId(), "_log")))
//                        .unlockedBy("has_sawmill", has(THERMAL_SAWMILL))
//                        .save(pRecipeOutput.withConditions(new ModLoadedCondition("thermal")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "thermal/sawmill/" + id.getPath()));
//            }
//            if (THERMAL_INSOLATOR != null) {
//                ThermalInsulatorRecipeBuilder.tree(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_sapling"), treeObject.getFruit().getItem().copy(), treeObject.getFruit().growthSpeed())
//                        .unlockedBy("has_sapling", has(TreeUtil.getBlock(treeObject.getId(), "_planks")))
//                        .unlockedBy("has_insolator", has(THERMAL_INSOLATOR))
//                        .save(pRecipeOutput.withConditions(new ModLoadedCondition("thermal")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "thermal/insolator/" + id.getPath()));
//            }
//            if (THERMAL_EXTRACTOR != null) {
//                String fluidName = "thermal:sap"; // resin, latex
//                if (Arrays.stream(RESIN_TREES).anyMatch(s -> s.equals(id.getPath()))) {
//                    fluidName = "thermal:resin";
//                }
//                if (id.getPath().equals("rubber_tree")) {
//                    fluidName = "thermal:latex";
//                }
//                if (id.getPath().equals("sugar_maple")) {
//                    fluidName = "productivetrees:maple_sap";
//                }

//                FluidStack fluid = new FluidStack(BuiltInRegistries.FLUID.get(new ResourceLocation(fluidName)), 50);
//                ThermalExtractorRecipeBuilder.direct(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_leaves"), fluid)
//                        .unlockedBy("has_log", has(TreeUtil.getBlock(treeObject.getId(), "_log")))
//                        .unlockedBy("has_tree_extractor", has(THERMAL_EXTRACTOR))
//                        .save(pRecipeOutput.withConditions(new ModLoadedCondition("thermal")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "thermal/tree_extractor/" + id.getPath()));
//            }
            if (treeObject.getStyle().hiveStyle() != null) {
                buildHiveRecipe(ProductiveBees.MODID, treeObject, pRecipeOutput);
                buildBoxRecipe(ProductiveBees.MODID, treeObject, pRecipeOutput);
            }
            buildBotanyPotsRecipe(treeObject, pRecipeOutput);
        });

        // vanilla wood sawmill processing
        buildCompatSawmillRecipes(pRecipeOutput);

        // Treetap recipes
        TreetapRecipeBuilder.direct(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "socotra_dragon_log")), new ItemStack(TreeRegistrator.DRACAENA_SAP.get()), new ItemStack(Items.GLASS_BOTTLE), "#9d0300", 1200)
                .save(pRecipeOutput.withConditions(modLoaded("treetap")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "treetap/dracaena_sap"));
        TreetapRecipeBuilder.direct(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sandalwood_log")), new ItemStack(TreeRegistrator.SANDALWOOD_OIL.get()), new ItemStack(Items.GLASS_BOTTLE), "#f1eda6", 2400)
                .save(pRecipeOutput.withConditions(modLoaded("treetap")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "treetap/sandalwood_oil"));
        TreetapRecipeBuilder.direct(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sugar_maple_log")), new ItemStack(TreeRegistrator.MAPLE_SAP_BUCKET.get()), new FluidStack(TreeRegistrator.MAPLE_SAP.get(), 1000), 2400)
                .save(pRecipeOutput.withConditions(modLoaded("treetap")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "treetap/maple_sap"));
        TreetapRecipeBuilder.direct(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "date_palm_log")), new ItemStack(TreeRegistrator.DATE_PALM_JUICE.get()), new ItemStack(Items.GLASS_BOTTLE), "#cd7408", 2400)
                .save(pRecipeOutput.withConditions(modLoaded("treetap")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "treetap/date_palm_juice"));

    }

    private void buildCompatSawmillRecipes(RecipeOutput consumer) {
        // TODO BOP, RU, BYG etc.

    }

    private void buildMekanismWoodcutterRecipes(WoodObject treeObject, RecipeOutput consumer) {
        var logTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, treeObject.getId().getPath() + "_logs"));
        mekanism.api.datagen.recipe.builder.SawmillRecipeBuilder.sawing(ItemStackIngredient.of(SizedIngredient.of(logTag, 1)), new ItemStack(TreeUtil.getBlock(treeObject.getId(), "_planks"), 6))
                .build(consumer.withConditions(new ModLoadedCondition("mekanism")), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "mekanism/sawmill/" + treeObject.getId().getPath()));
    }

    private void buildCorailWoodcutterRecipes(WoodObject treeObject, RecipeOutput consumer) {
//        String name = treeObject.getId().getPath();
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_planks"), 4)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_planks_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_button"), 4)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_fence"), 4)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_fence_gate"), 1)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_gate_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), 4)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_slab"), 8)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")), TreeUtil.getBlock(treeObject.getId(), "_stairs"), 4)
//                        .unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_log"), TreeUtil.getBlock(treeObject.getId(), "_wood"), TreeUtil.getBlock(treeObject.getId(), "_stripped_log"), TreeUtil.getBlock(treeObject.getId(), "_stripped_wood")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_logs"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_button"), 1)
//                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_button_from_planks"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_fence"), 1)
//                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_fence_from_planks"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_pressure_plate"), 1)
//                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_pressure_plate_from_planks"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_slab"), 2)
//                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_slab_from_planks"));
//
//        SingleConditionalRecipe.builder().addCondition(modLoaded("corail_woodcutter")).setRecipe(
//                woodcutter(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")), TreeUtil.getBlock(treeObject.getId(), "_stairs"), 1)
//                        .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(TreeUtil.getBlock(treeObject.getId(), "_planks")).build()))
//                        ::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "corail/woodcutter/" + name + "_stairs_from_planks"));
    }

//    private static SingleItemRecipeBuilder woodcutter(Ingredient ingredient, ItemLike output, int count) {
//        return new SingleItemRecipeBuilder(RecipeCategory.BUILDING_BLOCKS, ModRecipeSerializers.WOODCUTTING, ingredient, output, count);
//    }

    private void buildBotanyPotsRecipe(TreeObject treeObject, RecipeOutput consumer) {
//        String name = treeObject.getId().getPath();
//        List<HarvestEntry> drops = new ArrayList<>()
//        {{
//            add(new HarvestEntry(1.0F, new ItemStack(TreeUtil.getBlock(treeObject.getId(), "_log")), 2, 4));
//            add(new HarvestEntry(0.1F, new ItemStack(Items.STICK), 1, 2));
//            add(new HarvestEntry(0.15F, new ItemStack(TreeUtil.getBlock(treeObject.getId(), "_sapling")), 1, 1));
//        }};
//        if (treeObject.hasFruit()) {
//            ItemStack fruit = treeObject.getFruit().getItem();
//            if (!fruit.isEmpty()) {
//                drops.add(new HarvestEntry(treeObject.getFruit().growthSpeed(), new ItemStack(fruit.getItem()), 1, fruit.getCount()));
//            }
//        }

//        SingleConditionalRecipe.builder().addCondition(modLoaded("botanytrees")).setRecipe(
//            BotanyPotCropRecipeBuilder.direct(Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_sapling")), Set.of("dirt"), 2400, drops, TreeUtil.getBlock(treeObject.getId(), "_sapling").defaultBlockState(), 0)::save
//        ).build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "botanytrees/" + name));
    }

    public void buildHiveRecipe(String modid, WoodObject treeObject, RecipeOutput consumer) {
        Block hive = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                .define('W', Ingredient.of(TreeUtil.getBlock(treeObject.getId(), "_planks")))
                .define('H', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Common.HIVES))
                .define('C', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Common.HONEYCOMBS))
                .define('F', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Common.CAMPFIRES))
                .define('S', Ingredient.of(Tags.Items.TOOLS_SHEAR))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(consumer.withConditions(modLoaded(modid)), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "hives/" + BuiltInRegistries.BLOCK.getKey(hive).getPath().replace("advanced_", "")));

        buildHiveResetRecipes(modid, hive, ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "hives/" + BuiltInRegistries.BLOCK.getKey(hive).getPath().replace("advanced_", "") + "_clear"), consumer);
    }

    public void buildBoxRecipe(String modid, WoodObject treeObject, RecipeOutput consumer) {
        Block box = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "expansion_box_" + p));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                .define('W', TreeUtil.getBlock(treeObject.getId(), "_planks"))
                .define('C', Ingredient.of(cy.jdkdigital.productivebees.init.ModTags.Common.HONEYCOMBS))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(consumer.withConditions(modLoaded(modid)), ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "expansion_boxes/" + BuiltInRegistries.BLOCK.getKey(box).getPath()));
    }

    private void buildHiveResetRecipes(String modid, Block hive, ResourceLocation location, RecipeOutput consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, hive).group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .requires(hive)
                .save(consumer.withConditions(modLoaded(modid)), location);
    }
}
