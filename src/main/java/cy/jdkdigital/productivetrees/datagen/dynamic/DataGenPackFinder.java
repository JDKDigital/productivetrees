package cy.jdkdigital.productivetrees.datagen.dynamic;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;

import java.util.Optional;
import java.util.function.Consumer;

public class DataGenPackFinder implements RepositorySource
{
    private final PackType packType;

    public DataGenPackFinder(PackType packType) {
        this.packType = packType;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        var packInfo = new PackLocationInfo("productivetrees_" + packType.getDirectory(), Component.literal("Productive tree dynamic resources"), PackSource.BUILT_IN, Optional.of(new KnownPack(ProductiveTrees.MODID, "productivetrees_" + packType.getDirectory(), "1.0")));
        Pack pack = Pack.readMetaAndCreate(
                packInfo,
                BuiltInPackSource.fromName((path) -> new DynamicDataPack(TreeFinder.DYNAMIC_RESOURCE_PATH, packType, packInfo)),
                packType,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false)
        );
        consumer.accept(pack);
    }
}
