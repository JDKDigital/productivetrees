package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RecipeHelper
{
    public static RecipeHolder<TreePollinationRecipe> getPollinationRecipe(Level level, BlockState leafA, BlockState leafB) {
        List<RecipeHolder<TreePollinationRecipe>> matchedRecipes = new ArrayList<>();
        var allRecipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
        for (RecipeHolder<TreePollinationRecipe> treePollinationRecipe : allRecipes) {
            if (treePollinationRecipe.value().matches(leafA, leafB) || treePollinationRecipe.value().matches(leafB, leafA)) {
                matchedRecipes.add(treePollinationRecipe);
            }
        }
        return matchedRecipes.size() > 0 ? matchedRecipes.get(level.random.nextInt(matchedRecipes.size())) : null;
    }
}
