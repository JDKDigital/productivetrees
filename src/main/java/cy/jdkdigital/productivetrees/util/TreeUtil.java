package cy.jdkdigital.productivetrees.util;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

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
}
