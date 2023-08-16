package cy.jdkdigital.productivetrees;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.UpgradeItem;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivetrees.client.particle.ColoredParticleType;
import cy.jdkdigital.productivetrees.common.block.PollinatedLeaves;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.common.item.PollenItem;
import cy.jdkdigital.productivetrees.event.EventHandler;
import cy.jdkdigital.productivetrees.loot.OptionalLootItem;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod(ProductiveTrees.MODID)
public class ProductiveTrees
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "productivetrees";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation EMPTY_RL = new ResourceLocation(ProductiveTrees.MODID, "");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProductiveTrees.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProductiveTrees.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ProductiveTrees.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ProductiveTrees.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ProductiveTrees.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ProductiveTrees.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ProductiveTrees.MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, MODID);

    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRIES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MODID);
    public static final RegistryObject<LootPoolEntryType> OPTIONAL_LOOT_ITEM = LOOT_POOL_ENTRIES.register("optional_loot_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
    public static final RegistryObject<PoiType> ADVANCED_HIVES = POI_TYPES.register("advanced_beehive", () -> {
        Set<BlockState> blockStates = new HashSet<>();
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.registerWood()) {
                blockStates.addAll(treeObject.getHiveBlock().get().getStateDefinition().getPossibleStates());
            }
        });
        return new PoiType(blockStates, 1, 1);
    });

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, MODID));
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(MODID, () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(Items.OAK_SAPLING))
                .title(Component.translatable("itemGroup.productivetrees"))
                .build();
    });

    public static final RegistryObject<Item> UPGRADE_POLLEN_SIEVE = ITEMS.register("upgrade_pollen_sieve", () -> new UpgradeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> POLLEN = ITEMS.register("pollen", () -> new PollenItem(new Item.Properties()));
    public static final RegistryObject<Block> POLLINATED_LEAVES = BLOCKS.register("pollinated_leaves", () -> new PollinatedLeaves(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<BlockEntityType<PollinatedLeavesBlockEntity>> POLLINATED_LEAVES_BLOCK_ENTITY = BLOCK_ENTITIES.register("pollinated_leaves", () -> BlockEntityType.Builder.of(PollinatedLeavesBlockEntity::new, POLLINATED_LEAVES.get()).build(null));

    public static final RegistryObject<RecipeSerializer<?>> TREE_POLLINATION = RECIPE_SERIALIZERS.register("tree_pollination", () -> new TreePollinationRecipe.Serializer<>(TreePollinationRecipe::new));
    public static final RegistryObject<RecipeType<TreePollinationRecipe>> TREE_POLLINATION_TYPE = RECIPE_TYPES.register("tree_pollination", () -> new RecipeType<>() {});

    public static final RegistryObject<ColoredParticleType> PETAL_PARTICLES = PARTICLE_TYPES.register("petals", ColoredParticleType::new);

    public ProductiveTrees()
    {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TreeFinder.discoverTrees();

        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        LOOT_POOL_ENTRIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FEATURES.register(modEventBus);
        TREE_DECORATORS.register(modEventBus);
        POI_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(EventHandler::axeStrip);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::blockBreak);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::beeRelease);

        // TODO
        // Fruit drop rate tbd by loot table
        // trapdoors, boats?, signs, door

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
    }
}
