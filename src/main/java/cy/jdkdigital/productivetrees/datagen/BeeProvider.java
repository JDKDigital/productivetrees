package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivebees.datagen.BeeProvider.BeeConfig;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BeeProvider extends DatapackBuiltinEntriesProvider
{
    public BeeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(BeeRegistries.BEE_DATA, BeeProvider::bootstrap), Set.of(ProductiveTrees.MODID));
    }

    private static void bootstrap(BootstrapContext<BeeData> ctx) {
        BeeConfig config = new BeeConfig("allergy").primaryColor("#AAFF00").secondaryColor("#7FFFD4").tertiaryColor("#088F8F").particleColor("#4CBB17").renderer("default_foliage").flowerTag("minecraft:leaves").noComb().size(0.5).particleType("pop");
        ResourceKey<BeeData> key = ResourceKey.create(BeeRegistries.BEE_DATA, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, config.name()));
        ctx.register(key, config.toBeeData());
    }
}
