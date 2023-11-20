package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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

        tag(ModTags.ALMOND).add(TreeRegistrator.ALMOND.get());
        tag(ModTags.ACORN).add(TreeRegistrator.ACORN.get());
        tag(ModTags.BEECHNUT).add(TreeRegistrator.BEECHNUT.get());
        tag(ModTags.BRAZIL_NUT).add(TreeRegistrator.BRAZIL_NUT.get());
        tag(ModTags.BUTTERNUT).add(TreeRegistrator.BUTTERNUT.get());
        tag(ModTags.CANDLENUT).add(TreeRegistrator.CANDLENUT.get());
        tag(ModTags.CASHEW).add(TreeRegistrator.CASHEW.get());
        tag(ModTags.CHESTNUT).add(TreeRegistrator.CHESTNUT.get());
        tag(ModTags.COFFEE_BEAN).add(TreeRegistrator.COFFEE_BEAN.get());
        tag(ModTags.GINKGO_NUT).add(TreeRegistrator.GINKGO_NUT.get());
        tag(ModTags.HAZELNUT).add(TreeRegistrator.HAZELNUT.get());
        tag(ModTags.PECAN).add(TreeRegistrator.PECAN.get());
        tag(ModTags.PISTACHIO).add(TreeRegistrator.PISTACHIO.get());
        tag(ModTags.WALNUT).add(TreeRegistrator.WALNUT.get());
        tag(ModTags.CAROB).add(TreeRegistrator.CAROB.get());
        tag(ModTags.NUTS).addTags(ModTags.ALMOND, ModTags.ACORN, ModTags.BEECHNUT, ModTags.BRAZIL_NUT, ModTags.BUTTERNUT, ModTags.CANDLENUT, ModTags.CASHEW, ModTags.CHESTNUT, ModTags.COFFEE_BEAN, ModTags.GINKGO_NUT, ModTags.HAZELNUT, ModTags.PECAN, ModTags.PISTACHIO, ModTags.WALNUT, ModTags.CAROB);

        tag(ModTags.BLACKBERRY).add(TreeRegistrator.BLACKBERRY.get());
        tag(ModTags.BLACKCURRANT).add(TreeRegistrator.BLACKCURRANT.get());
        tag(ModTags.BLUEBERRY).add(TreeRegistrator.BLUEBERRY.get());
        tag(ModTags.REDCURRANT).add(TreeRegistrator.REDCURRANT.get());
        tag(ModTags.CRANBERRY).add(TreeRegistrator.CRANBERRY.get());
        tag(ModTags.ELDERBERRY).add(TreeRegistrator.ELDERBERRY.get());
        tag(ModTags.GOOSEBERRY).add(TreeRegistrator.GOOSEBERRY.get());
        tag(ModTags.RASPBERRY).add(TreeRegistrator.RASPBERRY.get());
        tag(ModTags.JUNIPER).add(TreeRegistrator.JUNIPER.get());
        tag(ModTags.GOLDEN_RASPBERRY).add(TreeRegistrator.GOLDEN_RASPBERRY.get());
        tag(ModTags.SLOE).add(TreeRegistrator.SLOE.get());
        tag(ModTags.HAW).add(TreeRegistrator.HAW.get());
        tag(ModTags.MIRACLE_BERRY).add(TreeRegistrator.MIRACLE_BERRY.get());
        tag(ModTags.BERRIES).addTags(ModTags.BLACKBERRY, ModTags.BLACKCURRANT, ModTags.BLUEBERRY, ModTags.REDCURRANT, ModTags.CRANBERRY, ModTags.ELDERBERRY, ModTags.GOOSEBERRY, ModTags.RASPBERRY, ModTags.JUNIPER, ModTags.GOLDEN_RASPBERRY, ModTags.SLOE, ModTags.HAW, ModTags.MIRACLE_BERRY);

        tag(ModTags.APRICOT).add(TreeRegistrator.APRICOT.get());
        tag(ModTags.BLACK_CHERRY).add(TreeRegistrator.BLACK_CHERRY.get());
        tag(ModTags.CHERRY_PLUM).add(TreeRegistrator.CHERRY_PLUM.get());
        tag(ModTags.OLIVE).add(TreeRegistrator.OLIVE.get());
        tag(ModTags.OSANGE_ORANGE).add(TreeRegistrator.OSANGE_ORANGE.get());
        tag(ModTags.KUMQUAT).add(TreeRegistrator.KUMQUAT.get());
        tag(ModTags.WILD_CHERRY).add(TreeRegistrator.WILD_CHERRY.get());
        tag(ModTags.SOUR_CHERRY).add(TreeRegistrator.SOUR_CHERRY.get());
        tag(ModTags.DATE).add(TreeRegistrator.DATE.get());
        tag(ModTags.PLUM).add(TreeRegistrator.PLUM.get());
        tag(ModTags.AVOCADO).add(TreeRegistrator.AVOCADO.get());
        tag(ModTags.CRABAPPLE).add(TreeRegistrator.CRABAPPLE.get());
        tag(ModTags.FIG).add(TreeRegistrator.FIG.get());
        tag(ModTags.GRAPEFRUIT).add(TreeRegistrator.GRAPEFRUIT.get());
        tag(ModTags.NECTARINE).add(TreeRegistrator.NECTARINE.get());
        tag(ModTags.PEACH).add(TreeRegistrator.PEACH.get());
        tag(ModTags.PEAR).add(TreeRegistrator.PEAR.get());
        tag(ModTags.POMELO).add(TreeRegistrator.POMELO.get());
        tag(ModTags.SAND_PEAR).add(TreeRegistrator.SAND_PEAR.get());
        tag(ModTags.SATSUMA).add(TreeRegistrator.SATSUMA.get());
        tag(ModTags.STAR_FRUIT).add(TreeRegistrator.STAR_FRUIT.get());
        tag(ModTags.TANGERINE).add(TreeRegistrator.TANGERINE.get());
        tag(ModTags.AKEBIA).add(TreeRegistrator.AKEBIA.get());
        tag(ModTags.COPOAZU).add(TreeRegistrator.COPOAZU.get());
        tag(ModTags.CEMPEDAK).add(TreeRegistrator.CEMPEDAK.get());
        tag(ModTags.JACKFRUIT).add(TreeRegistrator.JACKFRUIT.get());
        tag(ModTags.BANANA).add(TreeRegistrator.BANANA.get());
        tag(ModTags.COCONUT).add(TreeRegistrator.COCONUT.get());
        tag(ModTags.MANGO).add(TreeRegistrator.MANGO.get());
        tag(ModTags.PLANTAIN).add(TreeRegistrator.PLANTAIN.get());
        tag(ModTags.RED_BANANA).add(TreeRegistrator.RED_BANANA.get());
        tag(ModTags.PAPAYA).add(TreeRegistrator.PAPAYA.get());
        tag(ModTags.PERSIMMON).add(TreeRegistrator.PERSIMMON.get());
        tag(ModTags.POMEGRANATE).add(TreeRegistrator.POMEGRANATE.get());
        tag(ModTags.BREADFRUIT).add(TreeRegistrator.BREADFRUIT.get());
        tag(ModTags.MONSTERA_DELICIOSA).add(TreeRegistrator.MONSTERA_DELICIOSA.get());
        tag(ModTags.LIME).add(TreeRegistrator.LIME.get());
        tag(ModTags.KEY_LIME).add(TreeRegistrator.KEY_LIME.get());
        tag(ModTags.FINGER_LIME).add(TreeRegistrator.FINGER_LIME.get());
        tag(ModTags.CITRON).add(TreeRegistrator.CITRON.get());
        tag(ModTags.LEMON).add(TreeRegistrator.LEMON.get());
        tag(ModTags.ORANGE).add(TreeRegistrator.ORANGE.get());
        tag(ModTags.MANDARIN).add(TreeRegistrator.MANDARIN.get());
        tag(ModTags.BUDDHAS_HAND).add(TreeRegistrator.BUDDHAS_HAND.get());
        tag(ModTags.FRUITS).addTags(ModTags.APRICOT, ModTags.BLACK_CHERRY, ModTags.CHERRY_PLUM, ModTags.OLIVE, ModTags.OSANGE_ORANGE, ModTags.KUMQUAT, ModTags.WILD_CHERRY, ModTags.SOUR_CHERRY, ModTags.DATE, ModTags.PLUM, ModTags.AVOCADO, ModTags.CRABAPPLE, ModTags.FIG, ModTags.GRAPEFRUIT, ModTags.NECTARINE, ModTags.PEACH, ModTags.PEAR, ModTags.POMELO, ModTags.SAND_PEAR, ModTags.SATSUMA, ModTags.STAR_FRUIT, ModTags.TANGERINE, ModTags.AKEBIA, ModTags.COPOAZU, ModTags.CEMPEDAK, ModTags.JACKFRUIT, ModTags.BANANA, ModTags.COCONUT, ModTags.MANGO, ModTags.PLANTAIN, ModTags.RED_BANANA, ModTags.PAPAYA, ModTags.PERSIMMON, ModTags.POMEGRANATE, ModTags.BREADFRUIT, ModTags.MONSTERA_DELICIOSA, ModTags.LIME, ModTags.KEY_LIME, ModTags.FINGER_LIME, ModTags.CITRON, ModTags.LEMON, ModTags.ORANGE, ModTags.MANDARIN, ModTags.BUDDHAS_HAND);
    }

    @Override
    public String getName() {
        return "Productive Trees Item Tags Provider";
    }
}
