package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class ThermalInsulatorRecipeBuilder implements RecipeBuilder
{
    private final Ingredient ingredient;
    private final ItemStack log;
    private final ItemStack sapling;
    private final ItemStack fruit;
    private final float fruitChance;
    public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public ThermalInsulatorRecipeBuilder(Ingredient ingredient, ItemStack log, ItemStack sapling, ItemStack fruit, float fruitChance) {
        this.ingredient = ingredient;
        this.log = log;
        this.sapling = sapling;
        this.fruit = fruit;
        this.fruitChance = fruitChance;
    }

    public static ThermalInsulatorRecipeBuilder direct(Ingredient logs, ItemStack plank, ItemStack sapling, ItemStack fruit, float fruitChance) {
        return new ThermalInsulatorRecipeBuilder(logs, plank, sapling, fruit, fruitChance);
    }

    public static ThermalInsulatorRecipeBuilder tree(TreeObject tree) {
        return direct(Ingredient.of(tree.getSaplingBlock().get()), new ItemStack(tree.getLogBlock().get()), new ItemStack(tree.getSaplingBlock().get()), tree.getFruit().getItem(), tree.getFruit().growthSpeed());
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return null;
    }

    @Override
    public Item getResult() {
        return log.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, ingredient, log, sapling, fruit, fruitChance, this.advancement));
    }

    record Result(ResourceLocation id, Ingredient ingredient, ItemStack log, ItemStack sapling, ItemStack fruit, float fruitChance,
                  Advancement.Builder advancement) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", ingredient.toJson());

            JsonArray result = new JsonArray();
            result.add(TreeUtil.itemChanceToJson(log, 6f));
            result.add(TreeUtil.itemChanceToJson(sapling, 1.1f));
            if (!fruit.isEmpty()) {
                result.add(TreeUtil.itemChanceToJson(fruit, fruitChance));
            }
            json.add("result", result);

            json.addProperty("energy_mod", 3.0);
            json.addProperty("water_mod", 3.0);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("thermal:insolator"));
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
