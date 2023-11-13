package cy.jdkdigital.productivetrees.util;

import java.util.HashMap;
import java.util.Map;

public record WoodSet(String woodStyle, String plankStyle, String leavesStyle, String doorStyle, String saplingStyle)
{
    public static Map<String, WoodSet> STYLES = new HashMap<>()
    {{
        put("acacia", new WoodSet("acacia", "acacia", "acacia", "oak", "acacia"));
        put("birch", new WoodSet("birch", "birch", "birch", "oak", "birch"));
        put("bush", new WoodSet("bush", "oak", "oak", "oak", "bush"));
        put("cherry", new WoodSet("cherry", "cherry", "cherry", "oak", "cherry"));
        put("dark_oak", new WoodSet("dark_oak", "dark_oak", "dark_oak", "oak", "dark_oak"));
        put("jungle", new WoodSet("jungle", "jungle", "jungle", "oak", "jungle"));
        put("mangrove", new WoodSet("mangrove", "mangrove", "mangrove", "oak", "mangrove"));
        put("oak", new WoodSet("oak", "oak", "oak", "oak", "oak"));
        put("spruce", new WoodSet("spruce", "spruce", "spruce", "oak", "spruce"));

        put("alder", new WoodSet("alder", "alder", "alder", "oak", "oak"));
        put("allspice", new WoodSet("allspice", "allspice", "allspice", "oak", "oak"));
        put("almond", new WoodSet("almond", "almond", "almond", "oak", "acacia"));
        put("apricot", new WoodSet("apricot", "apricot", "apricot", "oak", "birch"));
        put("ash", new WoodSet("ash", "ash", "ash", "oak", "oak"));
        put("aspen", new WoodSet("aspen", "aspen", "aspen", "oak", "oak"));
        put("avocado", new WoodSet("avocado", "avocado", "avocado", "oak", "oak"));
        put("balsa", new WoodSet("balsa", "balsa", "balsa", "oak", "oak"));
        put("balsam_fir", new WoodSet("balsam_fir", "balsam_fir", "balsam_fir", "oak", "spruce"));
        put("banana", new WoodSet("banana", "banana", "banana", "oak", "mangrove"));
        put("beech", new WoodSet("beech", "beech", "beech", "oak", "dark_oak"));
        put("black_cherry", new WoodSet("black_cherry", "black_cherry", "black_cherry", "oak", "birch"));
        put("black_locust", new WoodSet("black_locust", "black_locust", "black_locust", "oak", "oak"));
        put("blackthorn", new WoodSet("blackthorn", "blackthorn", "blackthorn", "oak", "cherry"));
        put("blue_mahoe", new WoodSet("blue_mahoe", "blue_mahoe", "blue_mahoe", "oak", "oak"));
        put("boxwood", new WoodSet("boxwood", "boxwood", "boxwood", "oak", "dark_oak"));
        put("brazil_nut", new WoodSet("brazil_nut", "brazil_nut", "brazil_nut", "oak", "acacia"));
        put("copper_beech", new WoodSet("copper_beech", "copper_beech", "copper_beech", "oak", "birch"));
        put("douglas_fir", new WoodSet("douglas_fir", "douglas_fir", "douglas_fir", "oak", "spruce"));
        put("plantain", new WoodSet("plantain", "plantain", "plantain", "oak", "mangrove"));
        put("purple_blackthorn", new WoodSet("purple_blackthorn", "purple_blackthorn", "purple_blackthorn", "oak", "cherry"));
        put("red_banana", new WoodSet("red_banana", "red_banana", "red_banana", "oak", "mangrove"));
        put("silver_fir", new WoodSet("silver_fir", "silver_fir", "silver_fir", "oak", "spruce"));
        put("sour_cherry", new WoodSet("sour_cherry", "sour_cherry", "sour_cherry", "oak", "cherry"));
        put("wild_cherry", new WoodSet("wild_cherry", "wild_cherry", "wild_cherry", "oak", "cherry"));
        put("flowering_crabapple", new WoodSet("flowering_crabapple", "flowering_crabapple", "flowering_crabapple", "oak", "cherry"));
        put("sweet_crabapple", new WoodSet("sweet_crabapple", "sweet_crabapple", "sweet_crabapple", "oak", "cherry"));
        put("prarie_crabapple", new WoodSet("prarie_crabapple", "prarie_crabapple", "prarie_crabapple", "oak", "cherry"));
        put("finger_lime", new WoodSet("finger_lime", "finger_lime", "finger_lime", "oak", "oak"));
        put("key_lime", new WoodSet("key_lime", "key_lime", "key_lime", "oak", "oak"));
        put("lemon", new WoodSet("lemon", "lemon", "lemon", "oak", "oak"));
        put("lime", new WoodSet("lime", "lime", "lime", "oak", "oak"));
        put("silver_lime", new WoodSet("silver_lime", "silver_lime", "silver_lime", "oak", "birch"));
    }};
}
