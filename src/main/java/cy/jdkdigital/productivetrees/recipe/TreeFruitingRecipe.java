package cy.jdkdigital.productivetrees.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TreeFruitingRecipe implements Recipe<RecipeInput>
{
    public final Ingredient tree;
    public final ItemStack result;

    public TreeFruitingRecipe(Ingredient tree, ItemStack result) {
        this.tree = tree;
        this.result = result;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.TREE_FRUITING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.TREE_FRUITING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TreeFruitingRecipe>
    {
        private static final MapCodec<TreeFruitingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("tree").forGetter(recipe -> recipe.tree),
                                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        )
                        .apply(builder, TreeFruitingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TreeFruitingRecipe> STREAM_CODEC = StreamCodec.of(
                TreeFruitingRecipe.Serializer::toNetwork, TreeFruitingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<TreeFruitingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TreeFruitingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static TreeFruitingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new TreeFruitingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, TreeFruitingRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tree);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
