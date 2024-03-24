package cy.jdkdigital.productivetrees.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.*;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveHangingSignBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveSignBlockEntity;
import cy.jdkdigital.productivetrees.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.ModList;
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

            boolean noOcclusion = TreeUtil.isTranslucentTree(name);
            final ToIntFunction<BlockState> lightLevel = state -> treeObject.getDecoration().lightLevel();

            // Validate colors
            ColorUtil.getCacheColor(treeObject.getLeafColor());
            ColorUtil.getCacheColor(treeObject.getLogColor());
            ColorUtil.getCacheColor(treeObject.getPlankColor());
            if (treeObject.hasFruit()) {
                ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                ColorUtil.getCacheColor(treeObject.getFruit().unripeColor());
            }

            // Create grower
            var grower = treeObject.getMegaFeature().equals(TreeRegistrator.NULL_FEATURE) ? new AbstractTreeGrower()
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
            treeObject.setLeafBlock(registerBlock(name + "_leaves", () -> new ProductiveLeavesBlock(getProperties(Blocks.OAK_LEAVES, noOcclusion, lightLevel), treeObject)));
            // Register fruit block + BE
            if (treeObject.hasFruit()) {
                if (name.equals("coconut")) {
                    treeObject.setFruitBlock(ProductiveTrees.BLOCKS.register(name + "_fruit", () -> new ProductiveDroppyFruitBlock(getProperties(Blocks.OAK_LEAVES, noOcclusion, null), treeObject, TreeRegistrator.COCONUT_SPROUT)));
                } else if (name.equals("brown_amber")) {
                    treeObject.setFruitBlock(ProductiveTrees.BLOCKS.register(name + "_fruit", () -> new ProductiveDrippyFruitBlock(getProperties(Blocks.OAK_LEAVES, noOcclusion, null), treeObject, TreeRegistrator.AMBER_PUDDLE)));
                } else if (!treeObject.getFruit().style().equals("default")) {
                    treeObject.setFruitBlock(ProductiveTrees.BLOCKS.register(name + "_fruit", () -> new ProductiveDanglerFruitBlock(getProperties(Blocks.OAK_LEAVES, noOcclusion, null).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape(), treeObject)));
                } else {
                    treeObject.setFruitBlock(ProductiveTrees.BLOCKS.register(name + "_fruit", () -> new ProductiveFruitBlock(getProperties(Blocks.OAK_LEAVES, noOcclusion, null), treeObject)));
                }
            }

            // TODO don't use wood type
            var woodType = WoodType.register(new WoodType(ProductiveTrees.MODID + ":" + name, BlockSetType.register(new BlockSetType(ProductiveTrees.MODID + ":" + name))));

            // Register log block TODO map colors and properties
            treeObject.setLogBlock(registerBlock(name + "_log", () -> new ProductiveLogBlock(getProperties(treeObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_LOG, noOcclusion, lightLevel), treeObject)));
            // Stripped log
            treeObject.setStrippedLogBlock(registerBlock(name + "_stripped_log", () -> new ProductiveRotatedPillarBlock(getProperties(treeObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_LOG, noOcclusion, lightLevel), treeObject)));
            // Wood block
            treeObject.setWoodBlock(registerBlock(name + "_wood", () -> new ProductiveWoodBlock(getProperties(treeObject.isFireProof() ? Blocks.WARPED_STEM : Blocks.OAK_WOOD, noOcclusion, lightLevel), treeObject)));
            // Stripped wood
            treeObject.setStrippedWoodBlock(registerBlock(name + "_stripped_wood", () -> new ProductiveRotatedPillarBlock(getProperties(treeObject.isFireProof() ? Blocks.STRIPPED_WARPED_STEM : Blocks.STRIPPED_OAK_WOOD, noOcclusion, lightLevel), treeObject)));
            // Register planks block
            treeObject.setPlankBlock(registerBlock(name + "_planks", () -> new ProductivePlankBlock(getProperties(treeObject.isFireProof() ? Blocks.WARPED_PLANKS : Blocks.OAK_PLANKS, noOcclusion, lightLevel), treeObject)));
            // Stairs
            treeObject.setStairsBlock(registerBlock(name + "_stairs", () -> new StairBlock(() -> treeObject.getPlankBlock().get().defaultBlockState(), getProperties(Blocks.OAK_STAIRS, noOcclusion, lightLevel))));
            // Slab
            treeObject.setSlabBlock(registerBlock(name + "_slab", () -> new SlabBlock(getProperties(Blocks.OAK_SLAB, noOcclusion, lightLevel))));
            // Fence
            treeObject.setFenceBlock(registerBlock(name + "_fence", () -> new FenceBlock(getProperties(Blocks.OAK_FENCE, noOcclusion, lightLevel))));
            // Fence gate
            treeObject.setFenceGateBlock(registerBlock(name + "_fence_gate", () -> new FenceGateBlock(getProperties(Blocks.OAK_FENCE_GATE, noOcclusion, lightLevel), WoodType.OAK)));
            // Pressure plate
            treeObject.setPressurePlateBlock(registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, getProperties(Blocks.OAK_PRESSURE_PLATE, noOcclusion, lightLevel), BlockSetType.OAK)));
            // Button
            treeObject.setButtonBlock(registerBlock(name + "_button", () -> new ButtonBlock(getProperties(Blocks.OAK_BUTTON, noOcclusion, lightLevel), BlockSetType.OAK, 30, true)));
            // Door
            treeObject.setDoorBlock(registerBlock(name + "_door", () -> new DoorBlock(getProperties(Blocks.OAK_DOOR, noOcclusion, lightLevel), BlockSetType.OAK)));
            // Trapdoor
            treeObject.setTrapdoorBlock(registerBlock(name + "_trapdoor", () -> new TrapDoorBlock(getProperties(Blocks.ACACIA_TRAPDOOR, noOcclusion, lightLevel), BlockSetType.OAK)));
            // Bookshelf
            treeObject.setBookshelfBlock(registerBlock(name + "_bookshelf", () -> new Block(getProperties(Blocks.BOOKSHELF, noOcclusion, lightLevel))));
            // Signs
            treeObject.setSignBlock(registerBlock(name + "_sign", () -> new ProductiveStandingSignBlock(getProperties(Blocks.OAK_SIGN, noOcclusion, lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveSignBlockEntity(treeObject.getSignBlock().get(), pos, state), treeObject.getSignBlock().get()))), () -> new SignItem(new Item.Properties(), treeObject.getSignBlock().get(), treeObject.getWallSignBlock().get())));
            treeObject.setWallSignBlock(registerBlock(name + "_wall_sign", () -> new ProductiveWallSignBlock(getProperties(Blocks.OAK_WALL_SIGN, noOcclusion, lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_wall_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveSignBlockEntity(treeObject.getWallSignBlock().get(), pos, state), treeObject.getWallSignBlock().get()))), false));
            treeObject.setHangingSignBlock(registerBlock(name + "_hanging_sign", () -> new ProductiveCeilingHangingSignBlock(getProperties(Blocks.OAK_HANGING_SIGN, noOcclusion, lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_hanging_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveHangingSignBlockEntity(treeObject.getHangingSignBlock().get(), pos, state), treeObject.getHangingSignBlock().get()))), () -> new SignItem(new Item.Properties(), treeObject.getHangingSignBlock().get(), treeObject.getWallHangingSignBlock().get())));
            treeObject.setWallHangingSignBlock(registerBlock(name + "_wall_hanging_sign", () -> new ProductiveWallHangingSignBlock(getProperties(Blocks.OAK_WALL_HANGING_SIGN, noOcclusion, lightLevel), woodType, TreeRegistrator.registerBlockEntity(name + "_wall_hanging_sign", () -> TreeRegistrator.createBlockEntityType((pos, state) -> new ProductiveHangingSignBlockEntity(treeObject.getWallHangingSignBlock().get(), pos, state), treeObject.getWallHangingSignBlock().get()))), false));

            // Hives
            if (treeObject.getStyle().hiveStyle() != null && ModList.get().isLoaded("productivebees")) {
                CompatHandler.createHive(name, treeObject, lightLevel);
            }

            if (name.equals("monkey_puzzle")) {
                registerBlock("monkey_puzzle_small_leaves", () -> new ProductiveDirectionalLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES), treeObject));
                registerBlock("monkey_puzzle_medium_leaves", () -> new ProductiveDirectionalLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES), treeObject));
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

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier, boolean withItem) {
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

    private static BlockBehaviour.Properties getProperties(Block copyFrom, boolean noOcclusion, @Nullable ToIntFunction<BlockState> lightLevel) {
        var behavior = BlockBehaviour.Properties.copy(copyFrom);
        if (lightLevel != null) {
            behavior = behavior.lightLevel(lightLevel);
        }
        if (noOcclusion) {
            behavior = behavior.noOcclusion();
        }
        return behavior;
    }
}
