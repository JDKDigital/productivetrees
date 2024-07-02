package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class LogStrippingRecipeCategory implements IRecipeCategory<LogStrippingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public LogStrippingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/stripping.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 130, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TreeRegistrator.STRIPPER.get()));
    }

    @Override
    public RecipeType<LogStrippingRecipe> getRecipeType() {
        return ProductiveTreesJeiPlugin.LOG_STRIPPING_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivetrees.log_stripping");
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
    public void setRecipe(IRecipeLayoutBuilder builder, LogStrippingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 15)
                .addItemStack(recipe.log)
                .setSlotName("log");

        builder.addSlot(RecipeIngredientRole.INPUT, 30, 34)
                .addIngredients(VanillaTypes.ITEM_STACK, Arrays.asList(Ingredient.of(ModTags.STRIPPER_TOOLS).getItems()))
                .setSlotName("axe");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 15)
                .addItemStack(recipe.stripped)
                .setSlotName("stripped");

        if (recipe.log.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLogBlock logBlock) {
            var tree = TreeUtil.getTree(logBlock);
            if (tree != null && tree.getStripDrop().isPresent()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 34)
                        .addItemStack(tree.getStripDropStack())
                        .setSlotName("bark");
            }
        }
    }
}
