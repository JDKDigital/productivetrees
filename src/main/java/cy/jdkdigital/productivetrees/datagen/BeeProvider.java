package cy.jdkdigital.productivetrees.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BeeProvider extends cy.jdkdigital.productivebees.datagen.BeeProvider
{
    public BeeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output);
    }

    @Override
    public @NotNull String getName() {
        return "ProductiveTrees bee data provider";
    }

    @Override
    protected List<BeeConfig> getBeeConfigs() {
        return new ArrayList<>()
        {{
            add(new BeeConfig("allergy").primaryColor("#AAFF00").secondaryColor("#7FFFD4").tertiaryColor("#088F8F").particleColor("#4CBB17").renderer("default_foliage").flowerTag("minecraft:leaves").noComb().size(0.5).particleType("pop"));
        }};
    }
}
