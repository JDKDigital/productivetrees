package cy.jdkdigital.productivetrees.datagen.recipe;

import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public record TreePollinationRecipeBuilder(Ingredient leafA, Ingredient leafB, ItemStack result, float chance) implements RecipeBuilder
{
    public static TreePollinationRecipeBuilder direct(Ingredient leafA, Ingredient leafB, ItemStack result, float chance) {
        return new TreePollinationRecipeBuilder(leafA, leafB, result, chance);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return null;
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation id) {
        consumer.accept(id, new TreePollinationRecipe(leafA, leafB, result, chance), null);
    }
}
