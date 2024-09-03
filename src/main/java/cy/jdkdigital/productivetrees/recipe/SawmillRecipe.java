package cy.jdkdigital.productivetrees.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.common.recipe.TripleOutputRecipe;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class SawmillRecipe extends TripleOutputRecipe
{
    public SawmillRecipe(Ingredient log, ItemStack planks, ItemStack secondary, ItemStack tertiary) {
        super(log, planks, secondary, tertiary);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TreeRegistrator.SAW_MILLLING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TreeRegistrator.SAW_MILLLING_TYPE.get();
    }
}
