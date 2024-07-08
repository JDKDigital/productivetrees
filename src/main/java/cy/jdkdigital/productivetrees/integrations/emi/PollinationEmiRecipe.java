package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;

public class PollinationEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/tree_pollination.png");

    public PollinationEmiRecipe(RecipeHolder<TreePollinationRecipe> recipe) {
        super(ProductiveTreesEmiPlugin.POLLINATION_CATEGORY, recipe.id(), 130, 60);

        this.inputs.add(EmiIngredient.of(Arrays.stream(recipe.value().leafA.getItems()).map(TreeUtil::getSaplingFromLeaf).map(EmiStack::of).toList()));
        this.inputs.add(EmiIngredient.of(Arrays.stream(recipe.value().leafB.getItems()).map(TreeUtil::getSaplingFromLeaf).map(EmiStack::of).toList()));
        this.catalysts.add(EmiIngredient.of(Arrays.stream(recipe.value().leafA.getItems()).map(EmiStack::of).toList()));
        this.catalysts.add(EmiIngredient.of(Arrays.stream(recipe.value().leafB.getItems()).map(EmiStack::of).toList()));

        this.outputs.add(EmiStack.of(recipe.value().result));
        this.outputs.add(EmiStack.of(TreeUtil.getLeafFromSapling(recipe.value().result)));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 130, 60, 0, 0);

        widgets.addSlot(this.inputs.get(0), 12, 26);
        widgets.addSlot(this.inputs.get(1), 55, 26);
        widgets.addSlot(this.outputs.get(0), 108, 26).recipeContext(this);
    }
}
