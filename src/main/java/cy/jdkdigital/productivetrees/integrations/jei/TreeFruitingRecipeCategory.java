package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class TreeFruitingRecipeCategory implements IRecipeCategory<TreeFruitingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public TreeFruitingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveTrees.MODID, "textures/gui/jei/tree_fruiting.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 130, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "haw"))));
    }

    @Override
    public RecipeType<TreeFruitingRecipe> getRecipeType() {
        return ProductiveTreesJeiPlugin.TREE_FRUITING_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivetrees.tree_fruiting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, TreeFruitingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 27)
                .addItemStacks(Arrays.asList(recipe.tree.getItems()))
                .setSlotName("leafA");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 27)
                .addItemStacks(List.of(recipe.result))
                .setSlotName("result");
    }
}
