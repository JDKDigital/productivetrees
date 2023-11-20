package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record SawmillRecipeBuilder(Ingredient log, ItemStack plank, ItemStack secondary, ItemStack tertiary) implements RecipeBuilder
{
    public static SawmillRecipeBuilder direct(Ingredient logs, ItemStack plank, ItemStack secondary, ItemStack tertiary) {
        return new SawmillRecipeBuilder(logs, plank, secondary, tertiary);
    }

    public static SawmillRecipeBuilder tree(WoodObject tree) {
        return direct(Ingredient.of(tree.getLogBlock().get(), tree.getStrippedLogBlock().get(), tree.getWoodBlock().get(), tree.getStrippedWoodBlock().get()), new ItemStack(tree.getPlankBlock().get(), 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), ItemStack.EMPTY);
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
        consumer.accept(new Result(id, log, plank, secondary, tertiary));
    }

    record Result(ResourceLocation id, Ingredient log, ItemStack plank, ItemStack secondary, ItemStack tertiary) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("log", log.toJson());
            json.add("planks", itemToJson(plank));

            if (!secondary.isEmpty()) {
                json.add("secondary", itemToJson(secondary));
            }
            if (!tertiary.isEmpty()) {
                json.add("tertiary", itemToJson(tertiary));
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return TreeRegistrator.SAW_MILLLING.get();
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

    static JsonObject itemToJson(ItemStack item) {
        var json = new JsonObject();
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
        if (item.getCount() > 1) {
            json.addProperty("count", item.getCount());
        }
        if (item.getTag() != null) {
            json.addProperty("type", "forge:nbt");
            json.addProperty("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, item.getTag()).toString());
        }
        return json;
    }
}
