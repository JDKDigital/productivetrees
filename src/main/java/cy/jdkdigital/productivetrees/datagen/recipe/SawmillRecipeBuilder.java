package cy.jdkdigital.productivetrees.datagen.recipe;

import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SawmillRecipeBuilder(Ingredient log, ItemStackTemplate plank, Optional<ItemStackTemplate> secondary, Optional<ItemStackTemplate> tertiary) implements RecipeBuilder
{
    public static SawmillRecipeBuilder direct(Ingredient logs, ItemStackTemplate plank, Optional<ItemStackTemplate> secondary, Optional<ItemStackTemplate> tertiary) {
        return new SawmillRecipeBuilder(logs, plank, secondary, tertiary);
    }

    public static SawmillRecipeBuilder direct(Ingredient logs, ItemStackTemplate plank, ItemStackTemplate secondary) {
        return new SawmillRecipeBuilder(logs, plank, Optional.of(secondary), Optional.empty());
    }

    public static SawmillRecipeBuilder tree(WoodObject tree, Ingredient logs, Block plank) {
        Optional<ItemStackTemplate> tertiary =
                tree.getId().getPath().equals("old_fustic") ? Optional.of(new ItemStackTemplate(TreeRegistrator.FUSTIC.get())) :
                (tree.getId().getPath().equals("logwood") || tree.getId().getPath().equals("brazilwood")) ? Optional.of(new ItemStackTemplate(TreeRegistrator.HAEMATOXYLIN.get())) :
                Optional.empty();
        return new SawmillRecipeBuilder(logs, new ItemStackTemplate(plank.asItem(), 6), Optional.of(new ItemStackTemplate(TreeRegistrator.SAWDUST.get(), 2)), tertiary);
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
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(plank.item().value()));
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
        pRecipeOutput.accept(pId, new SawmillRecipe(log, plank, secondary, tertiary), null);
    }
}
