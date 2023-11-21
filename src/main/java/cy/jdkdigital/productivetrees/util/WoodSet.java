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
        put("buddhas_hand", new WoodSet("buddhas_hand", "buddhas_hand", "buddhas_hand", "oak", "oak"));
        put("bull_pine", new WoodSet("bull_pine", "bull_pine", "bull_pine", "oak", "spruce"));
        put("coconut", new WoodSet("coconut", "coconut", "coconut", "oak", "mangrove"));
        put("coffea", new WoodSet("coffea", "coffea", "coffea", "oak", "bush"));
        put("copper_beech", new WoodSet("copper_beech", "copper_beech", "copper_beech", "oak", "birch"));
        put("douglas_fir", new WoodSet("douglas_fir", "douglas_fir", "douglas_fir", "oak", "spruce"));
        put("finger_lime", new WoodSet("finger_lime", "finger_lime", "finger_lime", "oak", "oak"));
        put("flowering_crabapple", new WoodSet("flowering_crabapple", "flowering_crabapple", "flowering_crabapple", "oak", "cherry"));
        put("key_lime", new WoodSet("key_lime", "key_lime", "key_lime", "oak", "oak"));
        put("lemon", new WoodSet("lemon", "lemon", "lemon", "oak", "oak"));
        put("lime", new WoodSet("lime", "lime", "lime", "oak", "oak"));
        put("loblolly_pine", new WoodSet("loblolly_pine", "loblolly_pine", "loblolly_pine", "oak", "spruce"));
        put("peach", new WoodSet("peach", "peach", "peach", "oak", "oak"));
        put("plantain", new WoodSet("plantain", "plantain", "plantain", "oak", "mangrove"));
        put("prarie_crabapple", new WoodSet("prarie_crabapple", "prarie_crabapple", "prarie_crabapple", "oak", "cherry"));
        put("purple_blackthorn", new WoodSet("purple_blackthorn", "purple_blackthorn", "purple_blackthorn", "oak", "cherry"));
        put("purpleheart", new WoodSet("purpleheart", "purpleheart", "purpleheart", "oak", "jungle"));
        put("rainbow_gum", new WoodSet("rainbow_gum", "rainbow_gum", "rainbow_gum", "oak", "jungle"));
        put("red_banana", new WoodSet("red_banana", "red_banana", "red_banana", "oak", "mangrove"));
        put("rose_gum", new WoodSet("rose_gum", "rose_gum", "rose_gum", "oak", "jungle"));
        put("silver_fir", new WoodSet("silver_fir", "silver_fir", "silver_fir", "oak", "spruce"));
        put("sour_cherry", new WoodSet("sour_cherry", "sour_cherry", "sour_cherry", "oak", "cherry"));
        put("sweet_crabapple", new WoodSet("sweet_crabapple", "sweet_crabapple", "sweet_crabapple", "oak", "cherry"));
        put("silver_lime", new WoodSet("silver_lime", "silver_lime", "silver_lime", "oak", "birch"));
        put("swamp_gum", new WoodSet("swamp_gum", "swamp_gum", "swamp_gum", "oak", "jungle"));
        put("sweetgum", new WoodSet("sweetgum", "sweetgum", "sweetgum", "oak", "jungle"));
        put("wild_cherry", new WoodSet("wild_cherry", "wild_cherry", "wild_cherry", "oak", "cherry"));
        put("white_willow", new WoodSet("white_willow", "white_willow", "white_willow", "oak", "birch"));

        put("mandarin", new WoodSet("mandarin", "mandarin", "mandarin", "oak", "oak"));
        put("nectarine", new WoodSet("nectarine", "nectarine", "nectarine", "oak", "oak"));
        put("orange", new WoodSet("orange", "orange", "orange", "oak", "oak"));
        put("osange_orange", new WoodSet("osange_orange", "osange_orange", "osange_orange", "oak", "oak"));
        put("tangerine", new WoodSet("tangerine", "tangerine", "tangerine", "oak", "oak"));
        put("myrtle_ebony", new WoodSet("myrtle_ebony", "myrtle_ebony", "myrtle_ebony", "oak", "dark_oak"));
        put("ceylon_ebony", new WoodSet("ceylon_ebony", "ceylon_ebony", "ceylon_ebony", "oak", "dark_oak"));
        put("moonlight_magic_crepe_myrtle", new WoodSet("moonlight_magic_crepe_myrtle", "moonlight_magic_crepe_myrtle", "moonlight_magic_crepe_myrtle", "oak", "dark_oak"));
        put("purple_crepe_myrtle", new WoodSet("purple_crepe_myrtle", "purple_crepe_myrtle", "purple_crepe_myrtle", "oak", "dark_oak"));
        put("red_crepe_myrtle", new WoodSet("red_crepe_myrtle", "red_crepe_myrtle", "red_crepe_myrtle", "oak", "dark_oak"));
        put("tuscarora_crepe_myrtle", new WoodSet("tuscarora_crepe_myrtle", "tuscarora_crepe_myrtle", "tuscarora_crepe_myrtle", "oak", "dark_oak"));

        put("twinkle_field", new WoodSet("twinkle_field", "twinkle_field", "twinkle_field", "oak", "jungle"));
    }};
}
