package cy.jdkdigital.productivetrees.gametest;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.entity.PollenSifterBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.SawmillBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.common.item.PollenItem;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * In-code gametest harness for the productivetrees machines and pollination mechanics.
 *
 * <p>{@link #init()} (called from the mod constructor) records each test body into
 * {@link TestFunctions}. The {@link RegisterGameTestsEvent} handler then registers a shared empty
 * environment plus one {@link FunctionGameTestInstance} per test, each pointing at its recorded
 * body via ResourceKey and running inside the {@code productivetrees:empty_7x7} structure.
 *
 * <p>{@link #onRegisterGameTests} is added as a listener on the mod event bus from the mod
 * constructor; {@link RegisterGameTestsEvent} is a mod-bus event.
 */
public final class ProductiveTreesGameTests
{
    private ProductiveTreesGameTests() {}

    private static final int DEFAULT_MAX_TICKS = 100;

    public static final Map<String, Integer> MAX_TICKS = new LinkedHashMap<>();

    private static final Identifier STRUCTURE = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "empty_7x7");

    private static boolean initialised;

    /** Records every test body. Idempotent; called from the mod constructor. */
    public static void init() {
        if (initialised) return;
        initialised = true;

        // Pollination — pollen-item path and bee path.
        register("pollination_pollen_item", ProductiveTreesGameTests::testPollinationPollenItem);
        register("pollination_bee_path", ProductiveTreesGameTests::testPollinationBeePath, 400);

        // Stripper — needs an axe to function.
        register("stripper_requires_axe", ProductiveTreesGameTests::testStripperRequiresAxe, 200);
        register("stripper_strips_log_with_axe", ProductiveTreesGameTests::testStripperStripsLogWithAxe, 200);

        // Pollen sifter — random 10% output, run many cycles.
        register("pollen_sifter_produces_pollen", ProductiveTreesGameTests::testPollenSifterProducesPollen, 800);

        // Sawmill — deterministic recipe processing + secondary output.
        register("sawmill_processes_log", ProductiveTreesGameTests::testSawmillProcessesLog, 400);

        // Speed upgrade — deterministic on the sawmill.
        register("sawmill_speed_upgrade_advances_faster", ProductiveTreesGameTests::testSawmillSpeedUpgrade);

        TestFunctions.init();
    }

    private static void register(String name, Consumer<GameTestHelper> body) {
        register(name, body, DEFAULT_MAX_TICKS);
    }

    private static void register(String name, Consumer<GameTestHelper> body, int maxTicks) {
        TestFunctions.register(name, body);
        MAX_TICKS.put(name, maxTicks);
    }

    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        // Ensure the bodies are recorded even if the mod constructor's init() ordering changes.
        init();

        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
                Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "empty_env"));

        for (Map.Entry<String, Integer> entry : MAX_TICKS.entrySet()) {
            String name = entry.getKey();
            int maxTicks = entry.getValue();
            Identifier id = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, name);
            TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
                    environment, STRUCTURE, maxTicks, 0, true, Rotation.NONE, false, 1, 1, false, 0);
            GameTestInstance instance = new FunctionGameTestInstance(TestFunctions.key(name), data);
            event.registerTest(id, instance);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private static Block leafA() {
        return TreeUtil.getBlock(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "beech"), "_leaves");
    }

    private static Block leafB() {
        return Blocks.BIRCH_LEAVES;
    }

    private static Block expectedSapling() {
        return TreeUtil.getBlock(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "alder"), "_sapling");
    }

    /**
     * Adds a recipe to the live {@link RecipeManager} if absent. The generated recipe JSON encodes
     * its outputs through {@code ItemStack.CODEC}, which throws "does not have components yet" during
     * the gametest-server datapack load, so the data-driven sawmill/pollination recipes never load.
     * Building the recipe instances directly and rebuilding the immutable {@link RecipeMap} sidesteps
     * the codec entirely, giving the recipe-driven tests a deterministic input.
     */
    private static <T extends Recipe<?>> void ensureRecipe(ServerLevel level, String name, T recipe) {
        RecipeManager manager = level.recipeAccess();
        ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, name));
        if (manager.byKey(id).isPresent()) {
            return;
        }
        List<RecipeHolder<?>> all = new ArrayList<>(manager.getRecipes());
        all.add(new RecipeHolder<>(id, recipe));
        manager.recipes = RecipeMap.create(all);
    }

    private static void ensurePollinationRecipe(ServerLevel level) {
        ensureRecipe(level, "gametest_alder", new TreePollinationRecipe(
                Ingredient.of(leafA()), Ingredient.of(leafB()), new ItemStackTemplate(expectedSapling().asItem()), 0.1f));
    }

    private static void ensureSawmillRecipe(ServerLevel level) {
        ensureRecipe(level, "gametest_oak_sawmill", new SawmillRecipe(
                Ingredient.of(Blocks.OAK_LOG),
                new ItemStackTemplate(Blocks.OAK_PLANKS.asItem(), 6),
                Optional.of(new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)),
                Optional.empty()));
    }

    // ── Pollination: pollen-item path (PollenItem.useOn) ─────────────────────────
    private static void testPollinationPollenItem(GameTestHelper helper) {
        BlockPos leafPos = new BlockPos(2, 2, 2);
        helper.setBlock(leafPos, leafA().defaultBlockState());

        ItemStack pollen = new ItemStack(TreeRegistrator.POLLEN.get());
        pollen.set(TreeRegistrator.POLLEN_BLOCK_COMPONENT, BuiltInRegistries.BLOCK.getKey(leafB()));

        ServerLevel level = helper.getLevel();
        ensurePollinationRecipe(level);
        BlockPos absPos = helper.absolutePos(leafPos);
        var fakePlayer = FakePlayerFactory.get(level, new GameProfile(TreeUtil.STRIPPER_UUID, "pollinator"));
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, pollen);

        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absPos), Direction.UP, absPos, false);
        ((PollenItem) TreeRegistrator.POLLEN.get()).useOn(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, hit));

        BlockState resultState = level.getBlockState(absPos);
        if (!resultState.is(TreeRegistrator.POLLINATED_LEAVES.get())) {
            helper.fail("Leaf block did not become pollinated_leaves (is " + idOf(resultState.getBlock()) + ")", leafPos);
            return;
        }

        PollinatedLeavesBlockEntity be = helper.getBlockEntity(leafPos, PollinatedLeavesBlockEntity.class);
        if (be.getResult().isEmpty()) {
            helper.fail("PollinatedLeavesBlockEntity has an empty result", leafPos);
            return;
        }
        if (!idOf(be.getResult().getItem()).getPath().endsWith("_sapling")) {
            helper.fail("Pollination result " + idOf(be.getResult().getItem()) + " is not a sapling", leafPos);
            return;
        }

        helper.succeed();
    }

    // ── Pollination: bee path (TreeUtil.pollinateLeaves) ─────────────────────────
    private static void testPollinationBeePath(GameTestHelper helper) {
        BlockPos center = new BlockPos(3, 2, 3);
        BlockPos posA = new BlockPos(2, 2, 3);
        BlockPos posB = new BlockPos(4, 2, 3);
        helper.setBlock(posA, leafA().defaultBlockState());
        helper.setBlock(posB, leafB().defaultBlockState());

        ServerLevel level = helper.getLevel();
        ensurePollinationRecipe(level);
        BlockPos absCenter = helper.absolutePos(center);
        BlockPos absA = helper.absolutePos(posA);
        BlockPos absB = helper.absolutePos(posB);

        // The recipe chance is 0.1, so one call rarely pollinates; drive the same code path the
        // HONEY_DELIVERED handler uses repeatedly until one of the two leaves converts. With chance
        // 0.1 the probability of zero successes across this many calls is negligible.
        helper.startSequence()
                .thenWaitUntil(() -> {
                    TreeUtil.pollinateLeaves(level, absCenter, 4, false, new ArrayList<>());
                    boolean aPollinated = level.getBlockState(absA).is(TreeRegistrator.POLLINATED_LEAVES.get());
                    boolean bPollinated = level.getBlockState(absB).is(TreeRegistrator.POLLINATED_LEAVES.get());
                    if (!aPollinated && !bPollinated) {
                        throw helper.assertionException(center, "Neither leaf pollinated yet");
                    }
                    BlockPos pollinated = aPollinated ? absA : absB;
                    if (!(level.getBlockEntity(pollinated) instanceof PollinatedLeavesBlockEntity be) || be.getResult().isEmpty()) {
                        throw helper.assertionException(center, "Pollinated leaf has no result");
                    }
                })
                .thenSucceed();
    }

    // ── Stripper: refuses to work without an axe ─────────────────────────────────
    private static void testStripperRequiresAxe(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, TreeRegistrator.STRIPPER.get().defaultBlockState());
        StripperBlockEntity stripper = helper.getBlockEntity(pos, StripperBlockEntity.class);

        stripper.inventoryHandler.setStackInSlot(StripperBlockEntity.SLOT_IN, new ItemStack(Blocks.OAK_LOG, 4));

        ServerLevel level = helper.getLevel();
        BlockPos absPos = helper.absolutePos(pos);
        for (int i = 0; i < 50; ++i) {
            StripperBlockEntity.tick(level, absPos, stripper.getBlockState(), stripper);
        }

        ItemStack out = stripper.inventoryHandler.getStackInSlot(StripperBlockEntity.SLOT_OUT);
        if (!out.isEmpty()) {
            helper.fail("Stripper produced output without an axe: " + idOf(out.getItem()), pos);
            return;
        }
        helper.succeed();
    }

    // ── Stripper: strips a log when an axe is present ────────────────────────────
    private static void testStripperStripsLogWithAxe(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, TreeRegistrator.STRIPPER.get().defaultBlockState());
        StripperBlockEntity stripper = helper.getBlockEntity(pos, StripperBlockEntity.class);

        stripper.inventoryHandler.setStackInSlot(StripperBlockEntity.SLOT_IN, new ItemStack(Blocks.OAK_LOG, 4));
        stripper.inventoryHandler.setStackInSlot(StripperBlockEntity.SLOT_AXE, new ItemStack(Items.IRON_AXE));

        ServerLevel level = helper.getLevel();
        BlockPos absPos = helper.absolutePos(pos);
        BlockState state = stripper.getBlockState();

        helper.startSequence()
                .thenWaitUntil(() -> {
                    StripperBlockEntity.tick(level, absPos, state, stripper);
                    ItemStack out = stripper.inventoryHandler.getStackInSlot(StripperBlockEntity.SLOT_OUT);
                    if (!out.is(Blocks.STRIPPED_OAK_LOG.asItem())) {
                        throw helper.assertionException(pos, "Stripper output is "
                                + (out.isEmpty() ? "empty" : idOf(out.getItem())) + ", expected stripped_oak_log");
                    }
                })
                .thenSucceed();
    }

    // ── Pollen sifter: random 10% output of pollen for the input leaf ────────────
    private static void testPollenSifterProducesPollen(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, TreeRegistrator.POLLEN_SIFTER.get().defaultBlockState());
        PollenSifterBlockEntity sifter = helper.getBlockEntity(pos, PollenSifterBlockEntity.class);

        Block leaf = Blocks.OAK_LEAVES;
        sifter.inventoryHandler.setStackInSlot(PollenSifterBlockEntity.SLOT_IN, new ItemStack(leaf, 64));

        ServerLevel level = helper.getLevel();
        BlockPos absPos = helper.absolutePos(pos);
        BlockState state = sifter.getBlockState();

        // Output is random (~10% per completed 600-progress cycle), so drive many cycles per game
        // tick to converge quickly. Across the budgeted ticks the cumulative output probability is
        // overwhelming, keeping the assertion effectively deterministic.
        helper.startSequence()
                .thenWaitUntil(() -> {
                    for (int i = 0; i < 200; ++i) {
                        PollenSifterBlockEntity.tick(level, absPos, state, sifter);
                    }
                    ItemStack out = sifter.inventoryHandler.getStackInSlot(PollenSifterBlockEntity.SLOT_OUT);
                    if (out.isEmpty()) {
                        throw helper.assertionException(pos, "Sifter has not produced pollen yet");
                    }
                    if (!out.is(TreeRegistrator.POLLEN.get())) {
                        throw helper.assertionException(pos, "Sifter output is " + idOf(out.getItem()) + ", expected pollen");
                    }
                    Identifier component = out.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT);
                    if (component == null || !component.equals(BuiltInRegistries.BLOCK.getKey(leaf))) {
                        throw helper.assertionException(pos, "Pollen block component is " + component
                                + ", expected " + BuiltInRegistries.BLOCK.getKey(leaf));
                    }
                })
                .thenSucceed();
    }

    // ── Sawmill: deterministic log → planks + sawdust ────────────────────────────
    private static void testSawmillProcessesLog(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, TreeRegistrator.SAWMILL.get().defaultBlockState());
        SawmillBlockEntity sawmill = helper.getBlockEntity(pos, SawmillBlockEntity.class);

        sawmill.inventoryHandler.setStackInSlot(SawmillBlockEntity.SLOT_IN, new ItemStack(Blocks.OAK_LOG, 4));

        ServerLevel level = helper.getLevel();
        ensureSawmillRecipe(level);
        BlockPos absPos = helper.absolutePos(pos);
        BlockState state = sawmill.getBlockState();

        helper.startSequence()
                .thenWaitUntil(() -> {
                    SawmillBlockEntity.tick(level, absPos, state, sawmill);
                    ItemStack out = sawmill.inventoryHandler.getStackInSlot(SawmillBlockEntity.SLOT_OUT);
                    ItemStack secondary = sawmill.inventoryHandler.getStackInSlot(SawmillBlockEntity.SLOT_SECONDARY);
                    if (!out.is(Blocks.OAK_PLANKS.asItem())) {
                        throw helper.assertionException(pos, "Sawmill primary output is "
                                + (out.isEmpty() ? "empty" : idOf(out.getItem())) + ", expected oak_planks");
                    }
                    if (!secondary.is(TreeRegistrator.SAWDUST.get())) {
                        throw helper.assertionException(pos, "Sawmill secondary output is "
                                + (secondary.isEmpty() ? "empty" : idOf(secondary.getItem())) + ", expected sawdust");
                    }
                })
                .thenSucceed();
    }

    // ── Sawmill: a speed upgrade advances progress faster per tick ───────────────
    // Sawmill progress is non-random (progress += tickRate * speedModifier on each %tickRate tick),
    // so a fixed number of tick() calls yields strictly higher progress with an UPGRADE_TIME present.
    private static void testSawmillSpeedUpgrade(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        BlockPos basePos = new BlockPos(2, 2, 2);
        helper.setBlock(basePos, TreeRegistrator.SAWMILL.get().defaultBlockState());
        SawmillBlockEntity baseline = helper.getBlockEntity(basePos, SawmillBlockEntity.class);
        baseline.inventoryHandler.setStackInSlot(SawmillBlockEntity.SLOT_IN, new ItemStack(Blocks.OAK_LOG, 64));

        BlockPos upgPos = new BlockPos(4, 2, 2);
        helper.setBlock(upgPos, TreeRegistrator.SAWMILL.get().defaultBlockState());
        SawmillBlockEntity upgraded = helper.getBlockEntity(upgPos, SawmillBlockEntity.class);
        upgraded.inventoryHandler.setStackInSlot(SawmillBlockEntity.SLOT_IN, new ItemStack(Blocks.OAK_LOG, 64));
        ((InventoryHandlerHelper.UpgradeHandler) upgraded.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME.get()));

        if (upgraded.getUpgradeCount(LibItems.UPGRADE_TIME.get()) != 1) {
            helper.fail("Expected UPGRADE_TIME count of 1, got " + upgraded.getUpgradeCount(LibItems.UPGRADE_TIME.get()), upgPos);
            return;
        }

        // Drive both a fixed number of cycles short of completion (recipeTime=200, tickRate=10):
        // 10 acting cycles → baseline progress 100, upgraded (speedModifier=2) progress 200 which
        // would complete and reset. Use 5 acting cycles so neither completes: baseline=50, upgraded=100.
        BlockPos absBase = helper.absolutePos(basePos);
        BlockPos absUpg = helper.absolutePos(upgPos);
        for (int i = 0; i < 50; ++i) {
            SawmillBlockEntity.tick(level, absBase, baseline.getBlockState(), baseline);
            SawmillBlockEntity.tick(level, absUpg, upgraded.getBlockState(), upgraded);
        }

        if (upgraded.progress <= baseline.progress) {
            helper.fail("UPGRADE_TIME did not raise progress per tick: baseline=" + baseline.progress
                    + ", upgraded=" + upgraded.progress, upgPos);
            return;
        }
        helper.succeed();
    }

    private static Identifier idOf(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static Identifier idOf(net.minecraft.world.item.Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
