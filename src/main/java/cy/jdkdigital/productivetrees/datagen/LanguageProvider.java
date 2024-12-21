package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivelib.util.LangUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider
{
    public LanguageProvider(PackOutput output) {
        super(output, ProductiveTrees.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.productivetrees", "Productive Trees");
        add("block.productivetrees.maple_sap", "Maple Sap");
        add("jei.productivetrees.tree_pollination", "Tree Pollination");
        add("jei.productivetrees.tree_fruiting", "Tree Fruiting");
        add("jei.productivetrees.log_stripping", "Log Stripping");
        add("jei.productivetrees.sawmill", "Sawmill");
        add("emi.category.productivetrees.pollination", "Tree Pollination");
        add("emi.category.productivetrees.fruiting", "Tree Fruiting");
        add("emi.category.productivetrees.stripping", "Log Stripping");
        add("emi.category.productivetrees.sawmill", "Sawmill");
        add("productivetrees.pollen.name", "%s");
        add("productivetrees.screen.progress", "Progress: %s");
        add("productivetrees.information.upgrade.upgrade_pollen_sieve", "With this upgrade installed in the hive some pollen collected by bees will be sifted and deposited in the hive.");
        add("productivetrees.information.pollen", "Use on a leaf to manually pollinate it.");
        add("productivetrees.sapling.configurations", "Configurations: %s");
        add("item.productivetrees.roasted_coffee_bean", "Roasted Coffee Beans");

        add("productivetrees.sapling_description.black_ember", "Black Ember saplings can be found hidden away in bastion treasure chests in the Nether guarded by wild and hangry brutes.");
        add("productivetrees.sapling_description.blue_yonder", "Blue Yonder saplings loved to travel and see the world. They were transported by ship because of their delicate roots and can still be found in the treasure chests of sunken galleons.");
        add("productivetrees.sapling_description.firecracker", "These explosive trees were banned from parties eons ago but can still be found in ancient cities deep underground.");
        add("productivetrees.sapling_description.flickering_sun", "In the treasure rooms of desert temples among the rotten flesh, bones and spider eyes you'll sometimes find this rare sapling.");
        add("productivetrees.sapling_description.soul_tree", "Can be found where saplings go to die, in the far reached on the end in cities long forgotten.");
        add("productivetrees.sapling_description.brown_amber", "Tucked beneath the crushing weight of the cold ocean water, this sapling is waiting to be unearthed.");

        add("entity.productivebees.allergy_bee", "Allergy Bee");
        add("productivebees.ingredient.description.allergy_bee", "Puts in an extra effort to collect pollen so your allergies don't act up while working with trees.");

        add(TreeRegistrator.POLLINATED_LEAVES.get(), "Pollinated Leaves");
        add(TreeRegistrator.STRIPPER.get(), "Stripper");
        add(TreeRegistrator.SAWMILL.get(), "Sawmill");
        add(TreeRegistrator.WOOD_WORKER.get(), "Carpentry Bench");
        add(TreeRegistrator.POLLEN_SIFTER.get(), "Pollen Sifter");
        add(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get(), "Time Traveller Display");
        add(TreeRegistrator.COCONUT_SPROUT.get(), "Coconut Sprout");

        add(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get(), "Upgrade: Pollen Sieve");
        add(TreeRegistrator.POLLEN.get(), "Pollen");
        add(TreeRegistrator.SAWDUST.get(), "Sawdust");
        add(TreeRegistrator.ALLSPICE.get(), "Allspice");
        add(TreeRegistrator.CAROB.get(), "Carob");
        add(TreeRegistrator.COFFEE_BEAN.get(), "Coffee Beans");
        add(TreeRegistrator.CLOVE.get(), "Clove");
        add(TreeRegistrator.CINNAMON.get(), "Cinnamon");
        add(TreeRegistrator.NUTMEG.get(), "Nutmeg");
        add(TreeRegistrator.STAR_ANISE.get(), "Star Anise");
        add(TreeRegistrator.CORK.get(), "Cork");
        add(TreeRegistrator.FUSTIC.get(), "Fustic");
        add(TreeRegistrator.HAEMATOXYLIN.get(), "Haematoxylin");
        add(TreeRegistrator.DRACAENA_SAP.get(), "Dracaena Sap");
        add(TreeRegistrator.MAPLE_SAP_BUCKET.get(), "Maple Sap");
        add(TreeRegistrator.RUBBER.get(), "Rubber");
        add(TreeRegistrator.CURED_RUBBER.get(), "Cured Rubber");
        add(TreeRegistrator.SANDALWOOD_OIL.get(), "Sandalwood Oil");

        ProductiveTrees.ITEMS.getEntries().forEach(itemDeferredHolder -> {
            var stack = new ItemStack(itemDeferredHolder.get());
            if (stack.has(DataComponents.FOOD)) {
                add(itemDeferredHolder.get(), LangUtil.capName(BuiltInRegistries.ITEM.getKey(itemDeferredHolder.get()).getPath()));
            }
        });

        TreeRegistrator.CRATED_CROPS.forEach(crate -> {
            add(BuiltInRegistries.BLOCK.get(crate), "Crate of " + LangUtil.pluralCapName(crate.getPath().replace("_crate", "")));
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            String name = id.getPath();
            add(TreeUtil.getBlock(id, "_leaves"), LangUtil.capName(name) + " Leaves");
            add(TreeUtil.getBlock(id, "_sapling"), LangUtil.capName(name) + " Sapling");
            add(TreeUtil.getBlock(id, "_potted_sapling"), LangUtil.capName(name) + " Potted Sapling");
            if (treeObject.hasFruit()) {
                add(TreeUtil.getBlock(id, "_fruit"), LangUtil.capName(name) + " Fruiting Leaves");
                if (!treeObject.getId().getPath().contains("copper_beech") && !treeObject.getId().getPath().contains("purple_blackthorn")) {
                    add("tag.item.c.storage_blocks." + treeObject.getFruit().fruitItem().getPath(), LangUtil.pluralCapName(treeObject.getFruit().fruitItem().getPath()));
                }
            }
            addWoodStuff(treeObject, name);
            add("block.productivetrees." + name + ".latin", getLatinName(name));
            add("tag.item.productivetrees." + name + "_logs", LangUtil.capName(name) + " Logs");
        });
    }

    private void addWoodStuff(WoodObject woodObject, String name) {
        add(TreeUtil.getBlock(woodObject.getId(), "_log"), LangUtil.capName(name) + " Log");
        add(TreeUtil.getBlock(woodObject.getId(), "_wood"), LangUtil.capName(name) + " Wood");
        add(TreeUtil.getBlock(woodObject.getId(), "_stripped_log"), LangUtil.capName(name) + " Stripped Log");
        add(TreeUtil.getBlock(woodObject.getId(), "_stripped_wood"), LangUtil.capName(name) + " Stripped Wood");
        add(TreeUtil.getBlock(woodObject.getId(), "_planks"), LangUtil.capName(name) + " Planks");
        if (!ProductiveTrees.isMinimal) {
            add(TreeUtil.getBlock(woodObject.getId(), "_stairs"), LangUtil.capName(name) + " Stairs");
            add(TreeUtil.getBlock(woodObject.getId(), "_slab"), LangUtil.capName(name) + " Slab");
            add(TreeUtil.getBlock(woodObject.getId(), "_fence"), LangUtil.capName(name) + " Fence");
            add(TreeUtil.getBlock(woodObject.getId(), "_fence_gate"), LangUtil.capName(name) + " Fence Gate");
            add(TreeUtil.getBlock(woodObject.getId(), "_button"), LangUtil.capName(name) + " Button");
            add(TreeUtil.getBlock(woodObject.getId(), "_pressure_plate"), LangUtil.capName(name) + " Pressure Plate");
            add(TreeUtil.getBlock(woodObject.getId(), "_door"), LangUtil.capName(name) + " Door");
            add(TreeUtil.getBlock(woodObject.getId(), "_trapdoor"), LangUtil.capName(name) + " Trapdoor");
            add(TreeUtil.getBlock(woodObject.getId(), "_bookshelf"), LangUtil.capName(name) + " Bookshelf");
            add(TreeUtil.getBlock(woodObject.getId(), "_sign"), LangUtil.capName(name) + " Sign");
            add(TreeUtil.getBlock(woodObject.getId(), "_hanging_sign"), LangUtil.capName(name) + " Hanging Sign");
        }
        if (woodObject.getStyle().hiveStyle() != null) {
            add("block.productivetrees." + woodObject.getId().withPath(p -> "advanced_" + p + "_beehive").getPath(), "Advanced " + LangUtil.capName(name) + " Beehive");
            add("block.productivetrees." + woodObject.getId().withPath(p ->  "expansion_box_" + p).getPath(), LangUtil.capName(name) + " Expansion Box");
        }
    }

    @Override
    public String getName() {
        return "Productive Trees translation provider";
    }

    private static String getLatinName(String name) {
        Map<String, String> names = new HashMap<>() {{
            put("alder", "Alnus glutinosa");
            put("allspice", "Pimenta dioica");
            put("almond", "Prunus amygdalus");
            put("apricot", "Prunus Armeniaca");
            put("aquilaria", "Aquilaria malaccensis");
            put("asai_palm", "Euterpe oleracea");
            put("ash", "Fraxinus excelsior");
            put("aspen", "Populus tremula");
            put("avocado", "Persea americana");
            put("balsa", "Ochroma pyramidale");
            put("balsam_fir", "Abies balsamea");
            put("banana", "Musa acuminata");
            put("beech", "Fagus sylvatica");
            put("blackthorn", "Prunus spinosa");
            put("black_cherry", "Prunus serotina");
            put("black_locust", "Robinia pseudoacacia");
            put("blue_mahoe", "Talipariti elatum");
            put("boxwood", "Buxus sempervirens");
            put("brazilwood", "Paubrasilia echinata");
            put("brazil_nut", "Bertholletia excelsa");
            put("breadfruit", "Artocarpus altilis");
            put("buddhas_hand", "Citrus medica var. sarcodactylis");
            put("bull_pine", "Pinus ponderosa");
            put("butternut", "Juglans cinerea");
            put("cacao", "Theobroma cacao");
            put("candlenut", "Aleurites moluccanus");
            put("carob", "Ceratonia siliqua");
            put("cashew", "Anacardium occidentale");
            put("cedar", "Cedrus libani");
            put("cempedak", "Artocarpus integer");
            put("ceylon_ebony", "Diospyros ebenum");
            put("cherry_plum", "Prunus cerasifera");
            put("cinnamon", "Cinnamomum verum");
            put("citron", "Citrus medica");
            put("copper_beech", "Fagus sylvatica purpurea");
            put("cork_oak", "Quercus suber");
            put("clove", "Syzygium aromaticum");
            put("cocobolo", "Dalbergia retusa");
            put("coconut", "Cocos nucifera");
            put("coffea", "Coffea arabica");
            put("copoazu", "Theobroma grandiflorum");
            put("cultivated_pear", "Pyrus communis");
            put("date_palm", "Phoenix dactylifera");
            put("dogwood", "Cornus florida");
            put("douglas_fir", "Pseudotsuga menziesii");
            put("elm", "Ulmus laevis");
            put("european_larch", "Larix decidua");
            put("finger_lime", "Citrus australasica");
            put("flowering_crabapple", "Malus floribunda");
            put("ginkgo", "Ginkgo biloba");
            put("golden_raspberry", "Rubus ellipticus");
            put("gooseberry", "Ribes uva-crispa");
            put("grandidiers_baobab", "Adansonia grandidieri");
            put("grapefruit", "Citrus paradisi");
            put("great_sallow", "Salix caprea");
            put("greenheart", "Chlorocardium rodiei");
            put("hawthorn", "Crataegus pinnatifida");
            put("hazel", "Corylus avellana");
            put("holly", "Ilex aquifolium");
            put("hornbeam", "Carpinus betulus");
            put("ipe", "Handroanthus albus");
            put("pink_ipe", "Handroanthus impetiginosus");
            put("purple_ipe", "Handroanthus impetiginosus");
            put("white_ipe", "Tabebuia roseo-alba");
            put("iroko", "Milicia excelsa");
            put("jackfruit", "Artocarpus heterophyllus");
            put("juniper", "Juniperus communis");
            put("kadsura", "Kadsura japonica");
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
            put("beliy_naliv_apple", "Malus domestica 'White Cloud'");
            put("golden_delicious_apple", "Malus domestica 'Golden Delicious'");
            put("granny_smith_apple", "Malus domestica 'Granny Smith'");
            put("red_delicious_apple", "Malus domestica 'Red Delicious'");
            put("osage_orange", "Maclura pomifera");
            put("padauk", "Pterocarpus soyauxii");
            put("pandanus", "Pandanus tectorius");
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
            put("prairie_crabapple", "Malus ioensis");
            put("purpleheart", "Peltogyne purpurea");
            put("purple_blackthorn", "Prunus spinosa purpurea");
            put("rainbow_gum", "Eucalyptus deglupta");
            put("raspberry", "Rubus idaeus");
            put("redcurrant", "Ribes rubrum");
            put("red_banana", "Musa acuminata");
            put("red_maple", "Acer rubrum");
            put("rosewood", "Dalbergia nigra");
            put("rose_gum", "Eucalyptus grandis");
            put("rowan", "Sorbus aucuparia");
            put("rubber_tree", "Hevea brasiliensis");
            put("salak", "Salacca zalacca");
            put("sandalwood", "Santalum album");
            put("sand_pear", "Pyrus pyrifolia");
            put("satsuma", "Citrus unshiu");
            put("sequoia", "Sequoiadendron giganteum");
            put("silver_fir", "Abies alba");
            put("silver_lime", "Tilia tomentosa");
            put("socotra_dragon", "Dracaena cinnabari");
            put("soursop", "Annona muricata");
            put("sour_cherry", "Prunus cerasus");
            put("starfruit", "Averrhoa carambola");
            put("star_anise", "Illicium verum");
            put("sugar_apple", "Annona squamosa");
            put("sugar_maple", "Acer saccharum");
            put("swamp_gum", "Eucalyptus camphora");
            put("sweetgum", "Liquidambar styraciflua");
            put("sweet_chestnut", "Castanea sativa");
            put("sweet_crabapple", "Malus coronaria");
            put("sycamore_fig", "Ficus sycomorus");
            put("tangerine", "Citrus tangerina");
            put("teak", "Tectona grandis");
            put("walnut", "Juglans regia");
            put("wenge", "Millettia laurentii");
            put("western_hemlock", "Tsuga heterophylla");
            put("whitebeam", "Aria edulis");
            put("white_poplar", "Populus alba");
            put("white_willow", "Salix alba");
            put("wild_cherry", "Prunus avium");
            put("yellow_meranti", "Shorea faguetiana");
            put("yew", "Taxus baccata");
            put("zebrano", "Microberlinia brazzavillensis");
            put("elderberry", "Sambucus nigra");
            put("star_fruit", "Averrhoa carambola");

            put("black_ember", "Ignis obscurum");
            put("brown_amber", "Umbra electri");
            put("cave_dweller", "Specus habitatoria");
            put("firecracker", "Scintillat calamus");
            put("flickering_sun", "Stella coruscatio");
            put("foggy_blast", "Nebula inflatus");
            put("purple_spiral", "Ysabella purpurea");
            put("rippling_willow", "Salix fluctus");
            put("slimy_delight", "Salivarius delicium");
            put("sparkle_cherry", "Prunus splendico");
            put("soul_tree", "Aevum viatora");
            put("thunder_bolt", "");
            put("blue_yonder", "Stella caelus");
            put("time_traveller", "Aevum viatora");
            put("water_wonder", "Aevum viatora");
            put("night_fuchsia", "Fuchsia nox");
        }};

        return names.get(name);
    }
}
