package cy.jdkdigital.productivetrees.integrations.jei;

import cy.jdkdigital.productivetrees.ProductiveTrees;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class ProductiveTreesJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, ProductiveTrees.MODID);

    public static final RecipeType<TreePollinationRecipe> TREE_POLLINATION_TYPE = RecipeType.create(ProductiveTrees.MODID, "tree_pollination", TreePollinationRecipe.class);
    public static final RecipeType<TreeFruitingRecipe> TREE_FRUITING_TYPE = RecipeType.create(ProductiveTrees.MODID, "tree_fruiting", TreeFruitingRecipe.class);
    public static final RecipeType<LogStrippingRecipe> LOG_STRIPPING_TYPE = RecipeType.create(ProductiveTrees.MODID, "log_stripping", LogStrippingRecipe.class);
    public static final RecipeType<SawmillRecipe> SAWMILL_TYPE = RecipeType.create(ProductiveTrees.MODID, "sawmill", SawmillRecipe.class);

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TreeRegistrator.POLLEN.get()), TREE_POLLINATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(TreeRegistrator.STRIPPER.get()), LOG_STRIPPING_TYPE);
        Arrays.stream(Ingredient.of(ModTags.STRIPPER_TOOLS).getItems()).forEach(itemStack -> {
            registration.addRecipeCatalyst(itemStack.copy(), LOG_STRIPPING_TYPE);
        });
        registration.addRecipeCatalyst(new ItemStack(TreeRegistrator.SAWMILL.get()), SAWMILL_TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new TreePollinationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new TreeFruitingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new LogStrippingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new SawmillRecipeCategory(guiHelper));
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        List<String> mutationTrees = new ArrayList<>();
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (!treeObject.getMutationInfo().target().equals(ProductiveTrees.EMPTY_RL)) {
                mutationTrees.add(treeObject.getMutationInfo().target().getPath());
            }
        });
        List<ItemStack> hiddenTrees = new ArrayList<>();
        ProductiveTrees.ITEMS.getEntries().forEach(item -> {
            mutationTrees.forEach(s -> {
                if (item.getId().getPath().startsWith(s)) {
                    hiddenTrees.add(new ItemStack(item.get()));
                }
            });
        });

        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenTrees);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RecipeHolder<TreePollinationRecipe>> pollinationRecipeList = recipeManager.getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
        registration.addRecipes(TREE_POLLINATION_TYPE, pollinationRecipeList.stream().map(RecipeHolder::value).toList());

        // Tree fruiting recipes
        List<TreeFruitingRecipe> fruitingRecipeList = new ArrayList<>();
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.hasFruit()) {
                fruitingRecipeList.add(new TreeFruitingRecipe(Ingredient.of(TreeUtil.getBlock(id, "_sapling")), treeObject.getFruit().getItem().copy()));
            }
        });
        registration.addRecipes(TREE_FRUITING_TYPE, fruitingRecipeList);

        List<LogStrippingRecipe> jsonRecipes = recipeManager.getAllRecipesFor(TreeRegistrator.LOG_STRIPPING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<LogStrippingRecipe> stripList = new ArrayList<>(jsonRecipes);
        Arrays.stream(Ingredient.of(ItemTags.LOGS).getItems()).forEach(itemStack -> {
            var stripped = TreeUtil.getStrippedItem(itemStack);
            if (!stripped.isEmpty() && !ItemStack.isSameItem(itemStack, stripped)) {
                Optional<ItemStack> secondary = Optional.empty();
                if (itemStack.getItem() instanceof BlockItem blockItem) {
                    var tree = TreeUtil.getTree(blockItem.getBlock());
                    if (tree != null && tree.getStripDrop().isPresent()) {
                        secondary = Optional.of(tree.getStripDropStack());
                    }
                }
                stripList.add(new LogStrippingRecipe(itemStack, stripped, secondary));
            }
        });
        registration.addRecipes(LOG_STRIPPING_TYPE, stripList);

        List<RecipeHolder<SawmillRecipe>> sawmillRecipeList = recipeManager.getAllRecipesFor(TreeRegistrator.SAW_MILLLING_TYPE.get());
        registration.addRecipes(SAWMILL_TYPE, sawmillRecipeList.stream().map(RecipeHolder::value).toList());
    }
}
