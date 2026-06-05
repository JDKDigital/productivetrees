package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider
{
    public DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        final var compostables = builder(NeoForgeDataMaps.COMPOSTABLES);

        TreeFinder.trees.forEach((id, treeObject) -> {
            compostables.add(TreeUtil.getBlock(id, "_sapling").asItem().builtInRegistryHolder(), new Compostable(0.3f, false), false);
            compostables.add(TreeUtil.getBlock(id, "_leaves").asItem().builtInRegistryHolder(), new Compostable(0.3f, false), false);
            if (treeObject.hasFruit() && treeObject.getFruit().getItemType() != Items.AIR && treeObject.getFruit().fruitItem().getNamespace().equals(ProductiveTrees.MODID)) {
                compostables.add(treeObject.getFruit().getItemType().builtInRegistryHolder(), new Compostable(0.65F, false), false);
            }
        });
    }
}
