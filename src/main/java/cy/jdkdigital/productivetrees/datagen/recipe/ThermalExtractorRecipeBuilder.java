package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivelib.util.RecipeUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class ThermalExtractorRecipeBuilder implements RecipeBuilder
{
    private final Block log;
    private final Block leaves;
    private final FluidStack fluid;
    public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public ThermalExtractorRecipeBuilder(Block log, Block leaves, FluidStack fluid) {
        this.log = log;
        this.leaves = leaves;
        this.fluid = fluid;
    }

    public static ThermalExtractorRecipeBuilder direct(Block log, Block leaves, FluidStack fluid) {
        return new ThermalExtractorRecipeBuilder(log, leaves, fluid);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return null;
    }

    @Override
    public Item getResult() {
        return log.asItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, log, leaves, fluid, this.advancement));
    }

    record Result(ResourceLocation id, Block log, Block leaves, FluidStack fluid,
                  Advancement.Builder advancement) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            var tree = TreeUtil.getTree(log);
            json.add("trunk", BlockState.CODEC.encodeStart(JsonOps.INSTANCE, log.defaultBlockState()).getOrThrow(false, ProductiveTrees.LOGGER::error));

            var leafJson = BlockState.CODEC.encodeStart(JsonOps.INSTANCE, leaves.defaultBlockState()).getOrThrow(false, ProductiveTrees.LOGGER::error).getAsJsonObject();
            var properties = leafJson.get("Properties").getAsJsonObject();
            properties.remove("distance");
            properties.remove("waterlogged");
            leafJson.add("Properties", properties);
            if (tree.hasFruit()) {
                var fruit = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveTrees.MODID, tree.getId().getPath() + "_fruit"));
                var fruitJson = BlockState.CODEC.encodeStart(JsonOps.INSTANCE, fruit.defaultBlockState()).getOrThrow(false, ProductiveTrees.LOGGER::error).getAsJsonObject();
                var fruitProperties = fruitJson.get("Properties").getAsJsonObject();
                fruitProperties.remove("distance");
                fruitProperties.remove("waterlogged");
                fruitProperties.remove("age");
                fruitJson.add("Properties", fruitProperties);
                var leaves = new JsonArray();
                leaves.add(leafJson);
                leaves.add(fruitJson);
                json.add("leaves", leaves);
            } else {
                json.add("leaves", leafJson);
            }

            // TODO read tree size from tree feature
            json.addProperty("min_height", 4);
            json.addProperty("max_height", 20);
            json.addProperty("min_leaves", 10);
            json.addProperty("max_leaves", 10);

            json.add("result", RecipeUtil.fluidToJson(fluid));
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("thermal:tree_extractor"));
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return id.withPrefix("recipes/" + RecipeCategory.MISC.getFolderName() + "/");
        }
    }
}
