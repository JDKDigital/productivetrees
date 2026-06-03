package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.CocoaPodDecorator;
import cy.jdkdigital.productivetrees.feature.foliageplacers.AspenFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.ConeFoliagePlacer;
import cy.jdkdigital.productivetrees.common.feature.FruitTrunkDanglerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafReplacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.LeafTrimDecorator;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.feature.RootDecorator;
import cy.jdkdigital.productivetrees.common.feature.TaperDecorator;
import cy.jdkdigital.productivetrees.common.feature.TrunkVineDecorator;
import cy.jdkdigital.productivetrees.feature.foliageplacers.ConiferFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.PlumeFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.BallFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.FlameFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.DiamondFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.RayFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.FrondFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.RoughFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.foliageplacers.WillowFoliagePlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.CenteredUpwardsBranchingTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.UnlimitedStraightTrunkPlacer;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import cy.jdkdigital.productivetrees.feature.trunkplacers.LayeredTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.SpreadingTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.UnlimitedGiantTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.WideTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.ConiferTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.EmergentTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.StuddedTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.ParasolTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.SpiralTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.LightningTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.FlutedTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.MultiStemTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.TaperedMegaTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.WhorledTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class FeatureProvider implements DataProvider
{
    private final PackOutput output;

    public FeatureProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PackOutput.PathProvider placedFeaturePath = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/placed_feature");
        PackOutput.PathProvider configuredFeaturePath = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/configured_feature");

        List<CompletableFuture<?>> output = new ArrayList<>();

        Map<ResourceLocation, Supplier<JsonElement>> placedFeatures = Maps.newHashMap();
        Map<ResourceLocation, Supplier<JsonElement>> configuredFeatures = Maps.newHashMap();
        TreeFinder.trees.forEach((id, treeObject) -> {
            String path = treeObject.getId().getPath();
            // a null feature means the tree is mega-only (e.g. elm), so skip the regular 1x1 feature
            if (!treeObject.getFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                placedFeatures.put(treeObject.getId(), getPlacedFeature(treeObject));
                if (!TreeUtil.isSpecialTree(treeObject.getId())) {
                    // a 1x1 tree placed as an exact exported structure (e.g. time_traveller) emits a template feature; others grow procedurally
                    configuredFeatures.put(treeObject.getId(), regularTemplates.containsKey(path) ? () -> buildTemplateConfiguredFeature(regularTemplates.get(path)) : getConfiguredFeature(treeObject));
                }
            }
            if (!treeObject.getMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                ResourceLocation megaId = treeObject.getMegaFeature().location();
                placedFeatures.put(megaId, getMegaPlacedFeature(treeObject, megaId));
                // trees placed as exact exported structures (e.g. brown_amber) emit a template feature; others grow procedurally
                configuredFeatures.put(megaId, megaTemplates.containsKey(path) ? () -> buildTemplateConfiguredFeature(megaTemplates.get(path)) : getMegaConfiguredFeature(treeObject));
            }
            if (!treeObject.getLargeMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                ResourceLocation largeMegaId = treeObject.getLargeMegaFeature().location();
                placedFeatures.put(largeMegaId, getMegaPlacedFeature(treeObject, largeMegaId));
                configuredFeatures.put(largeMegaId, largeMegaTemplates.containsKey(path) ? () -> buildTemplateConfiguredFeature(largeMegaTemplates.get(path)) : getLargeMegaConfiguredFeature(treeObject));
            }
        });

        placedFeatures.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), placedFeaturePath.json(rLoc)));
        });
        configuredFeatures.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), configuredFeaturePath.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Productive Trees Feature generator";
    }

    private Supplier<JsonElement> getPlacedFeature(TreeObject treeObject) {
        return () -> {
            JsonElement placement = PlacementModifier.CODEC.encodeStart(JsonOps.INSTANCE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(TreeUtil.getBlock(treeObject.getId(), "_sapling").defaultBlockState(), Vec3i.ZERO))).getOrThrow();
            JsonArray placementArray = new JsonArray();
            placementArray.add(placement);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("feature", treeObject.getId().toString());
            jsonObject.add("placement", placementArray);
            return jsonObject;
        };
    }

    private Supplier<JsonElement> getConfiguredFeature(TreeObject treeObject) {
        String name = treeObject.getId().getPath();
        return () -> {
            JsonObject config = new JsonObject();

            // decorators
            JsonArray decoratorArray = new JsonArray();
            if (treeObject.hasFruit()) {
                var state = TreeUtil.getBlock(treeObject.getId(), "_fruit").defaultBlockState();
                if (treeObject.getId().getPath().equals("banana")) {
                    state = state.setValue(ProductiveFruitBlock.getAgeProperty(), 1);
                }
                decoratorArray.add(fruitDecorators.containsKey(name) ? fruitDecorators.get(name).apply(SimpleStateProvider.simple(state)) : fruitDecorators.get("default").apply(SimpleStateProvider.simple(state)));
            }
            if (ROOTED_REGULAR_TREES.contains(name)) {
                decoratorArray.add(rootDecorator(treeObject));
            }
            if (!treeObject.getDecoration().vine().isEmpty()) {
                decoratorArray.add(vineDecorator(treeObject));
            }
            if (COCOA_POD_TREES.contains(name)) {
                decoratorArray.add(cocoaDecorator());
            }
            // diagonal-leaf trees (palms, sunbursts) connect cornerwise, which the orthogonal trim would delete; their
            // runtime decay is diagonal-aware instead, so skip the gen-time trim for them
            if (!ProductiveLeavesBlock.CONNECTED_LEAF_TREES.contains(name)) {
                decoratorArray.add(LEAF_TRIM);
            }
            config.add("decorators", decoratorArray);
            config.add("dirt_provider", DIRT_PROVIDER);
            config.add("foliage_placer", foliagePlacers.containsKey(name) ? foliagePlacers.get(name) : foliagePlacers.get("default"));
            config.add("foliage_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_leaves"))).getOrThrow());
            config.add("minimum_size", FeatureSize.CODEC.encodeStart(JsonOps.INSTANCE, new TwoLayersFeatureSize(1, 0, 1)).getOrThrow());
            config.add("trunk_placer", trunkPlacers.containsKey(name) ? trunkPlacers.get(name) : trunkPlacers.get("default"));
            config.add("trunk_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_log"))).getOrThrow());

            config.addProperty("force_dirt", false);
            config.addProperty("ignore_vines", true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "minecraft:tree");
            jsonObject.add("config", config);
            return jsonObject;
        };
    }

    private Supplier<JsonElement> getMegaPlacedFeature(TreeObject treeObject, ResourceLocation megaId) {
        return () -> {
            JsonElement placement = PlacementModifier.CODEC.encodeStart(JsonOps.INSTANCE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(TreeUtil.getBlock(treeObject.getId(), "_sapling").defaultBlockState(), Vec3i.ZERO))).getOrThrow();
            JsonArray placementArray = new JsonArray();
            placementArray.add(placement);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("feature", megaId.toString());
            jsonObject.add("placement", placementArray);
            return jsonObject;
        };
    }

    // trees placed as exact exported structures: tree path -> the structure variant names (under data/productivetrees/structure/)
    // regular (1x1) trees placed as exact exported structures (no procedural variant at all)
    private final Map<String, List<String>> regularTemplates = Map.of(
            "time_traveller", List.of("time_traveller_1", "time_traveller_2", "time_traveller_3"),
            "blue_yonder", List.of("blue_yonder_1", "blue_yonder_2", "blue_yonder_3"),
            "flickering_sun", List.of("flickering_sun_1", "flickering_sun_2"));
    private final Map<String, List<String>> megaTemplates = Map.of(
            "brown_amber", List.of("brown_amber_mega_1", "brown_amber_mega_2"),
            "cave_dweller", List.of("cave_dweller_mega_1"),
            "firecracker", List.of("firecracker_mega_1"));
    private final Map<String, List<String>> largeMegaTemplates = Map.of(
            "brown_amber", List.of("brown_amber_giga_1"),
            "cave_dweller", List.of("cave_dweller_giga_1"));

    // a configured feature that stamps one of the listed exported structures verbatim (random variant + rotation)
    private JsonElement buildTemplateConfiguredFeature(List<String> structureNames) {
        JsonObject config = new JsonObject();
        JsonArray templates = new JsonArray();
        for (String name : structureNames) {
            templates.add(ProductiveTrees.MODID + ":" + name);
        }
        config.add("templates", templates);
        JsonObject feature = new JsonObject();
        feature.addProperty("type", ProductiveTrees.MODID + ":template_tree");
        feature.add("config", config);
        return feature;
    }

    private Supplier<JsonElement> getMegaConfiguredFeature(TreeObject treeObject) {
        String name = treeObject.getId().getPath();
        JsonElement foliage = megaFoliagePlacers.containsKey(name) ? megaFoliagePlacers.get(name) : megaFoliagePlacers.get("default");
        JsonElement trunk = megaTrunkPlacers.containsKey(name) ? megaTrunkPlacers.get(name) : megaTrunkPlacers.get("default");
        return buildMegaConfiguredFeature(treeObject, foliage, trunk);
    }

    // a second, larger mega tier (3x3) for trees that also have a 2x2 mega (e.g. yew)
    private Supplier<JsonElement> getLargeMegaConfiguredFeature(TreeObject treeObject) {
        String name = treeObject.getId().getPath();
        JsonElement foliage = largeMegaFoliagePlacers.containsKey(name) ? largeMegaFoliagePlacers.get(name) : megaFoliagePlacers.get("default");
        JsonElement trunk = largeMegaTrunkPlacers.containsKey(name) ? largeMegaTrunkPlacers.get(name) : megaTrunkPlacers.get("default");
        return buildMegaConfiguredFeature(treeObject, foliage, trunk);
    }

    private Supplier<JsonElement> buildMegaConfiguredFeature(TreeObject treeObject, JsonElement foliage, JsonElement trunk) {
        String name = treeObject.getId().getPath();
        return () -> {
            JsonObject config = new JsonObject();

            JsonArray decoratorArray = new JsonArray();
            if (treeObject.hasFruit()) {
                var state = TreeUtil.getBlock(treeObject.getId(), "_fruit").defaultBlockState();
                if (name.equals("banana")) {
                    state = state.setValue(ProductiveFruitBlock.getAgeProperty(), 1);
                }
                decoratorArray.add(fruitDecorators.containsKey(name) ? fruitDecorators.get(name).apply(SimpleStateProvider.simple(state)) : fruitDecorators.get("default").apply(SimpleStateProvider.simple(state)));
            }
            if (ROOTED_TREES.contains(name)) {
                decoratorArray.add(rootDecorator(treeObject));
            }
            if (TAPER_SMOOTHED_TREES.contains(name)) {
                decoratorArray.add(taperDecorator(treeObject));
            }
            if (!treeObject.getDecoration().vine().isEmpty()) {
                decoratorArray.add(vineDecorator(treeObject));
            }
            // diagonal-leaf trees (palms, sunbursts) connect cornerwise, which the orthogonal trim would delete; their
            // runtime decay is diagonal-aware instead, so skip the gen-time trim for them
            if (!ProductiveLeavesBlock.CONNECTED_LEAF_TREES.contains(name)) {
                decoratorArray.add(LEAF_TRIM);
            }
            config.add("decorators", decoratorArray);
            config.add("dirt_provider", DIRT_PROVIDER);
            config.add("foliage_placer", foliage);
            config.add("foliage_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_leaves"))).getOrThrow());
            config.add("minimum_size", MEGA_MINIMUM_SIZE);
            config.add("trunk_placer", trunk);
            config.add("trunk_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_log"))).getOrThrow());

            config.addProperty("force_dirt", false);
            config.addProperty("ignore_vines", true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "minecraft:tree");
            jsonObject.add("config", config);
            return jsonObject;
        };
    }

    private JsonElement createFoliage(int radius, int height) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), height)).getOrThrow();
    }

    private JsonElement createBallFoliage(int radius) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new BallFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0))).getOrThrow();
    }

    // a sphere whose radius varies per tree, for crowns of mixed sizes (e.g. firecracker)
    private JsonElement createBallFoliage(IntProvider radius) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new BallFoliagePlacer(radius, ConstantInt.of(0))).getOrThrow();
    }

    private JsonElement createDiamondFoliage(int radius) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new DiamondFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0))).getOrThrow();
    }

    private JsonElement createRayFoliage(int coreRadius, int offset, IntProvider rayCount, IntProvider rayLength) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new RayFoliagePlacer(ConstantInt.of(coreRadius), ConstantInt.of(offset), rayCount, rayLength)).getOrThrow();
    }

    private JsonElement createFrondFoliage(int coreRadius, int offset, IntProvider frondCount, IntProvider frondLength) {
        return createFrondFoliage(coreRadius, offset, frondCount, frondLength, true);
    }

    private JsonElement createFrondFoliage(int coreRadius, int offset, IntProvider frondCount, IntProvider frondLength, boolean droop) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new FrondFoliagePlacer(ConstantInt.of(coreRadius), ConstantInt.of(offset), frondCount, frondLength, droop)).getOrThrow();
    }

    private JsonElement createFlameFoliage(int radius, int height, int offset) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new FlameFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), ConstantInt.of(height))).getOrThrow();
    }

    private JsonElement createFancyFoliage(int radius, int offset, int height) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new FancyFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), height)).getOrThrow();
    }

    // a columnar crown of distinct stacked foliage sections separated by bare trunk (aspen)
    private JsonElement createAspenFoliage(int radius, int sectionCount, int sectionHeight, int sectionGap) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new AspenFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), sectionCount, sectionHeight, sectionGap)).getOrThrow();
    }

    // a dense, smooth conifer cone (filled diamond layers tapering to a point) — cypresses
    private JsonElement createConeFoliage(int radius, int height, int offset) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new ConeFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), ConstantInt.of(height))).getOrThrow();
    }

    private JsonElement createRoughFoliage(int radius, int height) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new RoughFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), height)).getOrThrow();
    }

    private JsonElement createPlumeFoliage(int radius, int height, int offset) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new PlumeFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), ConstantInt.of(height))).getOrThrow();
    }

    private JsonElement createWillowFoliage(int radius, int offset, int height, IntProvider droop) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new WillowFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), height, droop)).getOrThrow();
    }

    private JsonElement createGiantTrunk(int height, int randA, int randB) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new UnlimitedGiantTrunkPlacer(height, randA, randB)).getOrThrow();
    }

    private JsonElement createWideTrunk(int height, int randA, int randB, int radius) {
        return createWideTrunk(height, randA, randB, radius, 0.33F);
    }

    private JsonElement createWideTrunk(int height, int randA, int randB, int radius, float branchStart) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new WideTrunkPlacer(height, randA, randB, radius, branchStart, Optional.empty())).getOrThrow();
    }

    private JsonElement createWideTrunk(int height, int randA, int randB, int radius, float branchStart, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new WideTrunkPlacer(height, randA, randB, radius, branchStart, Optional.of(branchLength))).getOrThrow();
    }

    private JsonElement createSpreadingTrunk(int height, int randA, int randB, IntProvider branchCount, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new SpreadingTrunkPlacer(height, randA, randB, branchCount, branchLength)).getOrThrow();
    }

    private JsonElement createLayeredTrunk(int height, int randA, int randB, IntProvider branchCount, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new LayeredTrunkPlacer(height, randA, randB, branchCount, branchLength)).getOrThrow();
    }

    private JsonElement createWhorledTrunk(int height, int randA, int randB, int radius, float branchStart, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new WhorledTrunkPlacer(height, randA, randB, radius, branchStart, branchLength)).getOrThrow();
    }

    private JsonElement createTaperedMegaTrunk(int height, int randA, int randB, int baseHeight, IntProvider branchCount, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new TaperedMegaTrunkPlacer(height, randA, randB, baseHeight, branchCount, branchLength)).getOrThrow();
    }

    private JsonElement createFlutedTrunk(int height, int randA, int randB, int radius, float branchStart, IntProvider branchLength, int twist) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new FlutedTrunkPlacer(height, randA, randB, radius, branchStart, branchLength, twist)).getOrThrow();
    }

    private JsonElement createMultiStemTrunk(int height, int randA, int randB, IntProvider stemCount, IntProvider leanInterval) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new MultiStemTrunkPlacer(height, randA, randB, stemCount, leanInterval)).getOrThrow();
    }

    private JsonElement createConiferTrunk(int height, int randA, int randB, float branchStart, int maxBranch) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new ConiferTrunkPlacer(height, randA, randB, branchStart, maxBranch)).getOrThrow();
    }

    private JsonElement createEmergentTrunk(int height, int randA, int randB, int radius, float wideFraction, IntProvider armCount, IntProvider armLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new EmergentTrunkPlacer(height, randA, randB, radius, wideFraction, armCount, armLength)).getOrThrow();
    }

    private JsonElement createStuddedTrunk(int height, int randA, int randB, IntProvider branchCount, IntProvider branchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new StuddedTrunkPlacer(height, randA, randB, branchCount, branchLength)).getOrThrow();
    }

    private JsonElement createParasolTrunk(int height, int randA, int randB, IntProvider spokeCount, IntProvider spokeLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new ParasolTrunkPlacer(height, randA, randB, spokeCount, spokeLength)).getOrThrow();
    }

    private JsonElement createSpiralTrunk(int height, int randA, int randB, IntProvider armCount, IntProvider armLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new SpiralTrunkPlacer(height, randA, randB, armCount, armLength)).getOrThrow();
    }

    private JsonElement createLightningTrunk(int height, int randA, int randB, IntProvider forkCount, IntProvider forkLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new LightningTrunkPlacer(height, randA, randB, forkCount, forkLength)).getOrThrow();
    }

    private final JsonElement MEGA_MINIMUM_SIZE = FeatureSize.CODEC.encodeStart(JsonOps.INSTANCE, new TwoLayersFeatureSize(1, 1, 2)).getOrThrow();
    private static final Set<String> ROOTED_TREES = Set.of("elm", "copper_beech", "black_locust", "black_cherry", "breadfruit", "cocobolo", "cork_oak", "ginkgo", "great_sallow", "jackfruit", "walnut", "sweet_chestnut", "pandanus", "pecan", "padauk", "pink_ivory", "rose_gum", "rosewood", "yew", "kapok", "sequoia", "brazil_nut", "european_larch", "yellow_meranti", "black_ember");
    // regular (1x1 / wide-base) trees whose structures flare wood roots at the base, so they get the root decorator too
    private static final Set<String> ROOTED_REGULAR_TREES = Set.of("sycamore_fig", "sweet_chestnut");
    // megas with a tapering trunk whose taper steps should be eased with wood at the shoulders
    private static final Set<String> TAPER_SMOOTHED_TREES = Set.of("aspen", "jackfruit", "mango", "lawson_cypress", "rose_gum", "sweet_chestnut", "breadfruit", "ginkgo", "douglas_fir", "grandidiers_baobab", "sequoia", "kapok", "brazil_nut", "purpleheart", "silver_fir", "european_larch", "rainbow_gum", "yellow_meranti", "western_hemlock");
    private final JsonElement LEAF_TRIM = TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new LeafTrimDecorator(6)).getOrThrow();
    // trees with cocoa pods on the lower trunk
    private static final Set<String> COCOA_POD_TREES = Set.of("cacao");

    // cocoa pods up the lower trunk
    private JsonElement cocoaDecorator() {
        return TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new CocoaPodDecorator(0.7F, 4)).getOrThrow();
    }

    // a root spur decorator that flares the tree's wood block out along the ground from each base log
    private JsonElement rootDecorator(TreeObject treeObject) {
        return TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new RootDecorator(SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_wood")), 0.6F, UniformInt.of(1, 3))).getOrThrow();
    }

    // eases a tapering trunk's steps by filling the dropped-out shoulders with the tree's wood block
    private JsonElement taperDecorator(TreeObject treeObject) {
        return TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new TaperDecorator(SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_wood")), 0.7F)).getOrThrow();
    }

    // hangs vanilla vines off the trunk logs, using the block named in the tree's decoration.vine
    private JsonElement vineDecorator(TreeObject treeObject) {
        return TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new TrunkVineDecorator(SimpleStateProvider.simple(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(treeObject.getDecoration().vine()))))).getOrThrow();
    }

    private JsonElement createConiferFoliage(int radius, int height, int offset) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new ConiferFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), ConstantInt.of(height))).getOrThrow();
    }

    private JsonElement createStraightTrunk(int height, int randA, int randB) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new UnlimitedStraightTrunkPlacer(height, randA, randB)).getOrThrow();
    }

    private JsonElement createBranchingTrunk(int height, int randA, int randB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength) {
        return createBranchingTrunk(height, randA, randB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength, 0.4F); // default branch_start = upper ~60%
    }

    private JsonElement createBranchingTrunk(int height, int randA, int randB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength, float branchStartFraction) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new CenteredUpwardsBranchingTrunkPlacer(height, randA, randB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength, branchStartFraction)).getOrThrow();
    }

    private final JsonElement DIRT_PROVIDER = BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(Blocks.DIRT)).getOrThrow();

    private Function<SimpleStateProvider, JsonElement> createDanglerFruitProvider(float density, int maxFruits) {
        return (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafPlacerDecorator(density, maxFruits, fruitProvider)).getOrThrow();
    }

    // dangles fruit only from leaves within `reach` of the trunk, bunching it near the bole
    private Function<SimpleStateProvider, JsonElement> createTrunkDanglerFruitProvider(float density, int maxFruits, int reach) {
        return (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitTrunkDanglerDecorator(density, maxFruits, reach, fruitProvider)).getOrThrow();
    }

    private final Function<SimpleStateProvider, JsonElement> MEDIUM_FRUIT_DISTRIBUTION = (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.4f, fruitProvider)).getOrThrow();
    private final Map<String, Function<SimpleStateProvider, JsonElement>> fruitDecorators = new HashMap<>() {{
        put("default", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.6f, fruitProvider)).getOrThrow());
        put("almond", MEDIUM_FRUIT_DISTRIBUTION);
        put("avocado", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.3f, fruitProvider)).getOrThrow());
        put("banana", createDanglerFruitProvider(0.4f, 4));
        put("grandidiers_baobab", createDanglerFruitProvider(0.3f, 20));
        put("breadfruit", createDanglerFruitProvider(0.25f, 18));
        put("cempedak", createDanglerFruitProvider(0.25f, 15));
        put("coconut", createTrunkDanglerFruitProvider(0.4f, 4, 1));
        put("copoazu", createDanglerFruitProvider(0.2f, 5));
        put("jackfruit", createDanglerFruitProvider(0.25f, 16));
        put("pandanus", createDanglerFruitProvider(0.25f, 14));
        put("planet_peach", createDanglerFruitProvider(0.2f, 7));
        put("plantain", createDanglerFruitProvider(0.2f, 3));
        put("red_banana", createDanglerFruitProvider(0.2f, 3));
        put("beech", MEDIUM_FRUIT_DISTRIBUTION);
        put("butternut", MEDIUM_FRUIT_DISTRIBUTION);
        put("hazel", MEDIUM_FRUIT_DISTRIBUTION);
        put("pecan", MEDIUM_FRUIT_DISTRIBUTION);
        put("pistachio", MEDIUM_FRUIT_DISTRIBUTION);
        put("wallnut", MEDIUM_FRUIT_DISTRIBUTION);
    }};
    private final Map<String, JsonElement> foliagePlacers = new HashMap<>() {{
        put("default", createFoliage(2, 3));
        put("alder", createRoughFoliage(4, 3));
        put("avocado", createRoughFoliage(4, 3));
        put("banana", createFrondFoliage(0, 0, UniformInt.of(8, 8), UniformInt.of(3, 4)));
        put("balsam_fir", createConiferFoliage(2, 6, 1));
        put("bull_pine", createRoughFoliage(2, 2));
        put("red_banana", createFrondFoliage(0, 0, UniformInt.of(8, 8), UniformInt.of(3, 4)));
        put("plantain", createFrondFoliage(0, 0, UniformInt.of(8, 8), UniformInt.of(3, 4), false));
        put("asai_palm", createFrondFoliage(1, 0, UniformInt.of(7, 10), UniformInt.of(4, 5)));
        put("date_palm", createFrondFoliage(1, 0, UniformInt.of(8, 11), UniformInt.of(5, 6)));
        put("elderberry", createRoughFoliage(4, 3));
        put("juniper", createConiferFoliage(2, 7, 1));


        put("allspice", createRoughFoliage(3, 3));
        put("almond", createRoughFoliage(3, 3));
        put("apricot", createRoughFoliage(3, 3));
        put("aquilaria", createRoughFoliage(3, 3));
        put("ash", createRoughFoliage(4, 4));
        put("aspen", createAspenFoliage(2, 3, 2, 1));
        put("balsa", createRoughFoliage(2, 2));
        put("bay_leaf", createRoughFoliage(2, 3));
        put("beech", createRoughFoliage(2, 3));
        put("beliy_naliv_apple", createRoughFoliage(3, 3));
        put("blackthorn", createRoughFoliage(3, 3));
        put("black_cherry", createRoughFoliage(3, 3));
        put("black_ember", createFlameFoliage(3, 8, 3));
        put("black_locust", createRoughFoliage(3, 3));
        put("blue_mahoe", createRoughFoliage(4, 3));
        put("boxwood", createRoughFoliage(3, 3));
        put("brazilwood", createRoughFoliage(3, 3));
        put("brazil_nut", createFoliage(3, 3));
        put("breadfruit", createRoughFoliage(5, 4));
        put("buddhas_hand", createRoughFoliage(3, 3));
        put("butternut", createRoughFoliage(4, 3));
        put("cacao", createRoughFoliage(3, 3));
        put("candlenut", createRoughFoliage(4, 3));
        put("carob", createRoughFoliage(4, 4));
        put("cashew", createRoughFoliage(4, 3));
        put("cave_dweller", createFoliage(3, 3));
        put("cedar", createRoughFoliage(2, 2));
        put("cempedak", createRoughFoliage(3, 3));
        put("ceylon_ebony", createRoughFoliage(4, 2));
        put("cherry_plum", createRoughFoliage(3, 3));
        put("cinnamon", createRoughFoliage(3, 3));
        put("citron", createRoughFoliage(3, 3));
        put("clove", createPlumeFoliage(3, 11, 1));
        put("cocobolo", createRoughFoliage(3, 3));
        put("coconut", createFrondFoliage(1, 0, UniformInt.of(8, 12), UniformInt.of(5, 7)));
        put("coffea", createRoughFoliage(2, 2));
        put("copoazu", createRoughFoliage(3, 3));
        put("copper_beech", createRoughFoliage(4, 3));
        put("cork_oak", createRoughFoliage(3, 3));
        put("cultivated_pear", createRoughFoliage(3, 3));
        put("dogwood", createRoughFoliage(3, 3));
        put("douglas_fir", createConiferFoliage(6, 22, 2));
        put("elm", createFoliage(6, 7));
        put("european_larch", createConiferFoliage(2, 3, 1));
        put("finger_lime", createRoughFoliage(2, 2));
        put("firecracker", createBallFoliage(UniformInt.of(2, 4)));
        put("flickering_sun", createRayFoliage(2, 2, UniformInt.of(8, 12), UniformInt.of(3, 4)));
        put("flowering_crabapple", createRoughFoliage(2, 3));
        put("foggy_blast", createRoughFoliage(3, 3));
        put("ginkgo", createRoughFoliage(4, 4));
        put("golden_delicious_apple", createRoughFoliage(3, 3));
        put("grandidiers_baobab", createFoliage(3, 3));
        put("granny_smith_apple", createRoughFoliage(3, 3));
        put("grapefruit", createRoughFoliage(3, 3));
        put("great_sallow", createRoughFoliage(3, 3));
        put("greenheart", createRoughFoliage(5, 4));
        put("hawthorn", createRoughFoliage(3, 3));
        put("hazel", createRoughFoliage(3, 3));
        put("holly", createPlumeFoliage(3, 5, 1));
        put("hornbeam", createRoughFoliage(3, 5));
        put("ipe", createRoughFoliage(2, 3));
        put("pink_ipe", createRoughFoliage(2, 3));
        put("purple_ipe", createRoughFoliage(2, 3));
        put("white_ipe", createRoughFoliage(2, 3));
        put("iroko", createFoliage(3, 3));
        put("jackfruit", createRoughFoliage(4, 3));
        put("kapok", createFoliage(3, 3));
        put("key_lime", createRoughFoliage(2, 2));
        put("kumquat", createRoughFoliage(2, 2));
        put("lawson_cypress", createConeFoliage(3, 10, 1));
        put("lemon", createRoughFoliage(3, 3));
        put("lime", createRoughFoliage(3, 3));
        put("loblolly_pine", createRoughFoliage(2, 2));
        put("logwood", createRoughFoliage(4, 3));
        put("mahogany", createRoughFoliage(4, 2));
        put("mandarin", createRoughFoliage(3, 3));
        put("mango", createRoughFoliage(4, 3));
        put("monkey_puzzle", createFoliage(3, 3));
        put("moonlight_magic_crepe_myrtle", createRoughFoliage(3, 3));
        put("myrtle_ebony", createRoughFoliage(3, 3));
        put("nectarine", createRoughFoliage(3, 3));
        put("nutmeg", createRoughFoliage(3, 3));
        put("old_fustic", createRoughFoliage(4, 3));
        put("olive", createRoughFoliage(3, 3));
        put("orange", createRoughFoliage(3, 3));
        put("osage_orange", createRoughFoliage(3, 3));
        put("padauk", createFoliage(3, 3));
        put("pandanus", createFoliage(3, 3));
        put("papaya", createRoughFoliage(3, 3));
        put("peach", createRoughFoliage(3, 3));
        put("pecan", createFoliage(3, 3));
        put("persimmon", createRoughFoliage(4, 3));
        put("pink_ivory", createRoughFoliage(4, 3));
        put("pistachio", createRoughFoliage(4, 3));
        put("plum", createRoughFoliage(3, 3));
        put("pomegranate", createRoughFoliage(2, 3));
        put("pomelo", createRoughFoliage(4, 3));
        put("prairie_crabapple", createRoughFoliage(3, 3));
        put("purpleheart", createFoliage(3, 3));
        put("purple_blackthorn", createRoughFoliage(3, 3));
        put("purple_crepe_myrtle", createRoughFoliage(3, 3));
        put("rainbow_gum", createFoliage(3, 3));
        put("red_crepe_myrtle", createRoughFoliage(3, 3));
        put("red_delicious_apple", createRoughFoliage(2, 3));
        put("red_maple", createRoughFoliage(2, 3));
        put("rippling_willow", createWillowFoliage(4, 0, 2, UniformInt.of(3, 7)));
        put("rosewood", createRoughFoliage(4, 3));
        put("rose_gum", createFoliage(3, 3));
        put("rowan", createRoughFoliage(3, 3));
        put("rubber_tree", createRoughFoliage(3, 3));
        put("salak", createRoughFoliage(2, 3));
        put("sandalwood", createRoughFoliage(4, 3));
        put("sand_pear", createRoughFoliage(3, 3));
        put("satsuma", createRoughFoliage(3, 3));
        put("sequoia", createFoliage(3, 3));
        put("silver_fir", createConiferFoliage(5, 20, 2));
        put("silver_lime", createRoughFoliage(3, 3));
        put("socotra_dragon", createRoughFoliage(2, 2));
        put("purple_spiral", createRoughFoliage(2, 2));
        put("soursop", createRoughFoliage(3, 3));
        put("sour_cherry", createRoughFoliage(3, 3));
        put("soul_tree", createRoughFoliage(3, 3));
        put("sparkle_cherry", createRoughFoliage(3, 3));
        put("star_anise", createRoughFoliage(2, 2));
        put("star_fruit", createRoughFoliage(3, 3));
        put("sugar_apple", createRoughFoliage(3, 3));
        put("sugar_maple", createRoughFoliage(3, 3));
        put("swamp_gum", createRoughFoliage(3, 3));
        put("sweetgum", createRoughFoliage(3, 3));
        put("sweet_chestnut", createRoughFoliage(5, 3));
        put("sweet_crabapple", createRoughFoliage(3, 3));
        put("sycamore_fig", createRoughFoliage(5, 3));
        put("tangerine", createRoughFoliage(3, 3));
        put("teak", createRoughFoliage(4, 3));
        put("thunder_bolt", createRoughFoliage(2, 2));
        put("tuscarora_crepe_myrtle", createRoughFoliage(3, 3));
        put("blue_yonder", createFoliage(3, 3));
        put("walnut", createRoughFoliage(3, 3));
        put("wenge", createRoughFoliage(4, 3));
        put("western_hemlock", createRoughFoliage(3, 3));
        put("whitebeam", createRoughFoliage(4, 3));
        put("white_poplar", createRoughFoliage(2, 3));
        put("white_willow", createWillowFoliage(4, 0, 2, UniformInt.of(4, 9)));
        put("water_wonder", createWillowFoliage(5, 0, 2, UniformInt.of(5, 10)));
        put("slimy_delight", createDiamondFoliage(3));
        put("night_fuchsia", createRoughFoliage(4, 3));
        put("wild_cherry", createRoughFoliage(4, 3));
        put("yellow_meranti", createRoughFoliage(3, 3));
        put("yew", createFoliage(3, 3));
        put("zebrano", createRoughFoliage(3, 2));
    }};
    private final Map<String, JsonElement> trunkPlacers = new HashMap<>() {{
        put("default", createStraightTrunk(10, 3, 0));
        put("alder", createBranchingTrunk(13, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(2, 4)));
        put("aspen", createStraightTrunk(12, 1, 0));
        put("avocado", createBranchingTrunk(8, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("banana", createStraightTrunk(5, 6, 0));
        put("balsa", createStraightTrunk(3, 1, 0));
        put("balsam_fir", createStraightTrunk(5, 2, 0));
        put("bay_leaf", createMultiStemTrunk(6, 2, 0, UniformInt.of(4, 6), UniformInt.of(2, 3)));
        put("beech", createBranchingTrunk(9, 2, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 3)));
        put("black_locust", createBranchingTrunk(10, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 3)));
        put("blue_mahoe", createWideTrunk(12, 2, 0, 0, 0.45F, UniformInt.of(6, 8)));
        put("boxwood", createStraightTrunk(3, 4, 0));
        put("brazilwood", createBranchingTrunk(9, 3, 0, UniformInt.of(1, 3), 0.6F, UniformInt.of(2, 3)));
        put("bull_pine", createWhorledTrunk(10, 3, 0, 0, 0.15F, UniformInt.of(1, 2)));
        put("butternut", createBranchingTrunk(9, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(3, 5)));
        put("candlenut", createBranchingTrunk(9, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(3, 5)));
        put("cashew", createWideTrunk(7, 3, 0, 0, 0.3F, UniformInt.of(6, 8)));
        put("cinnamon", createBranchingTrunk(10, 3, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 3)));
        put("clove", createStraightTrunk(10, 3, 0));
        put("cocobolo", createBranchingTrunk(9, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("coconut", createStraightTrunk(15, 10, 0));
        put("coffea", createStraightTrunk(2, 1, 0));
        put("red_banana", createStraightTrunk(5, 6, 0));
        put("plantain", createStraightTrunk(4, 2, 0));
        put("asai_palm", createStraightTrunk(9, 5, 2));
        put("date_palm", createStraightTrunk(13, 4, 2));
        put("copoazu", createBranchingTrunk(8, 4, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("elderberry", createBranchingTrunk(3, 4, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("juniper", createStraightTrunk(5, 2, 0));
        put("allspice", createBranchingTrunk(10, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("almond", createBranchingTrunk(6, 1, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("apricot", createBranchingTrunk(4, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("aquilaria", createBranchingTrunk(28, 6, 0, UniformInt.of(1, 2), 0.7F, UniformInt.of(2, 4)));
        put("ash", createWideTrunk(12, 2, 0, 0, 0.4F, UniformInt.of(7, 9))); // mega is 43
        put("beliy_naliv_apple", createBranchingTrunk(4, 3, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("blackthorn", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("black_cherry", createBranchingTrunk(7, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("black_ember", createLayeredTrunk(11, 2, 0, UniformInt.of(4, 6), UniformInt.of(4, 5)));
        put("breadfruit", createBranchingTrunk(9, 2, 0, UniformInt.of(2, 4), 0.3F, UniformInt.of(3, 5)));
        put("buddhas_hand", createStraightTrunk(5, 2, 0));
        put("cacao", createBranchingTrunk(5, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2), 0.5F));
        put("carob", createStraightTrunk(6, 3, 0));
        put("cedar", createWhorledTrunk(11, 6, 0, 0, 0.1F, UniformInt.of(3, 6)));
        put("cempedak", createBranchingTrunk(12, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("ceylon_ebony", createBranchingTrunk(12, 4, 0, UniformInt.of(1, 3), 0.6F, UniformInt.of(3, 5)));
        put("cherry_plum", createBranchingTrunk(5, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("citron", createBranchingTrunk(4, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("copper_beech", createBranchingTrunk(8, 4, 0, UniformInt.of(1, 3), 0.85F, UniformInt.of(1, 2)));
        put("cork_oak", createBranchingTrunk(7, 4, 0, UniformInt.of(1, 3), 0.6F, UniformInt.of(1, 3)));
        put("cultivated_pear", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 3)));
        put("dogwood", createLayeredTrunk(4, 3, 0, UniformInt.of(5, 8), UniformInt.of(3, 4)));
        put("douglas_fir", createStraightTrunk(23, 5, 0));
        put("european_larch", createWhorledTrunk(20, 4, 0, 0, 0.1F, UniformInt.of(4, 7)));
        put("finger_lime", createStraightTrunk(2, 1, 0));
        put("firecracker", createStraightTrunk(6, 6, 0));
        put("flickering_sun", createStraightTrunk(7, 4, 0));
        put("flowering_crabapple", createBranchingTrunk(3, 2, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("ginkgo", createBranchingTrunk(15, 3, 0, UniformInt.of(2, 3), 0.5F, UniformInt.of(2, 4), 0.5F));
        put("golden_delicious_apple", createBranchingTrunk(4, 3, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("granny_smith_apple", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("grapefruit", createBranchingTrunk(3, 2, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("great_sallow", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("greenheart", createStraightTrunk(24, 31, 0));
        put("hawthorn", createBranchingTrunk(5, 6, 0, UniformInt.of(1, 3), 0.6F, UniformInt.of(1, 3)));
        put("hazel", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("holly", createStraightTrunk(4, 3, 0));
        put("hornbeam", createStraightTrunk(8, 4, 0));
        put("ipe", createBranchingTrunk(8, 5, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("pink_ipe", createBranchingTrunk(8, 5, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("purple_ipe", createBranchingTrunk(8, 5, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("white_ipe", createBranchingTrunk(8, 5, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("jackfruit", createBranchingTrunk(11, 2, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("key_lime", createStraightTrunk(2, 1, 0));
        put("kumquat", createStraightTrunk(2, 1, 0));
        put("lawson_cypress", createStraightTrunk(10, 4, 0));
        put("lemon", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("lime", createBranchingTrunk(4, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("loblolly_pine", createWhorledTrunk(12, 6, 0, 0, 0.2F, UniformInt.of(1, 2))); // mega is 50
        put("logwood", createLayeredTrunk(9, 3, 0, UniformInt.of(5, 8), UniformInt.of(2, 3)));
        put("mahogany", createBranchingTrunk(7, 4, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("mandarin", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("mango", createBranchingTrunk(6, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("monkey_puzzle", createStraightTrunk(8, 2, 0));
        put("moonlight_magic_crepe_myrtle", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 2), 0.45F));
        put("myrtle_ebony", createBranchingTrunk(11, 4, 0, UniformInt.of(1, 3), 0.6F, UniformInt.of(1, 3)));
        put("nectarine", createBranchingTrunk(2, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("nutmeg", createBranchingTrunk(7, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 3)));
        put("old_fustic", createWideTrunk(28, 8, 0, 0, 0.5F, UniformInt.of(2, 5)));
        put("olive", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(1, 3)));
        put("orange", createBranchingTrunk(4, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("osage_orange", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 3), 0.3F, UniformInt.of(2, 3)));
        put("pandanus", createStraightTrunk(4, 10, 0));
        put("papaya", createBranchingTrunk(7, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 3)));
        put("peach", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 3), 0.3F, UniformInt.of(1, 3)));
        put("persimmon", createBranchingTrunk(7, 3, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("pink_ivory", createBranchingTrunk(6, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("pistachio", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("plum", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("pomegranate", createBranchingTrunk(2, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("pomelo", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 3), 0.3F, UniformInt.of(2, 3)));
        put("prairie_crabapple", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("purple_blackthorn", createBranchingTrunk(3, 2, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("purple_crepe_myrtle", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 2), 0.45F));
        put("red_crepe_myrtle", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 2), 0.45F));
        put("red_delicious_apple", createBranchingTrunk(3, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("red_maple", createLayeredTrunk(8, 2, 0, UniformInt.of(3, 5), UniformInt.of(1, 2)));
        put("rosewood", createBranchingTrunk(11, 3, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 4)));
        put("rowan", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("rubber_tree", createBranchingTrunk(18, 6, 0, UniformInt.of(1, 2), 0.7F, UniformInt.of(1, 3)));
        put("salak", createStraightTrunk(2, 1, 0));
        put("sandalwood", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("sand_pear", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 2), 0.3F, UniformInt.of(1, 2)));
        put("satsuma", createBranchingTrunk(2, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("silver_fir", createStraightTrunk(22, 9, 0));
        put("silver_lime", createStraightTrunk(6, 2, 0));
        put("socotra_dragon", createParasolTrunk(4, 1, 0, UniformInt.of(6, 8), UniformInt.of(2, 3)));
        put("purple_spiral", createSpiralTrunk(6, 1, 0, UniformInt.of(4, 6), UniformInt.of(4, 5)));
        put("soursop", createStraightTrunk(4, 1, 0));
        put("sour_cherry", createBranchingTrunk(3, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("foggy_blast", createBranchingTrunk(4, 3, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(3, 4)));
        put("soul_tree", createBranchingTrunk(8, 4, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("sparkle_cherry", createBranchingTrunk(6, 3, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 3)));
        put("star_anise", createStraightTrunk(2, 1, 0));
        put("star_fruit", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(1, 3)));
        put("sugar_apple", createBranchingTrunk(2, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("sugar_maple", createBranchingTrunk(12, 3, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 3)));
        put("swamp_gum", createBranchingTrunk(12, 5, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("sweetgum", createBranchingTrunk(12, 3, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 3))); // 45 mega
        put("sweet_chestnut", createWideTrunk(20, 2, 0, 1, 0.3F, UniformInt.of(3, 6)));
        put("sweet_crabapple", createBranchingTrunk(3, 1, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
        put("sycamore_fig", createWideTrunk(6, 3, 0, 1, 0.3F, UniformInt.of(2, 3)));
        put("tangerine", createBranchingTrunk(2, 1, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("teak", createBranchingTrunk(16, 6, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("thunder_bolt", createLightningTrunk(12, 4, 0, UniformInt.of(3, 5), UniformInt.of(5, 6)));
        put("tuscarora_crepe_myrtle", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 2), 0.45F));
        put("walnut", createBranchingTrunk(8, 4, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(1, 3)));
        put("wenge", createBranchingTrunk(12, 4, 0, UniformInt.of(1, 3), 0.5F, UniformInt.of(2, 4)));
        put("whitebeam", createBranchingTrunk(6, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("white_poplar", createBranchingTrunk(14, 6, 0, UniformInt.of(1, 2), 0.6F, UniformInt.of(1, 2), 0.15F));
        put("white_willow", createBranchingTrunk(8, 3, 0, UniformInt.of(2, 4), 0.6F, UniformInt.of(3, 6)));
        put("rippling_willow", createBranchingTrunk(6, 2, 0, UniformInt.of(2, 4), 0.6F, UniformInt.of(3, 5)));
        put("water_wonder", createBranchingTrunk(10, 3, 0, UniformInt.of(2, 4), 0.6F, UniformInt.of(4, 7)));
        put("slimy_delight", createStraightTrunk(7, 5, 0));
        put("night_fuchsia", createBranchingTrunk(4, 2, 0, UniformInt.of(1, 2), 0.5F, UniformInt.of(1, 2)));
        put("wild_cherry", createBranchingTrunk(5, 2, 0, UniformInt.of(1, 3), 0.4F, UniformInt.of(2, 3)));
        put("yellow_meranti", createStraightTrunk(40, 30, 0)); // mega is 100
        put("yew", createStraightTrunk(10, 10, 0));
        put("zebrano", createBranchingTrunk(11, 3, 0, UniformInt.of(1, 2), 0.4F, UniformInt.of(1, 2)));
    }};
    private final Map<String, JsonElement> megaFoliagePlacers = new HashMap<>() {{
        put("default", createFancyFoliage(2, 4, 4));
        put("aspen", createAspenFoliage(2, 5, 2, 2));
        put("balsa", createRoughFoliage(4, 3));
        put("beech", createRoughFoliage(4, 3));
        put("black_cherry", createRoughFoliage(4, 3));
        put("black_ember", createFlameFoliage(4, 12, 3));
        put("bull_pine", createRoughFoliage(3, 2));
        put("cedar", createRoughFoliage(2, 2));
        put("cempedak", createRoughFoliage(4, 3));
        put("ceylon_ebony", createRoughFoliage(5, 3));
        put("cocobolo", createRoughFoliage(3, 3));
        put("jackfruit", createRoughFoliage(5, 4));
        put("iroko", createRoughFoliage(5, 4));
        put("mango", createRoughFoliage(5, 4));
        put("lawson_cypress", createConeFoliage(4, 40, 1));
        put("osage_orange", createRoughFoliage(4, 3));
        put("padauk", createRoughFoliage(5, 4));
        put("pandanus", createFrondFoliage(1, 0, UniformInt.of(7, 10), UniformInt.of(5, 7)));
        put("pecan", createRoughFoliage(5, 4));
        put("pink_ivory", createRoughFoliage(5, 4));
        put("rainbow_gum", createRoughFoliage(5, 4));
        put("rose_gum", createRoughFoliage(5, 4));
        put("rosewood", createRoughFoliage(5, 4));
        put("old_fustic", createRoughFoliage(5, 4));
        put("sweet_chestnut", createRoughFoliage(5, 4));
        put("black_locust", createRoughFoliage(4, 3));
        put("breadfruit", createRoughFoliage(5, 4));
        put("ginkgo", createRoughFoliage(5, 4));
        put("walnut", createRoughFoliage(4, 3));
        put("silver_fir", createConiferFoliage(2, 3, 1));
        put("ipe", createRoughFoliage(3, 3));
        put("pink_ipe", createRoughFoliage(3, 3));
        put("purple_ipe", createRoughFoliage(3, 3));
        put("white_ipe", createRoughFoliage(3, 3));
        put("douglas_fir", createRoughFoliage(3, 2));
        put("holly", createPlumeFoliage(5, 22, 1));
        put("elm", createRoughFoliage(4, 3));
        put("hazel", createRoughFoliage(4, 3));
        put("great_sallow", createRoughFoliage(5, 3));
        put("grandidiers_baobab", createRoughFoliage(3, 3));
        put("cork_oak", createRoughFoliage(3, 3));
        put("copper_beech", createRoughFoliage(4, 3));
        put("yew", createRoughFoliage(4, 3));
        put("kapok", createRoughFoliage(3, 3));
        put("sequoia", createRoughFoliage(3, 3));
        put("night_fuchsia", createRoughFoliage(4, 3));
        put("brazil_nut", createRoughFoliage(3, 3));
        put("purpleheart", createRoughFoliage(3, 3));
        put("purple_spiral", createRoughFoliage(2, 2));
        put("european_larch", createConiferFoliage(2, 3, 1));
        put("yellow_meranti", createRoughFoliage(3, 3));
        put("western_hemlock", createRoughFoliage(3, 3));
    }};
    private final Map<String, JsonElement> megaTrunkPlacers = new HashMap<>() {{
        put("default", createGiantTrunk(20, 11, 0));
        put("yew", createSpreadingTrunk(8, 3, 0, UniformInt.of(4, 6), UniformInt.of(3, 5)));
        put("aspen", createTaperedMegaTrunk(24, 6, 0, 2, UniformInt.of(0, 0), UniformInt.of(1, 1)));
        put("balsa", createSpreadingTrunk(13, 4, 0, UniformInt.of(4, 7), UniformInt.of(4, 7)));
        put("beech", createSpreadingTrunk(13, 3, 0, UniformInt.of(5, 8), UniformInt.of(4, 7)));
        put("black_cherry", createSpreadingTrunk(15, 4, 0, UniformInt.of(5, 8), UniformInt.of(5, 8)));
        put("black_ember", createSpreadingTrunk(14, 4, 0, UniformInt.of(6, 9), UniformInt.of(3, 5)));
        put("bull_pine", createWhorledTrunk(18, 4, 0, 0, 0.2F, UniformInt.of(2, 4)));
        put("cedar", createWhorledTrunk(40, 8, 0, 1, 0.7F, UniformInt.of(2, 4)));
        put("cempedak", createSpreadingTrunk(16, 4, 0, UniformInt.of(5, 8), UniformInt.of(5, 8)));
        put("ceylon_ebony", createSpreadingTrunk(16, 4, 0, UniformInt.of(6, 9), UniformInt.of(5, 8)));
        put("cocobolo", createSpreadingTrunk(20, 4, 0, UniformInt.of(8, 12), UniformInt.of(3, 5)));
        put("jackfruit", createWideTrunk(12, 4, 0, 2, 0.3F, UniformInt.of(4, 8)));
        put("iroko", createSpreadingTrunk(32, 8, 0, UniformInt.of(6, 9), UniformInt.of(4, 7)));
        put("mango", createWideTrunk(16, 4, 0, 1, 0.25F, UniformInt.of(4, 8)));
        put("lawson_cypress", createWideTrunk(40, 6, 0, 1, 0.9F, UniformInt.of(1, 2)));
        put("osage_orange", createSpreadingTrunk(14, 4, 0, UniformInt.of(5, 8), UniformInt.of(4, 7)));
        put("padauk", createSpreadingTrunk(24, 4, 0, UniformInt.of(6, 9), UniformInt.of(4, 7)));
        put("pandanus", createSpreadingTrunk(4, 2, 0, UniformInt.of(1, 3), UniformInt.of(2, 3)));
        put("pecan", createSpreadingTrunk(22, 4, 0, UniformInt.of(6, 9), UniformInt.of(4, 7)));
        put("pink_ivory", createSpreadingTrunk(28, 4, 0, UniformInt.of(6, 9), UniformInt.of(4, 7)));
        put("rainbow_gum", createFlutedTrunk(58, 10, 0, 2, 0.85F, UniformInt.of(4, 8), 9));
        put("rose_gum", createWideTrunk(48, 8, 0, 1, 0.6F, UniformInt.of(4, 8)));
        put("rosewood", createSpreadingTrunk(32, 4, 0, UniformInt.of(6, 9), UniformInt.of(4, 7)));
        put("old_fustic", createEmergentTrunk(64, 8, 0, 2, 0.45F, UniformInt.of(6, 9), UniformInt.of(7, 10)));
        put("sweet_chestnut", createWideTrunk(14, 4, 0, 3, 0.4F, UniformInt.of(4, 8)));
        put("black_locust", createSpreadingTrunk(14, 4, 0, UniformInt.of(4, 7), UniformInt.of(4, 7)));
        put("breadfruit", createWideTrunk(18, 6, 0, 3, 0.4F, UniformInt.of(4, 8)));
        put("ginkgo", createWideTrunk(14, 4, 0, 2, 0.3F, UniformInt.of(5, 8)));
        put("walnut", createSpreadingTrunk(12, 6, 0, UniformInt.of(4, 6), UniformInt.of(4, 7)));
        put("silver_fir", createConiferTrunk(58, 8, 0, 0.12F, 6));
        put("ipe", createSpreadingTrunk(27, 8, 0, UniformInt.of(5, 8), UniformInt.of(5, 9)));
        put("pink_ipe", createSpreadingTrunk(27, 8, 0, UniformInt.of(5, 8), UniformInt.of(5, 9)));
        put("purple_ipe", createSpreadingTrunk(27, 8, 0, UniformInt.of(5, 8), UniformInt.of(5, 9)));
        put("white_ipe", createSpreadingTrunk(27, 8, 0, UniformInt.of(5, 8), UniformInt.of(5, 9)));
        put("douglas_fir", createWideTrunk(98, 12, 0, 2));
        put("holly", createGiantTrunk(22, 4, 0));
        put("elm", createSpreadingTrunk(12, 4, 0, UniformInt.of(4, 7), UniformInt.of(5, 8)));
        put("hazel", createMultiStemTrunk(5, 2, 0, UniformInt.of(3, 5), UniformInt.of(2, 3)));
        put("great_sallow", createWideTrunk(8, 4, 0, 1));
        put("grandidiers_baobab", createWideTrunk(22, 6, 0, 2, 0.65F, UniformInt.of(6, 10)));
        put("cork_oak", createSpreadingTrunk(12, 4, 0, UniformInt.of(4, 7), UniformInt.of(5, 9)));
        put("copper_beech", createSpreadingTrunk(22, 4, 0, UniformInt.of(10, 14), UniformInt.of(5, 8)));
        put("kapok", createEmergentTrunk(54, 12, 0, 2, 0.45F, UniformInt.of(6, 8), UniformInt.of(7, 9)));
        put("sequoia", createWideTrunk(30, 6, 0, 2, 0.35F, UniformInt.of(3, 6)));
        put("night_fuchsia", createStuddedTrunk(8, 2, 0, UniformInt.of(3, 5), UniformInt.of(3, 5)));
        put("brazil_nut", createEmergentTrunk(26, 6, 0, 2, 0.4F, UniformInt.of(5, 7), UniformInt.of(6, 8)));
        put("purpleheart", createEmergentTrunk(30, 10, 0, 2, 0.5F, UniformInt.of(5, 7), UniformInt.of(5, 7)));
        put("purple_spiral", createSpiralTrunk(7, 2, 0, UniformInt.of(5, 7), UniformInt.of(6, 8)));
        put("european_larch", createConiferTrunk(26, 4, 0, 0.08F, 9));
        put("yellow_meranti", createEmergentTrunk(72, 14, 0, 2, 0.4F, UniformInt.of(6, 8), UniformInt.of(7, 10)));
        put("western_hemlock", createWideTrunk(56, 12, 0, 2, 0.4F, UniformInt.of(5, 9)));
    }};
    // larger (3x3) mega tier for trees that also have a 2x2 mega (e.g. yew); falls back to the mega "default"
    private final Map<String, JsonElement> largeMegaFoliagePlacers = new HashMap<>() {{
        put("yew", createRoughFoliage(5, 4));
        put("sequoia", createRoughFoliage(3, 4));
    }};
    private final Map<String, JsonElement> largeMegaTrunkPlacers = new HashMap<>() {{
        put("yew", createWideTrunk(13, 3, 0, 1, 0.2F, UniformInt.of(3, 6)));
        put("sequoia", createWideTrunk(100, 26, 0, 7, 0.4F, UniformInt.of(6, 12)));
    }};
}
