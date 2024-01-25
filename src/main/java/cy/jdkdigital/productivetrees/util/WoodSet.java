package cy.jdkdigital.productivetrees.util;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public record WoodSet(String woodStyle, String plankStyle, String leafStyle, String doorStyle, String saplingStyle, @Nullable String hiveStyle)
{
    public static Map<String, WoodSet> STYLES = new HashMap<>()
    {{
        put("alder", new WoodSet("alder", "alder", "alder", "alder", "oak", "fir"));
        put("allspice", new WoodSet("allspice", "allspice", "allspice", "allspice", "oak", "palm"));
        put("almond", new WoodSet("almond", "almond", "almond", "almond", "acacia", "dark_oak"));
        put("apricot", new WoodSet("apricot", "apricot", "apricot", "apricot", "birch", "crimson"));
        put("asai_palm", new WoodSet("asai_palm", "asai_palm", "asai_palm", "asai_palm", "jungle", "bamboo"));
        put("ash", new WoodSet("ash", "ash", "ash", "ash", "oak", "dead"));
        put("aspen", new WoodSet("aspen", "aspen", "aspen", "aspen", "oak", "aspen"));
        put("avocado", new WoodSet("avocado", "avocado", "avocado", "avocado", "oak", "magic"));
        put("aquilaria", new WoodSet("aquilaria", "aquilaria", "aquilaria", "aquilaria", "dark_oak", "wisteria"));
        put("balsa", new WoodSet("balsa", "balsa", "balsa", "balsa", "oak", "warped"));
        put("balsam_fir", new WoodSet("balsam_fir", "balsam_fir", "balsam_fir", "balsam_fir", "spruce", "jungle"));
        put("banana", new WoodSet("banana", "banana", "banana", "banana", "mangrove", "bamboo"));
        put("beech", new WoodSet("beech", "beech", "beech", "beech", "dark_oak", "redwood"));
        put("beliy_naliv_apple", new WoodSet("beliy_naliv_apple", "beliy_naliv_apple", "beliy_naliv_apple", "beliy_naliv_apple", "oak", "umbran"));
        put("black_cherry", new WoodSet("black_cherry", "black_cherry", "black_cherry", "black_cherry", "birch", "wisteria"));
        put("black_locust", new WoodSet("black_locust", "black_locust", "black_locust", "black_locust", "oak", "dark_oak"));
        put("blackthorn", new WoodSet("blackthorn", "blackthorn", "blackthorn", "blackthorn", "cherry", "acacia"));
        put("blue_mahoe", new WoodSet("blue_mahoe", "blue_mahoe", "blue_mahoe", "blue_mahoe", "oak", "maple"));
        put("boxwood", new WoodSet("boxwood", "boxwood", "boxwood", "boxwood", "dark_oak", "dead"));
        put("brazil_nut", new WoodSet("brazil_nut", "brazil_nut", "brazil_nut", "brazil_nut", "acacia", "jungle"));
        put("brazilwood", new WoodSet("brazilwood", "brazilwood", "brazilwood", "brazilwood", "acacia", "cherry"));
        put("breadfruit", new WoodSet("breadfruit", "breadfruit", "breadfruit", "breadfruit", "dark_oak", "driftwood"));
        put("buddhas_hand", new WoodSet("buddhas_hand", "buddhas_hand", "buddhas_hand", "buddhas_hand", "oak", "dead"));
        put("bull_pine", new WoodSet("bull_pine", "bull_pine", "bull_pine", "bull_pine", "spruce", "magic"));
        put("butternut", new WoodSet("butternut", "butternut", "butternut", "butternut", "oak", "aspen"));
        put("cacao", new WoodSet("cacao", "cacao", "cacao", "cacao", "jungle", "hellbark"));
        put("candlenut", new WoodSet("candlenut", "candlenut", "candlenut", "candlenut", "oak", "grimwood"));
        put("carob", new WoodSet("carob", "carob", "carob", "carob", "dark_oak", "driftwood"));
        put("cashew", new WoodSet("cashew", "cashew", "cashew", "cashew", "acacia", "jacaranda"));
        put("cedar", new WoodSet("cedar", "cedar", "cedar", "cedar", "spruce", "river"));
        put("cempedak", new WoodSet("cempedak", "cempedak", "cempedak", "cempedak", "jungle", "bamboo"));
        put("ceylon_ebony", new WoodSet("ceylon_ebony", "ceylon_ebony", "ceylon_ebony", "ceylon_ebony", "dark_oak", "cherry"));
        put("cherry_plum", new WoodSet("cherry_plum", "cherry_plum", "cherry_plum", "cherry_plum", "cherry", "umbran"));
        put("cinnamon", new WoodSet("cinnamon", "cinnamon", "cinnamon", "cinnamon", "acacia", "mangrove"));
        put("citron", new WoodSet("citron", "citron", "citron", "citron", "oak", "willow"));
        put("clove", new WoodSet("clove", "clove", "clove", "clove", "bush", "wisteria"));
        put("cocobolo", new WoodSet("cocobolo", "cocobolo", "cocobolo", "cocobolo", "dark_oak", "driftwood"));
        put("coconut", new WoodSet("coconut", "coconut", "coconut", "coconut", "mangrove", "rosewood"));
        put("coffea", new WoodSet("coffea", "coffea", "coffea", "coffea", "bush", "umbran"));
        put("copoazu", new WoodSet("copoazu", "copoazu", "copoazu", "copoazu", "jungle", "jungle"));
        put("copper_beech", new WoodSet("copper_beech", "copper_beech", "copper_beech", "copper_beech", "birch", "yucca"));
        put("cork_oak", new WoodSet("cork_oak", "cork_oak", "cork_oak", "cork_oak", "oak", "spruce"));
        put("cultivated_pear", new WoodSet("cultivated_pear", "cultivated_pear", "cultivated_pear", "cultivated_pear", "oak", "maple"));
        put("date_palm", new WoodSet("date_palm", "date_palm", "date_palm", "date_palm", "mangrove", "palm"));
        put("dogwood", new WoodSet("dogwood", "dogwood", "dogwood", "dogwood", "cherry", "mangrove"));
        put("douglas_fir", new WoodSet("douglas_fir", "douglas_fir", "douglas_fir", "douglas_fir", "spruce", "jungle"));
        put("elderberry", new WoodSet("elderberry", "elderberry", "elderberry", "elderberry", "bush", null));
        put("elm", new WoodSet("elm", "elm", "elm", "elm", "oak", "kousa"));
        put("european_larch", new WoodSet("european_larch", "european_larch", "european_larch", "european_larch", "spruce", "maple"));
        put("finger_lime", new WoodSet("finger_lime", "finger_lime", "finger_lime", "finger_lime", "oak", "redwood"));
        put("flowering_crabapple", new WoodSet("flowering_crabapple", "flowering_crabapple", "flowering_crabapple", "flowering_crabapple", "cherry", "dead"));
        put("ginkgo", new WoodSet("ginkgo", "ginkgo", "ginkgo", "ginkgo", "cherry", "grimwood"));
        put("golden_delicious_apple", new WoodSet("golden_delicious_apple", "golden_delicious_apple", "golden_delicious_apple", "golden_delicious_apple", "oak", "aspen"));
        put("grandidiers_baobab", new WoodSet("grandidiers_baobab", "grandidiers_baobab", "grandidiers_baobab", "grandidiers_baobab", "dark_oak", "hellbark"));
        put("granny_smith_apple", new WoodSet("granny_smith_apple", "granny_smith_apple", "granny_smith_apple", "granny_smith_apple", "oak", "redwood"));
        put("grapefruit", new WoodSet("grapefruit", "grapefruit", "grapefruit", "grapefruit", "oak", "driftwood"));
        put("great_sallow", new WoodSet("great_sallow", "great_sallow", "great_sallow", "great_sallow", "mangrove", "bamboo"));
        put("greenheart", new WoodSet("greenheart", "greenheart", "greenheart", "greenheart", "jungle", "jungle"));
        put("juniper", new WoodSet("juniper", "juniper", "juniper", "juniper", "bush", "umbran"));
        put("hawthorn", new WoodSet("hawthorn", "hawthorn", "hawthorn", "hawthorn", "birch", "fir"));
        put("hazel", new WoodSet("hazel", "hazel", "hazel", "hazel", "birch", "kousa"));
        put("holly", new WoodSet("holly", "holly", "holly", "holly", "bush", "umbran"));
        put("hornbeam", new WoodSet("hornbeam", "hornbeam", "hornbeam", "hornbeam", "acacia", "aspen"));
        put("ipe", new WoodSet("ipe", "ipe", "ipe", "ipe", "birch", "cherry"));
        put("iroko", new WoodSet("iroko", "iroko", "iroko", "iroko", "birch", "jacaranda"));
        put("jackfruit", new WoodSet("jackfruit", "jackfruit", "jackfruit", "jackfruit", "jungle", "fir"));
        put("kapok", new WoodSet("kapok", "kapok", "kapok", "kapok", "oak", "grimwood"));
        put("key_lime", new WoodSet("key_lime", "key_lime", "key_lime", "key_lime", "oak", "dark_oak"));
        put("kumquat", new WoodSet("kumquat", "kumquat", "kumquat", "kumquat", "oak", "rosewood"));
        put("lawson_cypress", new WoodSet("lawson_cypress", "lawson_cypress", "lawson_cypress", "lawson_cypress", "spruce", "kousa"));
        put("lemon", new WoodSet("lemon", "lemon", "lemon", "lemon", "oak", "birch"));
        put("lime", new WoodSet("lime", "lime", "lime", "lime", "oak", "aspen"));
        put("loblolly_pine", new WoodSet("loblolly_pine", "loblolly_pine", "loblolly_pine", "loblolly_pine", "spruce", "acacia"));
        put("logwood", new WoodSet("logwood", "logwood", "logwood", "logwood", "acacia", "mangrove"));
        put("mahogany", new WoodSet("mahogany", "mahogany", "mahogany", "mahogany", "dark_oak", "mahogany"));
        put("mango", new WoodSet("mango", "mango", "mango", "mango", "oak", "cherry"));
        put("mandarin", new WoodSet("mandarin", "mandarin", "mandarin", "mandarin", "oak", "birch"));
        put("monkey_puzzle", new WoodSet("monkey_puzzle", "monkey_puzzle", "monkey_puzzle", "monkey_puzzle", "spruce", "palm"));
        put("moonlight_magic_crepe_myrtle", new WoodSet("moonlight_magic_crepe_myrtle", "moonlight_magic_crepe_myrtle", "moonlight_magic_crepe_myrtle", "moonlight_magic_crepe_myrtle", "dark_oak", "river"));
        put("myrtle_ebony", new WoodSet("myrtle_ebony", "myrtle_ebony", "myrtle_ebony", "myrtle_ebony", "dark_oak", "mangrove"));
        put("nectarine", new WoodSet("nectarine", "nectarine", "nectarine", "nectarine", "oak", "fir"));
        put("nutmeg", new WoodSet("nutmeg", "nutmeg", "nutmeg", "nutmeg", "oak", "aspen"));
        put("old_fustic", new WoodSet("old_fustic", "old_fustic", "old_fustic", "old_fustic", "acacia", "acacia"));
        put("olive", new WoodSet("olive", "olive", "olive", "olive", "acacia", "driftwood"));
        put("orange", new WoodSet("orange", "orange", "orange", "orange", "oak", "yucca"));
        put("osange_orange", new WoodSet("osange_orange", "osange_orange", "osange_orange", "osange_orange", "oak", "wisteria"));
        put("padauk", new WoodSet("padauk", "padauk", "padauk", "padauk", "jungle", "jungle"));
        put("pandanus", new WoodSet("pandanus", "pandanus", "pandanus", "pandanus", "mangrove", "jacaranda"));
        put("papaya", new WoodSet("papaya", "papaya", "papaya", "papaya", "oak", "umbran"));
        put("peach", new WoodSet("peach", "peach", "peach", "peach", "oak", "rosewood"));
        put("pecan", new WoodSet("pecan", "pecan", "pecan", "pecan", "oak", "grimwood"));
        put("persimmon", new WoodSet("persimmon", "persimmon", "persimmon", "persimmon", "oak", "maple"));
        put("pink_ivory", new WoodSet("pink_ivory", "pink_ivory", "pink_ivory", "pink_ivory", "birch", "birch"));
        put("pistachio", new WoodSet("pistachio", "pistachio", "pistachio", "pistachio", "acacia", "acacia"));
        put("plantain", new WoodSet("plantain", "plantain", "plantain", "plantain", "mangrove", "magic"));
        put("plum", new WoodSet("plum", "plum", "plum", "plum", "cherry", "kousa"));
        put("pomegranate", new WoodSet("pomegranate", "pomegranate", "pomegranate", "pomegranate", "oak", "driftwood"));
        put("pomelo", new WoodSet("pomelo", "pomelo", "pomelo", "pomelo", "oak", "aspen"));
        put("prairie_crabapple", new WoodSet("prairie_crabapple", "prairie_crabapple", "prairie_crabapple", "prairie_crabapple", "cherry", "umbran"));
        put("purple_blackthorn", new WoodSet("purple_blackthorn", "purple_blackthorn", "purple_blackthorn", "purple_blackthorn", "cherry", "jungle"));
        put("purple_crepe_myrtle", new WoodSet("purple_crepe_myrtle", "purple_crepe_myrtle", "purple_crepe_myrtle", "purple_crepe_myrtle", "dark_oak", "river"));
        put("purpleheart", new WoodSet("purpleheart", "purpleheart", "purpleheart", "purpleheart", "jungle", "birch"));
        put("rainbow_gum", new WoodSet("rainbow_gum", "rainbow_gum", "rainbow_gum", "rainbow_gum", "jungle", "maple"));
        put("red_banana", new WoodSet("red_banana", "red_banana", "red_banana", "red_banana", "mangrove", "magic"));
        put("red_crepe_myrtle", new WoodSet("red_crepe_myrtle", "red_crepe_myrtle", "red_crepe_myrtle", "red_crepe_myrtle", "dark_oak", "river"));
        put("red_delicious_apple", new WoodSet("red_delicious_apple", "red_delicious_apple", "red_delicious_apple", "red_delicious_apple", "oak", "redwood"));
        put("red_maple", new WoodSet("red_maple", "red_maple", "red_maple", "red_maple", "birch", "maple"));
        put("rose_gum", new WoodSet("rose_gum", "rose_gum", "rose_gum", "rose_gum", "jungle", "mahogany"));
        put("rosewood", new WoodSet("rosewood", "rosewood", "rosewood", "rosewood", "birch", "rosewood"));
        put("rowan", new WoodSet("rowan", "rowan", "rowan", "rowan", "birch", "dark_oak"));
        put("rubber_tree", new WoodSet("rubber_tree", "rubber_tree", "rubber_tree", "rubber_tree", "jungle", "acacia"));
        put("sandalwood", new WoodSet("sandalwood", "sandalwood", "sandalwood", "sandalwood", "acacia", "driftwood"));
        put("sand_pear", new WoodSet("sand_pear", "sand_pear", "sand_pear", "sand_pear", "acacia", "jacaranda"));
        put("salak", new WoodSet("salak", "salak", "salak", "salak", "mangrove", "maple"));
        put("satsuma", new WoodSet("satsuma", "satsuma", "satsuma", "satsuma", "oak", "umbran"));
        put("sequoia", new WoodSet("sequoia", "sequoia", "sequoia", "sequoia", "dark_oak", "magic"));
        put("silver_fir", new WoodSet("silver_fir", "silver_fir", "silver_fir", "silver_fir", "spruce", "spruce"));
        put("socotra_dragon", new WoodSet("socotra_dragon", "socotra_dragon", "socotra_dragon", "socotra_dragon", "acacia", "dark_oak"));
        put("soursop", new WoodSet("soursop", "soursop", "soursop", "soursop", "bush", "magic"));
        put("sour_cherry", new WoodSet("sour_cherry", "sour_cherry", "sour_cherry", "sour_cherry", "cherry", "umbran"));
        put("sweet_chestnut", new WoodSet("sweet_chestnut", "sweet_chestnut", "sweet_chestnut", "sweet_chestnut", "oak", "magic"));
        put("sweet_crabapple", new WoodSet("sweet_crabapple", "sweet_crabapple", "sweet_crabapple", "sweet_crabapple", "cherry", "river"));
        put("silver_lime", new WoodSet("silver_lime", "silver_lime", "silver_lime", "silver_lime", "birch", "dead"));
        put("star_anise", new WoodSet("star_anise", "star_anise", "star_anise", "star_anise", "birch", "redwood"));
        put("star_fruit", new WoodSet("star_fruit", "star_fruit", "star_fruit", "star_fruit", "birch", "river"));
        put("sugar_apple", new WoodSet("sugar_apple", "sugar_apple", "sugar_apple", "sugar_apple", "birch", "kousa"));
        put("sugar_maple", new WoodSet("sugar_maple", "sugar_maple", "sugar_maple", "sugar_maple", "birch", "maple"));
        put("swamp_gum", new WoodSet("swamp_gum", "swamp_gum", "swamp_gum", "swamp_gum", "jungle", "mahogany"));
        put("sweetgum", new WoodSet("sweetgum", "sweetgum", "sweetgum", "sweetgum", "jungle", "dead"));
        put("sycamore_fig", new WoodSet("sycamore_fig", "sycamore_fig", "sycamore_fig", "sycamore_fig", "birch", "yucca"));
        put("tangerine", new WoodSet("tangerine", "tangerine", "tangerine", "tangerine", "oak", "magic"));
        put("teak", new WoodSet("teak", "teak", "teak", "teak", "birch", "dark_oak"));
        put("tuscarora_crepe_myrtle", new WoodSet("tuscarora_crepe_myrtle", "tuscarora_crepe_myrtle", "tuscarora_crepe_myrtle", "tuscarora_crepe_myrtle", "dark_oak", "dead"));
        put("walnut", new WoodSet("walnut", "walnut", "walnut", "walnut", "oak", "redwood"));
        put("wenge", new WoodSet("wenge", "wenge", "wenge", "wenge", "dark_oak", "river"));
        put("western_hemlock", new WoodSet("western_hemlock", "western_hemlock", "western_hemlock", "western_hemlock", "spruce", "fir"));
        put("whitebeam", new WoodSet("whitebeam", "whitebeam", "whitebeam", "whitebeam", "birch", "umbran"));
        put("white_poplar", new WoodSet("white_poplar", "white_poplar", "white_poplar", "white_poplar", "birch", "river"));
        put("white_willow", new WoodSet("white_willow", "white_willow", "white_willow", "white_willow", "birch", "birch"));
        put("wild_cherry", new WoodSet("wild_cherry", "wild_cherry", "wild_cherry", "wild_cherry", "cherry", "rosewood"));
        put("yellow_meranti", new WoodSet("yellow_meranti", "yellow_meranti", "yellow_meranti", "yellow_meranti", "jungle", "mahogany"));
        put("yew", new WoodSet("yew", "yew", "yew", "yew", "bush", "rosewood"));
        put("zebrano", new WoodSet("zebrano", "zebrano", "zebrano", "zebrano", "jungle", "redwood"));

        put("brown_amber", new WoodSet("brown_amber", "brown_amber", "brown_amber", "brown_amber", "jungle", "snake_block"));
        put("black_ember", new WoodSet("black_ember", "black_ember", "black_ember", "black_ember", "jungle", "snake_block"));
        put("cave_dweller", new WoodSet("cave_dweller", "cave_dweller", "cave_dweller", "cave_dweller", "jungle", "snake_block"));
        put("firecracker", new WoodSet("firecracker", "firecracker", "firecracker", "firecracker", "jungle", "snake_block"));
        put("flickering_sun", new WoodSet("flickering_sun", "flickering_sun", "flickering_sun", "flickering_sun", "jungle", "snake_block"));
        put("foggy_blast", new WoodSet("foggy_blast", "foggy_blast", "foggy_blast", "foggy_blast", "jungle", "snake_block"));
        put("night_fuchsia", new WoodSet("night_fuchsia", "night_fuchsia", "night_fuchsia", "night_fuchsia", "jungle", "snake_block"));
        put("rippling_willow", new WoodSet("rippling_willow", "rippling_willow", "rippling_willow", "rippling_willow", "jungle", "snake_block"));
        put("sparkle_cherry", new WoodSet("sparkle_cherry", "sparkle_cherry", "sparkle_cherry", "sparkle_cherry", "jungle", "snake_block"));
        put("slimy_delight", new WoodSet("slimy_delight", "slimy_delight", "slimy_delight", "slimy_delight", "jungle", "snake_block"));
        put("soul_tree", new WoodSet("soul_tree", "soul_tree", "soul_tree", "soul_tree", "jungle", "snake_block"));
        put("time_traveller", new WoodSet("time_traveller", "time_traveller", "time_traveller", "time_traveller", "jungle", "snake_block"));
        put("thunder_bolt", new WoodSet("thunder_bolt", "thunder_bolt", "thunder_bolt", "thunder_bolt", "jungle", "snake_block"));
        put("blue_yonder", new WoodSet("blue_yonder", "blue_yonder", "blue_yonder", "blue_yonder", "jungle", "snake_block"));
        put("purple_spiral", new WoodSet("purple_spiral", "purple_spiral", "purple_spiral", "purple_spiral", "jungle", "snake_block"));
        put("water_wonder", new WoodSet("water_wonder", "water_wonder", "water_wonder", "water_wonder", "jungle", "snake_block"));
    }};
}
