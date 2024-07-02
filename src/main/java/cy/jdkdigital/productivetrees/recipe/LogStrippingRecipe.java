package cy.jdkdigital.productivetrees.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class LogStrippingRecipe implements Recipe<RecipeInput>
{
    public final ItemStack log;
    public final ItemStack stripped;

    public LogStrippingRecipe(ItemStack log, ItemStack stripped) {
        this.log = log;
        this.stripped = stripped;
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
        return this.stripped.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.LOG_STRIPPING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.LOG_STRIPPING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<LogStrippingRecipe>
    {
        private static final MapCodec<LogStrippingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ItemStack.CODEC.fieldOf("log").forGetter(recipe -> recipe.log),
                                ItemStack.CODEC.fieldOf("stripped").forGetter(recipe -> recipe.stripped)
                        )
                        .apply(builder, LogStrippingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, LogStrippingRecipe> STREAM_CODEC = StreamCodec.of(
                LogStrippingRecipe.Serializer::toNetwork, LogStrippingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<LogStrippingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LogStrippingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static LogStrippingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new LogStrippingRecipe(ItemStack.STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, LogStrippingRecipe recipe) {
            try {
                ItemStack.STREAM_CODEC.encode(buffer, recipe.log);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.stripped);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
