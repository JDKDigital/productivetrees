package cy.jdkdigital.productivetrees.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.TreeCreator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ConditionContext;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TreeFinder
{
    public static ICondition.IContext context;
    public static Map<ResourceLocation, TreeObject> trees = new LinkedHashMap<>();

    public static void discoverTrees() {
        try {
            discoverTreeFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void discoverTreeFiles() throws IOException {
        File lockFile = new File(TreeUtil.LOCK_FILE.toString(), "defaults.lock");
        // TODO reactivate before launch
//        if (!lockFile.exists()) {
            FileUtils.write(lockFile, "This lock file means the standard trees have already been added and you can now do your own custom stuff to them.", StandardCharsets.UTF_8);
            setupDefaultFiles("/data/" + ProductiveTrees.MODID + "/trees", Paths.get(TreeUtil.TREE_PATH.toString()), true);
//        } else {
//            setupDefaultFiles("/data/" + ProductiveTrees.MODID + "/trees", Paths.get(TreeUtil.TREE_PATH.toString()), false);
//        }

        var files = TreeUtil.TREE_PATH.toFile().listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (files == null)
            return;

        for (var file : files) {
            JsonObject json;
            InputStreamReader reader = null;
            ResourceLocation id = null;
            TreeObject tree = null;

            try {
                var parser = new JsonParser();
                reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                json = parser.parse(reader).getAsJsonObject();
                var name = file.getName().replace(".json", "");
                id = new ResourceLocation(ProductiveTrees.MODID, name);
                ProductiveTrees.LOGGER.info("Import tree config " + id);

                if (!CraftingHelper.processConditions(json, "conditions", context)) {
                    continue;
                }

                tree = TreeCreator.create(id, json);

                reader.close();
            } catch (Exception e) {
                ProductiveTrees.LOGGER.error("An error occurred while creating tree with id {}", id, e);
            } finally {
                IOUtils.closeQuietly(reader);
            }

            if (tree != null) {
                trees.put(tree.getId(), tree);
            } else {
                ProductiveTrees.LOGGER.error("failed to load tree " + id);
            }
        }
    }

    public static void setupDefaultFiles(String dataPath, Path targetPath, boolean override) {
        List<Path> roots = List.of(ModList.get().getModFileById(ProductiveTrees.MODID).getFile().getFilePath());
        ProductiveTrees.LOGGER.info("Productive Trees Pulling defaults from: " + roots);

        if (roots.isEmpty()) {
            throw new RuntimeException("Failed to load defaults.");
        }

        for (Path modRoot : roots) {
            setupDefaultFiles(dataPath, targetPath, modRoot, override);
        }
    }

    public static void setupDefaultFiles(String dataPath, Path targetPath, Path modPath, boolean override) {
        if (Files.isRegularFile(modPath)) {
            try(FileSystem fileSystem = FileSystems.newFileSystem(modPath)) {
                Path path = fileSystem.getPath(dataPath);
                if (Files.exists(path)) {
                    copyFiles(path, targetPath, override);
                }
            } catch (IOException e) {
                ProductiveTrees.LOGGER.error("Could not load source {}!!", modPath);
                e.printStackTrace();
            }
        } else if (Files.isDirectory(modPath)) {
            copyFiles(Paths.get(modPath.toString(), dataPath), targetPath, override);
        }
    }

    private static void copyFiles(Path source, Path targetPath, boolean override) {
        try (Stream<Path> sourceStream = Files.walk(source)) {
            sourceStream.filter(f -> f.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            if (override) {
                                Files.copy(path, Paths.get(targetPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                Files.copy(path, Paths.get(targetPath.toString(), path.getFileName().toString()));
                            }
                        } catch (IOException e) {
                            if (!override) {
                                ProductiveTrees.LOGGER.error("Could not copy file: {}, Target: {}", path, targetPath);
                            }
                        }
                    });
        } catch (IOException e) {
            ProductiveTrees.LOGGER.error("Could not stream source files: {}", source);
        }
    }
}
