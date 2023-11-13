package cy.jdkdigital.productivetrees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class LogStrippingRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final ItemStack log;
    public final ItemStack stripped;

    public LogStrippingRecipe(ResourceLocation id, ItemStack log, ItemStack stripped) {
        this.id = id;
        this.log = log;
        this.stripped = stripped;
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
        return this.stripped.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProductiveTrees.LOG_STRIPPING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProductiveTrees.LOG_STRIPPING_TYPE.get();
    }

    public static class Serializer<T extends LogStrippingRecipe> implements RecipeSerializer<T>
    {
        final LogStrippingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(LogStrippingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            ItemStack in = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "log"));
            ItemStack out = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "stripped"));

            return this.factory.create(id, in, out);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, buffer.readItem(), buffer.readItem());
            } catch (Exception e) {
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeItem(recipe.log);
                buffer.writeItem(recipe.stripped);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends LogStrippingRecipe>
        {
            T create(ResourceLocation id, ItemStack in, ItemStack out);
        }
    }
}
