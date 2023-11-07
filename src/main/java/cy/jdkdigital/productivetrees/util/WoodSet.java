package cy.jdkdigital.productivetrees.util;

import java.util.HashMap;
import java.util.Map;

public record WoodSet(String woodStyle, String plankStyle, String leavesStyle, String saplingStyle)
{
    public static Map<String, WoodSet> STYLES = new HashMap<>()
    {{
        put("acacia", new WoodSet("acacia", "acacia", "acacia", "acacia"));
        put("birch", new WoodSet("birch", "birch", "birch", "birch"));
        put("bush", new WoodSet("bush", "oak", "oak", "bush"));
        put("cherry", new WoodSet("cherry", "cherry", "cherry", "cherry"));
        put("dark_oak", new WoodSet("dark_oak", "dark_oak", "dark_oak", "dark_oak"));
        put("jungle", new WoodSet("jungle", "jungle", "jungle", "jungle"));
        put("mangrove", new WoodSet("mangrove", "mangrove", "mangrove", "mangrove"));
        put("oak", new WoodSet("oak", "oak", "oak", "oak"));
        put("spruce", new WoodSet("spruce", "spruce", "spruce", "spruce"));
    }};
}
