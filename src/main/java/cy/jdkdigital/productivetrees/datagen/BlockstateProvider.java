package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockstateProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();

    public BlockstateProvider(PackOutput packOutput) {
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

        TreeFinder.trees.forEach((id, treeObject) -> {
            addBlockItemModel(treeObject.getLeafBlock().get(), "leaves/" + treeObject.getStyle(), itemModels);
            if (treeObject.registerWood()) {
                addBlockItemModel(treeObject.getPlankBlock().get(), "base_planks", itemModels);
                addBlockItemModel(treeObject.getLogBlock().get(), "log/" + treeObject.getStyle() + "_log", itemModels);
                addBlockItemModel(treeObject.getStrippedLogBlock().get(), "log/" + treeObject.getStyle() + "_stripped_log", itemModels);
                addBlockItemModel(treeObject.getWoodBlock().get(), "log/" + treeObject.getStyle() + "_wood", itemModels);
                addBlockItemModel(treeObject.getStrippedWoodBlock().get(), "log/" + treeObject.getStyle() + "_stripped_wood", itemModels);
                addBlockItemModel(treeObject.getSlabBlock().get(), "base_slab", itemModels);
                addBlockItemModel(treeObject.getStairsBlock().get(), "base_stairs", itemModels);
                addBlockItemModel(treeObject.getButtonBlock().get(), "base_button_inventory", itemModels);
                addBlockItemModel(treeObject.getPressurePlateBlock().get(), "base_pressure_plate", itemModels);
                addBlockItemModel(treeObject.getFenceBlock().get(), "base_fence_inventory", itemModels);
                addBlockItemModel(treeObject.getFenceGateBlock().get(), "base_fence_gate", itemModels);
                addBlockItemParentModel(treeObject.getHiveBlock().get(), itemModels);
                addBlockItemParentModel(treeObject.getExpansionBoxBlock().get(), itemModels);
            }
        });

        generateFruitItem(TreeRegistrator.BLACKBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BLACKCURRANT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BLUEBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.REDCURRANT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CRANBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ELDERBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GOOSEBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.RASPBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.JUNIPER.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GOLDEN_RASPBERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SLOE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.HAW.get(), modelOutput);
        generateFruitItem(TreeRegistrator.MIRACLE_BERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.APRICOT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BLACK_CHERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CHERRY_PLUM.get(), modelOutput);
        generateFruitItem(TreeRegistrator.OLIVE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.OSANGE_ORANGE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.KUMQUAT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.WILD_CHERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SOUR_CHERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.DATE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PLUM.get(), modelOutput);
        generateFruitItem(TreeRegistrator.AVOCADO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CRABAPPLE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.FIG.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GRAPEFRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.NECTARINE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PEACH.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PEAR.get(), modelOutput);
        generateFruitItem(TreeRegistrator.POMELO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SAND_PEAR.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SATSUMA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.STAR_FRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.TANGERINE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.AKEBIA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.COPOAZU.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BANANA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.COCONUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.MANGO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PLANTAIN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.RED_BANANA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PAPAYA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BREADFRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.MONSTERA_DELICIOSA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.LIME.get(), modelOutput);
        generateFruitItem(TreeRegistrator.KEY_LIME.get(), modelOutput);
        generateFruitItem(TreeRegistrator.FINGER_LIME.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CITRON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.LEMON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ORANGE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.MANDARIN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BUDDHAS_HAND.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ALMOND.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ACORN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BEECHNUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BRAZIL_NUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BUTTERNUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CANDLENUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CASHEW.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CHESTNUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.COFFEE_BEAN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GINKGO_NUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.HAZELNUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PECAN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PISTACHIO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.WALNUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CAROB.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ALLSPICE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CHILLI.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CLOVE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CINNAMON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.NUTMEG.get(), modelOutput);
        generateFruitItem(TreeRegistrator.STAR_ANISE.get(), modelOutput);

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), blockstatePathProvider.json(ForgeRegistries.BLOCKS.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    private void generateFruitItem(Item item, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFruitItemTextureMap(item), modelOutput);
    }
    private static TextureMapping getFruitItemTextureMap(Item item) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
        return (new TextureMapping()).put(TextureSlot.LAYER0, resourcelocation.withPrefix("item/fruit/"));
    }

    private void addItemModel(Item item, Supplier<JsonElement> supplier, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        if (item != null) {
            ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
            if (!itemModels.containsKey(resourcelocation)) {
                itemModels.put(resourcelocation, supplier);
            }
        }
    }

    private void addBlockItemModel(Block block, String base, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            addItemModel(item, new DelegatedModel(new ResourceLocation(ProductiveTrees.MODID, "block/" + base)), itemModels);
        }
    }

    private void addBlockItemParentModel(Block block, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            addItemModel(item, new DelegatedModel(ForgeRegistries.BLOCKS.getKey(block)), itemModels);
        }
    }

    @Override
    public String getName() {
        return "Productive Trees Blockstate and Model generator";
    }

    static class ModelGenerator
    {
        Consumer<BlockStateGenerator> blockStateOutput;
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

        protected void registerStatesAndModels(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
            this.blockStateOutput = blockStateOutput;
            this.modelOutput = modelOutput;
            Map<ResourceLocation, BlockStateGenerator> hivBlockStates = new HashMap<>();

            TreeFinder.trees.forEach((id, treeObject) -> {
                this.createSapling(treeObject);
                this.createBaseBlock(treeObject.getLeafBlock().get(), "leaves/" + treeObject.getStyle());
                if (treeObject.hasFruit()) {
                    this.createFruitBlock(treeObject);
                }
                if (treeObject.registerWood()) {
                    new WoodProvider().logWithHorizontal(treeObject.getStyle(), treeObject.getLogBlock().get(), false).wood(treeObject.getStyle(), treeObject.getWoodBlock().get(), false);
                    new WoodProvider().logWithHorizontal(treeObject.getStyle(), treeObject.getStrippedLogBlock().get(), true).wood(treeObject.getStyle(), treeObject.getStrippedWoodBlock().get(), true);
                    this.createBaseBlock(treeObject.getPlankBlock().get(), "planks/" + treeObject.getStyle());
                    this.createStairsBlock(treeObject.getStairsBlock().get());
                    this.createSlabBlock(treeObject.getSlabBlock().get());
                    this.createPressurePlateBlock(treeObject.getPressurePlateBlock().get());
                    this.createButtonBlock(treeObject.getButtonBlock().get());
                    this.createFenceGateBlock(treeObject.getFenceGateBlock().get());
                    this.createFenceBlock(treeObject.getFenceBlock().get());

                    cy.jdkdigital.productivebees.datagen.BlockstateProvider.generateModels(treeObject.getHiveBlock().get(), treeObject.getExpansionBoxBlock().get(), id.getPath(), new HiveType(false, treeObject.getPlankColor(), treeObject.getHiveStyle(), Ingredient.of(treeObject.getPlankBlock().get())), hivBlockStates, this.modelOutput);
                }
            });

            hivBlockStates.forEach((resourceLocation, stateGenerator) -> {
                this.blockStateOutput.accept(stateGenerator);
            });
        }

        private void createSapling(TreeObject treeObject) {
            Block block = treeObject.getSaplingBlock().get();
            Block pottedBlock = treeObject.getPottedSaplingBlock().get();

            String baseSapling = "";
            if (treeObject.hasFruit()) {
                baseSapling = "fruiting_";
            }

            Item item = block.asItem();
            if (item != Items.AIR) {
                this.modelOutput.accept(ModelLocationUtils.getModelLocation(item), new DelegatedModel(new ResourceLocation(ProductiveTrees.MODID, "item/sapling/base_" + baseSapling + treeObject.getStyle())));
            }
            this.blockStateOutput.accept(createSimpleBlock(pottedBlock, new ResourceLocation(ProductiveTrees.MODID, "block/sapling/base_potted_sapling_" + treeObject.getStyle()))); // TODO
            this.blockStateOutput.accept(createSimpleBlock(block, new ResourceLocation(ProductiveTrees.MODID, "block/sapling/base_" + baseSapling + treeObject.getStyle())));
        }

        static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation resourceLocation) {
            return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation));
        }

        static BlockStateGenerator createAxisAlignedPillarBlock(Block block, ResourceLocation resourceLocation) {
            return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation)).with(createRotatedPillar());
        }

        static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block p_124925_, ResourceLocation p_124926_, ResourceLocation resourceLocation) {
            return MultiVariantGenerator.multiVariant(p_124925_).with(PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, p_124926_)).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
        }

        private static PropertyDispatch createRotatedPillar() {
            return PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant()).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
        }

        private void createBaseBlock(Block block, String baseName) {
            this.blockStateOutput.accept(createSimpleBlock(block, new ResourceLocation(ProductiveTrees.MODID, "block/" + baseName)));
        }

        private void createFruitBlock(TreeObject treeObject) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(treeObject.getFruitBlock().get()).with(PropertyDispatch.property(BlockStateProperties.AGE_5).generate(age -> {
                var template = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fruit/" + treeObject.getFruit().style() + "/fruit_" + age)), Optional.empty(), TextureSlot.ALL);
                return Variant.variant().with(VariantProperties.MODEL, template.create(new ResourceLocation(ProductiveTrees.MODID, "block/fruit/" + treeObject.getId().getPath() + "/" + age), (new TextureMapping()).put(TextureSlot.ALL, new ResourceLocation(ProductiveTrees.MODID, "block/leaves/" + (treeObject.getStyle().equals("bush") ? "oak" : treeObject.getStyle()))), modelOutput));
            })));
        }

        private final ResourceLocation stairs = new ResourceLocation(ProductiveTrees.MODID, "block/base_stairs");
        private final ResourceLocation stairsInner = new ResourceLocation(ProductiveTrees.MODID, "block/base_stairs_inner");
        private final ResourceLocation stairsOuter = new ResourceLocation(ProductiveTrees.MODID, "block/base_stairs_outer");
        private void createStairsBlock(Block block) {
            this.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.properties(HorizontalDirectionalBlock.FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).generate((facing, half, shape) -> {
                        int yRotValue = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
                        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                            yRotValue += 270; // Left facing stairs are rotated 90 degrees clockwise
                        }
                        if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                            yRotValue += 90; // Top stairs are rotated 90 degrees clockwise
                        }
                        yRotValue %= 360;
                        boolean uvLock = yRotValue != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation

                        var yRot = switch (yRotValue) {
                            case 90 -> VariantProperties.Rotation.R90;
                            case 180 -> VariantProperties.Rotation.R180;
                            case 270 -> VariantProperties.Rotation.R270;
                            default -> VariantProperties.Rotation.R0;
                        };

                        var variant = Variant.variant().with(VariantProperties.MODEL, shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter);

                        if (half != Half.BOTTOM) {
                            variant.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180);
                        }
                        if (!yRot.equals(VariantProperties.Rotation.R0)) {
                            variant.with(VariantProperties.Y_ROT, yRot);
                        }
                        if (uvLock) {
                            variant.with(VariantProperties.UV_LOCK, true);
                        }
                        return variant;
                    }))
            );
        }

        private final ResourceLocation slab = new ResourceLocation(ProductiveTrees.MODID, "block/base_slab");
        private final ResourceLocation planks = new ResourceLocation(ProductiveTrees.MODID, "block/base_planks");
        private final ResourceLocation slabTop = new ResourceLocation(ProductiveTrees.MODID, "block/base_slab_top");
        private void createSlabBlock(Block block) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE).generate(slabType -> Variant.variant().with(VariantProperties.MODEL, slabType == SlabType.BOTTOM ? slab : slabType == SlabType.TOP ? slabTop : planks))));
        }

        private final ResourceLocation plate = new ResourceLocation(ProductiveTrees.MODID, "block/base_pressure_plate");
        private final ResourceLocation plateDown = new ResourceLocation(ProductiveTrees.MODID, "block/base_pressure_plate_down");
        private void createPressurePlateBlock(Block block) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.POWERED).generate(powered -> Variant.variant().with(VariantProperties.MODEL, powered ? plateDown : plate))));
        }

        private final ResourceLocation button = new ResourceLocation(ProductiveTrees.MODID, "block/base_button");
        private final ResourceLocation buttonPressed = new ResourceLocation(ProductiveTrees.MODID, "block/base_button_pressed");
        private void createButtonBlock(Block block) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.properties(ButtonBlock.FACING, ButtonBlock.FACE, ButtonBlock.POWERED)
                    .generate((facing, face, powered) -> {
                        var variant = Variant.variant().with(VariantProperties.MODEL, powered ? buttonPressed : button);

                        int yRotValue = (int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot();
                        var yRot = switch (yRotValue) {
                            case 90 -> VariantProperties.Rotation.R90;
                            case 180 -> VariantProperties.Rotation.R180;
                            case 270 -> VariantProperties.Rotation.R270;
                            default -> VariantProperties.Rotation.R0;
                        };

                        if (face != AttachFace.FLOOR) {
                            variant.with(VariantProperties.X_ROT, face == AttachFace.WALL ? VariantProperties.Rotation.R90 : VariantProperties.Rotation.R180);
                        }
                        if (!yRot.equals(VariantProperties.Rotation.R0)) {
                            variant.with(VariantProperties.Y_ROT, yRot);
                        }
                        if (face == AttachFace.WALL) {
                            variant.with(VariantProperties.UV_LOCK, true);
                        }

                        return variant;
                    })));
        }

        private final ResourceLocation fenceGate = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_gate");
        private final ResourceLocation fenceGateOpen = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_gate_open");
        private final ResourceLocation fenceGateWall = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_gate_wall");
        private final ResourceLocation fenceGateWallOpen = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_gate_wall_open");
        private void createFenceGateBlock(Block block) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.properties(FenceGateBlock.FACING, FenceGateBlock.IN_WALL, FenceGateBlock.OPEN)
                    .generate((facing, inWall, open) -> {
                        var variant = Variant.variant().with(VariantProperties.MODEL, open ? (inWall ? fenceGateWallOpen : fenceGateOpen) : (inWall ? fenceGateWall : fenceGate));

                        int yRotValue = (int) facing.toYRot();
                        var yRot = switch (yRotValue) {
                            case 90 -> VariantProperties.Rotation.R90;
                            case 180 -> VariantProperties.Rotation.R180;
                            case 270 -> VariantProperties.Rotation.R270;
                            default -> VariantProperties.Rotation.R0;
                        };

                        if (!yRot.equals(VariantProperties.Rotation.R0)) {
                            variant.with(VariantProperties.Y_ROT, yRot);
                        }
                        variant.with(VariantProperties.UV_LOCK, true);

                        return variant;
                    })));
        }

        private final ResourceLocation fencePost = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_post");
        private final ResourceLocation fenceSide = new ResourceLocation(ProductiveTrees.MODID, "block/base_fence_side");
        private void createFenceBlock(Block block) {
            this.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(block)
                            .with(Variant.variant().with(VariantProperties.MODEL, fencePost))
                            .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true))
                            .with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                            .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                            .with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
            );
        }

        class WoodProvider
        {
            public WoodProvider() {}

            public WoodProvider wood(String style, Block block, boolean stripped) {
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createAxisAlignedPillarBlock(block, new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_wood" : "block/log/" + style + "_wood")));
                return this;
            }

            public WoodProvider logWithHorizontal(String style, Block block, boolean stripped) {
                ResourceLocation rLoc = new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_log" : "block/log/" + style + "_log");
                ResourceLocation rLocHor = new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_log_horizontal" : "block/log/" + style + "_log_horizontal");
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createRotatedPillarWithHorizontalVariant(block, new ResourceLocation(ProductiveTrees.MODID, rLoc.getPath()), new ResourceLocation(ProductiveTrees.MODID, rLocHor.getPath())));
                return this;
            }
        }
    }
}
