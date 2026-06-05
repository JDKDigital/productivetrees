package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.compat.CompatLootDataProvider;
import cy.jdkdigital.productivetrees.datagen.compat.CompatModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveTrees.MODID)
public class ProductiveTreesDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        generator.addProvider(true, new LanguageProvider(output));
        generator.addProvider(true, new ModelProvider(output));
        generator.addProvider(true, new CompatModelProvider(output));

        generator.addProvider(true, new LootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(LootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        generator.addProvider(true, new CompatLootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(CompatLootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        generator.addProvider(true, new LootModifierProvider(output, provider));
        generator.addProvider(true, new FeatureProvider(output));
        generator.addProvider(true, new RecipeProvider.Runner(output, provider));
        generator.addProvider(true, new BeeProvider(output, provider));
        generator.addProvider(true, new DataMapProvider(output, provider));

        BlockTagProvider blockTags = new BlockTagProvider(output, provider);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ItemTagProvider(output, provider, blockTags.contentsGetter()));
    }
}
