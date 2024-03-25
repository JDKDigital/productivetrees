package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record TreePollinationRecipeBuilder(Ingredient leafA, Ingredient leafB, Ingredient result, int chance) implements RecipeBuilder
{
    public static TreePollinationRecipeBuilder direct(Ingredient leafA, Ingredient leafB, Ingredient result, int chance) {
        return new TreePollinationRecipeBuilder(leafA, leafB, result, chance);
    }

    @Override
    public RecipeBuilder unlockedBy(String p_176496_, CriterionTriggerInstance p_176497_) {
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
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, leafA, leafB, result, chance));
    }

    record Result(ResourceLocation id, Ingredient leafA, Ingredient leafB, Ingredient result,
                  int chance) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("leafA", leafA.toJson());
            json.add("leafB", leafB.toJson());
            json.add("result", result.toJson());
            json.addProperty("chance", chance);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return TreeRegistrator.TREE_POLLINATION.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
