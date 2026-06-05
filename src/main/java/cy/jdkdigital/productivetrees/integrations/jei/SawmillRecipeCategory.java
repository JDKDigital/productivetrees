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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SawmillRecipeCategory implements IRecipeCategory<SawmillRecipe>
{
    protected static final int BACKGROUND_WIDTH = 130;
    protected static final int BACKGROUND_HEIGHT = 60;
    private final IDrawable background;
    private final IDrawable icon;

    public SawmillRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/sawmill.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
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

    @Override
    public int getWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int getHeight() {
        return BACKGROUND_HEIGHT;
    }

    @SuppressWarnings("unused")
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
                .addIngredients(recipe.input())
                .setSlotName("log");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 15)
                .addItemStack(recipe.output())
                .setSlotName("planks");

        if (!recipe.secondary().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 33)
                    .addItemStack(recipe.secondary())
                    .setSlotName("secondary");
        }
        if (!recipe.tertiary().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 33)
                    .addItemStack(recipe.tertiary())
                    .setSlotName("tertiary");
        }
    }
}
