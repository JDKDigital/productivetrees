package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.integrations.jei.LogStrippingRecipeCategory;
import cy.jdkdigital.productivetrees.integrations.jei.SawmillRecipeCategory;
import cy.jdkdigital.productivetrees.integrations.jei.TreeFruitingRecipeCategory;
import cy.jdkdigital.productivetrees.integrations.jei.TreePollinationRecipeCategory;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EmiEntrypoint
public class ProductiveTreesEmiPlugin implements EmiPlugin
{
    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(EmiStack.of(ModItems.ADV_BREED_BEE));
        registry.removeEmiStacks(EmiStack.of(ModItems.ADV_BREED_ALL_BEES));
    }
}
