package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
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
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SawmillRecipeCategory implements IRecipeCategory<SawmillRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public SawmillRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveTrees.MODID, "textures/gui/jei/sawmill.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 130, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TreeRegistrator.SAWMILL.get()));
    }

    @Override
    public RecipeType<SawmillRecipe> getRecipeType() {
        return ProductiveTreesJeiPlugin.SAWMILL_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivetrees.sawmill");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SawmillRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 24)
                .addIngredients(recipe.log)
                .setSlotName("log");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 15)
                .addItemStack(recipe.planks)
                .setSlotName("planks");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 33)
                .addItemStack(recipe.secondary)
                .setSlotName("secondary");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 33)
                .addItemStack(recipe.tertiary)
                .setSlotName("tertiary");
    }
}
