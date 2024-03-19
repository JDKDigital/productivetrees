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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class TreetapRecipeBuilder implements RecipeBuilder
{
    private final Ingredient log;
    private final ItemStack result;
    private final ItemStack harvestItem;
    private final String fluidColor;
    private final FluidStack displayFluid;
    private final boolean collectBucket;
    private final int processingTime;
    private final int blocksRequired;
    public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    private TreetapRecipeBuilder(Ingredient log, ItemStack result, ItemStack harvestItem, String fluidColor, FluidStack displayFluid, boolean collectBucket, int processingTime, int blocksRequired) {
        this.log = log;
        this.result = result;
        this.harvestItem = harvestItem;
        this.fluidColor = fluidColor;
        this.displayFluid = displayFluid;
        this.collectBucket = collectBucket;
        this.processingTime = processingTime;
        this.blocksRequired = blocksRequired;
    }

    public static TreetapRecipeBuilder direct(Ingredient log, ItemStack result, ItemStack harvestItem, String fluidColor, FluidStack displayFluid, boolean collectBucket, int processingTime) {
        return new TreetapRecipeBuilder(log, result, harvestItem, fluidColor, displayFluid, collectBucket, processingTime, 5);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, FluidStack fluid, int processingTime) {
        return direct(Ingredient.of(log), result, ItemStack.EMPTY, "", fluid, true, processingTime);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, String fluidColor, int processingTime) {
        return direct(Ingredient.of(log), result, ItemStack.EMPTY, fluidColor, FluidStack.EMPTY, false, processingTime);
    }

    public static TreetapRecipeBuilder direct(Block log, ItemStack result, ItemStack harvestItem, String fluidColor, int processingTime) {
        return direct(Ingredient.of(log), result, harvestItem, fluidColor, FluidStack.EMPTY, false, processingTime);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return null;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, log, result, harvestItem, fluidColor, displayFluid, collectBucket, processingTime, blocksRequired, this.advancement));
    }

    record Result(ResourceLocation id, Ingredient log, ItemStack result, ItemStack harvestItem, String fluidColor,
                  FluidStack displayFluid, boolean collectBucket, int processingTime, int blocksRequired,
                  Advancement.Builder advancement) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("log", log.toJson());
            json.add("result", RecipeUtil.itemToJson(result));
            json.addProperty("processing_time", processingTime);
            if (!harvestItem.isEmpty()) {
                json.add("harvest_item", RecipeUtil.itemToJson(harvestItem));
            }
            if (collectBucket) {
                json.addProperty("collect_bucket", true);
            }
            if (blocksRequired > 1) {
                json.addProperty("required_block_count", blocksRequired);
            }
            if (!displayFluid.isEmpty()) {
                json.add("display_fluid", RecipeUtil.fluidToJson(displayFluid));
            } else {
                json.addProperty("fluid_color", fluidColor);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("treetap:tap_extract"));
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
