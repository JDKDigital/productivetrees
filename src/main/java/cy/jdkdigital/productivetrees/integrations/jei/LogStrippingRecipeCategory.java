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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LogStrippingRecipeCategory implements IRecipeCategory<LogStrippingRecipe>
{
    protected static final int BACKGROUND_WIDTH = 130;
    protected static final int BACKGROUND_HEIGHT = 60;
    private final IDrawable background;
    private final IDrawable icon;

    public LogStrippingRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/stripping.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
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
    public void setRecipe(IRecipeLayoutBuilder builder, LogStrippingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 15)
                .addIngredients(recipe.input())
                .setSlotName("log");

        List<ItemStack> axes = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(ModTags.STRIPPER_TOOLS).forEach((Holder<Item> h) -> axes.add(new ItemStack(h.value())));
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 34)
                .addItemStacks(axes)
                .setSlotName("axe");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 15)
                .addItemStack(recipe.output())
                .setSlotName("stripped");

        ItemStack firstInput = recipe.input().items().<ItemStack>map(h -> new ItemStack(h.value())).findFirst().orElse(ItemStack.EMPTY);
        if (firstInput.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLogBlock logBlock) {
            var tree = TreeUtil.getTree(logBlock);
            if (tree != null && tree.getStripDrop().isPresent()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 34)
                        .addItemStack(tree.getStripDropStack())
                        .setSlotName("bark");
            }
        }
    }
}
