package cy.jdkdigital.productivetrees.integrations.au;

import com.almostreliable.unified.api.plugin.AlmostUnifiedPlugin;
import com.almostreliable.unified.api.unification.bundled.GenericRecipeUnifier;
import com.almostreliable.unified.api.unification.recipe.RecipeJson;
import com.almostreliable.unified.api.unification.recipe.RecipeUnifier;
import com.almostreliable.unified.api.unification.recipe.RecipeUnifierRegistry;
import com.almostreliable.unified.api.unification.recipe.UnificationHelper;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.resources.Identifier;

public class AlmostUnifiedCompat implements AlmostUnifiedPlugin
{
    private static final Identifier pluginId = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, ProductiveTrees.MODID);

    @Override
    public Identifier getPluginId() {
        return pluginId;
    }

    @Override
    public void registerRecipeUnifiers(RecipeUnifierRegistry registry) {
        registry.registerForModId(ProductiveTrees.MODID, new Unifier());
    }

    static class Unifier implements RecipeUnifier
    {
        @Override
        public void unify(UnificationHelper helper, RecipeJson recipe) {
            GenericRecipeUnifier.INSTANCE.unify(helper, recipe);
            helper.unifyInputs(recipe, "leafA", "leafB", "log", "tree");
            helper.unifyOutputs(recipe, "planks", "stripped", "secondary", "tertiary");
        }
    }
}
