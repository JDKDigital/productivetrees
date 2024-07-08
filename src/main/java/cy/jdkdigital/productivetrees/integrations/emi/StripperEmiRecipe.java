package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;

public class StripperEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/jei/stripping.png");

    public StripperEmiRecipe(RecipeHolder<LogStrippingRecipe> recipe) {
        super(ProductiveTreesEmiPlugin.STRIPPING_CATEGORY, recipe.id(), 130, 60);

        this.inputs.add(EmiStack.of(recipe.value().log));
        this.inputs.add(EmiIngredient.of(ModTags.STRIPPER_TOOLS));

        this.outputs.add(EmiStack.of(recipe.value().stripped));
        recipe.value().secondary.ifPresent(itemStack -> {
            this.outputs.add(EmiStack.of(itemStack));
        });
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 130, 60, 0, 0);

        widgets.addSlot(this.inputs.get(0), 29, 14);
        widgets.addSlot(this.inputs.get(1), 29, 33);

        widgets.addSlot(this.outputs.get(0), 82, 14).recipeContext(this);
        if (this.outputs.size() > 1) {
            widgets.addSlot(this.outputs.get(1), 82, 33).recipeContext(this);
        }
    }
}
