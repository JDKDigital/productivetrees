package cy.jdkdigital.productivetrees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TreePollinationRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Ingredient leafA;
    public final Ingredient leafB;
    public final ItemStack result;
    public final Integer chance;

    public TreePollinationRecipe(ResourceLocation id, Ingredient leafA, Ingredient leafB, ItemStack result, Integer chance) {
        this.id = id;
        this.leafA = leafA;
        this.leafB = leafB;
        this.result = result;
        this.chance = chance;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean matches(BlockState leafAState, BlockState leafBState) {
        return leafA.test(new ItemStack(leafAState.getBlock().asItem())) && leafB.test(new ItemStack(leafBState.getBlock().asItem()));
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
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProductiveTrees.TREE_POLLINATION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProductiveTrees.TREE_POLLINATION_TYPE.get();
    }

    public static class Serializer<T extends TreePollinationRecipe> implements RecipeSerializer<T>
    {
        final TreePollinationRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(TreePollinationRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient inputA;
            if (GsonHelper.isArrayNode(json, "leafA")) {
                inputA = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "leafA"));
            } else {
                inputA = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "leafA"));
            }
            Ingredient inputB;
            if (GsonHelper.isArrayNode(json, "leafB")) {
                inputB = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "leafB"));
            } else {
                inputB = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "leafB"));
            }
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            int chance = GsonHelper.getAsInt(json, "chance", 100);

            return this.factory.create(id, inputA, inputB, output, chance);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readInt());
            } catch (Exception e) {
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.leafA.toNetwork(buffer);
                recipe.leafB.toNetwork(buffer);
                buffer.writeItem(recipe.result);
                buffer.writeInt(recipe.chance);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends TreePollinationRecipe>
        {
            T create(ResourceLocation id, Ingredient leafA, Ingredient leafB, ItemStack result, Integer chance);
        }
    }
}
