package cy.jdkdigital.productivetrees.datagen.recipe;

import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TreetapRecipeBuilder implements RecipeBuilder
{
    private final Ingredient log;
    private final ItemStack result;
    private final ItemStack harvestItem;
    private final String fluidColor;
    private final FluidStack displayFluid;
    private final boolean collectBucket;
    private final int processingTime;
    private final int blocksRequired;

    private TreetapRecipeBuilder(Ingredient log, ItemStack result, ItemStack harvestItem, String fluidColor, FluidStack displayFluid, boolean collectBucket, int processingTime, int blocksRequired) {
        this.log = log;
        this.result = result;
        this.harvestItem = harvestItem;
        this.fluidColor = fluidColor;
        this.displayFluid = displayFluid;
        this.collectBucket = collectBucket;
        this.processingTime = processingTime;
        this.blocksRequired = blocksRequired;
    }

    public static TreetapRecipeBuilder direct(Ingredient log, ItemStack result, ItemStack harvestItem, String fluidColor, FluidStack displayFluid, boolean collectBucket, int processingTime) {
        return new TreetapRecipeBuilder(log, result, harvestItem, fluidColor, displayFluid, collectBucket, processingTime, 5);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, FluidStack fluid, int processingTime) {
        return direct(Ingredient.of(log), result, ItemStack.EMPTY, "", fluid, true, processingTime);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, String fluidColor, int processingTime) {
        return direct(Ingredient.of(log), result, ItemStack.EMPTY, fluidColor, new FluidStack(Fluids.WATER, 1000), false, processingTime);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, ItemStack harvestItem, String fluidColor, int processingTime) {
        return direct(Ingredient.of(log), result, harvestItem, fluidColor, new FluidStack(Fluids.WATER, 1000), false, processingTime);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return null;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation id) {
        consumer.accept(id, new TapExtractRecipe(log, result, ItemStack.EMPTY, harvestItem, collectBucket, processingTime, displayFluid, fluidColor, blocksRequired, List.of()), null);
    }
}
