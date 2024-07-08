package cy.jdkdigital.productivetrees.integrations.emi;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.recipe.LogStrippingRecipe;
import cy.jdkdigital.productivetrees.recipe.SawmillRecipe;
import cy.jdkdigital.productivetrees.recipe.TreeFruitingRecipe;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@EmiEntrypoint
public class ProductiveTreesEmiPlugin implements EmiPlugin
{
    public static final EmiRecipeCategory POLLINATION_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "pollination"),
            EmiStack.of(TreeRegistrator.POLLEN.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/emi/icon/pollination.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory STRIPPING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "stripping"),
            EmiStack.of(TreeRegistrator.STRIPPER.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/emi/icon/stripping.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory SAWMILL_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "sawmill"),
            EmiStack.of(TreeRegistrator.SAWMILL.get()),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/emi/icon/sawmill.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );
    public static final EmiRecipeCategory FRUITING_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "fruiting"),
            EmiStack.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "haw"))),
            new EmiTexture(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/emi/icon/fruiting.png"), 0, 0, 16, 16, 16, 16, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(POLLINATION_CATEGORY);
        registry.addCategory(STRIPPING_CATEGORY);
        registry.addCategory(SAWMILL_CATEGORY);
        registry.addCategory(FRUITING_CATEGORY);

        RecipeManager recipeManager = registry.getRecipeManager();

        List<RecipeHolder<TreePollinationRecipe>> pollinationRecipeList = recipeManager.getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
        pollinationRecipeList.forEach(recipeHolder -> {
            registry.addRecipe(new PollinationEmiRecipe(recipeHolder));
        });

        // Tree fruiting recipes
        List<RecipeHolder<TreeFruitingRecipe>> fruitingRecipeList = new ArrayList<>();
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.hasFruit()) {
                fruitingRecipeList.add(new RecipeHolder<>(id.withPath(p -> "/" + p + "_fruiting"), new TreeFruitingRecipe(Ingredient.of(TreeUtil.getBlock(id, "_sapling")), treeObject.getFruit().getItem().copy())));
            }
        });
        fruitingRecipeList.forEach(recipeHolder -> {
            registry.addRecipe(new FruitingEmiRecipe(recipeHolder));
        });

        List<RecipeHolder<LogStrippingRecipe>> jsonRecipes = recipeManager.getAllRecipesFor(TreeRegistrator.LOG_STRIPPING_TYPE.get());
        List<RecipeHolder<LogStrippingRecipe>> stripList = new ArrayList<>(jsonRecipes);
        Arrays.stream(Ingredient.of(ItemTags.LOGS).getItems()).forEach(itemStack -> {
            var stripped = TreeUtil.getStrippedItem(itemStack);
            if (!stripped.isEmpty() && !ItemStack.isSameItem(itemStack, stripped)) {
                Optional<ItemStack> secondary = Optional.empty();
                var id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                if (itemStack.getItem() instanceof BlockItem blockItem) {
                    var tree = TreeUtil.getTree(blockItem.getBlock());
                    if (tree != null && tree.getStripDrop().isPresent()) {
                        secondary = Optional.of(tree.getStripDropStack());
                    }
                }
                stripList.add(new RecipeHolder<>(id.withPath(p -> "/" + p + "_sawmill"), new LogStrippingRecipe(itemStack, stripped, secondary)));
            }
        });
        stripList.forEach(recipeHolder -> {
            registry.addRecipe(new StripperEmiRecipe(recipeHolder));
        });

        List<RecipeHolder<SawmillRecipe>> sawmillRecipeList = recipeManager.getAllRecipesFor(TreeRegistrator.SAW_MILLLING_TYPE.get());
        sawmillRecipeList.forEach(recipeHolder -> {
            registry.addRecipe(new SawmillEmiRecipe(recipeHolder));
        });
    }
}
