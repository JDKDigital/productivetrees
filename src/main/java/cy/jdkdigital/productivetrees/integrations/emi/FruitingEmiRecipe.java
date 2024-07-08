package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;

public class FruitingEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/tree_fruiting.png");

    public FruitingEmiRecipe(RecipeHolder<TreeFruitingRecipe> recipe) {
        super(ProductiveTreesEmiPlugin.FRUITING_CATEGORY, recipe.id(), 130, 60);

        this.inputs.add(EmiIngredient.of(Arrays.stream(recipe.value().tree.getItems()).map(TreeUtil::getSaplingFromLeaf).map(EmiStack::of).toList()));

        this.outputs.add(EmiStack.of(recipe.value().result));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 130, 60, 0, 0);

        widgets.addSlot(this.inputs.get(0), 27, 26);
        widgets.addSlot(this.outputs.get(0), 93, 26).recipeContext(this);
    }
}
