package cy.jdkdigital.productivetrees.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.ExpansionBoxBlockEntity;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveFruitBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveHangingSignBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveSignBlockEntity;
import cy.jdkdigital.productivetrees.registry.Features;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

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
            var grower = treeObject.getMegaFeature().equals(Features.NULL) ? new AbstractTreeGrower()
            {
                @Override
                protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource rand, boolean hasFlowers) {
                    return treeObject.getFeature();
                }
            } : new AbstractMegaTreeGrower()
            {
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
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(ProductiveTrees.MODID, name + "_sapling"), treeObject.getPottedSaplingBlock());
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
        var wood = new WoodObject(new ResourceLocation(ProductiveTrees.MODID, name), false, colors, () -> ItemStack.EMPTY);
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

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier, Supplier<Item> itemSupplier) {
        RegistryObject<Block> block = ProductiveTrees.BLOCKS.register(name, blockSupplier);

        ProductiveTrees.ITEMS.register(name, itemSupplier);

        return block;
    }

    private static void registerWoodBlocks(WoodObject woodObject, String name) {
        var woodType = WoodType.register(new WoodType(ProductiveTrees.MODID + ":" + name, BlockSetType.register(new BlockSetType(ProductiveTrees.MODID + ":" + name))));

        final ToIntFunction<BlockState> lightLevel = woodObject instanceof TreeObject treeObject ? state -> treeObject.getDecoration().lightLevel() : state -> 0;

        // Register log block TODO map colors and properties
        if (name.equals("bush")) {
            woodObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveBranchedLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG).lightLevel(lightLevel), woodObject)));
            // Stripped log
            woodObject.setStrippedLogBlock(registerBlock(name + "_stripped_log", () -> new ProductiveBranchedLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_LOG).lightLevel(lightLevel), woodObject)));
            // Wood block
            woodObject.setWoodBlock(registerBlock(name + "_wood", () -> new ProductiveBranchedWoodBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_WOOD).lightLevel(lightLevel), woodObject)));
            // Stripped wood
            woodObject.setStrippedWoodBlock(registerBlock(name + "_stripped_wood", () -> new ProductiveBranchedWoodBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_WOOD).lightLevel(lightLevel), woodObject)));
        } else {
            woodObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveLogBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG).lightLevel(lightLevel), woodObject)));
            // Stripped log
            woodObject.setStrippedLogBlock(registerBlock(name + "_stripped_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_LOG).lightLevel(lightLevel))));
            // Wood block
            woodObject.setWoodBlock(registerBlock(name + "_wood", () -> new ProductiveWoodBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_WOOD).lightLevel(lightLevel), woodObject)));
            // Stripped wood
            woodObject.setStrippedWoodBlock(registerBlock(name + "_stripped_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_WOOD).lightLevel(lightLevel))));
        }
        // Register planks block
        woodObject.setPlankBlock(registerBlock(name + "_planks", () -> new Block(BlockBehaviour.Properties.copy(woodObject.isFireProof() ? Blocks.WARPED_PLANKS : Blocks.OAK_PLANKS).lightLevel(lightLevel))));
        // Stairs
        woodObject.setStairsBlock(registerBlock(name + "_stairs", () -> new StairBlock(() -> woodObject.getPlankBlock().get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS).lightLevel(lightLevel))));
        // Slab
        woodObject.setSlabBlock(registerBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).lightLevel(lightLevel))));
        // Fence
        woodObject.setFenceBlock(registerBlock(name + "_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE).lightLevel(lightLevel))));
        // Fence gate
        woodObject.setFenceGateBlock(registerBlock(name + "_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE).lightLevel(lightLevel), WoodType.OAK)));
        // Pressure plate
        woodObject.setPressurePlateBlock(registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE).lightLevel(lightLevel), BlockSetType.OAK)));
        // Button
        woodObject.setButtonBlock(registerBlock(name + "_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON).lightLevel(lightLevel), BlockSetType.OAK, 30, true)));
        // Door
        woodObject.setDoorBlock(registerBlock(name + "_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).lightLevel(lightLevel), BlockSetType.OAK)));
        // Trapdoor
        woodObject.setTrapdoorBlock(registerBlock(name + "_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).lightLevel(lightLevel), BlockSetType.OAK)));
        // Bookshelf
        woodObject.setBookshelfBlock(registerBlock(name + "_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF).lightLevel(lightLevel))));
        // Signs
        woodObject.setSignBlock(registerBlock(name + "_sign", () -> new ProductiveStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).lightLevel(lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveSignBlockEntity(woodObject.getSignBlock().get(), pos, state), woodObject.getSignBlock().get()))), () -> new SignItem(new Item.Properties(), woodObject.getSignBlock().get(), woodObject.getWallSignBlock().get())));
        woodObject.setWallSignBlock(registerBlock(name + "_wall_sign", () -> new ProductiveWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN).lightLevel(lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_wall_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveSignBlockEntity(woodObject.getWallSignBlock().get(), pos, state), woodObject.getWallSignBlock().get()))), false));
        woodObject.setHangingSignBlock(registerBlock(name + "_hanging_sign", () -> new ProductiveCeilingHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN).lightLevel(lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_hanging_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveHangingSignBlockEntity(woodObject.getHangingSignBlock().get(), pos, state), woodObject.getHangingSignBlock().get()))), () -> new SignItem(new Item.Properties(), woodObject.getHangingSignBlock().get(), woodObject.getWallHangingSignBlock().get())));
        woodObject.setWallHangingSignBlock(registerBlock(name + "_wall_hanging_sign", () -> new ProductiveWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN).lightLevel(lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_wall_hanging_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveHangingSignBlockEntity(woodObject.getWallHangingSignBlock().get(), pos, state), woodObject.getWallHangingSignBlock().get()))), false));

        // Hives
        if (woodObject.getStyle().hiveStyle() != null) {
            String hiveName = "advanced_" + name + "_beehive";
            String boxName = "expansion_box_" + name;
            woodObject.setHiveBlock(registerBlock(hiveName, () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE).lightLevel(lightLevel), TreeRegistrator.registerBlockEntity(hiveName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new AdvancedBeehiveBlockEntity((AdvancedBeehive) woodObject.getHiveBlock().get(), pos, state), woodObject.getHiveBlock().get()))), true));
            woodObject.setExpansionBoxBlock(registerBlock(boxName, () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE).lightLevel(lightLevel), TreeRegistrator.registerBlockEntity(boxName, () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ExpansionBoxBlockEntity((ExpansionBox) woodObject.getExpansionBoxBlock().get(), pos, state), woodObject.getExpansionBoxBlock().get()))), true));
        }
    }
}
