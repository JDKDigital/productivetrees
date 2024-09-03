package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;

public class SawmillEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/sawmill.png");

    public SawmillEmiRecipe(RecipeHolder<SawmillRecipe> recipe) {
        super(ProductiveTreesEmiPlugin.SAWMILL_CATEGORY, recipe.id(), 130, 60);

        this.inputs.add(EmiIngredient.of(Arrays.stream(recipe.value().input().getItems()).map(TreeUtil::getSaplingFromLeaf).map(EmiStack::of).toList()));

        this.outputs.add(EmiStack.of(recipe.value().output()));
        if (!recipe.value().secondary().isEmpty()) {
            this.outputs.add(EmiStack.of(recipe.value().secondary()));
        }
        if (!recipe.value().tertiary().isEmpty()) {
            this.outputs.add(EmiStack.of(recipe.value().tertiary()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 130, 60, 0, 0);

        widgets.addSlot(this.inputs.get(0), 29, 23);

        widgets.addSlot(this.outputs.get(0), 87, 14).recipeContext(this);
        if (this.outputs.size() > 1) {
            widgets.addSlot(this.outputs.get(1), 78, 32).recipeContext(this);
        }
        if (this.outputs.size() > 2) {
            widgets.addSlot(this.outputs.get(2), 96, 32).recipeContext(this);
        }
    }
}
