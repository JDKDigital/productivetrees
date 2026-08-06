package cy.jdkdigital.productivetrees.datagen.compat;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.recipe.AgriTechEvolvedTreeRecipeBuilder;
import cy.jdkdigital.productivetrees.datagen.recipe.AgriTechTreeRecipeBuilder;
import cy.jdkdigital.productivetrees.datagen.recipe.TreeDropSpec;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AgriTechRecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    private static final String AGRITECH = "agritechtwo";
    private static final String AGRITECH_EVOLVED = "agritechevolved";
    private static final TagKey<Item> AGRITECH_SOILS = ItemTags.create(Identifier.fromNamespaceAndPath(AGRITECH, "tree_soils"));
    private static final TagKey<Item> AGRITECH_EVOLVED_SOILS = ItemTags.create(Identifier.fromNamespaceAndPath(AGRITECH_EVOLVED, "tree_soils"));

    public AgriTechRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        RecipeOutput agritech = this.output.withConditions(new ModLoadedCondition(AGRITECH));
        RecipeOutput agritechEvolved = this.output.withConditions(new ModLoadedCondition(AGRITECH_EVOLVED));

        TreeFinder.trees.forEach((id, tree) -> {
            Block log = TreeUtil.getBlock(id, "_log");
            Block sapling = TreeUtil.getBlock(id, "_sapling");
            if (log.equals(Blocks.AIR) || sapling.equals(Blocks.AIR)) {
                return;
            }

            List<TreeDropSpec> drops = new ArrayList<>();
            drops.add(new TreeDropSpec(log.asItem(), 2, 6, 1.0F));
            drops.add(new TreeDropSpec(sapling.asItem(), 1, 2, 0.5F));
            drops.add(new TreeDropSpec(Items.STICK, 1, 2, 0.5F));
            addFruitDrop(tree, drops);

            Ingredient saplingIngredient = Ingredient.of(sapling);

            Identifier agritechId = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "agritech/" + id.getPath());
            AgriTechTreeRecipeBuilder.tree(agritechId, saplingIngredient, List.of(tag(AGRITECH_SOILS)), drops)
                    .save(agritech, ResourceKey.create(Registries.RECIPE, agritechId));

            Identifier evolvedId = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "agritech_evolved/" + id.getPath());
            AgriTechEvolvedTreeRecipeBuilder.tree(evolvedId, saplingIngredient, List.of(tag(AGRITECH_EVOLVED_SOILS)), drops)
                    .save(agritechEvolved, ResourceKey.create(Registries.RECIPE, evolvedId));
        });
    }

    private static void addFruitDrop(TreeObject tree, List<TreeDropSpec> drops) {
        if (!tree.hasFruit()) {
            return;
        }
        Item fruit = tree.getFruit().getItemType();
        if (!fruit.equals(Items.AIR)) {
            drops.add(new TreeDropSpec(fruit, 1, Math.max(1, tree.getFruit().count()), tree.getFruit().growthSpeed()));
        }
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new AgriTechRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Productive Trees AgriTech Compat Recipes";
        }
    }
}
