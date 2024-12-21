package cy.jdkdigital.productivetrees.datagen.compat;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import cy.jdkdigital.productivebees.datagen.BlockstateProvider;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CompatModelProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();

    public CompatModelProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Block, BlockStateGenerator> blockModels = Maps.newHashMap();
        Consumer<BlockStateGenerator> blockStateOutput = (blockStateGenerator) -> {
            Block block = blockStateGenerator.getBlock();
            BlockStateGenerator blockstategenerator = blockModels.put(block, blockStateGenerator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> itemModels = Maps.newHashMap();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, elementSupplier) -> {
            Supplier<JsonElement> supplier = itemModels.put(resourceLocation, elementSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };

        ModelGenerator generator = new ModelGenerator();
        try {
            generator.registerStatesAndModels(blockStateOutput, modelOutput);
        } catch (Exception e) {
            ProductiveTrees.LOGGER.error("Error registering states and models", e);
        }

        PackOutput.PathProvider blockstatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        PackOutput.PathProvider modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), blockstatePathProvider.json(BuiltInRegistries.BLOCK.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Productive Trees Compat Blockstate and Model generator";
    }

    static class ModelGenerator
    {
        Consumer<BlockStateGenerator> blockStateOutput;
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

        protected void registerStatesAndModels(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
            this.blockStateOutput = blockStateOutput;
            this.modelOutput = modelOutput;
            Map<ResourceLocation, BlockStateGenerator> hiveBlockStates = new HashMap<>();

            TreeFinder.trees.forEach((id, treeObject) -> {
                if (treeObject.getStyle().hiveStyle() != null) {
                    Block hive = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive"));
                    Block box = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p ->  "expansion_box_" + p));
                    BlockstateProvider.generateModels(hive, box, id.getPath(), new HiveType(false, treeObject.getPlankColor(), treeObject.getStyle().hiveStyle(), TreeUtil.getBlock(id, "_planks"), null), hiveBlockStates, this.modelOutput);
                }
            });

            hiveBlockStates.forEach((resourceLocation, stateGenerator) -> {
                this.blockStateOutput.accept(stateGenerator);
            });
        }
    }
}
