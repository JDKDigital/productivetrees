package cy.jdkdigital.productivetrees.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TreePollinationRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<TreePollinationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("leafA").forGetter(recipe -> recipe.leafA),
                            Ingredient.CODEC.fieldOf("leafB").forGetter(recipe -> recipe.leafB),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.resultTemplate),
                            Codec.FLOAT.fieldOf("chance").orElse(0.1f).forGetter(recipe -> recipe.chance)
                    )
                    .apply(builder, TreePollinationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TreePollinationRecipe> STREAM_CODEC = StreamCodec.of(
            TreePollinationRecipe::toNetwork, TreePollinationRecipe::fromNetwork
    );

    public static final RecipeSerializer<TreePollinationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Ingredient leafA;
    public final Ingredient leafB;
    public final ItemStackTemplate resultTemplate;
    public final float chance;

    private ItemStack resultCache;

    public TreePollinationRecipe(Ingredient leafA, Ingredient leafB, ItemStackTemplate resultTemplate, float chance) {
        this.leafA = leafA;
        this.leafB = leafB;
        this.resultTemplate = resultTemplate;
        this.chance = chance;
    }

    public ItemStack result() {
        if (resultCache == null) {
            resultCache = resultTemplate.create();
        }
        return resultCache;
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
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<TreePollinationRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<TreePollinationRecipe> getType() {
        return TreeRegistrator.TREE_POLLINATION_TYPE.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static TreePollinationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        return new TreePollinationRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer), buffer.readFloat());
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, TreePollinationRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.leafA);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.leafB);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.resultTemplate);
        buffer.writeFloat(recipe.chance);
    }
}
