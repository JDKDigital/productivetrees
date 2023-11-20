package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
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
        add("jei.productivetrees.log_stripping", "Log Stripping");
        add("jei.productivetrees.sawmill", "Sawmill");
        add("productivetrees.pollen.name", "%s");
        add("productivetrees.screen.progress", "Progress: %s");
        add("productivebees.information.upgrade.upgrade_pollen_sieve", "With this upgrade installed in the hive some pollen collected by bees will be sifted and deposited in the hive.");
        add("productivetrees.information.pollen", "Use on a leaf to manually pollinate it.");

        add(TreeRegistrator.POLLEN.get(), "Pollen");
        add(TreeRegistrator.SAWDUST.get(), "Sawdust");
        add(TreeRegistrator.POLLINATED_LEAVES.get(), "Pollinated Leaves");
        add(TreeRegistrator.STRIPPER.get(), "Stripper");
        add(TreeRegistrator.SAWMILL.get(), "Sawmill");
        add(TreeRegistrator.WOOD_WORKER.get(), "Carpentry Bench");

        TreeFinder.trees.forEach((id, treeObject) -> {
            String name = id.getPath();
            add(treeObject.getLeafBlock().get(), capName(name) + " Leaves");
            add(treeObject.getSaplingBlock().get(), capName(name) + " Sapling");
            add(treeObject.getPottedSaplingBlock().get(), capName(name) + " Potted Sapling");
            if (treeObject.hasFruit()) {
                add(treeObject.getFruitBlock().get(), capName(name) + " Fruiting Leaves");
                var fruit = ForgeRegistries.ITEMS.getValue(treeObject.getFruit().fruitItem());
                if (fruit != null && fruit != Items.AIR) {
                    try {
                        add(fruit, capName(treeObject.getFruit().fruitItem().getPath()));
                    } catch (IllegalStateException ise) {
                        // Ignore dupes
                    }
                } else {
                    ProductiveTrees.LOGGER.warn("Invalid fruit item " + fruit + " " + treeObject.getFruit().fruitItem());
                }
            }
            if (treeObject.registerWood()) {
                add(treeObject.getLogBlock().get(), capName(name) + " Log");
                add(treeObject.getWoodBlock().get(), capName(name) + " Wood");
                add(treeObject.getStrippedLogBlock().get(), "Stripped " + capName(name) + " Log");
                add(treeObject.getStrippedWoodBlock().get(), "Stripped " + capName(name) + " Wood");
                add(treeObject.getPlankBlock().get(), capName(name) + " Planks");
                add(treeObject.getStairsBlock().get(), capName(name) + " Stairs");
                add(treeObject.getSlabBlock().get(), capName(name) + " Slab");
                add(treeObject.getFenceBlock().get(), capName(name) + " Fence");
                add(treeObject.getFenceGateBlock().get(), capName(name) + " Fence Gate");
                add(treeObject.getButtonBlock().get(), capName(name) + " Button");
                add(treeObject.getPressurePlateBlock().get(), capName(name) + " Pressure Plate");
                add(treeObject.getDoorBlock().get(), capName(name) + " Door");
                add(treeObject.getTrapdoorBlock().get(), capName(name) + " Trapdoor");
                add(treeObject.getBookshelfBlock().get(), capName(name) + " Bookshelf");
                add(treeObject.getSignBlock().get(), capName(name) + " Sign");
                add(treeObject.getHangingSignBlock().get(), capName(name) + " Hanging Sign");
                add(treeObject.getHiveBlock().get(), "Advanced " + capName(name) + " Beehive");
                add(treeObject.getExpansionBoxBlock().get(), capName(name) + " Expansion Box");
            }
            add("block.productivetrees." + name + ".latin", getLatinName(name));
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            String name = id.getPath();
            add(woodObject.getLogBlock().get(), capName(name) + " Log");
            add(woodObject.getWoodBlock().get(), capName(name) + " Wood");
            add(woodObject.getStrippedLogBlock().get(), capName(name) + " Stripped Log");
            add(woodObject.getStrippedWoodBlock().get(), capName(name) + " Stripped Wood");
            add(woodObject.getPlankBlock().get(), capName(name) + " Planks");
            add(woodObject.getStairsBlock().get(), capName(name) + " Stairs");
            add(woodObject.getSlabBlock().get(), capName(name) + " Slab");
            add(woodObject.getFenceBlock().get(), capName(name) + " Fence");
            add(woodObject.getFenceGateBlock().get(), capName(name) + " Fence Gate");
            add(woodObject.getButtonBlock().get(), capName(name) + " Button");
            add(woodObject.getPressurePlateBlock().get(), capName(name) + " Pressure Plate");
            if (woodObject.getHiveStyle() != null) {
                add(woodObject.getHiveBlock().get(), "Advanced " + capName(name) + " Beehive");
                add(woodObject.getExpansionBoxBlock().get(), capName(name) + " Expansion Box");
            }
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
            put("akebia", "Akebia quinata");
            put("alder", "Alnus glutinosa");
            put("allspice", "Pimenta dioica");
            put("almond", "Prunus amygdalus");
            put("apricot", "Prunus Armeniaca");
            put("ash", "Fraxinus excelsior");
            put("aspen", "Populus tremula");
            put("avocado", "Persea americana");
            put("balsa", "Ochroma pyramidale");
            put("balsam_fir", "Abies balsamea");
            put("banana", "Musa acuminata");
            put("beech", "Fagus sylvatica");
            put("blackberry", "Rubus fruticosus");
            put("blackcurrant", "Ribes nigrum");
            put("blackthorn", "Prunus spinosa");
            put("black_cherry", "Prunus serotina");
            put("black_locust", "Robinia pseudoacacia");
            put("blueberry", "Vaccinium angustifolium");
            put("blue_mahoe", "Talipariti elatum");
            put("boxwood", "Buxus sempervirens");
            put("brazilwood", "Paubrasilia echinata");
            put("brazil_nut", "Bertholletia excelsa");
            put("breadfruit", "Artocarpus altilis");
            put("buddhas_hand", "Citrus medica var. sarcodactylis");
            put("bull_pine", "Pinus ponderosa");
            put("butternut", "Juglans cinerea");
            put("candlenut", "Aleurites moluccanus");
            put("carob", "Ceratonia siliqua");
            put("cashew", "Anacardium occidentale");
            put("cherry_plum", "Prunus cerasifera");
            put("cheese_plant", "Monstera deliciosa");
            put("chilli_pepper", "Capsicum annuum");
            put("cinnamon", "Cinnamomum verum");
            put("citron", "Citrus medica");
            put("clove", "Syzygium aromaticum");
            put("cocobolo", "Dalbergia retusa");
            put("coconut", "Cocos nucifera");
            put("coffea", "Coffea arabica");
            put("copoazu", "Theobroma grandiflorum");
            put("copper_beech", "Fagus sylvatica purpurea");
            put("cranberry", "Vaccinium oxycoccos");
            put("cultivated_pear", "Pyrus communis");
            put("date_palm", "Phoenix dactylifera");
            put("douglas_fir", "Pseudotsuga menziesii");
            put("elderberry", "Sambucus nigra");
            put("elm", "Ulmus laevis");
            put("finger_lime", "Citrus australasica");
            put("flowering_crabapple", "Malus floribunda");
            put("ginkgo", "Ginkgo biloba");
            put("golden_raspberry", "Rubus ellipticus");
            put("gooseberry", "Ribes uva-crispa");
            put("grandidiers_baobab", "Adansonia grandidieri");
            put("grapefruit", "Citrus paradisi");
            put("cedar", "Cedrus libani");
            put("great_sallow", "Salix caprea");
            put("hawthorn", "Crataegus rhipidophylla");
            put("hazel", "Corylus avellana");
            put("holly", "Ilex aquifolium");
            put("hornbeam", "Carpinus betulus");
            put("ipe", "Handroanthus albus");
            put("iroko", "Milicia excelsa");
            put("juniper", "Juniperus communis");
            put("kapok", "Bombax ceiba");
            put("key_lime", "Citrus aurantiifolia");
            put("kumquat", "Citrus hindsii");
            put("lawson_cypress", "Chamaecyparis lawsoniana");
            put("lemon", "Citrus limon");
            put("lime", "Citrus limetta");
            put("loblolly_pine", "Pinus taeda");
            put("logwood", "Haematoxylum campechianum");
            put("mahogany", "Swietenia mahagoni");
            put("mandarin", "Citrus reticulata");
            put("mango", "Mangifera indica");
            put("miracle_berry", "Synsepalum dulcificum");
            put("monkey_puzzle", "Araucaria araucana");
            put("european_larch", "Larix decidua");
            put("ceylon_ebony", "Diospyros ebenum");
            put("myrtle_ebony", "Diospyros pentamera");
            put("purple_crepe_myrtle", "Lagerstroemia indica");
            put("moonlight_magic_crepe_myrtle", "Lagerstroemia indica 'PIILAG-IV'");
            put("red_crepe_myrtle", "Lagerstroemia indica 'Whit II'");
            put("tuscarora_crepe_myrtle", "Lagerstroemia indica 'Tuscarora'");
            put("nectarine", "Prunus var. nectarina");
            put("nutmeg", "Myristica fragrans");
            put("old_fustic", "Maclura tinctoria");
            put("olive", "Olea europaea");
            put("orange", "Citrus sinensis");
            put("orchard_apple", "Malus domestica");
            put("osange_orange", "Maclura pomifera");
            put("padauk", "Pterocarpus soyauxii");
            put("papaya", "Carica papaya");
            put("peach", "Prunus persica");
            put("pecan", "Carya illinoinensis");
            put("persimmon", "Diospyros kaki");
            put("pink_ivory", "Phyllogeiton zeyheri");
            put("pistachio", "Pistacia vera");
            put("plantain", "Musa paradisiaca");
            put("plum", "Prunus domestica");
            put("pomegranate", "Punica granatum");
            put("pomelo", "Citrus maxima");
            put("prarie_crabapple", "Malus ioensis");
            put("purple_blackthorn", "Prunus spinosa purpurea");
            put("purpleheart", "Peltogyne purpurea");
            put("rainbow_gum", "Eucalyptus deglupta");
            put("raspberry", "Rubus idaeus");
            put("redcurrant", "Ribes rubrum");
            put("red_banana", "Musa acuminata");
            put("red_maple", "Acer rubrum");
            put("rosewood", "Dalbergia nigra");
            put("rose_gum", "Eucalyptus grandis");
            put("rowan", "Sorbus aucuparia");
            put("sand_pear", "Pyrus pyrifolia");
            put("satsuma", "Citrus unshiu");
            put("sequoia", "Sequoia sempervirens");
            put("silver_fir", "Abies alba");
            put("silver_lime", "Tilia tomentosa");
            put("cogwood", "Chlorocardium rodiei");
            put("sour_cherry", "Prunus cerasus");
            put("starfruit", "Averrhoa carambola");
            put("star_anise", "Illicium verum");
            put("sugar_maple", "Acer saccharum");
            put("swamp_gum", "Eucalyptus camphora");
            put("sweet_chestnut", "Castanea sativa");
            put("sweet_crabapple", "Malus coronaria");
            put("sweetgum", "Liquidambar styraciflua");
            put("sycamore_fig", "Ficus sycomorus");
            put("tangerine", "Citrus tangerina");
            put("teak", "Tectona grandis");
            put("walnut", "Juglans regia");
            put("wenge", "Millettia laurentii");
            put("western_hemlock", "Tsuga heterophylla");
            put("whitebeam", "Sorbus aria");
            put("white_poplar", "Populus alba");
            put("white_willow", "Salix alba");
            put("wild_cherry", "Prunus avium");
            put("yellow_meranti", "Shorea faguetiana");
            put("yew", "Taxus baccata");
            put("zebrano", "Microberlinia brazzavillensis");
            put("ysabella_purpurea", "Ysabella purpurea");
            put("twinkle_field", "Stella caelus");
        }};

        return names.get(name);
    }
}
