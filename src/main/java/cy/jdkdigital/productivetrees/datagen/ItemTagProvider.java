package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.Tags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
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
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);

        copy(ModTags.HIVES_BLOCK, ModTags.HIVES);
        copy(ModTags.BOXES_BLOCK, ModTags.BOXES);

        tag(Tags.ALMOND).add(TreeRegistrator.ALMOND.get());
        tag(Tags.ACORN).add(TreeRegistrator.ACORN.get());
        tag(Tags.BEECHNUT).add(TreeRegistrator.BEECHNUT.get());
        tag(Tags.BRAZIL_NUT).add(TreeRegistrator.BRAZIL_NUT.get());
        tag(Tags.BUTTERNUT).add(TreeRegistrator.BUTTERNUT.get());
        tag(Tags.CANDLENUT).add(TreeRegistrator.CANDLENUT.get());
        tag(Tags.CASHEW).add(TreeRegistrator.CASHEW.get());
        tag(Tags.CHESTNUT).add(TreeRegistrator.CHESTNUT.get());
        tag(Tags.COFFEE_BEAN).add(TreeRegistrator.COFFEE_BEAN.get());
        tag(Tags.GINKGO_NUT).add(TreeRegistrator.GINKGO_NUT.get());
        tag(Tags.HAZELNUT).add(TreeRegistrator.HAZELNUT.get());
        tag(Tags.PECAN).add(TreeRegistrator.PECAN.get());
        tag(Tags.PISTACHIO).add(TreeRegistrator.PISTACHIO.get());
        tag(Tags.WALNUT).add(TreeRegistrator.WALNUT.get());
        tag(Tags.CAROB).add(TreeRegistrator.CAROB.get());
        tag(Tags.NUTS).addTags(Tags.ALMOND, Tags.ACORN, Tags.BEECHNUT, Tags.BRAZIL_NUT, Tags.BUTTERNUT, Tags.CANDLENUT, Tags.CASHEW, Tags.CHESTNUT, Tags.COFFEE_BEAN, Tags.GINKGO_NUT, Tags.HAZELNUT, Tags.PECAN, Tags.PISTACHIO, Tags.WALNUT, Tags.CAROB);

        tag(Tags.BLACKBERRY).add(TreeRegistrator.BLACKBERRY.get());
        tag(Tags.BLACKCURRANT).add(TreeRegistrator.BLACKCURRANT.get());
        tag(Tags.BLUEBERRY).add(TreeRegistrator.BLUEBERRY.get());
        tag(Tags.REDCURRANT).add(TreeRegistrator.REDCURRANT.get());
        tag(Tags.CRANBERRY).add(TreeRegistrator.CRANBERRY.get());
        tag(Tags.ELDERBERRY).add(TreeRegistrator.ELDERBERRY.get());
        tag(Tags.GOOSEBERRY).add(TreeRegistrator.GOOSEBERRY.get());
        tag(Tags.RASPBERRY).add(TreeRegistrator.RASPBERRY.get());
        tag(Tags.JUNIPER).add(TreeRegistrator.JUNIPER.get());
        tag(Tags.GOLDEN_RASPBERRY).add(TreeRegistrator.GOLDEN_RASPBERRY.get());
        tag(Tags.SLOE).add(TreeRegistrator.SLOE.get());
        tag(Tags.HAW).add(TreeRegistrator.HAW.get());
        tag(Tags.MIRACLE_BERRY).add(TreeRegistrator.MIRACLE_BERRY.get());
        tag(Tags.BERRIES).addTags(Tags.BLACKBERRY, Tags.BLACKCURRANT, Tags.BLUEBERRY, Tags.REDCURRANT, Tags.CRANBERRY, Tags.ELDERBERRY, Tags.GOOSEBERRY, Tags.RASPBERRY, Tags.JUNIPER, Tags.GOLDEN_RASPBERRY, Tags.SLOE, Tags.HAW, Tags.MIRACLE_BERRY);

        tag(Tags.APRICOT).add(TreeRegistrator.APRICOT.get());
        tag(Tags.BLACK_CHERRY).add(TreeRegistrator.BLACK_CHERRY.get());
        tag(Tags.CHERRY_PLUM).add(TreeRegistrator.CHERRY_PLUM.get());
        tag(Tags.OLIVE).add(TreeRegistrator.OLIVE.get());
        tag(Tags.OSANGE_ORANGE).add(TreeRegistrator.OSANGE_ORANGE.get());
        tag(Tags.KUMQUAT).add(TreeRegistrator.KUMQUAT.get());
        tag(Tags.WILD_CHERRY).add(TreeRegistrator.WILD_CHERRY.get());
        tag(Tags.SOUR_CHERRY).add(TreeRegistrator.SOUR_CHERRY.get());
        tag(Tags.DATE).add(TreeRegistrator.DATE.get());
        tag(Tags.PLUM).add(TreeRegistrator.PLUM.get());
        tag(Tags.AVOCADO).add(TreeRegistrator.AVOCADO.get());
        tag(Tags.CRABAPPLE).add(TreeRegistrator.CRABAPPLE.get());
        tag(Tags.FIG).add(TreeRegistrator.FIG.get());
        tag(Tags.GRAPEFRUIT).add(TreeRegistrator.GRAPEFRUIT.get());
        tag(Tags.NECTARINE).add(TreeRegistrator.NECTARINE.get());
        tag(Tags.PEACH).add(TreeRegistrator.PEACH.get());
        tag(Tags.PEAR).add(TreeRegistrator.PEAR.get());
        tag(Tags.POMELO).add(TreeRegistrator.POMELO.get());
        tag(Tags.SAND_PEAR).add(TreeRegistrator.SAND_PEAR.get());
        tag(Tags.SATSUMA).add(TreeRegistrator.SATSUMA.get());
        tag(Tags.STAR_FRUIT).add(TreeRegistrator.STAR_FRUIT.get());
        tag(Tags.TANGERINE).add(TreeRegistrator.TANGERINE.get());
        tag(Tags.AKEBIA).add(TreeRegistrator.AKEBIA.get());
        tag(Tags.COPOAZU).add(TreeRegistrator.COPOAZU.get());
        tag(Tags.BANANA).add(TreeRegistrator.BANANA.get());
        tag(Tags.COCONUT).add(TreeRegistrator.COCONUT.get());
        tag(Tags.MANGO).add(TreeRegistrator.MANGO.get());
        tag(Tags.PLANTAIN).add(TreeRegistrator.PLANTAIN.get());
        tag(Tags.RED_BANANA).add(TreeRegistrator.RED_BANANA.get());
        tag(Tags.PAPAYA).add(TreeRegistrator.PAPAYA.get());
        tag(Tags.BREADFRUIT).add(TreeRegistrator.BREADFRUIT.get());
        tag(Tags.MONSTERA_DELICIOSA).add(TreeRegistrator.MONSTERA_DELICIOSA.get());
        tag(Tags.LIME).add(TreeRegistrator.LIME.get());
        tag(Tags.KEY_LIME).add(TreeRegistrator.KEY_LIME.get());
        tag(Tags.FINGER_LIME).add(TreeRegistrator.FINGER_LIME.get());
        tag(Tags.CITRON).add(TreeRegistrator.CITRON.get());
        tag(Tags.LEMON).add(TreeRegistrator.LEMON.get());
        tag(Tags.ORANGE).add(TreeRegistrator.ORANGE.get());
        tag(Tags.MANDARIN).add(TreeRegistrator.MANDARIN.get());
        tag(Tags.BUDDHAS_HAND).add(TreeRegistrator.BUDDHAS_HAND.get());
        tag(Tags.FRUITS).addTags(Tags.APRICOT, Tags.BLACK_CHERRY, Tags.CHERRY_PLUM, Tags.OLIVE, Tags.OSANGE_ORANGE, Tags.KUMQUAT, Tags.WILD_CHERRY, Tags.SOUR_CHERRY, Tags.DATE, Tags.PLUM, Tags.AVOCADO, Tags.CRABAPPLE, Tags.FIG, Tags.GRAPEFRUIT, Tags.NECTARINE, Tags.PEACH, Tags.PEAR, Tags.POMELO, Tags.SAND_PEAR, Tags.SATSUMA, Tags.STAR_FRUIT, Tags.TANGERINE, Tags.AKEBIA, Tags.COPOAZU, Tags.BANANA, Tags.COCONUT, Tags.MANGO, Tags.PLANTAIN, Tags.RED_BANANA, Tags.PAPAYA, Tags.BREADFRUIT, Tags.MONSTERA_DELICIOSA, Tags.LIME, Tags.KEY_LIME, Tags.FINGER_LIME, Tags.CITRON, Tags.LEMON, Tags.ORANGE, Tags.MANDARIN, Tags.BUDDHAS_HAND);
    }

    @Override
    public String getName() {
        return "Productive Trees Item Tags Provider";
    }
}
