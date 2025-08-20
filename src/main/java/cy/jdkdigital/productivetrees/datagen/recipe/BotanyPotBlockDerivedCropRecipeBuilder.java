package cy.jdkdigital.productivetrees.datagen.recipe;

import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BlockDerivedCrop;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BotanyPotBlockDerivedCropRecipeBuilder(Block block, Optional<Ingredient> seed, Ingredient soil, int growTime, Optional<List<Display>> display, int lightLevel, Optional<List<ItemDropProvider>> drops, Optional<BasicOptions> renderOptions, Optional<ResourceLocation> functionId, Optional<BlockPredicate> potPredicate, float baseYield, float yieldScale) implements RecipeBuilder
{
    public static BotanyPotBlockDerivedCropRecipeBuilder direct(Block block, Optional<Ingredient> seed, Ingredient soil, int growTime, Optional<List<Display>> display, int lightLevel, Optional<List<ItemDropProvider>> drops, Optional<BasicOptions> renderOptions, Optional<ResourceLocation> functionId, Optional<BlockPredicate> potPredicate, float baseYield, float yieldScale) {
        return new BotanyPotBlockDerivedCropRecipeBuilder(block, seed, soil, growTime, display, lightLevel, drops, renderOptions, functionId, potPredicate, baseYield, yieldScale);
    }

    public static BotanyPotBlockDerivedCropRecipeBuilder drops(Block block, Ingredient input, Ingredient soil, List<ItemDropProvider> drops, List<Display> display) {
        return direct(block, Optional.of(input), soil, 2400, Optional.of(display), 0, Optional.of(drops), Optional.empty(), Optional.empty(), Optional.empty(), 1f, 1f);
    }

    public static BotanyPotBlockDerivedCropRecipeBuilder drops(Block block, Ingredient input, Ingredient soil, List<ItemDropProvider> drops) {
        return drops(block, input, soil, drops, List.of(new AgingDisplayState(block, BasicOptions.ofDefault())));
    }

    public static BotanyPotBlockDerivedCropRecipeBuilder drops(Block block, Ingredient input, List<ItemDropProvider> drops) {
        return drops(block, input, BasicCrop.DIRT, drops);
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return null;
    }

    @Override
    public Item getResult() {
        return Items.STICK;
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation id) {
        consumer.accept(id, new BlockDerivedCrop(new BlockDerivedCrop.Properties(block, seed, soil, growTime, display, lightLevel, drops, renderOptions, functionId, potPredicate, baseYield, yieldScale)), null);
    }
}
