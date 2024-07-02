package cy.jdkdigital.productivetrees.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TreePollinationRecipe implements Recipe<RecipeInput>
{
    public final Ingredient leafA;
    public final Ingredient leafB;
    public final ItemStack result;
    public final float chance;

    public TreePollinationRecipe(Ingredient leafA, Ingredient leafB, ItemStack result, float chance) {
        this.leafA = leafA;
        this.leafB = leafB;
        this.result = result;
        this.chance = chance;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    public boolean matches(BlockState leafAState, BlockState leafBState) {
        return leafA.test(new ItemStack(leafAState.getBlock().asItem())) && leafB.test(new ItemStack(leafBState.getBlock().asItem()));
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
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.TREE_POLLINATION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.TREE_POLLINATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TreePollinationRecipe>
    {
        private static final MapCodec<TreePollinationRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("leafA").forGetter(recipe -> recipe.leafA),
                                Ingredient.CODEC.fieldOf("leafB").forGetter(recipe -> recipe.leafB),
                                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Codec.FLOAT.fieldOf("processingTime").orElse(0.1f).forGetter(recipe -> recipe.chance)
                        )
                        .apply(builder, TreePollinationRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TreePollinationRecipe> STREAM_CODEC = StreamCodec.of(
                TreePollinationRecipe.Serializer::toNetwork, TreePollinationRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<TreePollinationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TreePollinationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static TreePollinationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new TreePollinationRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer), buffer.readFloat());
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, TreePollinationRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.leafA);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.leafB);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                buffer.writeFloat(recipe.chance);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
