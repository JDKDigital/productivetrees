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
import java.util.Optional;

public class SawmillRecipe implements Recipe<RecipeInput>
{
    public final Ingredient log;
    public final ItemStack planks;
    public final Optional<ItemStack> secondary;
    public final Optional<ItemStack> tertiary;

    public SawmillRecipe(Ingredient log, ItemStack planks, Optional<ItemStack> secondary, Optional<ItemStack> tertiary) {
        this.log = log;
        this.planks = planks;
        this.secondary = secondary;
        this.tertiary = tertiary;
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
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.planks.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.SAW_MILLLING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.SAW_MILLLING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SawmillRecipe>
    {
        private static final MapCodec<SawmillRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        Ingredient.CODEC.fieldOf("log").forGetter(recipe -> recipe.log),
                        ItemStack.CODEC.fieldOf("planks").forGetter(recipe -> recipe.planks),
                        ItemStack.CODEC.optionalFieldOf("secondary").forGetter(recipe -> recipe.secondary),
                        ItemStack.CODEC.optionalFieldOf("tertiary").forGetter(recipe -> recipe.tertiary)
                )
                .apply(builder, SawmillRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> STREAM_CODEC = StreamCodec.of(
                SawmillRecipe.Serializer::toNetwork, SawmillRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static SawmillRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new SawmillRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        ItemStack.STREAM_CODEC.decode(buffer),
                        Optional.of(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)),
                        Optional.of(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer))
                );
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, SawmillRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.log);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.planks);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.secondary.orElse(ItemStack.EMPTY));
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.tertiary.orElse(ItemStack.EMPTY));
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
