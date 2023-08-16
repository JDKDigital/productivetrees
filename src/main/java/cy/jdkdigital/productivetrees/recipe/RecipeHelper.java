package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RecipeHelper
{
    public static TreePollinationRecipe getRecipe(Level level, BlockState leafA, BlockState leafB) {
        var allRecipes = level.getRecipeManager().getAllRecipesFor(ProductiveTrees.TREE_POLLINATION_TYPE.get());
        for (TreePollinationRecipe treePollinationRecipe : allRecipes) {
            if (treePollinationRecipe.matches(leafA, leafB) || treePollinationRecipe.matches(leafB, leafA)) {
                return treePollinationRecipe;
            }
        }
        return null;
    }
}
