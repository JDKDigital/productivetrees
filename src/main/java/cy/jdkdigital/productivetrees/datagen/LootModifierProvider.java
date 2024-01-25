package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class LootModifierProvider extends GlobalLootModifierProvider
{
    public LootModifierProvider(PackOutput output) {
        super(output, ProductiveTrees.MODID);
    }

    @Override
    protected void start() {
        add("black_ember_sapling", new ItemLootModifier(lootConditions("chests/bastion_treasure"), ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "black_ember_sapling")), 0.1f));
        add("blue_yonder_sapling", new ItemLootModifier(lootConditions("chests/shipwreck_treasure"), ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "blue_yonder_sapling")), 0.1f));
        add("soul_tree_sapling", new ItemLootModifier(lootConditions("chests/end_city_treasure"), ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "soul_tree_sapling")), 0.1f));
        add("flickering_sun_sapling", new ItemLootModifier(lootConditions("chests/desert_pyramid"), ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "flickering_sun_sapling")), 0.05f));
        add("firecracker_sapling", new ItemLootModifier(lootConditions("chests/ancient_city"), ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "firecracker_sapling")), 0.1f));
        add("brown_amber_sapling", new IngredientModifier(lootConditions("archaeology/ocean_ruin_cold"), Ingredient.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveTrees.MODID, "brown_amber_sapling"))), 0.1f, true));
    }

    private LootItemCondition[] lootConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(new ResourceLocation(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }
}
