package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, ProductiveTrees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);

        copy(cy.jdkdigital.productivebees.init.ModTags.HIVES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.HIVES);
        copy(cy.jdkdigital.productivebees.init.ModTags.BOXES_BLOCK, cy.jdkdigital.productivebees.init.ModTags.BOXES);

        tag(ModTags.CINNAMON).add(TreeRegistrator.CINNAMON.get());

        tag(ModTags.NUTS_ALMOND).add(TreeRegistrator.ALMOND.get());
        tag(ModTags.NUTS_ACORN).add(TreeRegistrator.ACORN.get());
        tag(ModTags.NUTS_BEECHNUT).add(TreeRegistrator.BEECHNUT.get());
        tag(ModTags.NUTS_BRAZIL_NUT).add(TreeRegistrator.BRAZIL_NUT.get());
        tag(ModTags.NUTS_BUTTERNUT).add(TreeRegistrator.BUTTERNUT.get());
        tag(ModTags.NUTS_CANDLENUT).add(TreeRegistrator.CANDLENUT.get());
        tag(ModTags.NUTS_CASHEW).add(TreeRegistrator.CASHEW.get());
        tag(ModTags.NUTS_CHESTNUT).add(TreeRegistrator.CHESTNUT.get());
        tag(ModTags.NUTS_COFFEE_BEAN).add(TreeRegistrator.COFFEE_BEAN.get());
        tag(ModTags.NUTS_GINKGO_NUT).add(TreeRegistrator.GINKGO_NUT.get());
        tag(ModTags.NUTS_HAZELNUT).add(TreeRegistrator.HAZELNUT.get());
        tag(ModTags.NUTS_PECAN).add(TreeRegistrator.PECAN.get());
        tag(ModTags.NUTS_PISTACHIO).add(TreeRegistrator.PISTACHIO.get());
        tag(ModTags.NUTS_WALNUT).add(TreeRegistrator.WALNUT.get());
        tag(ModTags.NUTS_CAROB).add(TreeRegistrator.CAROB.get());
        tag(ModTags.NUTS).addTags(ModTags.NUTS_ALMOND, ModTags.NUTS_ACORN, ModTags.NUTS_BEECHNUT, ModTags.NUTS_BRAZIL_NUT, ModTags.NUTS_BUTTERNUT, ModTags.NUTS_CANDLENUT, ModTags.NUTS_CASHEW, ModTags.NUTS_CHESTNUT, ModTags.NUTS_COFFEE_BEAN, ModTags.NUTS_GINKGO_NUT, ModTags.NUTS_HAZELNUT, ModTags.NUTS_PECAN, ModTags.NUTS_PISTACHIO, ModTags.NUTS_WALNUT, ModTags.NUTS_CAROB);

        tag(ModTags.BERRIES_BLACKBERRY).add(TreeRegistrator.BLACKBERRY.get());
        tag(ModTags.BERRIES_BLACKCURRANT).add(TreeRegistrator.BLACKCURRANT.get());
        tag(ModTags.BERRIES_BLUEBERRY).add(TreeRegistrator.BLUEBERRY.get());
        tag(ModTags.BERRIES_REDCURRANT).add(TreeRegistrator.REDCURRANT.get());
        tag(ModTags.BERRIES_CRANBERRY).add(TreeRegistrator.CRANBERRY.get());
        tag(ModTags.BERRIES_ELDERBERRY).add(TreeRegistrator.ELDERBERRY.get());
        tag(ModTags.BERRIES_GOOSEBERRY).add(TreeRegistrator.GOOSEBERRY.get());
        tag(ModTags.BERRIES_RASPBERRY).add(TreeRegistrator.RASPBERRY.get());
        tag(ModTags.BERRIES_JUNIPER).add(TreeRegistrator.JUNIPER.get());
        tag(ModTags.BERRIES_GOLDEN_RASPBERRY).add(TreeRegistrator.GOLDEN_RASPBERRY.get());
        tag(ModTags.BERRIES_SLOE).add(TreeRegistrator.SLOE.get());
        tag(ModTags.BERRIES_HAW).add(TreeRegistrator.HAW.get());
        tag(ModTags.BERRIES_MIRACLE_BERRY).add(TreeRegistrator.MIRACLE_BERRY.get());
        tag(ModTags.BERRIES).addTags(ModTags.BERRIES_BLACKBERRY, ModTags.BERRIES_BLACKCURRANT, ModTags.BERRIES_BLUEBERRY, ModTags.BERRIES_REDCURRANT, ModTags.BERRIES_CRANBERRY, ModTags.BERRIES_ELDERBERRY, ModTags.BERRIES_GOOSEBERRY, ModTags.BERRIES_RASPBERRY, ModTags.BERRIES_JUNIPER, ModTags.BERRIES_GOLDEN_RASPBERRY, ModTags.BERRIES_SLOE, ModTags.BERRIES_HAW, ModTags.BERRIES_MIRACLE_BERRY);

        tag(ModTags.FRUITS_APPLE).add(Items.APPLE, TreeRegistrator.GOLDEN_DELICIOUS.get(), TreeRegistrator.GRANNY_SMITH.get(), TreeRegistrator.BELIY_NALIV.get());
        tag(ModTags.FRUITS_CHERRY).add(TreeRegistrator.SPARKLING_CHERRY.get()).add(TreeRegistrator.SOUR_CHERRY.get()).add(TreeRegistrator.WILD_CHERRY.get()).add(TreeRegistrator.BLACK_CHERRY.get());
        tag(ModTags.FRUITS_APRICOT).add(TreeRegistrator.APRICOT.get());
        tag(ModTags.FRUITS_BLACK_CHERRY).add(TreeRegistrator.BLACK_CHERRY.get());
        tag(ModTags.FRUITS_CHERRY_PLUM).add(TreeRegistrator.CHERRY_PLUM.get());
        tag(ModTags.FRUITS_OLIVE).add(TreeRegistrator.OLIVE.get());
        tag(ModTags.FRUITS_OSANGE_ORANGE).add(TreeRegistrator.OSANGE_ORANGE.get());
        tag(ModTags.FRUITS_KUMQUAT).add(TreeRegistrator.KUMQUAT.get());
        tag(ModTags.FRUITS_WILD_CHERRY).add(TreeRegistrator.WILD_CHERRY.get());
        tag(ModTags.FRUITS_SOUR_CHERRY).add(TreeRegistrator.SOUR_CHERRY.get());
        tag(ModTags.FRUITS_DATE).add(TreeRegistrator.DATE.get());
        tag(ModTags.FRUITS_PLUM).add(TreeRegistrator.PLUM.get());
        tag(ModTags.FRUITS_AVOCADO).add(TreeRegistrator.AVOCADO.get());
        tag(ModTags.FRUITS_CRABAPPLE).add(TreeRegistrator.SWEET_CRABAPPLE.get());
        tag(ModTags.FRUITS_CRABAPPLE).add(TreeRegistrator.PRAIRIE_CRABAPPLE.get());
        tag(ModTags.FRUITS_CRABAPPLE).add(TreeRegistrator.FLOWERING_CRABAPPLE.get());
        tag(ModTags.FRUITS_FIG).add(TreeRegistrator.FIG.get());
        tag(ModTags.FRUITS_GRAPEFRUIT).add(TreeRegistrator.GRAPEFRUIT.get());
        tag(ModTags.FRUITS_NECTARINE).add(TreeRegistrator.NECTARINE.get());
        tag(ModTags.FRUITS_PEACH).add(TreeRegistrator.PEACH.get());
        tag(ModTags.FRUITS_PEAR).add(TreeRegistrator.PEAR.get());
        tag(ModTags.FRUITS_POMELO).add(TreeRegistrator.POMELO.get());
        tag(ModTags.FRUITS_SAND_PEAR).add(TreeRegistrator.SAND_PEAR.get());
        tag(ModTags.FRUITS_SATSUMA).add(TreeRegistrator.SATSUMA.get());
        tag(ModTags.FRUITS_STAR_FRUIT).add(TreeRegistrator.STAR_FRUIT.get());
        tag(ModTags.FRUITS_TANGERINE).add(TreeRegistrator.TANGERINE.get());
        tag(ModTags.FRUITS_AKEBIA).add(TreeRegistrator.AKEBIA.get());
        tag(ModTags.FRUITS_COPOAZU).add(TreeRegistrator.COPOAZU.get());
        tag(ModTags.FRUITS_CEMPEDAK).add(TreeRegistrator.CEMPEDAK.get());
        tag(ModTags.FRUITS_JACKFRUIT).add(TreeRegistrator.JACKFRUIT.get());
        tag(ModTags.FRUITS_BANANA).add(TreeRegistrator.BANANA.get());
        tag(ModTags.FRUITS_COCONUT).add(TreeRegistrator.COCONUT.get());
        tag(ModTags.FRUITS_MANGO).add(TreeRegistrator.MANGO.get());
        tag(ModTags.FRUITS_PLANTAIN).add(TreeRegistrator.PLANTAIN.get());
        tag(ModTags.FRUITS_RED_BANANA).add(TreeRegistrator.RED_BANANA.get());
        tag(ModTags.FRUITS_PAPAYA).add(TreeRegistrator.PAPAYA.get());
        tag(ModTags.FRUITS_PERSIMMON).add(TreeRegistrator.PERSIMMON.get());
        tag(ModTags.FRUITS_POMEGRANATE).add(TreeRegistrator.POMEGRANATE.get());
        tag(ModTags.FRUITS_BREADFRUIT).add(TreeRegistrator.BREADFRUIT.get());
        tag(ModTags.FRUITS_LIME).add(TreeRegistrator.LIME.get());
        tag(ModTags.FRUITS_KEY_LIME).add(TreeRegistrator.KEY_LIME.get());
        tag(ModTags.FRUITS_FINGER_LIME).add(TreeRegistrator.FINGER_LIME.get());
        tag(ModTags.FRUITS_CITRON).add(TreeRegistrator.CITRON.get());
        tag(ModTags.FRUITS_LEMON).add(TreeRegistrator.LEMON.get());
        tag(ModTags.FRUITS_ORANGE).add(TreeRegistrator.ORANGE.get());
        tag(ModTags.FRUITS_MANDARIN).add(TreeRegistrator.MANDARIN.get());
        tag(ModTags.FRUITS_BUDDHAS_HAND).add(TreeRegistrator.BUDDHAS_HAND.get());
        tag(ModTags.FRUITS).addTags(ModTags.FRUITS_APRICOT, ModTags.FRUITS_BLACK_CHERRY, ModTags.FRUITS_CHERRY_PLUM, ModTags.FRUITS_OLIVE, ModTags.FRUITS_OSANGE_ORANGE, ModTags.FRUITS_KUMQUAT, ModTags.FRUITS_WILD_CHERRY, ModTags.FRUITS_SOUR_CHERRY, ModTags.FRUITS_DATE, ModTags.FRUITS_PLUM, ModTags.FRUITS_AVOCADO, ModTags.FRUITS_CRABAPPLE, ModTags.FRUITS_FIG, ModTags.FRUITS_GRAPEFRUIT, ModTags.FRUITS_NECTARINE, ModTags.FRUITS_PEACH, ModTags.FRUITS_PEAR, ModTags.FRUITS_POMELO, ModTags.FRUITS_SAND_PEAR, ModTags.FRUITS_SATSUMA, ModTags.FRUITS_STAR_FRUIT, ModTags.FRUITS_TANGERINE, ModTags.FRUITS_AKEBIA, ModTags.FRUITS_COPOAZU, ModTags.FRUITS_CEMPEDAK, ModTags.FRUITS_JACKFRUIT, ModTags.FRUITS_BANANA, ModTags.FRUITS_COCONUT, ModTags.FRUITS_MANGO, ModTags.FRUITS_PLANTAIN, ModTags.FRUITS_RED_BANANA, ModTags.FRUITS_PAPAYA, ModTags.FRUITS_PERSIMMON, ModTags.FRUITS_POMEGRANATE, ModTags.FRUITS_BREADFRUIT, ModTags.FRUITS_LIME, ModTags.FRUITS_KEY_LIME, ModTags.FRUITS_FINGER_LIME, ModTags.FRUITS_CITRON, ModTags.FRUITS_LEMON, ModTags.FRUITS_ORANGE, ModTags.FRUITS_MANDARIN, ModTags.FRUITS_BUDDHAS_HAND);
    }

    @Override
    public String getName() {
        return "Productive Trees Item Tags Provider";
    }
}
