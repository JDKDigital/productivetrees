package cy.jdkdigital.productivetrees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class SawmillRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Ingredient log;
    public final ItemStack planks;
    public final ItemStack secondary;
    public final ItemStack tertiary;

    public SawmillRecipe(ResourceLocation id, Ingredient log, ItemStack planks, ItemStack secondary, ItemStack tertiary) {
        this.id = id;
        this.log = log;
        this.planks = planks;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess level) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess level) {
        return this.planks.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.SAW_MILLLING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.SAW_MILLLING_TYPE.get();
    }

    public static class Serializer<T extends SawmillRecipe> implements RecipeSerializer<T>
    {
        final SawmillRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(SawmillRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient in;
            if (GsonHelper.isArrayNode(json, "log")) {
                in = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "log"));
            } else {
                in = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "log"));
            }
            ItemStack out = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "planks"));
            ItemStack secondary = ItemStack.EMPTY;
            if (json.has("secondary")) {
                secondary = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "secondary"));
            }
            ItemStack tertiary = ItemStack.EMPTY;
            if (json.has("tertiary")) {
                tertiary = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "tertiary"));
            }

            return this.factory.create(id, in, out, secondary, tertiary);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readItem(), buffer.readItem());
            } catch (Exception e) {
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.log.toNetwork(buffer);
                buffer.writeItem(recipe.planks);
                buffer.writeItem(recipe.secondary);
                buffer.writeItem(recipe.tertiary);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends SawmillRecipe>
        {
            T create(ResourceLocation id, Ingredient in, ItemStack out, ItemStack out2, ItemStack out3);
        }
    }
}
