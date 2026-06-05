package cy.jdkdigital.productivetrees.recipe;

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

import javax.annotation.Nonnull;

public class TreeFruitingRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<TreeFruitingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("tree").forGetter(recipe -> recipe.tree),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.resultTemplate)
                    )
                    .apply(builder, TreeFruitingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TreeFruitingRecipe> STREAM_CODEC = StreamCodec.of(
            TreeFruitingRecipe::toNetwork, TreeFruitingRecipe::fromNetwork
    );

    public static final RecipeSerializer<TreeFruitingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Ingredient tree;
    public final ItemStackTemplate resultTemplate;

    private ItemStack resultCache;

    public TreeFruitingRecipe(Ingredient tree, ItemStackTemplate resultTemplate) {
        this.tree = tree;
        this.resultTemplate = resultTemplate;
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

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<TreeFruitingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<TreeFruitingRecipe> getType() {
        return TreeRegistrator.TREE_FRUITING_TYPE.get();
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

    public static TreeFruitingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        return new TreeFruitingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer));
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, TreeFruitingRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tree);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.resultTemplate);
    }
}
