package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class TreePollinationRecipeCategory implements IRecipeCategory<TreePollinationRecipe>
{
    protected static final int BACKGROUND_WIDTH = 130;
    protected static final int BACKGROUND_HEIGHT = 60;
    private final IDrawable background;
    private final IDrawable icon;

    public TreePollinationRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/tree_pollination.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TreeRegistrator.POLLEN.get()));
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
    public void setRecipe(IRecipeLayoutBuilder builder, TreePollinationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 27)
                .addItemStacks(recipe.leafA.items().<ItemStack>map(h -> new ItemStack(h.value())).map(TreeUtil::getSaplingFromLeaf).toList())
                .setSlotName("leafA");
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.leafA.items().<ItemStack>map(h -> new ItemStack(h.value())).filter(itemStack -> itemStack.getItem() instanceof BlockItem).map(itemStack -> TreeUtil.getPollen(((BlockItem) itemStack.getItem()).getBlock())).toList());

        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.leafA.items().<ItemStack>map(h -> new ItemStack(h.value())).toList());

        builder.addSlot(RecipeIngredientRole.INPUT, 56, 27)
                .addItemStacks(recipe.leafB.items().<ItemStack>map(h -> new ItemStack(h.value())).map(TreeUtil::getSaplingFromLeaf).toList())
                .setSlotName("leafB");
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.leafB.items().<ItemStack>map(h -> new ItemStack(h.value())).filter(itemStack -> itemStack.getItem() instanceof BlockItem).map(itemStack -> TreeUtil.getPollen(((BlockItem) itemStack.getItem()).getBlock())).toList());

        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.leafB.items().<ItemStack>map(h -> new ItemStack(h.value())).toList());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 27)
                .addItemStacks(List.of(recipe.result()))
                .setSlotName("result");

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(TreeUtil.getLeafFromSapling(recipe.result()));
    }
}
