package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonObject;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.darkhax.botanypots.data.recipes.crop.SerializerHarvestEntry;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public record BotanyPotCropRecipeBuilder(Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, BlockState displayStates, int lightLevel) implements RecipeBuilder
{
    public static BotanyPotCropRecipeBuilder direct(Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, BlockState displayStates, int lightLevel) {
        return new BotanyPotCropRecipeBuilder(seed, soilCategories, growthTicks, results, displayStates, lightLevel);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance triggerInstance) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return null;
    }

    @Override
    public Item getResult() {
        return Items.STICK;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, seed, soilCategories, growthTicks, results, displayStates, lightLevel));
    }

    record Result(ResourceLocation id, Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, BlockState displayState, int lightLevel) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("seed", Serializers.INGREDIENT.toJSON(seed));
            json.add("categories", Serializers.STRING.toJSONSet(soilCategories));
            json.add("growthTicks", Serializers.INT.toJSON(growthTicks));
            json.add("drops", SerializerHarvestEntry.SERIALIZER.toJSONList(results));
            json.add("display", Serializers.BLOCK_STATE.toJSON(displayState));
            if (lightLevel > 0) {
                json.addProperty("lightLevel", lightLevel);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return BotanyPotHelper.CROP_SERIALIZER.get();
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
