package cy.jdkdigital.productivetrees.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Grows a tree's configured feature on an empty platform and checks its shape metrics against the
 * bands bracketed by the original exported structures (tree_metrics.json). Because the features are
 * intentionally random, the check is a fuzzy per-metric tolerance, not a block-for-block match.
 * Prototype: a handful of small single-structure trees.
 */
@GameTestHolder(ProductiveTrees.MODID)
@PrefixGameTestTemplate(false)
public class TreeFeatureTests
{
    private static final String TEMPLATE = ProductiveTrees.MODID + ":test_platform";
    private static JsonObject metrics;

    // one grow test per tree that has reference bands (regular features that fit a test cell)
    @GameTestGenerator
    public static List<TestFunction> generateTreeTests() {
        List<TestFunction> tests = new ArrayList<>();
        for (String tree : bands().keySet()) {
            tests.add(new TestFunction("trees", tree, TEMPLATE, 100, 0L, true, helper -> growAndCheck(helper, tree)));
        }
        tests.add(new TestFunction("trees", "template_tree_protected_blocks", TEMPLATE, 100, 0L, true,
                TreeFeatureTests::templateTreePreservesProtectedBlocks));
        return tests;
    }

    private static void templateTreePreservesProtectedBlocks(GameTestHelper helper) {
        String tree = "blue_yonder";
        ServerLevel level = helper.getLevel();
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, tree));
        var holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(key);
        if (holder.isEmpty()) {
            helper.fail("no configured feature productivetrees:" + tree);
            return;
        }
        ConfiguredFeature<?, ?> feature = holder.get().value();
        BlockPos origin = helper.absolutePos(new BlockPos(CELL, 1, CELL));
        long seed = 0xB10E_70ADL;

        clearCell(level, origin);
        if (!feature.place(level, level.getChunkSource().getGenerator(), RandomSource.create(seed), origin)) {
            helper.fail(tree + ": unobstructed feature.place() false");
            return;
        }
        BlockPos collisionPos = findTreeBlock(level, origin, tree);
        if (collisionPos == null) {
            helper.fail(tree + ": unobstructed template placed no tree blocks");
            return;
        }

        for (Block obstruction : List.of(Blocks.BEDROCK, Blocks.REINFORCED_DEEPSLATE, Blocks.BARRIER, Blocks.STONE)) {
            clearCell(level, origin);
            level.setBlock(collisionPos, obstruction.defaultBlockState(), 2);
            if (!feature.place(level, level.getChunkSource().getGenerator(), RandomSource.create(seed), origin)) {
                helper.fail(tree + ": obstructed feature.place() false for " + BuiltInRegistries.BLOCK.getKey(obstruction));
                return;
            }
            if (!level.getBlockState(collisionPos).is(obstruction)) {
                helper.fail(BuiltInRegistries.BLOCK.getKey(obstruction) + " was replaced by template tree at " + collisionPos);
                return;
            }
            if (findTreeBlock(level, origin, tree) == null) {
                helper.fail(tree + ": tree did not grow around " + BuiltInRegistries.BLOCK.getKey(obstruction));
                return;
            }
        }
        helper.succeed();
    }

    private static final int SAMPLES = 5;
    private static final int CELL = 10;

    private static void growAndCheck(GameTestHelper helper, String tree) {
        JsonObject band = bands().getAsJsonObject(tree);
        if (band == null) {
            helper.fail("no reference metrics for " + tree + " in tree_metrics.json");
            return;
        }
        ServerLevel level = helper.getLevel();
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, tree));
        var holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(key);
        if (holder.isEmpty()) {
            helper.fail("no configured feature productivetrees:" + tree);
            return;
        }
        ConfiguredFeature<?, ?> feature = holder.get().value();
        BlockPos origin = helper.absolutePos(new BlockPos(CELL, 1, CELL));

        // grow several times and take the median of each metric, so normal growth variance doesn't flake the gate.
        // seed each grow deterministically (per tree + sample) so the result is reproducible across test runs
        int[] heights = new int[SAMPLES];
        int[] radii = new int[SAMPLES];
        int[] leaves = new int[SAMPLES];
        for (int s = 0; s < SAMPLES; ++s) {
            RandomSource random = RandomSource.create(tree.hashCode() * 31L + s);
            clearCell(level, origin);
            if (!feature.place(level, level.getChunkSource().getGenerator(), random, origin)) {
                helper.fail(tree + ": feature.place() false; below=" + id(level, origin.below()));
                return;
            }
            int[] m = scan(level, origin, tree);
            if (m == null) {
                helper.fail(tree + ": no logs placed");
                return;
            }
            heights[s] = m[0];
            radii[s] = m[1];
            leaves[s] = m[2];
        }
        int height = median(heights);
        int crownRadius = median(radii);
        int leafCount = median(leaves);

        int[] h = range(band, "height");
        int[] r = range(band, "crown_radius");
        int[] lc = range(band, "leaf_count");
        StringBuilder fails = new StringBuilder();
        if (height < h[0] - 1 || height > h[1] + 1) {
            fails.append(String.format(" height %d outside [%d,%d];", height, h[0], h[1]));
        }
        if (crownRadius < r[0] - 1 || crownRadius > r[1] + 1) {
            fails.append(String.format(" crown radius %d outside [%d,%d];", crownRadius, r[0], r[1]));
        }
        if (fails.length() > 0) {
            helper.fail(tree + " (median of " + SAMPLES + "):" + fails + " (leaf count " + leafCount + " ref [" + lc[0] + "," + lc[1] + "])");
        } else {
            helper.succeed();
        }
    }

    /** Clears the cell back to air with a fresh dirt footing so each sample grows from the same blank slate. */
    private static void clearCell(ServerLevel level, BlockPos origin) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -CELL; dx <= CELL; ++dx) {
            for (int dz = -CELL; dz <= CELL; ++dz) {
                for (int dy = 0; dy <= 26; ++dy) {
                    level.setBlock(cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz), Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                level.setBlock(origin.offset(dx, -1, dz), Blocks.DIRT.defaultBlockState(), 2);
            }
        }
    }

    /** Reduces the grown tree to {height, crownRadius, leafCount}, or null if no logs were placed. */
    private static int[] scan(ServerLevel level, BlockPos origin, String tree) {
        int leafCount = 0;
        int crownRadius = 0;
        int topLog = origin.getY() - 1;
        boolean anyLog = false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -CELL; dx <= CELL; ++dx) {
            for (int dz = -CELL; dz <= CELL; ++dz) {
                for (int dy = 0; dy <= 26; ++dy) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(cursor).getBlock());
                    if (!blockId.getNamespace().equals(ProductiveTrees.MODID)) {
                        continue;
                    }
                    String path = blockId.getPath();
                    if (path.equals(tree + "_log") || path.equals(tree + "_wood")) {
                        anyLog = true;
                        topLog = Math.max(topLog, cursor.getY());
                    } else if (path.equals(tree + "_leaves") || path.equals(tree + "_fruit")) {
                        ++leafCount;
                        crownRadius = Math.max(crownRadius, Math.max(Math.abs(dx), Math.abs(dz)));
                    }
                }
            }
        }
        return anyLog ? new int[]{topLog - origin.getY() + 1, crownRadius, leafCount} : null;
    }

    private static BlockPos findTreeBlock(ServerLevel level, BlockPos origin, String tree) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -CELL; dx <= CELL; ++dx) {
            for (int dz = -CELL; dz <= CELL; ++dz) {
                for (int dy = 0; dy <= 26; ++dy) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(cursor).getBlock());
                    if (id.getNamespace().equals(ProductiveTrees.MODID) && id.getPath().startsWith(tree + "_")) {
                        return cursor.immutable();
                    }
                }
            }
        }
        return null;
    }

    private static int median(int[] values) {
        int[] copy = values.clone();
        java.util.Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    private static String id(ServerLevel level, BlockPos pos) {
        return BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
    }

    private static int[] range(JsonObject band, String key) {
        JsonArray a = band.getAsJsonArray(key);
        return new int[]{a.get(0).getAsInt(), a.get(1).getAsInt()};
    }

    private static JsonObject bands() {
        if (metrics == null) {
            try (Reader reader = new InputStreamReader(TreeFeatureTests.class.getResourceAsStream("/tree_metrics.json"))) {
                metrics = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                throw new IllegalStateException("tree_metrics.json missing from resources", e);
            }
        }
        return metrics;
    }
}
