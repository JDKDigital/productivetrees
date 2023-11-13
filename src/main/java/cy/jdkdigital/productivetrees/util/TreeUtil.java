package cy.jdkdigital.productivetrees.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveWoodBlock;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
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
import java.util.List;
import java.util.UUID;

public class TreeUtil
{
    public static final Path LOCK_FILE = createCustomPath("");
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

    public static ItemStack getSaplingFromLeaf(ItemStack saplingStack) {
        if (saplingStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLeavesBlock leavesBlock) {
            return new ItemStack(leavesBlock.getTree().getSaplingBlock().get());
        }
        var resourceLocation = ForgeRegistries.ITEMS.getKey(saplingStack.getItem());
        if (resourceLocation != null) {
            var block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resourceLocation.getPath().replace("leaves", "sapling")));
            if (block != null) {
                return new ItemStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    public static void registerStrippable(Block log, Block stripped_log) {
        AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
        AxeItem.STRIPPABLES.put(log, stripped_log);
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
}
