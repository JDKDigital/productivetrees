package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.compat.CompatLootDataProvider;
import cy.jdkdigital.productivetrees.datagen.compat.CompatModelProvider;
import cy.jdkdigital.productivetrees.datagen.compat.CompatRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveTrees.MODID)
public class ProductiveTreesDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // Moved to dynamic datagen

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new LanguageProvider(output));
        generator.addProvider(event.includeServer(), new ModelProvider(output));

        generator.addProvider(event.includeClient(), new CompatModelProvider(output));

        generator.addProvider(event.includeServer(), new CompatLootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(CompatLootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        generator.addProvider(event.includeServer(), new LootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(LootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        generator.addProvider(event.includeServer(), new LootModifierProvider(output, provider));
        generator.addProvider(event.includeServer(), new FeatureProvider(output));
        generator.addProvider(event.includeServer(), new CompatRecipeProvider(output, provider));
        generator.addProvider(event.includeServer(), new RecipeProvider(output, provider));
        generator.addProvider(event.includeServer(), new BeeProvider(output, provider));
        generator.addProvider(event.includeServer(), new DataMapProvider(output, provider));
//        gen.addProvider(event.includeServer(), new AdvancementProvider(output, provider, helper));

        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
    }
}
