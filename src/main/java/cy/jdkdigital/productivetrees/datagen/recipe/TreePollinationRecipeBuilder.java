package cy.jdkdigital.productivetrees.datagen.recipe;

import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public record TreePollinationRecipeBuilder(Ingredient leafA, Ingredient leafB, ItemStackTemplate result, float chance) implements RecipeBuilder
{
    public static TreePollinationRecipeBuilder direct(Ingredient leafA, Ingredient leafB, ItemStackTemplate result, float chance) {
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
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(result.item().value()));
    }

    @Override
    public void save(RecipeOutput consumer, ResourceKey<Recipe<?>> id) {
        consumer.accept(id, new TreePollinationRecipe(leafA, leafB, result, chance), null);
    }
}
