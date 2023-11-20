package cy.jdkdigital.productivetrees;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

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
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

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

        // TODO
        // tree features
        // custom tree textures
        // boats?

        // Trees to add
        // cacao, Socotra Dragon, sugar apple, coconut sprout, kadsura, snake fruit, pandanus, starry night, Sandalwood, Agarwood

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
    }
}
