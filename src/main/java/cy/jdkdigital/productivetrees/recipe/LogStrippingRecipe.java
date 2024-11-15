package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivelib.common.recipe.TripleOutputRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class LogStrippingRecipe extends TripleOutputRecipe
{
    public LogStrippingRecipe(Ingredient input, ItemStack output, ItemStack secondary, ItemStack tertiary) {
        super(input, output, secondary, tertiary);
    }

    public LogStrippingRecipe(ItemStack log, ItemStack stripped, ItemStack secondary) {
        this(Ingredient.of(log), stripped, secondary, ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.LOG_STRIPPING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.LOG_STRIPPING_TYPE.get();
    }
}
