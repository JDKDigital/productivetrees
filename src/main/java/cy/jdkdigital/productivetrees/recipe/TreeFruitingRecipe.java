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

public class TreeFruitingRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Ingredient tree;
    public final ItemStack result;

    public TreeFruitingRecipe(ResourceLocation id, Ingredient tree, ItemStack result) {
        this.id = id;
        this.tree = tree;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess level) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess level) {
        return this.result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.TREE_FRUITING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.TREE_FRUITING_TYPE.get();
    }

    public static class Serializer<T extends TreeFruitingRecipe> implements RecipeSerializer<T>
    {
        final TreeFruitingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(TreeFruitingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient tree;
            if (GsonHelper.isArrayNode(json, "tree")) {
                tree = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "tree"));
            } else {
                tree = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "tree"));
            }
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return this.factory.create(id, tree, output);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), buffer.readItem());
            } catch (Exception e) {
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.tree.toNetwork(buffer);
                buffer.writeItem(recipe.result);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends TreeFruitingRecipe>
        {
            T create(ResourceLocation id, Ingredient tree, ItemStack result);
        }
    }
}
