package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SingleConditionalRecipe
{
    public static SingleConditionalRecipe.Builder builder() {
        return new SingleConditionalRecipe.Builder();
    }

    public static class Builder
    {
        private List<ICondition> conditions = new ArrayList<>();
        private FinishedRecipe recipe = null;
        private ResourceLocation advId;
        private ConditionalAdvancement.Builder adv;

        public SingleConditionalRecipe.Builder addCondition(ICondition condition) {
            conditions.add(condition);
            return this;
        }

        public SingleConditionalRecipe.Builder setRecipe(Consumer<Consumer<FinishedRecipe>> callable) {
            callable.accept(this::setRecipe);
            return this;
        }

        public SingleConditionalRecipe.Builder setRecipe(FinishedRecipe recipe) {
            if (conditions.isEmpty()) {
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            }
            this.recipe = recipe;
            return this;
        }

        public SingleConditionalRecipe.Builder generateAdvancement() {
            return generateAdvancement(null);
        }

        public SingleConditionalRecipe.Builder generateAdvancement(@Nullable ResourceLocation id) {
            ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();
            for (ICondition cond : conditions) {
                builder = builder.addCondition(cond);
            }
            builder = builder.addAdvancement(recipe);
            return setAdvancement(id, builder);
        }

        public SingleConditionalRecipe.Builder setAdvancement(ConditionalAdvancement.Builder advancement) {
            return setAdvancement(null, advancement);
        }

        public SingleConditionalRecipe.Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement) {
            return setAdvancement(new ResourceLocation(namespace, path), advancement);
        }

        public SingleConditionalRecipe.Builder setAdvancement(@Nullable ResourceLocation id, ConditionalAdvancement.Builder advancement) {
            if (this.adv != null) {
                throw new IllegalStateException("Invalid SingleConditionalRecipeBuilder, Advancement already set");
            }
            this.advId = id;
            this.adv = advancement;
            return this;
        }

        public void build(Consumer<FinishedRecipe> consumer, String namespace, String path) {
            build(consumer, new ResourceLocation(namespace, path));
        }

        public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
            if (recipe == null) {
                throw new IllegalStateException("Invalid SingleConditionalRecipe builder, No recipe");
            }
            if (advId == null && adv != null) {
                advId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
            }

            consumer.accept(new SingleConditionalRecipe.Finished(id, conditions, recipe, advId, adv));
        }
    }

    private static class Finished implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final List<ICondition> conditions;
        private final FinishedRecipe recipe;
        private final ResourceLocation advId;
        private final ConditionalAdvancement.Builder adv;

        private Finished(ResourceLocation id, List<ICondition> conditions, FinishedRecipe recipe, @Nullable ResourceLocation advId, @Nullable ConditionalAdvancement.Builder adv) {
            this.id = id;
            this.conditions = conditions;
            this.recipe = recipe;
            this.advId = advId;
            this.adv = adv;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
        }

        @Override
        public JsonObject serializeRecipe() {
            JsonObject json = recipe.serializeRecipe();

            JsonArray jsonConditions = new JsonArray();
            for (ICondition condition : this.conditions) {
                jsonConditions.add(CraftingHelper.serialize(condition));
            }
            json.add("conditions", jsonConditions);

            return json;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return recipe.getType();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return adv == null ? null : adv.write();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return advId;
        }
    }
}
