package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
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

@EventBusSubscriber(modid = ProductiveTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ProductiveTreesDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new LanguageProvider(output));

        gen.addProvider(event.includeClient(), new BlockstateProvider(output));

        gen.addProvider(event.includeServer(), new LootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(LootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        gen.addProvider(event.includeServer(), new LootModifierProvider(output, provider));
        gen.addProvider(event.includeServer(), new FeatureProvider(output));
        gen.addProvider(event.includeServer(), new RecipeProvider(output, provider));
        gen.addProvider(event.includeServer(), new BeeProvider(output, provider));
//        gen.addProvider(event.includeServer(), new AdvancementProvider(output, provider, helper));

        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
    }
}
