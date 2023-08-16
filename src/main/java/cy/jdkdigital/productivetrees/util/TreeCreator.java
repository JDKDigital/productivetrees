package cy.jdkdigital.productivetrees.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.ExpansionBoxBlockEntity;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveFruitBlockEntity;
import cy.jdkdigital.productivetrees.registry.Features;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TreeCreator
{
    public static TreeObject create(ResourceLocation id, JsonObject json) throws JsonSyntaxException {
        var treeOptional = TreeObject.codec(id).parse(JsonOps.INSTANCE, json).result();

        if (treeOptional.isPresent()) {
            var treeObject = treeOptional.get();
            var name = treeObject.getId().getPath();

            // Register sapling block
            var grower = treeObject.getMegaFeature().equals(Features.NULL) ? new AbstractTreeGrower() {
                @Override
                protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource rand, boolean hasFlowers) {
                    return treeObject.getFeature();
                }
            } : new AbstractMegaTreeGrower() {
                @Override
                protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource rand, boolean hasFlowers) {
                    return treeObject.getFeature();
                }

                @Nullable
                @Override
                protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource rand) {
                    return treeObject.getMegaFeature();
                }
            };
            // Register sapling block
            treeObject.setSaplingBlock(registerBlock(name + "_sapling", () -> new ProductiveSaplingBlock(grower, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING), treeObject)));
            // Potted sapling
            treeObject.setPottedSaplingBlock(registerBlock(name + "_potted_sapling", () -> new FlowerPotBlock(null, treeObject.getSaplingBlock(), BlockBehaviour.Properties.copy(Blocks.POTTED_OAK_SAPLING)), false));
            // Register leaf block
            treeObject.setLeafBlock(registerBlock(name + "_leaves", () -> new ProductiveLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES), treeObject)));
            // Register fruit block + BE
            if (treeObject.hasFruit()) {
                treeObject.setFruitBlock(
                    ProductiveTrees.BLOCKS.register(name + "_fruit", () -> new ProductiveFruitBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES), treeObject)),
                    TreeRegistrator.registerBlockEntity(name, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveFruitBlockEntity(treeObject, pos, state), treeObject.getFruitBlock().get()))
                );
            }

            if (treeObject.registerWood()) {
                // Register log block TODO map colors and properties TODO set log even if it doesn't register one
                treeObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveLogBlock(BlockBehaviour.Properties.copy(treeObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG), treeObject)));
                // Register planks block
                treeObject.setPlankBlock(registerBlock(name + "_planks", () -> new Block(BlockBehaviour.Properties.copy(treeObject.isFireProof() ? Blocks.WARPED_PLANKS : Blocks.OAK_PLANKS))));
                // Stripped log
                treeObject.setStrippedLogBlock(registerBlock(name + "_stripped_log", () -> new ProductiveLogBlock(BlockBehaviour.Properties.copy(treeObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_LOG), treeObject)));
                // Wood block
                treeObject.setWoodBlock(registerBlock(name + "_wood", () -> new ProductiveWoodBlock(BlockBehaviour.Properties.copy(treeObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_WOOD), treeObject)));
                // Stripped wood
                treeObject.setStrippedWoodBlock(registerBlock(name + "_stripped_wood", () -> new ProductiveWoodBlock(BlockBehaviour.Properties.copy(treeObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_WOOD), treeObject)));
                // Stairs
                treeObject.setStairsBlock(registerBlock(name + "_stairs", () -> new StairBlock(() -> treeObject.getPlankBlock().get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))));
                // Slab
                treeObject.setSlabBlock(registerBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB))));
                // Fence
                treeObject.setFenceBlock(registerBlock(name + "_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE))));
                // Fence gate
                treeObject.setFenceGateBlock(registerBlock(name + "_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK)));
                // Pressure plate
                treeObject.setPressurePlateBlock(registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK)));
                // Button
                treeObject.setButtonBlock(registerBlock(name + "_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true)));

                // Hives
                String hiveName = "advanced_" + name + "_beehive";
                String boxName = "expansion_box_" + name;
                treeObject.setHiveBlock(registerBlock(hiveName, () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE), TreeRegistrator.registerBlockEntity(hiveName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new AdvancedBeehiveBlockEntity((AdvancedBeehive) treeObject.getHiveBlock().get(), pos, state), treeObject.getHiveBlock().get()))), true));
                treeObject.setExpansionBoxBlock(registerBlock(boxName, () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE), TreeRegistrator.registerBlockEntity(boxName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ExpansionBoxBlockEntity((ExpansionBox) treeObject.getExpansionBoxBlock().get(), pos, state), treeObject.getExpansionBoxBlock().get()))), true));
            }
            return treeObject;
        } else {
            ProductiveTrees.LOGGER.info("failed to read tree configuration for " + id);
        }
        return null;
    }

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier) {
        return registerBlock(name, blockSupplier, true);
    }
    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier, boolean withItem) {
        RegistryObject<Block> block = ProductiveTrees.BLOCKS.register(name, blockSupplier);

        if (withItem) {
            ProductiveTrees.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        return block;
    }
}
