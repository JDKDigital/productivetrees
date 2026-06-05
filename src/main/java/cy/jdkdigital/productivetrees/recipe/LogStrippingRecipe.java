package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivelib.common.recipe.TripleOutputRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class LogStrippingRecipe extends TripleOutputRecipe
{
    public LogStrippingRecipe(Ingredient input, ItemStackTemplate output, Optional<ItemStackTemplate> secondary, Optional<ItemStackTemplate> tertiary) {
        super(input, output, secondary, tertiary);
    }

    public LogStrippingRecipe(ItemStack log, ItemStack stripped, ItemStack secondary) {
        this(Ingredient.of(log.getItem()), ItemStackTemplate.fromNonEmptyStack(stripped),
                secondary.isEmpty() ? Optional.empty() : Optional.of(ItemStackTemplate.fromNonEmptyStack(secondary)),
                Optional.empty());
    }

    @Override
    public RecipeSerializer<LogStrippingRecipe> getSerializer() {
        return TreeRegistrator.LOG_STRIPPING.get();
    }

    @Override
    public RecipeType<LogStrippingRecipe> getType() {
        return TreeRegistrator.LOG_STRIPPING_TYPE.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }
}
