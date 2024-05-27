package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivelib.util.RecipeUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class ThermalSawmilRecipeBuilder implements RecipeBuilder
{
    private final Ingredient log;
    private final ItemStack plank;
    private final ItemStack secondary;
    public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public ThermalSawmilRecipeBuilder(Ingredient log, ItemStack plank, ItemStack secondary) {
        this.log = log;
        this.plank = plank;
        this.secondary = secondary;
    }

    public static ThermalSawmilRecipeBuilder direct(Ingredient logs, ItemStack plank, ItemStack secondary) {
        return new ThermalSawmilRecipeBuilder(logs, plank, secondary);
    }

    public static ThermalSawmilRecipeBuilder tree(Block log, Block strippedLog, Block wood, Block strippedwood, Block plank) {
        return direct(Ingredient.of(log, strippedLog, wood, strippedwood), new ItemStack(plank, 6), new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:sawdust")), 1));
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return this;
    }

    @Override
    public Item getResult() {
        return plank.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, log, plank, secondary, this.advancement));
    }

    record Result(ResourceLocation id, Ingredient log, ItemStack plank, ItemStack secondary,
                  Advancement.Builder advancement) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", log.toJson());

            JsonArray result = new JsonArray();
            result.add(RecipeUtil.itemToJson(plank));
            result.add(RecipeUtil.itemChanceToJson(secondary, 1.25f));
            json.add("result", result);

            json.addProperty("energy", 1000);
            json.addProperty("experience", 0.15);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("thermal:sawmill"));
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return id.withPrefix("recipes/" + RecipeCategory.MISC.getFolderName() + "/");
        }
    }
}
