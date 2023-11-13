package cy.jdkdigital.productivetrees.integrations;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JeiPlugin
public class ProductiveTreesJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveTrees.MODID, ProductiveTrees.MODID);

    public static final RecipeType<TreePollinationRecipe> TREE_POLLINATION_TYPE = RecipeType.create(ProductiveTrees.MODID, "tree_pollination", TreePollinationRecipe.class);
    public static final RecipeType<LogStrippingRecipe> LOG_STRIPPING_TYPE = RecipeType.create(ProductiveTrees.MODID, "log_stripping", LogStrippingRecipe.class);

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ProductiveTrees.POLLEN.get()), TREE_POLLINATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ProductiveTrees.STRIPPER.get()), LOG_STRIPPING_TYPE);
        Arrays.stream(Ingredient.of(ItemTags.AXES).getItems()).forEach(itemStack -> {
            registration.addRecipeCatalyst(itemStack, LOG_STRIPPING_TYPE);
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new TreePollinationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new LogStrippingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<TreePollinationRecipe> recipeMap = recipeManager.getAllRecipesFor(ProductiveTrees.TREE_POLLINATION_TYPE.get());
        registration.addRecipes(TREE_POLLINATION_TYPE, recipeMap);

        List<LogStrippingRecipe> jsonRecipes = recipeManager.getAllRecipesFor(ProductiveTrees.LOG_STRIPPING_TYPE.get());
        List<LogStrippingRecipe> stripMap = new ArrayList<>(jsonRecipes);
        Arrays.stream(Ingredient.of(ItemTags.LOGS).getItems()).forEach(itemStack -> {
            var stripped = TreeUtil.getStrippedItem(itemStack);
            if (!stripped.isEmpty() && !ItemStack.isSameItem(itemStack, stripped)) {
                var id = new ResourceLocation(ProductiveTrees.MODID, ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath() + "_" + ForgeRegistries.ITEMS.getKey(stripped.getItem()).getPath());
                stripMap.add(new LogStrippingRecipe(id, itemStack, stripped));
            }
        });
        registration.addRecipes(LOG_STRIPPING_TYPE, stripMap);
    }
}
