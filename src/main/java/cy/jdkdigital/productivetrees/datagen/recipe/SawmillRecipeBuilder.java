package cy.jdkdigital.productivetrees.datagen.recipe;

import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public record SawmillRecipeBuilder(Ingredient log, ItemStack plank, ItemStack secondary, ItemStack tertiary) implements RecipeBuilder
{
    public static SawmillRecipeBuilder direct(Ingredient logs, ItemStack plank, ItemStack secondary, ItemStack tertiary) {
        return new SawmillRecipeBuilder(logs, plank, secondary, tertiary);
    }

    public static SawmillRecipeBuilder tree(WoodObject tree, TagKey<Item> woodTag, Block plank) {
        ItemStack tertiary =
                tree.getId().getPath().equals("old_fustic") ? new ItemStack(TreeRegistrator.FUSTIC.get()) :
                (tree.getId().getPath().equals("logwood") || tree.getId().getPath().equals("brazilwood")) ? new ItemStack(TreeRegistrator.HAEMATOXYLIN.get()) :
                ItemStack.EMPTY;
        return direct(Ingredient.of(woodTag), new ItemStack(plank, 6), new ItemStack(TreeRegistrator.SAWDUST.get(), 2), tertiary);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return null;
    }

    @Override
    public Item getResult() {
        return plank.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        pRecipeOutput.accept(pId, new SawmillRecipe(log, plank, secondary, tertiary), null);
    }
}
