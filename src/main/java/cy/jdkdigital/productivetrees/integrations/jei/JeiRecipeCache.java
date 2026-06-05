package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ProductiveTrees.MODID)
public final class JeiRecipeCache
{
    private static RecipeMap recipeMap = RecipeMap.EMPTY;

    private JeiRecipeCache() {}

    public static RecipeMap getRecipeMap() {
        return recipeMap;
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        recipeMap = event.getRecipeMap();
    }
}
