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
import cy.jdkdigital.productivetrees.registry.WoodObject;
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

            // Validate colors
            ColorUtil.getCacheColor(treeObject.getLeafColor());
            ColorUtil.getCacheColor(treeObject.getLogColor());
            ColorUtil.getCacheColor(treeObject.getPlankColor());
            if (treeObject.hasFruit()) {
                ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                ColorUtil.getCacheColor(treeObject.getFruit().unripeColor());
            }

            // Create grower
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
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(new ResourceLocation(ProductiveTrees.MODID, name + "_sapling"), treeObject.getPottedSaplingBlock());
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
                registerWoodBlocks(treeObject, name);
            } else {
                treeObject.setLogBlock(() -> Blocks.OAK_LOG);
            }
            return treeObject;
        } else {
            ProductiveTrees.LOGGER.info("failed to read tree configuration for " + id);
        }
        return null;
    }

    public static WoodObject createWood(String name, WoodObject.TreeColors colors) {
        var wood = new WoodObject(new ResourceLocation(ProductiveTrees.MODID, name), false, colors, null);
        registerWoodBlocks(wood, name);
        return wood;
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

    private static void registerWoodBlocks(WoodObject woodObject, String name) {
        // Register log block TODO map colors and properties
        if (name.equals("bush")) {
            woodObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveBranchedLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG), woodObject)));
        } else {
            woodObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG), woodObject)));
        }
        // Register planks block
        woodObject.setPlankBlock(registerBlock(name + "_planks", () -> new Block(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_PLANKS : Blocks.OAK_PLANKS))));
        // Stripped log
        woodObject.setStrippedLogBlock(registerBlock(name + "_stripped_log", () -> new ProductiveLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_LOG), woodObject)));
        // Wood block
        woodObject.setWoodBlock(registerBlock(name + "_wood", () -> new ProductiveWoodBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_WOOD), woodObject)));
        // Stripped wood
        woodObject.setStrippedWoodBlock(registerBlock(name + "_stripped_wood", () -> new ProductiveWoodBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_WOOD), woodObject)));
        // Stairs
        woodObject.setStairsBlock(registerBlock(name + "_stairs", () -> new StairBlock(() -> woodObject.getPlankBlock().get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))));
        // Slab
        woodObject.setSlabBlock(registerBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB))));
        // Fence
        woodObject.setFenceBlock(registerBlock(name + "_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE))));
        // Fence gate
        woodObject.setFenceGateBlock(registerBlock(name + "_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK)));
        // Pressure plate
        woodObject.setPressurePlateBlock(registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK)));
        // Button
        woodObject.setButtonBlock(registerBlock(name + "_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true)));

        // Hives
        if (woodObject.getHiveStyle() != null) {
            String hiveName = "advanced_" + name + "_beehive";
            String boxName = "expansion_box_" + name;
            woodObject.setHiveBlock(registerBlock(hiveName, () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE), TreeRegistrator.registerBlockEntity(hiveName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new AdvancedBeehiveBlockEntity((AdvancedBeehive) woodObject.getHiveBlock().get(), pos, state), woodObject.getHiveBlock().get()))), true));
            woodObject.setExpansionBoxBlock(registerBlock(boxName, () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE), TreeRegistrator.registerBlockEntity(boxName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ExpansionBoxBlockEntity((ExpansionBox) woodObject.getExpansionBoxBlock().get(), pos, state), woodObject.getExpansionBoxBlock().get()))), true));
        }
    }
}
