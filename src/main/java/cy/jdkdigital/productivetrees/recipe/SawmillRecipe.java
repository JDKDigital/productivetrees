package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivelib.common.recipe.TripleOutputRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class SawmillRecipe extends TripleOutputRecipe
{
    public SawmillRecipe(Ingredient log, ItemStackTemplate planks, Optional<ItemStackTemplate> secondary, Optional<ItemStackTemplate> tertiary) {
        super(log, planks, secondary, tertiary);
    }

    @Override
    public RecipeSerializer<SawmillRecipe> getSerializer() {
        return TreeRegistrator.SAW_MILLLING.get();
    }

    @Override
    public RecipeType<SawmillRecipe> getType() {
        return TreeRegistrator.SAW_MILLLING_TYPE.get();
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
