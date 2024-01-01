package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
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

public record TreetapRecipeBuilder(Ingredient log, ItemStack plank, ItemStack secondary, ItemStack tertiary) implements RecipeBuilder
{
    public static TreetapRecipeBuilder direct(Ingredient logs, ItemStack plank, ItemStack secondary, ItemStack tertiary) {
        return new TreetapRecipeBuilder(logs, plank, secondary, tertiary);
    }

    public static TreetapRecipeBuilder tree(WoodObject tree) {
        ItemStack tertiary =
                tree.getId().getPath().equals("old_fustic") ? new ItemStack(TreeRegistrator.FUSTIC.get()) :
                tree.getId().getPath().equals("logwood") ? new ItemStack(TreeRegistrator.HAEMATOXYLIN.get()) :
                ItemStack.EMPTY;
        return direct(Ingredient.of(tree.getLogBlock().get(), tree.getStrippedLogBlock().get(), tree.getWoodBlock().get(), tree.getStrippedWoodBlock().get()), new ItemStack(tree.getPlankBlock().get(), 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), tertiary);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
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
            json.add("planks", TreeUtil.itemToJson(plank));

            if (!secondary.isEmpty()) {
                json.add("secondary", TreeUtil.itemToJson(secondary));
            }
            if (!tertiary.isEmpty()) {
                json.add("tertiary", TreeUtil.itemToJson(tertiary));
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
}
