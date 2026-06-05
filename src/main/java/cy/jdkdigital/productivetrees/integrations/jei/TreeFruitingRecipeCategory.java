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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.List;

public class TreeFruitingRecipeCategory implements IRecipeCategory<TreeFruitingRecipe>
{
    protected static final int BACKGROUND_WIDTH = 130;
    protected static final int BACKGROUND_HEIGHT = 60;
    private final IDrawable background;
    private final IDrawable icon;

    public TreeFruitingRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/tree_fruiting.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "haw")).map(Holder::value).orElse(Items.AIR)));
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
    public void setRecipe(IRecipeLayoutBuilder builder, TreeFruitingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 27)
                .addItemStacks(recipe.tree.items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                .setSlotName("tree");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 27)
                .addItemStacks(List.of(recipe.result()))
                .setSlotName("result");
    }
}
