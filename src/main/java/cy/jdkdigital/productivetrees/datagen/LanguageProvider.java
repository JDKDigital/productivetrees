package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider
{
    public LanguageProvider(PackOutput output) {
        super(output, ProductiveTrees.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.productivetrees", "Productive Trees");
        add("item.productivetrees.upgrade_pollen_sieve", "Upgrade: Pollen Sieve");
        add("jei.productivetrees.tree_pollination", "Tree Pollination");
        add("productivetrees.pollen.name", "%s");
        add("productivebees.information.upgrade.upgrade_pollen_sieve", "With this upgrade installed in the hive some pollen collected by bees will be sifted and deposited in the hive.");
        add("productivetrees.information.pollen", "Use on a leaf to manually pollinate it.");

        add(ProductiveTrees.POLLEN.get(), "Pollen");
        add(ProductiveTrees.POLLINATED_LEAVES.get(), "Pollinated Leaves");

        TreeFinder.trees.forEach((id, treeObject) -> {
            String name = id.getPath();
            add(treeObject.getLeafBlock().get(), capName(name) + " Leaves");
            add(treeObject.getSaplingBlock().get(), capName(name) + " Sapling");
            add(treeObject.getPottedSaplingBlock().get(), capName(name) + " Potted Sapling");
            if (treeObject.hasFruit()) {
                add(treeObject.getFruitBlock().get(), capName(name) + " Fruiting Leaves");
                var fruit = ForgeRegistries.ITEMS.getValue(treeObject.getFruit().fruitItem());
                if (fruit != null && treeObject.getFruit().fruitItem().getNamespace().equals(ProductiveTrees.MODID)) {
                    add(fruit, capName(treeObject.getFruit().fruitItem().getPath()));
                }
            }
            if (treeObject.registerWood()) {
                add(treeObject.getLogBlock().get(), capName(name) + " Log");
                add(treeObject.getWoodBlock().get(), capName(name) + " Wood");
                add(treeObject.getStrippedLogBlock().get(), capName(name) + " Stripped Log");
                add(treeObject.getStrippedWoodBlock().get(), capName(name) + " Stripped Wood");
                add(treeObject.getPlankBlock().get(), capName(name) + " Planks");
                add(treeObject.getStairsBlock().get(), capName(name) + " Stairs");
                add(treeObject.getSlabBlock().get(), capName(name) + " Slab");
                add(treeObject.getFenceBlock().get(), capName(name) + " Fence");
                add(treeObject.getFenceGateBlock().get(), capName(name) + " Fence Gate");
                add(treeObject.getButtonBlock().get(), capName(name) + " Button");
                add(treeObject.getPressurePlateBlock().get(), capName(name) + " Pressure Plate");
                add(treeObject.getHiveBlock().get(), "Advanced " + capName(name) + " Beehive");
                add(treeObject.getExpansionBoxBlock().get(), capName(name) + " Expansion Box");
            }
            add("block.productivetrees." + name + ".latin", getLatinName(name));
        });
    }

    @Override
    public String getName() {
        return "Productive Trees translation provider";
    }

    private String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }

    private static String getLatinName(String name) {
        Map<String, String> names = new HashMap<>() {{
            put("alder", "Alnus glutinosa");
            put("allspice", "");
            put("almond", "Prunus amygdalus");
            put("apricot", "");
            put("ash", "Fraxinus excelsior");
            put("aspen", "");
            put("avocado", "");
            put("balsa", "");
            put("balsam_fir", "");
            put("banana", "");
            put("beech", "Fagus sylvatica");
            put("blackberry", "");
            put("blackcurrant", "");
            put("blackthorn", "");
            put("black_cherry", "");
            put("black_locust", "Robinia pseudoacacia");
            put("blueberry", "");
            put("blue_mahoe", "Talipariti elatum");
            put("boxwood", "Buxus sempervirens");
            put("brazilwood", "Paubrasilia echinata");
            put("brazil_nut", "Bertholletia excelsa");
            put("buddhas_hand", "Citrus medica var. sarcodactylis");
            put("bull_pine", "Pinus ponderosa");
            put("butternut", "Juglans cinerea");
            put("candlenut", "Aleurites moluccanus");
            put("cashew", "Anacardium occidentale");
            put("cherry_plum", "Prunus cerasifera");
            put("chilli_pepper", "Capsicum annuum");
            put("cinnamon", "Cinnamomum verum");
            put("citron", "Citrus medica");
            put("clove", "Syzygium aromaticum");
            put("cocobolo", "Dalbergia retusa");
            put("coconut", "Cocos nucifera");
            put("coffea", "Coffea arabica");
            put("copper_beech", "Fagus sylvatica purpurea");
            put("cranberry", "");
            put("cultivated_pear", "Pyrus communis");
            put("date_palm", "Phoenix dactylifera");
            put("desert_acacia", "");
            put("douglas_fir", "Pseudotsuga menziesii");
            put("dwarf_hazel", "");
            put("elderberry", "");
            put("elm", "");
            put("finger_lime", "");
            put("flowering_crabapple", "");
            put("ginkgo", "");
            put("golden_raspberry", "");
            put("gooseberry", "");
            put("grandidiers_baobab", "");
            put("grapefruit", "");
            put("great_cedar", "");
            put("great_sallow", "");
            put("hawthorne", "");
            put("hazel", "");
            put("holly", "");
            put("hornbeam", "");
            put("ipe", "");
            put("iroko", "");
            put("juniper", "");
            put("kapok", "");
            put("key_lime", "");
            put("kumquat", "");
            put("lawson_cypress", "");
            put("lemon", "");
            put("lime", "");
            put("loblolly_pine", "");
            put("logwood", "");
            put("mahogany", "");
            put("mandarin", "");
            put("mango", "");
            put("monkey_puzzle", "");
            put("mundane_larch", "");
            put("myrtle_ebony", "");
            put("nectarine", "");
            put("nutmeg", "");
            put("old_fustic", "");
            put("olive", "");
            put("orange", "");
            put("orchard_apple", "");
            put("osange_orange", "");
            put("padauk", "");
            put("papaya", "");
            put("peach", "");
            put("pecan", "");
            put("pink_ivory", "");
            put("pistachio", "Pistacia vera");
            put("plantain", "");
            put("plum", "");
            put("pomelo", "");
            put("prarie_crabapple", "");
            put("purpleheart", "");
            put("rainbow_gum", "");
            put("raspberry", "");
            put("redcurrant", "");
            put("red_banana", "");
            put("red_maple", "");
            put("rosewood", "");
            put("rose_gum", "");
            put("rowan", "");
            put("sand_pear", "Pyrus pyrifolia");
            put("satsuma", "");
            put("sequoia", "");
            put("silver_fir", "");
            put("silver_lime", "");
            put("sipiri", "");
            put("sour_cherry", "");
            put("starfruit", "");
            put("star_anise", "");
            put("sugar_maple", "");
            put("swamp_gum", "");
            put("sweet_chestnut", "");
            put("sweet_crabapple", "");
            put("sweet_gum", "");
            put("sycamore_fig", "");
            put("tangerine", "");
            put("teak", "");
            put("walnut", "");
            put("wenge", "");
            put("western_hemlock", "");
            put("whitebeam", "");
            put("white_poplar", "");
            put("white_willow", "");
            put("wild_cherry", "");
            put("yellow_meranti", "");
            put("yew", "");
            put("ysabella_purpurea", "");
            put("zebrawood", "");

        }};

        return names.get(name);
    }
}
