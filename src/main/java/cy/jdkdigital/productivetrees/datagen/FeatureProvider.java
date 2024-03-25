package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafReplacerDecorator;
import cy.jdkdigital.productivetrees.feature.trunkplacers.CenteredUpwardsBranchingTrunkPlacer;
import cy.jdkdigital.productivetrees.feature.trunkplacers.UnlimitedStraightTrunkPlacer;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.registries.ForgeRegistries;

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

    private static Block getBlock(ResourceLocation tree, String name) {
        return ForgeRegistries.BLOCKS.getValue(tree.withPath(p -> p + name));
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
            if (!TreeUtil.isSpecialTree(treeObject.getId())) {
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
            JsonElement placement = PlacementModifier.CODEC.encodeStart(JsonOps.INSTANCE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(TreeUtil.getBlock(treeObject.getId(), "_sapling").defaultBlockState(), Vec3i.ZERO))).getOrThrow(false, ProductiveTrees.LOGGER::error);
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
            config.add("decorators", decoratorArray);
            // dirt_provider
            config.add("dirt_provider", DIRT_PROVIDER);
            // foliage_placer
            config.add("foliage_placer", foliagePlacers.containsKey(name) ? foliagePlacers.get(name) : foliagePlacers.get("default"));
            // foliage_provider
            config.add("foliage_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_leaves"))).getOrThrow(false, ProductiveTrees.LOGGER::error));
//            config.add("foliage_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(Blocks.AIR)).getOrThrow(false, ProductiveTrees.LOGGER::error));
            // minimum_size
            config.add("minimum_size", FeatureSize.CODEC.encodeStart(JsonOps.INSTANCE, new TwoLayersFeatureSize(1, 0, 1)).getOrThrow(false, ProductiveTrees.LOGGER::error));
            // trunk_placer
            config.add("trunk_placer", trunkPlacers.containsKey(name) ? trunkPlacers.get(name) : trunkPlacers.get("default"));
            // trunk_provider
            config.add("trunk_provider", BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(TreeUtil.getBlock(treeObject.getId(), "_log"))).getOrThrow(false, ProductiveTrees.LOGGER::error));

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

    private JsonElement createSpruceFoliage(int radius, int height, int offset) {
        return FoliagePlacer.CODEC.encodeStart(JsonOps.INSTANCE, new SpruceFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(offset), ConstantInt.of(height))).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private JsonElement createStraightTrunk(int height, int randA, int randB) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new UnlimitedStraightTrunkPlacer(height, randA, randB)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private JsonElement createBranchingTrunk(int height, int randA, int randB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength) {
        return TrunkPlacer.CODEC.encodeStart(JsonOps.INSTANCE, new CenteredUpwardsBranchingTrunkPlacer(height, randA, randB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private final JsonElement DIRT_PROVIDER = BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE, SimpleStateProvider.simple(Blocks.DIRT)).getOrThrow(false, ProductiveTrees.LOGGER::error);

    private Function<SimpleStateProvider, JsonElement> createDanglerFruitProvider(float density, int maxFruits) {
        return (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafPlacerDecorator(density, maxFruits, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    }

    private final Function<SimpleStateProvider, JsonElement> MEDIUM_FRUIT_DISTRIBUTION = (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.4f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error);
    private final Map<String, Function<SimpleStateProvider, JsonElement>> fruitDecorators = new HashMap<>() {{
        put("default", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.6f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error));
        put("almond", MEDIUM_FRUIT_DISTRIBUTION);
        put("avocado", (fruitProvider) -> TreeDecorator.CODEC.encodeStart(JsonOps.INSTANCE, new FruitLeafReplacerDecorator(0.3f, fruitProvider)).getOrThrow(false, ProductiveTrees.LOGGER::error));
        put("banana", createDanglerFruitProvider(0.4f, 4));
        put("baobab", createDanglerFruitProvider(0.3f, 6));
        put("breadfruit", createDanglerFruitProvider(0.2f, 5));
        put("cempedak", createDanglerFruitProvider(0.2f, 3));
        put("coconut", createDanglerFruitProvider(0.2f, 4));
        put("copoazu", createDanglerFruitProvider(0.2f, 5));
        put("jackfruit", createDanglerFruitProvider(0.2f, 4));
        put("pandanus", createDanglerFruitProvider(0.2f, 4));
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
        put("alder", createFoliage(4, 5));
        put("avocado", createFoliage(4, 3));
        put("banana", createFoliage(3, 1));
        put("balsam_fir", createSpruceFoliage(3, 1, 7));
        put("bull_pine", createSpruceFoliage(2, 3, 10));
        put("red_banana", createFoliage(3, 1));
        put("plantain", createFoliage(3, 1));
        put("asai_palm", createFoliage(3, 2));
        put("date_palm", createFoliage(4, 2));
        put("elderberry", createFoliage(4, 4));
        put("juniper", createFoliage(2, 6));


        put("allspice", createFoliage(3, 3));
        put("almond", createFoliage(3, 3));
        put("apricot", createFoliage(3, 3));
        put("aquilaria", createFoliage(3, 3));
        put("ash", createFoliage(3, 3));
        put("aspen", createFoliage(3, 3));
        put("balsa", createFoliage(3, 3));
        put("beech", createFoliage(3, 3));
        put("beliy_naliv_apple", createFoliage(3, 3));
        put("blackthorn", createFoliage(3, 3));
        put("black_cherry", createFoliage(3, 3));
        put("black_locust", createFoliage(3, 3));
        put("blue_mahoe", createFoliage(3, 3));
        put("boxwood", createFoliage(3, 3));
        put("brazilwood", createFoliage(3, 3));
        put("brazil_nut", createFoliage(3, 3));
        put("breadfruit", createFoliage(3, 3));
        put("buddhas_hand", createFoliage(3, 3));
        put("butternut", createFoliage(3, 3));
        put("cacao", createFoliage(3, 3));
        put("candlenut", createFoliage(3, 3));
        put("carob", createFoliage(3, 3));
        put("cashew", createFoliage(3, 3));
        put("cave_dweller", createFoliage(3, 3));
        put("cedar", createFoliage(3, 3));
        put("cempedak", createFoliage(3, 3));
        put("ceylon_ebony", createFoliage(3, 3));
        put("cherry_plum", createFoliage(3, 3));
        put("cinnamon", createFoliage(3, 3));
        put("citron", createFoliage(3, 3));
        put("clove", createFoliage(3, 3));
        put("cocobolo", createFoliage(3, 3));
        put("coconut", createFoliage(3, 3));
        put("coffea", createFoliage(3, 3));
        put("copoazu", createFoliage(3, 3));
        put("copper_beech", createFoliage(3, 3));
        put("cork_oak", createFoliage(3, 3));
        put("cultivated_pear", createFoliage(3, 3));
        put("dogwood", createFoliage(6, 3));
        put("douglas_fir", createFoliage(3, 3));
        put("elm", createFoliage(6, 7));
        put("european_larch", createFoliage(3, 3));
        put("finger_lime", createFoliage(3, 3));
        put("flowering_crabapple", createFoliage(3, 3));
        put("foggy_blast", createFoliage(3, 3));
        put("ginkgo", createFoliage(3, 3));
        put("golden_delicious_apple", createFoliage(3, 3));
        put("grandidiers_baobab", createFoliage(3, 3));
        put("granny_smith_apple", createFoliage(3, 3));
        put("grapefruit", createFoliage(3, 3));
        put("great_sallow", createFoliage(3, 3));
        put("greenheart", createFoliage(3, 3));
        put("hawthorn", createFoliage(3, 3));
        put("hazel", createFoliage(3, 3));
        put("holly", createFoliage(3, 3));
        put("hornbeam", createFoliage(3, 3));
        put("ipe", createFoliage(3, 3));
        put("iroko", createFoliage(3, 3));
        put("jackfruit", createFoliage(3, 3));
        put("kapok", createFoliage(3, 3));
        put("key_lime", createFoliage(3, 3));
        put("kumquat", createFoliage(3, 3));
        put("lawson_cypress", createSpruceFoliage(3, 4, 10));
        put("lemon", createFoliage(3, 3));
        put("lime", createFoliage(3, 3));
        put("loblolly_pine", createFoliage(3, 3));
        put("logwood", createFoliage(3, 3));
        put("mahogany", createFoliage(3, 3));
        put("mandarin", createFoliage(3, 3));
        put("mango", createFoliage(3, 3));
        put("monkey_puzzle", createFoliage(3, 3));
        put("moonlight_magic_crepe_myrtle", createFoliage(3, 3));
        put("myrtle_ebony", createFoliage(3, 3));
        put("nectarine", createFoliage(3, 3));
        put("nutmeg", createFoliage(3, 3));
        put("old_fustic", createFoliage(3, 3));
        put("olive", createFoliage(3, 3));
        put("orange", createFoliage(3, 3));
        put("osange_orange", createFoliage(3, 3));
        put("padauk", createFoliage(3, 3));
        put("pandanus", createFoliage(3, 3));
        put("papaya", createFoliage(3, 3));
        put("peach", createFoliage(3, 3));
        put("pecan", createFoliage(3, 3));
        put("persimmon", createFoliage(3, 3));
        put("pink_ivory", createFoliage(3, 3));
        put("pistachio", createFoliage(3, 3));
        put("plum", createFoliage(3, 3));
        put("pomegranate", createFoliage(3, 3));
        put("pomelo", createFoliage(3, 3));
        put("prairie_crabapple", createFoliage(3, 3));
        put("purpleheart", createFoliage(3, 3));
        put("purple_blackthorn", createFoliage(3, 3));
        put("purple_crepe_myrtle", createFoliage(3, 3));
        put("rainbow_gum", createFoliage(3, 3));
        put("red_crepe_myrtle", createFoliage(3, 3));
        put("red_delicious_apple", createFoliage(3, 3));
        put("red_maple", createFoliage(3, 3));
        put("rippling_willow", createFoliage(3, 3));
        put("rosewood", createFoliage(3, 3));
        put("rose_gum", createFoliage(3, 3));
        put("rowan", createFoliage(3, 3));
        put("rubber_tree", createFoliage(3, 3));
        put("salak", createFoliage(3, 3));
        put("sandalwood", createFoliage(3, 3));
        put("sand_pear", createFoliage(3, 3));
        put("satsuma", createFoliage(3, 3));
        put("sequoia", createFoliage(3, 3));
        put("silver_fir", createFoliage(3, 3));
        put("silver_lime", createFoliage(3, 3));
        put("socotra_dragon", createFoliage(3, 3));
        put("soursop", createFoliage(3, 3));
        put("sour_cherry", createFoliage(3, 3));
        put("star_anise", createFoliage(3, 3));
        put("star_fruit", createFoliage(3, 3));
        put("sugar_apple", createFoliage(3, 3));
        put("sugar_maple", createFoliage(3, 3));
        put("swamp_gum", createFoliage(3, 3));
        put("sweetgum", createFoliage(3, 3));
        put("sweet_chestnut", createFoliage(3, 3));
        put("sweet_crabapple", createFoliage(3, 3));
        put("sycamore_fig", createFoliage(3, 3));
        put("tangerine", createFoliage(3, 3));
        put("teak", createFoliage(3, 3));
        put("tuscarora_crepe_myrtle", createFoliage(3, 3));
        put("blue_yonder", createFoliage(3, 3));
        put("walnut", createFoliage(3, 3));
        put("wenge", createFoliage(3, 3));
        put("western_hemlock", createFoliage(3, 3));
        put("whitebeam", createFoliage(3, 3));
        put("white_poplar", createFoliage(3, 3));
        put("white_willow", createFoliage(3, 3));
        put("wild_cherry", createFoliage(3, 3));
        put("yellow_meranti", createFoliage(3, 3));
        put("yew", createFoliage(3, 3));
        put("zebrano", createFoliage(3, 3));
    }};
    private final Map<String, JsonElement> trunkPlacers = new HashMap<>() {{
        put("default", createStraightTrunk(10, 3, 0));
//        put("alder", createBranchingTrunk(24, 2, 2, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1)));
//        put("aspen", createStraightTrunk(25, 15, 0));
        put("avocado", createStraightTrunk(9, 10, 0));
        put("banana", createStraightTrunk(5, 6, 0));
//        put("balsa", createBranchingTrunk(20, 7, 2, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1)));
        put("balsam_fir", createStraightTrunk(14, 6, 0));
//        put("beech", createStraightTrunk(25, 10, 0));
        put("black_locust", createStraightTrunk(12, 18, 0));
        put("blue_mahoe", createStraightTrunk(15, 5, 0));
        put("boxwood", createStraightTrunk(3, 6, 0));
        put("brazilwood", createStraightTrunk(12, 4, 0));
//        put("brazil_nut", createStraightTrunk(30, 20, 0));
//        put("bull_pine", createStraightTrunk(33, 24, 0));
//        put("butternut", createStraightTrunk(15, 5, 0));
//        put("candlenut", createStraightTrunk(25, 5, 0));
        put("cashew", createStraightTrunk(6, 8, 0));
        put("cinnamon", createStraightTrunk(10, 5, 0));
        put("clove", createStraightTrunk(8, 4, 0));
        put("cocobolo", createStraightTrunk(20, 5, 0));
        put("coconut", createStraightTrunk(15, 10, 0));
        put("coffea", createStraightTrunk(9, 3, 0));
        put("red_banana", createStraightTrunk(5, 6, 0));
        put("plantain", createStraightTrunk(5, 6, 0));
        put("asai_palm", createStraightTrunk(9, 5, 2));
        put("date_palm", createStraightTrunk(8, 4, 2));
        put("copoazu", createStraightTrunk(8, 2, 1));
        put("elderberry", createStraightTrunk(5, 0, 0));
        put("juniper", createStraightTrunk(4, 0, 0));
        put("allspice", createStraightTrunk(4, 2, 0));
        put("almond", createStraightTrunk(8, 2, 0));
        put("apricot", createStraightTrunk(8, 2, 0));
        put("aquilaria", createStraightTrunk(6, 14, 0));
        put("ash", createStraightTrunk(12, 6, 0)); // mega is 43
        put("beliy_naliv_apple", createStraightTrunk(6, 2, 0));
        put("blackthorn", createStraightTrunk(5, 1, 1));
//        put("black_cherry", createStraightTrunk(15, 9, 0));
//        put("breadfruit", createStraightTrunk(20, 6, 0));
        put("buddhas_hand", createStraightTrunk(8, 2, 0));
        put("cacao", createStraightTrunk(6, 6, 0));
//        put("carob", createStraightTrunk(10, 5, 1));
//        put("cedar", createStraightTrunk(20, 20, 0));
//        put("cempedak", createStraightTrunk(12, 8, 2));
        put("ceylon_ebony", createStraightTrunk(10, 15, 0));
        put("cherry_plum", createStraightTrunk(8, 4, 0));
        put("citron", createStraightTrunk(4, 2, 0));
//        put("copper_beech", createStraightTrunk(25, 10, 0));
//        put("cork_oak", createStraightTrunk(10, 5, 0));
        put("cultivated_pear", createStraightTrunk(8, 2, 0));
        put("dogwood", createStraightTrunk(6, 5, 0));
//        put("douglas_fir", createStraightTrunk(20, 10, 0)); // mega is 100
//        put("elm", createStraightTrunk(15, 10, 0));
//        put("european_larch", createStraightTrunk(25, 20, 0)); // mega is 53
        put("finger_lime", createStraightTrunk(4, 3, 0));
        put("flowering_crabapple", createStraightTrunk(8, 4, 0));
//        put("ginkgo", createStraightTrunk(20, 15, 0));
        put("golden_delicious_apple", createStraightTrunk(7, 4, 0));
//        put("grandidiers_baobab", createStraightTrunk(25, 5, 0));
        put("granny_smith_apple", createStraightTrunk(7, 4, 0));
        put("grapefruit", createStraightTrunk(8, 2, 0));
        put("great_sallow", createStraightTrunk(4, 2, 0));
//        put("greenheart", createStraightTrunk(15, 15, 0));
        put("hawthorn", createStraightTrunk(8, 2, 0));
        put("hazel", createStraightTrunk(3, 5, 0));
        put("holly", createStraightTrunk(8, 15, 0));
        put("hornbeam", createStraightTrunk(15, 10, 0));
        put("ipe", createStraightTrunk(8, 5, 0));
//        put("iroko", createStraightTrunk(20, 30, 0));
        put("jackfruit", createStraightTrunk(9, 12, 0));
//        put("kapok", createStraightTrunk(15, 7, 0)); // mega is 60
        put("key_lime", createStraightTrunk(6, 2, 0));
        put("kumquat", createStraightTrunk(8, 2, 0));
        put("lawson_cypress", createStraightTrunk(40, 20, 0)); // mega is 60
        put("lemon", createStraightTrunk(6, 2, 0));
        put("lime", createStraightTrunk(6, 2, 0));
        put("loblolly_pine", createStraightTrunk(25, 10, 0)); // mega is 50
        put("logwood", createStraightTrunk(5, 10, 0));
//        put("mahogany", createStraightTrunk(30, 5, 0));
        put("mandarin", createStraightTrunk(8, 2, 0));
//        put("mango", createStraightTrunk(15, 15, 0));
        put("monkey_puzzle", createStraightTrunk(8, 2, 0));
        put("moonlight_magic_crepe_myrtle", createStraightTrunk(8, 2, 0));
        put("myrtle_ebony", createStraightTrunk(6, 34, 0));
        put("nectarine", createStraightTrunk(7, 2, 0));
        put("nutmeg", createStraightTrunk(5, 10, 0));
        put("old_fustic", createStraightTrunk(5, 20, 0));
        put("olive", createStraightTrunk(5, 2, 0));
        put("orange", createStraightTrunk(7, 2, 0));
        put("osange_orange", createStraightTrunk(8, 2, 0));
//        put("padauk", createStraightTrunk(27, 7, 0));
        put("pandanus", createStraightTrunk(4, 10, 0));
        put("papaya", createStraightTrunk(5, 5, 0));
        put("peach", createStraightTrunk(8, 2, 0));
//        put("pecan", createStraightTrunk(20, 20, 0));
        put("persimmon", createStraightTrunk(8, 2, 0));
        put("pink_ivory", createStraightTrunk(8, 2, 0));
        put("pistachio", createStraightTrunk(8, 2, 0));
        put("plum", createStraightTrunk(8, 2, 0));
        put("pomegranate", createStraightTrunk(8, 2, 0));
        put("pomelo", createStraightTrunk(8, 2, 0));
        put("prairie_crabapple", createStraightTrunk(8, 2, 0));
//        put("purpleheart", createStraightTrunk(40, 10, 0));
        put("purple_blackthorn", createStraightTrunk(5, 1, 1));
        put("purple_crepe_myrtle", createStraightTrunk(9, 2, 0));
//        put("rainbow_gum", createStraightTrunk(60, 15, 10));
        put("red_crepe_myrtle", createStraightTrunk(8, 2, 0));
        put("red_delicious_apple", createStraightTrunk(7, 4, 0));
//        put("red_maple", createStraightTrunk(27, 11, 0));
//        put("rosewood", createStraightTrunk(15, 10, 0));
//        put("rose_gum", createStraightTrunk(40, 10, 0)); // mega is 70
        put("rowan", createStraightTrunk(5, 10, 0));
        put("rubber_tree", createStraightTrunk(38, 5, 0));
        put("salak", createStraightTrunk(1, 2, 0));
        put("sandalwood", createStraightTrunk(4, 8, 0));
        put("sand_pear", createStraightTrunk(8, 2, 0));
        put("satsuma", createStraightTrunk(7, 2, 0));
//        put("sequoia", createStraightTrunk(40, 20, 0)); // mega is 80-100
//        put("silver_fir", createStraightTrunk(30, 20, 0));
//        put("silver_lime", createStraightTrunk(20, 15, 0));
        put("socotra_dragon", createStraightTrunk(9, 3, 0));
        put("soursop", createStraightTrunk(7, 2, 0));
        put("sour_cherry", createStraightTrunk(8, 2, 0));
        put("star_anise", createStraightTrunk(8, 2, 0));
        put("star_fruit", createStraightTrunk(8, 2, 0));
        put("sugar_apple", createStraightTrunk(8, 2, 0));
        put("sugar_maple", createStraightTrunk(10, 2, 0));
//        put("swamp_gum", createStraightTrunk(20, 5, 0));
//        put("sweetgum", createStraightTrunk(15, 5, 0)); // 45 mega
        put("sweet_chestnut", createStraightTrunk(20, 15, 0));
        put("sweet_crabapple", createStraightTrunk(8, 2, 0));
        put("sycamore_fig", createStraightTrunk(8, 2, 0));
        put("tangerine", createStraightTrunk(8, 2, 0));
//        put("teak", createStraightTrunk(30, 10, 0));
        put("tuscarora_crepe_myrtle", createStraightTrunk(8, 2, 0));
//        put("walnut", createStraightTrunk(25, 10, 0));
//        put("wenge", createStraightTrunk(20, 10, 0));
//        put("western_hemlock", createStraightTrunk(40, 10, 0)); // mega 70-80
        put("whitebeam", createStraightTrunk(13, 7, 0));
        put("white_poplar", createStraightTrunk(15, 15, 0));
        put("white_willow", createStraightTrunk(10, 20, 0));
        put("wild_cherry", createStraightTrunk(5, 13, 0));
        put("yellow_meranti", createStraightTrunk(40, 30, 0)); // mega is 100
        put("yew", createStraightTrunk(10, 10, 0));
//        put("zebrano", createStraightTrunk(30, 10, 0));
    }};
}
