package cy.jdkdigital.productivetrees.recipe;

import cy.jdkdigital.productivelib.compat.jei.RecipeMapCache;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RecipeHelper
{
    public static RecipeHolder<TreePollinationRecipe> getPollinationRecipe(Level level, BlockState leafA, BlockState leafB) {
        List<RecipeHolder<TreePollinationRecipe>> matchedRecipes = new ArrayList<>();
        RecipeMap recipeMap = level instanceof ServerLevel serverLevel ? serverLevel.recipeAccess().recipeMap() : RecipeMapCache.getRecipeMap();
        var allRecipes = recipeMap.byType(TreeRegistrator.TREE_POLLINATION_TYPE.get());
        for (RecipeHolder<TreePollinationRecipe> treePollinationRecipe : allRecipes) {
            if (treePollinationRecipe.value().matches(leafA, leafB) || treePollinationRecipe.value().matches(leafB, leafA)) {
                matchedRecipes.add(treePollinationRecipe);
            }
        }
        return matchedRecipes.size() > 0 ? matchedRecipes.get(level.getRandom().nextInt(matchedRecipes.size())) : null;
    }
}
