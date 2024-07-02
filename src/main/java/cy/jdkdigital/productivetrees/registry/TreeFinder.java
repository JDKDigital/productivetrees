package cy.jdkdigital.productivetrees.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.TreeCreator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TreeFinder
{
    public static ICondition.IContext context;
    public static Map<ResourceLocation, TreeObject> trees = new LinkedHashMap<>();

    public static void discoverTrees() {
        try {
            discoverTreeFiles();
            TreeRegistrator.registerSignBlockEntities();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void discoverTreeFiles() throws IOException {
        JsonObject TREES_JSON = getStreamAsJsonObject("/data/" + ProductiveTrees.MODID + "/trees.json");

        for (var name : TREES_JSON.keySet()) {
            JsonObject json = TREES_JSON.get(name).getAsJsonObject();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, name);

            if (!json.has("feature")) {
                json.addProperty("feature", id.toString());
            }

            TreeObject tree = TreeCreator.create(id, json);

            if (tree != null) {
                trees.put(tree.getId(), tree);
            } else {
                ProductiveTrees.LOGGER.error("failed to load tree " + id);
            }
        }
    }

    private static JsonObject getStreamAsJsonObject(String pPath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ProductiveTrees.class.getResourceAsStream(pPath))))).getAsJsonObject();
    }
}
