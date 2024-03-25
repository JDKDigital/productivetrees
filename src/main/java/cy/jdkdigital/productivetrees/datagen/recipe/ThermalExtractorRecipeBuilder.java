package cy.jdkdigital.productivetrees.datagen.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivelib.util.RecipeUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
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
            json.addProperty("trunk", ForgeRegistries.BLOCKS.getKey(log).toString());
            json.addProperty("leaves", ForgeRegistries.BLOCKS.getKey(leaves).toString());

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
