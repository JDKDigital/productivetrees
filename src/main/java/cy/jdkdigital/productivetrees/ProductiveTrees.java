package cy.jdkdigital.productivetrees;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.productivetrees.gametest.ProductiveTreesGameTests;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.material.Fluid;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(ProductiveTrees.MODID)
public class ProductiveTrees
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "productivetrees";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier EMPTY_RL = Identifier.fromNamespaceAndPath(MODID, "");

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(BuiltInRegistries.TREE_DECORATOR_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends LootPoolEntryContainer>> LOOT_POOL_ENTRIES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, MODID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static boolean isMinimal = false;

    public ProductiveTrees(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.STARTUP_CONFIG);
        isMinimal = Config.STARTUP.minimal.get();

        TreeFinder.discoverTrees();

        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        LOOT_POOL_ENTRIES.register(modEventBus);
        LOOT_CONDITIONS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FEATURES.register(modEventBus);
        TREE_DECORATORS.register(modEventBus);
        POI_TYPES.register(modEventBus);
        FOLIAGE_PLACERS.register(modEventBus);
        TRUNK_PLACERS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);

        TreeRegistrator.init();
        ClientRegistration.init();

        modEventBus.addListener(this::commonSetup);

        ProductiveTreesGameTests.init();
        modEventBus.addListener(ProductiveTreesGameTests::onRegisterGameTests);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
    }

    private static final String[] FLAMMABLE_LOGS = {"_log", "_wood", "_stripped_log", "_stripped_wood"};
    private static final String[] FLAMMABLE_PRODUCTS = {"_planks", "_stairs", "_slab", "_fence", "_fence_gate", "_door", "_trapdoor", "_pressure_plate", "_button", "_bookshelf"};

    public static final Map<Block, int[]> FLAMMABILITY = new HashMap<>();

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TreeFinder.trees.forEach((id, tree) -> {
                if (tree.isFireProof()) {
                    return;
                }
                setFlammable(id, "_leaves", 30, 60);
                setFlammable(id, "_fruit", 30, 60);
                for (String suffix : FLAMMABLE_LOGS) {
                    setFlammable(id, suffix, 5, 5);
                }
                for (String suffix : FLAMMABLE_PRODUCTS) {
                    setFlammable(id, suffix, 5, 20);
                }
            });
        });
    }

    private static void setFlammable(Identifier tree, String suffix, int encouragement, int flammability) {
        Block block = TreeUtil.getBlock(tree, suffix);
        if (block != Blocks.AIR) {
            FLAMMABILITY.put(block, new int[]{encouragement, flammability});
        }
    }
}
