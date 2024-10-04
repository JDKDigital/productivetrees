package cy.jdkdigital.productivetrees.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class TreeUtil
{
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
            var tree = TreeUtil.getTree(saplingBlock);
            if (tree != null) {
                return new ItemStack(getBlock(tree.getId(), "_leaves"));
            }
        }
        var resourceLocation = BuiltInRegistries.ITEM.getKey(saplingStack.getItem());
        if (resourceLocation != null) {
            var block = BuiltInRegistries.BLOCK.get(resourceLocation.withPath(p -> p.replace("_sapling", "_propagule").replace("_sapling", "_leaves")));
            if (block.equals(Blocks.AIR)) {
                block = BuiltInRegistries.BLOCK.get(resourceLocation.withPath(p -> p + "_leaves"));
            }
            if (block != null) {
                return new ItemStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getSaplingFromLeaf(ItemStack leafStack) {
        if (leafStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLeavesBlock leavesBlock) {
            var tree = TreeUtil.getTree(leavesBlock);
            if (tree != null) {
                return new ItemStack(getBlock(tree.getId(), "_sapling"));
            }
        }
        var resourceLocation = BuiltInRegistries.ITEM.getKey(leafStack.getItem());
        if (resourceLocation != null) {
            var block = BuiltInRegistries.BLOCK.get(resourceLocation.withPath(p -> p.replace("_leaves", "_sapling")));
            if (block.equals(Blocks.AIR)) {
                block = BuiltInRegistries.BLOCK.get(resourceLocation.withPath(p -> p.replace("_leaves", "_propagule")));
            }
            if (block.equals(Blocks.AIR)) {
                block = BuiltInRegistries.BLOCK.get(resourceLocation.withPath(p -> p.replace("_leaves", "")));
            }
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
//            Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(STRIPPER_UUID, "stripper"));
//            var pos = blockEntity.getBlockPos();
//            var blockHit = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.DOWN, pos, false);
//            UseOnContext context = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, blockHit);
//            stripState = ForgeEventFactory.onToolUse(initialState, context, ItemAbilities.AXE_STRIP, true);
            return stripState != null && !stripState.equals(initialState) ? new ItemStack(initialState.getBlock()) : ItemStack.EMPTY;
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
                var tree = TreeUtil.getTree(pLog);
                if (tree != null) {
                    return new ItemStack(getBlock(tree.getId(), "_stripped_log"));
                }
            }
            if (initialState.getBlock() instanceof ProductiveWoodBlock pWood) {
                var tree = TreeUtil.getTree(pWood);
                if (tree != null) {
                    return new ItemStack(getBlock(tree.getId(), "_stripped_wood"));
                }
            }
        }
        return ItemStack.EMPTY;
    }

    static Map<String, RecipeHolder<SawmillRecipe>> sawmillRecipeCache = new HashMap<>();
    public static RecipeHolder<SawmillRecipe> getSawmillRecipe(Level level, ItemStack stack) {
        if (!stack.isEmpty()) {
            var cacheItem = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString() + (!stack.getComponents().isEmpty() ? stack.getComponents().stream().map(TypedDataComponent::toString).reduce((s, s2) -> s + s2) : "");
            if (sawmillRecipeCache.containsKey(cacheItem)) {
                return sawmillRecipeCache.get(cacheItem);
            }

            List<RecipeHolder<SawmillRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.SAW_MILLLING_TYPE.get());
            for (RecipeHolder<SawmillRecipe> recipe : recipes) {
                if (recipe.value().input().test(stack)) {
                    sawmillRecipeCache.put(cacheItem, recipe);
                    return recipe;
                }
            }
        }
        return null;
    }

    public static ItemStack getPollen(Block block) {
        var pollenStack = new ItemStack(TreeRegistrator.POLLEN.get());
        pollenStack.set(TreeRegistrator.POLLEN_BLOCK_COMPONENT, BuiltInRegistries.BLOCK.getKey(block));
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

    public static Block getBlock(ResourceLocation tree, String name) {
        return BuiltInRegistries.BLOCK.get(tree.withPath(p -> p + name));
    }

    public static TreeObject getTree(Block block) {
        return TreeFinder.trees.get(BuiltInRegistries.BLOCK.getKey(block).withPath(p -> p.replace("_log", "").replace("_wood", "").replace("_sapling", "").replace("_leaves", "")));
    }

    public static void pollinateLeaves(Level level, BlockPos pos, int distance, boolean isSpecialPollinator, List<BlockState> uniqueLeaves) {
        List<BlockPos> leaves = BlockPos.betweenClosedStream(pos.offset(-distance, -distance, -distance), pos.offset(distance, distance, distance)).map(BlockPos::immutable).toList();
        // Build permutation map
        Map<BlockState, BlockPos> leafMap = new HashMap<>();
        leaves.forEach(blockPos -> {
            var state = level.getBlockState(blockPos);
            if (state.is(ModTags.POLLINATABLE) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get()) && !(state.getBlock() instanceof ProductiveFruitBlock)) {
                leafMap.put(state, blockPos);
                if (!uniqueLeaves.contains(state)) {
                    uniqueLeaves.add(state);
                }
            }
        });

        if (!uniqueLeaves.isEmpty()) {
            // Pollinate leaves
            Map<RecipeHolder<TreePollinationRecipe>, Pair<BlockState, BlockState>> matchedRecipes = new HashMap<>();
            var allRecipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
            allRecipes.forEach(treePollinationRecipe -> {
                if (!matchedRecipes.containsKey(treePollinationRecipe)) {
                    uniqueLeaves.forEach(stateA -> {
                        uniqueLeaves.forEach(stateB -> {
                            if (treePollinationRecipe.value().matches(stateA, stateB)) {
                                matchedRecipes.put(treePollinationRecipe, Pair.of(stateA, stateB));
                            }
                        });
                    });
                }
            });

            if (!matchedRecipes.isEmpty()) {
                RecipeHolder<TreePollinationRecipe> pickedRecipe = (RecipeHolder<TreePollinationRecipe>) matchedRecipes.keySet().toArray()[level.random.nextInt(matchedRecipes.size())];
                Pair<BlockState, BlockState> states = matchedRecipes.get(pickedRecipe);

                BlockPos posA = level.random.nextBoolean() ? leafMap.get(states.getFirst()) : leafMap.get(states.getSecond());

                if (level.random.nextFloat() <= (pickedRecipe.value().chance * (isSpecialPollinator ? 5 : 1)) && level.getBlockState(posA).is(BlockTags.LEAVES)) {
                    level.setBlock(posA, TreeRegistrator.POLLINATED_LEAVES.get().defaultBlockState(), Block.UPDATE_ALL);
                    if (level.getBlockEntity(posA) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                        pollinatedLeavesBlockEntity.setLeafA(states.getFirst().getBlock());
                        pollinatedLeavesBlockEntity.setLeafB(states.getSecond().getBlock());
                        pollinatedLeavesBlockEntity.setResult(pickedRecipe.value().result);
                        pollinatedLeavesBlockEntity.setChanged();
                    }
                }
            }
        }
    }
}
