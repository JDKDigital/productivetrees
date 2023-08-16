package cy.jdkdigital.productivetrees.integrations;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class TreePollinationRecipeCategory implements IRecipeCategory<TreePollinationRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public TreePollinationRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveTrees.MODID, "textures/gui/tree_pollination.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 130, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ProductiveTrees.POLLEN.get()));
    }

    @Override
    public RecipeType<TreePollinationRecipe> getRecipeType() {
        return ProductiveTreesJeiPlugin.TREE_POLLINATION_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivetrees.tree_pollination");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TreePollinationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 27)
                .addItemStacks(Arrays.asList(recipe.leafA.getItems()))
                .setSlotName("leafA");

        Arrays.stream(recipe.leafA.getItems()).forEach(itemStack -> {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(TreeUtil.getSaplingFromLeaf(itemStack));
        });

        builder.addSlot(RecipeIngredientRole.INPUT, 56, 27)
                .addItemStacks(Arrays.asList(recipe.leafB.getItems()))
                .setSlotName("leafB");

        Arrays.stream(recipe.leafB.getItems()).forEach(itemStack -> {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(TreeUtil.getSaplingFromLeaf(itemStack));
        });

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 27)
                .addItemStacks(List.of(recipe.result))
                .setSlotName("result");

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(TreeUtil.getLeafFromSapling(recipe.result));
    }
}
