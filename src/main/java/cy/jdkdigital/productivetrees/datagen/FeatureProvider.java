package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafReplacerDecorator;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            placedFeatures.put(treeObject.getId(), getPlacedFeature(treeObject));
            if (!treeObject.getId().getPath().equals("ysabella_purpurea")) {
                configuredFeatures.put(treeObject.getId(), getConfiguredFeature(treeObject));
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
            JsonElement placement = PlacementModifier.CODEC.encodeStart(JsonOps.INSTANCE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(treeObject.getSaplingBlock().get().defaultBlockState(), Vec3i.ZERO))).getOrThrow(false, ProductiveTrees.LOGGER::error);
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
                decoratorArray.add(fruitDecorators.containsKey(name) ? fruitDecorators.get(name).apply(SimpleStateProvider.simple(treeObject.getFruitBlock().get())) : fruitDecorators.get("default").apply(SimpleStateProvider.simple(treeObject.getFruitBlock().get())));
            }
            config.add("decorators", decoratorArray);
            // dirt_provider
            config.add("dirt_provider", DIRT_PROVIDER);
            // foliage_placer
            config.add("foliage_placer", foliagePlacers.containsKey(name) ? foliagePlacers.get(name) : foliagePlacers.get("default"));
            // foliage_provider
            config.add("foliage_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(treeObject.getLeafBlock().get())).getOrThrow(false, ProductiveTrees.LOGGER::error));
            // minimum_size
            config.add("minimum_size", FeatureSize.CODEC.encodeStart(JsonOps.INSTANCE, new TwoLayersFeatureSize(1, 0, 1)).getOrThrow(false, ProductiveTrees.LOGGER::error));
            // trunk_placer
            config.add("trunk_placer", trunkPlacers.containsKey(name) ? trunkPlacers.get(name) : trunkPlacers.get("default"));
            // trunk_provider
            config.add("trunk_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(treeObject.getLogBlock().get())).getOrThrow(false, ProductiveTrees.LOGGER::error));

            config.addProperty("force_dirt", false);
            config.addProperty("ignore_vines", true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "minecraft:tree");
            jsonObject.add("config", config);
            return jsonObject;
        };
    }

    private JsonElement createFoliage(int radius, int height) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), height)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private JsonElement createTrunk(int height, int randA, int randB) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new StraightTrunkPlacer(height, randA, randB)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private final JsonElement DIRT_PROVIDER = BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(Blocks.DIRT)).getOrThrow(false, ProductiveTrees.LOGGER::error);

    private final Function<SimpleStateProvider, JsonElement> MEDIUM_FRUIT_DISTRIBUTION = (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.4f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    private final Map<String, Function<SimpleStateProvider, JsonElement>> fruitDecorators = new HashMap<>() {{
        put("default", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.6f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error));
        put("almond", MEDIUM_FRUIT_DISTRIBUTION);
        put("avocado", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.3f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error));
        put("banana", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafPlacerDecorator(0.2f, 2, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error));
        put("beech", MEDIUM_FRUIT_DISTRIBUTION);
        put("butternut", MEDIUM_FRUIT_DISTRIBUTION);
        put("hazel", MEDIUM_FRUIT_DISTRIBUTION);
        put("pecan", MEDIUM_FRUIT_DISTRIBUTION);
        put("pistachio", MEDIUM_FRUIT_DISTRIBUTION);
        put("wallnut", MEDIUM_FRUIT_DISTRIBUTION);
    }};
    private final JsonElement BUSH_FOLIAGE = FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    private final Map<String, JsonElement> foliagePlacers = new HashMap<>() {{
        put("default", createFoliage(2, 3));
        put("avocado", createFoliage(4, 3));
        put("banana", createFoliage(3, 1));
        put("blackberry", BUSH_FOLIAGE);
        put("blackcurrant", BUSH_FOLIAGE);
        put("blueberry", BUSH_FOLIAGE);
        put("cranberry", BUSH_FOLIAGE);
        put("elderberry", BUSH_FOLIAGE);
        put("golden_raspberry", BUSH_FOLIAGE);
        put("gooseberry", BUSH_FOLIAGE);
        put("juniper", BUSH_FOLIAGE);
        put("raspberry", BUSH_FOLIAGE);
        put("redcurrant", BUSH_FOLIAGE);
    }};
    private final JsonElement BUSH_TRUNK = createTrunk(1, 0, 0);
    private final Map<String, JsonElement> trunkPlacers = new HashMap<>() {{
        put("default", createTrunk(4, 2, 0));
        put("avocado", createTrunk(9, 10, 0));
        put("banana", createTrunk(5, 6, 0));
        put("beech", createTrunk(20, 10, 0));
        put("blackberry", BUSH_TRUNK);
        put("blackcurrant", BUSH_TRUNK);
        put("blueberry", BUSH_TRUNK);
        put("cranberry", BUSH_TRUNK);
        put("elderberry", BUSH_TRUNK);
        put("golden_raspberry", BUSH_TRUNK);
        put("gooseberry", BUSH_TRUNK);
        put("juniper", BUSH_TRUNK);
        put("raspberry", BUSH_TRUNK);
        put("redcurrant", BUSH_TRUNK);
    }};
}
