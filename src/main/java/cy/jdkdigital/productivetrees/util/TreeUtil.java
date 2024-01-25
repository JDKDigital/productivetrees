package cy.jdkdigital.productivetrees.util;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveWoodBlock;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Supplier;

public class TreeUtil
{
    public static final Codec<Supplier<ItemStack>> ITEM_STACK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ResourceLocation.CODEC.fieldOf("item").forGetter(stack -> {
            return ForgeRegistries.ITEMS.getKey(stack.get().getItem());
        }), Codec.INT.fieldOf("count").orElse(1).forGetter(stack -> stack.get().getCount()), CompoundTag.CODEC.optionalFieldOf("nbt").forGetter((stack) -> Optional.ofNullable(stack.get().getTag()))).apply(instance, (item, count, tag) -> {
            return () -> {
                var stack = new ItemStack(ForgeRegistries.ITEMS.getValue(item), count);
                tag.ifPresent(stack::setTag);
                return stack;
            };
        });
    });

    public static final Path TREE_PATH = createCustomPath("trees");
    public static final Path INTERNAL_TREE_PATH = createModPath("/data/" + ProductiveTrees.MODID + "/trees");

    private static Path createCustomPath(String pathName) {
        Path customPath = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), ProductiveTrees.MODID, pathName);
        createDirectory(customPath, pathName);
        return customPath;
    }

    private static Path createModPath(String pathName) {
        List<Path> roots = List.of(ModList.get().getModFileById(ProductiveTrees.MODID).getFile().getFilePath());
        for (Path modRoot : roots) {
            if (Files.isRegularFile(modRoot)) {
                try(FileSystem fileSystem = FileSystems.newFileSystem(modRoot)) {
                    Path path = fileSystem.getPath(pathName);
                    if (Files.exists(path)) {
                        return path;
                    }
                } catch (IOException e) {
                    ProductiveTrees.LOGGER.error("Could not load source {}!!", modRoot);
                    e.printStackTrace();
                }
            } else if (Files.isDirectory(modRoot)) {
                return modRoot;
            }
        }
        return null;
    }

    private static void createDirectory(Path path, String dirName) {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) { //ignored
        } catch (IOException e) {
            ProductiveTrees.LOGGER.error("failed to create \""+dirName+"\" directory");
        }
    }

    public static int getLeafColor(Block leaf) {
        return getLeafColor(leaf, null, null);
    }

    public static int getLeafColor(Block leaf, BlockAndTintGetter lightReader, BlockPos pos) {
        if (leaf != null) {
            if (leaf instanceof ProductiveLeavesBlock leafBlock) {
                return ColorUtil.getCacheColor(leafBlock.getTree().getLeafColor());
            }
            if (leaf.equals(Blocks.SPRUCE_LEAVES)) {
                return FoliageColor.getEvergreenColor();
            }
            if (leaf.equals(Blocks.BIRCH_LEAVES)) {
                return FoliageColor.getBirchColor();
            }
        }
        if (lightReader != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(lightReader, pos);
        }
        return FoliageColor.getDefaultColor();
    }

    public static ItemStack getLeafFromSapling(ItemStack saplingStack) {
        if (saplingStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveSaplingBlock saplingBlock) {
            return new ItemStack(saplingBlock.getTree().getLeafBlock().get());
        }
        var resourceLocation = ForgeRegistries.ITEMS.getKey(saplingStack.getItem());
        if (resourceLocation != null) {
            var block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resourceLocation.getPath().replace("sapling", "leaves")));
            if (block != null) {
                return new ItemStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getSaplingFromLeaf(ItemStack leafStack) {
        if (leafStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLeavesBlock leavesBlock) {
            return new ItemStack(leavesBlock.getTree().getSaplingBlock().get());
        }
        var resourceLocation = ForgeRegistries.ITEMS.getKey(leafStack.getItem());
        if (resourceLocation != null) {
            var block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resourceLocation.getPath().replace("leaves", "sapling")));
            if (block != null) {
                return new ItemStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    public static final UUID STRIPPER_UUID = UUID.nameUUIDFromBytes("pt_stripper".getBytes(StandardCharsets.UTF_8)); // d6608568-e24a-326a-a40b-503179fff39e
    public static ItemStack getStrippedItem(StripperBlockEntity blockEntity, ServerLevel level, ItemStack stack) {
        var stripped = getStrippedItem(stack);
        if (stripped.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            var initialState = blockItem.getBlock().defaultBlockState();
            var stripState = AxeItem.getAxeStrippingState(initialState);
            Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(STRIPPER_UUID, "stripper"));
            var pos = blockEntity.getBlockPos();
            var blockHit = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.DOWN, pos, false);
            UseOnContext context = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, blockHit);
            stripState = ForgeEventFactory.onToolUse(initialState, context, ToolActions.AXE_STRIP, true);
            return !stripState.equals(initialState) ? new ItemStack(initialState.getBlock()) : ItemStack.EMPTY;
        }
        return stripped;
    }

    public static ItemStack getStrippedItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            var initialState = blockItem.getBlock().defaultBlockState();
            var stripState = AxeItem.getAxeStrippingState(initialState);
            if (stripState != null ) {
                return new ItemStack(stripState.getBlock());
            }
            if (initialState.getBlock() instanceof ProductiveLogBlock pLog) {
                return new ItemStack(pLog.getTree().getStrippedLogBlock().get());
            }
            if (initialState.getBlock() instanceof ProductiveWoodBlock pLog) {
                return new ItemStack(pLog.getTree().getStrippedWoodBlock().get());
            }
        }
        return ItemStack.EMPTY;
    }

    static Map<ItemStack, SawmillRecipe> sawmillRecipeCache = new HashMap<>();
    public static SawmillRecipe getSawmillRecipe(Level level, ItemStack stack) {
        if (!stack.isEmpty()) {
            var cacheItem = stack.copy();
            cacheItem.setCount(1);
            if (sawmillRecipeCache.containsKey(cacheItem)) {
                return sawmillRecipeCache.get(cacheItem);
            }

            List<SawmillRecipe> recipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.SAW_MILLLING_TYPE.get());
            for (SawmillRecipe recipe : recipes) {
                if (recipe.log.test(stack)) {
                    sawmillRecipeCache.put(cacheItem, recipe);
                    return recipe;
                }
            }
        }
        return null;
    }

    public static ItemStack getPollen(Block block) {
        var pollenStack = new ItemStack(TreeRegistrator.POLLEN.get());
        pollenStack.getOrCreateTag().putString("Block", ForgeRegistries.BLOCKS.getKey(block).toString());
        return pollenStack;
    }

    public static boolean isSpecialTree(ResourceLocation id) {
        return  id.getPath().equals("purple_spiral") || // purple
                id.getPath().equals("cave_dweller") || // gray
                id.getPath().equals("black_ember") || // black
                id.getPath().equals("brown_amber") || // brown
                id.getPath().equals("firecracker") || // red
                id.getPath().equals("flickering_sun") || // yellow
                id.getPath().equals("foggy_blast") || // light gray
                id.getPath().equals("night_fuchsia") || // magenta
                id.getPath().equals("time_traveller") || // cyan
                id.getPath().equals("rippling_willow") || // green
                id.getPath().equals("soul_tree") || // white
                id.getPath().equals("sparkle_cherry") || // pink
                id.getPath().equals("slimy_delight") || // lime
                id.getPath().equals("thunder_bolt") || // orange
                id.getPath().equals("blue_yonder") || // blue
                id.getPath().equals("water_wonder"); // light blue
    }

    public static boolean isTranslucentTree(String name) {
        return name.equals("brown_amber") || name.equals("slimy_delight") || name.equals("foggy_blast") || name.equals("soul_tree") || name.equals("water_wonder");
    }
}
