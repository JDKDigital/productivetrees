package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootModifierProvider extends GlobalLootModifierProvider
{
    public LootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ProductiveTrees.MODID);
    }

    @Override
    protected void start() {
        add("black_ember_sapling", new ItemLootModifier(anyOfConditions("chests/bastion_treasure", "chests/bastion_hoglin_stable"), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "black_ember_sapling"))), 0.3f));
        add("blue_yonder_sapling", new ItemLootModifier(lootConditions("chests/shipwreck_treasure"), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "blue_yonder_sapling"))), 0.3f));
        add("soul_tree_sapling", new ItemLootModifier(lootConditions("chests/end_city_treasure"), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "soul_tree_sapling"))), 0.05f));
        add("flickering_sun_sapling", new ItemLootModifier(lootConditions("chests/desert_pyramid"), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "flickering_sun_sapling"))), 0.15f));
        add("firecracker_sapling", new ItemLootModifier(lootConditions("chests/ancient_city"), 0, new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "firecracker_sapling"))), 0.15f));
        add("brown_amber_sapling", new IngredientModifier(lootConditions("archaeology/ocean_ruin_cold"), 0, Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "brown_amber_sapling"))), 0.1f, true));
    }

    private LootItemCondition[] lootConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] anyOfConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition.Builder>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)));
        }
        return List.of(AnyOfCondition.anyOf(list.toArray(new LootItemCondition.Builder[0])).build()).toArray(new LootItemCondition[0]);
    }
}
