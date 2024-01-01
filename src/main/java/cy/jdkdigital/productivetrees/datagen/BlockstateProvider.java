package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import cy.jdkdigital.productivetrees.util.WoodSet;
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
import net.minecraft.world.level.block.*;
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
            addBlockItemModel(treeObject.getLeafBlock().get(), "leaves/" + treeObject.getStyle().leafStyle(), itemModels);
            if (treeObject.registerWood()) {
                addBlockItemModel(treeObject.getPlankBlock().get(), "planks/" + treeObject.getStyle().plankStyle(), itemModels);
                addBlockItemModel(treeObject.getLogBlock().get(), "log/" + treeObject.getStyle().woodStyle() + "_log", itemModels);
                addBlockItemModel(treeObject.getStrippedLogBlock().get(), "log/" + treeObject.getStyle().woodStyle() + "_stripped_log", itemModels);
                addBlockItemModel(treeObject.getWoodBlock().get(), "log/" + treeObject.getStyle().woodStyle() + "_wood", itemModels);
                addBlockItemModel(treeObject.getStrippedWoodBlock().get(), "log/" + treeObject.getStyle().woodStyle() + "_stripped_wood", itemModels);
                addBlockItemModel(treeObject.getSlabBlock().get(), "slab/" + treeObject.getStyle().plankStyle() + "_slab", itemModels);
                addBlockItemModel(treeObject.getStairsBlock().get(), "stairs/" + treeObject.getStyle().plankStyle() + "_stairs", itemModels);
                addBlockItemModel(treeObject.getButtonBlock().get(), "button/" + treeObject.getStyle().plankStyle() + "_button_inventory", itemModels);
                addBlockItemModel(treeObject.getPressurePlateBlock().get(), "pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate", itemModels);
                addBlockItemModel(treeObject.getFenceBlock().get(), "fence/" + treeObject.getStyle().plankStyle() + "_fence_inventory", itemModels);
                addBlockItemModel(treeObject.getFenceGateBlock().get(), "fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate", itemModels);
//                generateFlatItem(treeObject.getDoorBlock().get().asItem(), "item/door/", modelOutput);
                ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(treeObject.getDoorBlock().get().asItem()), (new TextureMapping()).put(TextureSlot.LAYER0, new ResourceLocation(ProductiveTrees.MODID, "item/door/" + treeObject.getStyle().doorStyle())), modelOutput);
                addBlockItemModel(treeObject.getTrapdoorBlock().get(), "trapdoor/" + treeObject.getStyle().doorStyle(), itemModels);
                addBlockItemModel(treeObject.getBookshelfBlock().get(), "bookshelf/" + treeObject.getStyle().plankStyle(), itemModels);
                generateFlatItem(treeObject.getSignBlock().get().asItem(), "item/sign/", modelOutput);
                generateFlatItem(treeObject.getHangingSignBlock().get().asItem(), "item/hanging_sign/", modelOutput);
            }
        });

        generateFruitItem(TreeRegistrator.ELDERBERRY.get(), modelOutput);
        generateMultiItem(TreeRegistrator.JUNIPER.get(), modelOutput);
        generateMultiItem(TreeRegistrator.SLOE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.HAW.get(), modelOutput);
        generateMultiItem(TreeRegistrator.ASAI_BERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.APRICOT.get(), modelOutput);
        generateMultiItem(TreeRegistrator.BLACK_CHERRY.get(), modelOutput);
        generateMultiItem(TreeRegistrator.WILD_CHERRY.get(), modelOutput);
        generateMultiItem(TreeRegistrator.SOUR_CHERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SPARKLING_CHERRY.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CHERRY_PLUM.get(), modelOutput);
        generateMultiItem(TreeRegistrator.OLIVE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.OSANGE_ORANGE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.KUMQUAT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.DATE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PLUM.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SNAKE_FRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.AVOCADO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SWEET_CRABAPPLE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PRAIRIE_CRABAPPLE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.FLOWERING_CRABAPPLE.get(), modelOutput);
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
        generateFruitItem(TreeRegistrator.CEMPEDAK.get(), modelOutput);
        generateFruitItem(TreeRegistrator.JACKFRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.HALA_FRUIT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SOURSOP.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BANANA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.COCONUT.get(), modelOutput);
        generateFruitItem(TreeRegistrator.MANGO.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PLANTAIN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.RED_BANANA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PAPAYA.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PERSIMMON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.POMEGRANATE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.SWEETSOP.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BREADFRUIT.get(), modelOutput);
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
        generateFruitItem(TreeRegistrator.CLOVE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CINNAMON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.NUTMEG.get(), modelOutput);
        generateFruitItem(TreeRegistrator.STAR_ANISE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.BELIY_NALIV.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GOLDEN_DELICIOUS.get(), modelOutput);
        generateFruitItem(TreeRegistrator.GRANNY_SMITH.get(), modelOutput);
        generateMultiItem(TreeRegistrator.FUSTIC.get(), "item/", modelOutput);
        generateFruitItem(TreeRegistrator.PLANET_PEACH.get(), modelOutput);
        generateFlatItem(TreeRegistrator.CORK.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.HAEMATOXYLIN.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.DRACAENA_SAP.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.RUBBER.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.MAPLE_SAP_BUCKET.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.MAPLE_SYRUP.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.SANDALWOOD_OIL.get(), "item/", modelOutput);

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), blockstatePathProvider.json(ForgeRegistries.BLOCKS.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    private void generateFlatItem(Item item, String prefix, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFlatItemTextureMap(item, prefix), modelOutput);
    }

    private void generateFruitItem(Item item, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        generateFlatItem(item, "item/fruit/", modelOutput);
    }

    private void generateMultiItem(Item item, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        generateMultiItem(item, "item/fruit/", modelOutput);
    }
    private void generateMultiItem(Item item, String suffix, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        var tLocation = ForgeRegistries.ITEMS.getKey(item).withPrefix("item/").withSuffix("_two");
        ModelTemplates.FLAT_ITEM.create(tLocation, getFlatItemTextureMap(item, suffix, "_two"), modelOutput);
        var mLocation = ForgeRegistries.ITEMS.getKey(item).withPrefix("item/").withSuffix("_multiple");
        ModelTemplates.FLAT_ITEM.create(mLocation, getFlatItemTextureMap(item, suffix, "_multiple"), modelOutput);
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFlatItemTextureMap(item, suffix), modelOutput, this::createFruitTemplate);
    }

    public JsonObject createFruitTemplate(ResourceLocation resourceLocation, Map<TextureSlot, ResourceLocation> slotResourceLocationMap) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("parent", "minecraft:item/generated");
        if (!slotResourceLocationMap.isEmpty()) {
            JsonObject textureLocations = new JsonObject();
            slotResourceLocationMap.forEach((textureSlot, resourceLocation1) -> {
                textureLocations.addProperty(textureSlot.getId(), resourceLocation1.toString());
            });
            jsonobject.add("textures", textureLocations);
        }

        JsonArray overrides = new JsonArray();
        JsonObject twoOverride = new JsonObject();
        JsonObject twoPredicate = new JsonObject();
        twoPredicate.addProperty("count", 2);
        twoOverride.add("predicate", twoPredicate);
        twoOverride.addProperty("model", resourceLocation.toString() + "_two");
        JsonObject moreOverride = new JsonObject();
        JsonObject morePredicate = new JsonObject();
        morePredicate.addProperty("count", 3);
        moreOverride.add("predicate", morePredicate);
        moreOverride.addProperty("model", resourceLocation + "_multiple");
        overrides.add(twoOverride);
        overrides.add(moreOverride);
        jsonobject.add("overrides", overrides);

        return jsonobject;
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix) {
        return getFlatItemTextureMap(item, prefix, "");
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix, String suffix) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
        return (new TextureMapping()).put(TextureSlot.LAYER0, resourcelocation.withPrefix(prefix).withSuffix(suffix));
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
            var rl = ForgeRegistries.BLOCKS.getKey(block);
            addItemModel(item, new DelegatedModel(new ResourceLocation(rl.getNamespace(), "block/" + rl.getPath())), itemModels);
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
            Map<ResourceLocation, BlockStateGenerator> hiveBlockStates = new HashMap<>();

            TreeFinder.trees.forEach((id, treeObject) -> {
                this.createSapling(treeObject);
                this.createBaseBlock(treeObject.getLeafBlock().get(), "leaves/" + treeObject.getStyle().leafStyle());
                if (treeObject.hasFruit()) {
                    this.createFruitBlock(treeObject);
                }
                if (treeObject.registerWood()) {
                    new WoodProvider().logWithHorizontal(treeObject.getStyle().woodStyle(), treeObject.getLogBlock().get(), false).wood(treeObject.getStyle().woodStyle(), treeObject.getWoodBlock().get(), false);
                    new WoodProvider().logWithHorizontal(treeObject.getStyle().woodStyle(), treeObject.getStrippedLogBlock().get(), true).wood(treeObject.getStyle().woodStyle(), treeObject.getStrippedWoodBlock().get(), true);
                    this.createBaseBlock(treeObject.getPlankBlock().get(), "planks/" + treeObject.getStyle().plankStyle());
                    this.createStairsBlock(treeObject);
                    this.createSlabBlock(treeObject);
                    this.createPressurePlateBlock(treeObject);
                    this.createButtonBlock(treeObject);
                    this.createFenceGateBlock(treeObject);
                    this.createFenceBlock(treeObject);
                    this.createDoorBlock(treeObject);
                    this.createTrapdoorBlock(treeObject);
                    this.blockStateOutput.accept(createSimpleBlock(treeObject.getBookshelfBlock().get(), new ResourceLocation(ProductiveTrees.MODID, "block/bookshelf/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(treeObject.getSignBlock().get(), new ResourceLocation(ProductiveTrees.MODID, "block/sign/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(treeObject.getWallSignBlock().get(), new ResourceLocation(ProductiveTrees.MODID, "block/sign/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(treeObject.getHangingSignBlock().get(), new ResourceLocation(ProductiveTrees.MODID, "block/sign/hanging_" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(treeObject.getWallHangingSignBlock().get(), new ResourceLocation(ProductiveTrees.MODID, "block/sign/hanging_" + treeObject.getStyle().plankStyle())));

                    if (treeObject.getStyle().hiveStyle() != null) {
                        cy.jdkdigital.productivebees.datagen.BlockstateProvider.generateModels(treeObject.getHiveBlock().get(), treeObject.getExpansionBoxBlock().get(), id.getPath(), new HiveType(false, treeObject.getPlankColor(), treeObject.getStyle().hiveStyle(), Ingredient.of(treeObject.getPlankBlock().get())), hiveBlockStates, this.modelOutput);
                    }
                }
            });

            createBaseModels();

            hiveBlockStates.forEach((resourceLocation, stateGenerator) -> {
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
                this.modelOutput.accept(ModelLocationUtils.getModelLocation(item), new DelegatedModel(new ResourceLocation(ProductiveTrees.MODID, "item/sapling/base_" + baseSapling + treeObject.getStyle().saplingStyle())));
            }
            this.blockStateOutput.accept(createSimpleBlock(pottedBlock, new ResourceLocation(ProductiveTrees.MODID, "block/sapling/base_potted_sapling_" + treeObject.getStyle().saplingStyle())));
            this.blockStateOutput.accept(createSimpleBlock(block, new ResourceLocation(ProductiveTrees.MODID, "block/sapling/base_" + baseSapling + treeObject.getStyle().saplingStyle())));
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
                String fruitStyle = treeObject.getFruit().style();
                var template = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fruit/" + fruitStyle + "/fruit_" + age)), Optional.empty(), TextureSlot.ALL);
                return Variant.variant().with(VariantProperties.MODEL, template.create(new ResourceLocation(ProductiveTrees.MODID, "block/fruit/" + treeObject.getId().getPath() + "/" + age), (new TextureMapping()).put(TextureSlot.ALL, new ResourceLocation(ProductiveTrees.MODID, "block/leaves/" + treeObject.getStyle().leafStyle())), modelOutput));
            })));
        }

        private void createStairsBlock(WoodObject treeObject) {
            Block block = treeObject.getStairsBlock().get();
            ResourceLocation stairs = new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs");
            ResourceLocation stairsInner = new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs_inner");
            ResourceLocation stairsOuter = new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs_outer");
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

        private void createSlabBlock(WoodObject treeObject) {
            Block block = treeObject.getSlabBlock().get();
            ResourceLocation slab = new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + treeObject.getStyle().plankStyle() + "_slab");
            ResourceLocation planks = new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + treeObject.getStyle().plankStyle());
            ResourceLocation slabTop = new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + treeObject.getStyle().plankStyle() + "_slab_top");
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE).generate(slabType -> Variant.variant().with(VariantProperties.MODEL, slabType == SlabType.BOTTOM ? slab : slabType == SlabType.TOP ? slabTop : planks))));
        }

        private void createPressurePlateBlock(WoodObject treeObject) {
            Block block = treeObject.getPressurePlateBlock().get();
            ResourceLocation plate = new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate");
            ResourceLocation plateDown = new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate_down");
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.POWERED).generate(powered -> Variant.variant().with(VariantProperties.MODEL, powered ? plateDown : plate))));
        }

        private void createButtonBlock(WoodObject treeObject) {
            Block block = treeObject.getButtonBlock().get();
            ResourceLocation button = new ResourceLocation(ProductiveTrees.MODID, "block/button/" + treeObject.getStyle().plankStyle() + "_button");
            ResourceLocation buttonPressed = new ResourceLocation(ProductiveTrees.MODID, "block/button/" + treeObject.getStyle().plankStyle() + "_button_pressed");
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

        private void createFenceGateBlock(WoodObject treeObject) {
            Block block = treeObject.getFenceGateBlock().get();
            ResourceLocation fenceGate = new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate");
            ResourceLocation fenceGateOpen = new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_open");
            ResourceLocation fenceGateWall = new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_wall");
            ResourceLocation fenceGateWallOpen = new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_wall_open");
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

        private void createFenceBlock(WoodObject woodObject) {
            Block block = woodObject.getFenceBlock().get();
            ResourceLocation fencePost = new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + woodObject.getStyle().plankStyle() + "_fence_post");
            ResourceLocation fenceSide = new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + woodObject.getStyle().plankStyle() + "_fence_side");
            this.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(block)
                            .with(Variant.variant().with(VariantProperties.MODEL, fencePost))
                            .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true))
                            .with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                            .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                            .with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, fenceSide).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
            );
        }

        private void createDoorBlock(WoodObject woodObject) {
            Block block = woodObject.getDoorBlock().get();
            ResourceLocation bottomLeft = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_bottom_left");
            ResourceLocation bottomLeftOpen = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_bottom_left_open");
            ResourceLocation bottomRight = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_bottom_right");
            ResourceLocation bottomRightOpen = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_bottom_right_open");
            ResourceLocation topLeft = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_top_left");
            ResourceLocation topLeftOpen = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_top_left_open");
            ResourceLocation topRight = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_top_right");
            ResourceLocation topRightOpen = new ResourceLocation(ProductiveTrees.MODID, "block/door/" + woodObject.getStyle().doorStyle() + "_top_right_open");

            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(configureDoorHalf(configureDoorHalf(PropertyDispatch.properties(DoorBlock.FACING, DoorBlock.HALF, DoorBlock.HINGE, DoorBlock.OPEN), DoubleBlockHalf.LOWER, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen), DoubleBlockHalf.UPPER, topLeft, topLeftOpen, topRight, topRightOpen)));
        }

        private static PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> p_236305_, DoubleBlockHalf half, ResourceLocation left, ResourceLocation leftOpen, ResourceLocation right, ResourceLocation rightOpen) {
            return p_236305_
                    .select(Direction.EAST, half, DoorHingeSide.LEFT, false, Variant.variant()
                            .with(VariantProperties.MODEL, left))
                    .select(Direction.EAST, half, DoorHingeSide.RIGHT, false, Variant.variant()
                            .with(VariantProperties.MODEL, right))
                    .select(Direction.EAST, half, DoorHingeSide.LEFT, true, Variant.variant()
                            .with(VariantProperties.MODEL, leftOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .select(Direction.EAST, half, DoorHingeSide.RIGHT, true, Variant.variant()
                            .with(VariantProperties.MODEL, rightOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))

                    .select(Direction.SOUTH, half, DoorHingeSide.LEFT, false, Variant.variant()
                            .with(VariantProperties.MODEL, left)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .select(Direction.SOUTH, half, DoorHingeSide.RIGHT, false, Variant.variant()
                            .with(VariantProperties.MODEL, right)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .select(Direction.SOUTH, half, DoorHingeSide.LEFT, true, Variant.variant()
                            .with(VariantProperties.MODEL, leftOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .select(Direction.SOUTH, half, DoorHingeSide.RIGHT, true, Variant.variant()
                            .with(VariantProperties.MODEL, rightOpen))

                    .select(Direction.WEST, half, DoorHingeSide.LEFT, false, Variant.variant()
                            .with(VariantProperties.MODEL, left)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .select(Direction.WEST, half, DoorHingeSide.RIGHT, false, Variant.variant()
                            .with(VariantProperties.MODEL, right)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .select(Direction.WEST, half, DoorHingeSide.LEFT, true, Variant.variant()
                            .with(VariantProperties.MODEL, leftOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .select(Direction.WEST, half, DoorHingeSide.RIGHT, true, Variant.variant()
                            .with(VariantProperties.MODEL, rightOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))

                    .select(Direction.NORTH, half, DoorHingeSide.LEFT, false, Variant.variant()
                            .with(VariantProperties.MODEL, left)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .select(Direction.NORTH, half, DoorHingeSide.RIGHT, false, Variant.variant()
                            .with(VariantProperties.MODEL, right)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .select(Direction.NORTH, half, DoorHingeSide.LEFT, true, Variant.variant()
                            .with(VariantProperties.MODEL, leftOpen))
                    .select(Direction.NORTH, half, DoorHingeSide.RIGHT, true, Variant.variant()
                            .with(VariantProperties.MODEL, rightOpen)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
        }

        private void createTrapdoorBlock(WoodObject treeObject) {
            Block block = treeObject.getTrapdoorBlock().get();
            ResourceLocation top = new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_top");
            ResourceLocation bottom = new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_bottom");
            ResourceLocation open = new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_open");

            this.blockStateOutput.accept(createTrapdoor(block, top, bottom, open));
        }

        private static BlockStateGenerator createTrapdoor(Block block, ResourceLocation top, ResourceLocation bottom, ResourceLocation open) {
            return MultiVariantGenerator.multiVariant(block)
                    .with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN)
                            .select(Direction.NORTH, Half.BOTTOM, false, Variant.variant()
                                    .with(VariantProperties.MODEL, bottom))
                            .select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant()
                                    .with(VariantProperties.MODEL, bottom))
                            .select(Direction.EAST, Half.BOTTOM, false, Variant.variant()
                                    .with(VariantProperties.MODEL, bottom))
                            .select(Direction.WEST, Half.BOTTOM, false, Variant.variant()
                                    .with(VariantProperties.MODEL, bottom))
                            .select(Direction.NORTH, Half.TOP, false, Variant.variant()
                                    .with(VariantProperties.MODEL, top))
                            .select(Direction.SOUTH, Half.TOP, false, Variant.variant()
                                    .with(VariantProperties.MODEL, top))
                            .select(Direction.EAST, Half.TOP, false, Variant.variant()
                                    .with(VariantProperties.MODEL, top))
                            .select(Direction.WEST, Half.TOP, false, Variant.variant()
                                    .with(VariantProperties.MODEL, top))
                            .select(Direction.NORTH, Half.BOTTOM, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open))
                            .select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                            .select(Direction.EAST, Half.BOTTOM, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                            .select(Direction.WEST, Half.BOTTOM, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                            .select(Direction.NORTH, Half.TOP, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open))
                            .select(Direction.SOUTH, Half.TOP, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                            .select(Direction.EAST, Half.TOP, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                            .select(Direction.WEST, Half.TOP, true, Variant.variant()
                                    .with(VariantProperties.MODEL, open)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    );
        }

        private void createBaseModels() {
            WoodSet.STYLES.forEach((name, style) -> {
                String modelPrefix = TreeUtil.isTranslucentTree(name) ? "translucent_" : "";

                var planksModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + modelPrefix + "base_planks")), Optional.empty(), TextureSlot.ALL);

                var buttonModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button")), Optional.empty(), TextureSlot.TEXTURE);
                var buttonInventoryModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button_inventory")), Optional.empty(), TextureSlot.TEXTURE);
                var buttonPressedModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button_pressed")), Optional.empty(), TextureSlot.TEXTURE);

                var fenceInventoryModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_inventory")), Optional.empty(), TextureSlot.TEXTURE);
                var fencePostModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_post")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceSideModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_side")), Optional.empty(), TextureSlot.TEXTURE);

                var fenceGateModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_open")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateWallModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_wall")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateWallOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_wall_open")), Optional.empty(), TextureSlot.TEXTURE);

                var pressurePlateModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + modelPrefix + "base_pressure_plate")), Optional.empty(), TextureSlot.TEXTURE);
                var pressurePlateDownModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + modelPrefix + "base_pressure_plate_down")), Optional.empty(), TextureSlot.TEXTURE);

                var slabModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + modelPrefix + "base_slab")), Optional.empty(), TextureSlot.TEXTURE);
                var slabTopModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + modelPrefix + "base_slab_top")), Optional.empty(), TextureSlot.TEXTURE);

                var stairsModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs")), Optional.empty(), TextureSlot.TEXTURE);
                var stairsInnerModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs_inner")), Optional.empty(), TextureSlot.TEXTURE);
                var stairsOuterModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs_outer")), Optional.empty(), TextureSlot.TEXTURE);

                var leavesModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/leaves/" + modelPrefix + "base_leaves")), Optional.empty(), TextureSlot.ALL);

                var logModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + modelPrefix + "base_log")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var logHorizontalModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + modelPrefix + "base_log_horizontal")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var cubeColumnModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/" + modelPrefix + "cube_column")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var cubeColumnHorizontalModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/" + modelPrefix + "cube_column_horizontal")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);

                var doorBottomLeftModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_left")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomLeftOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_left_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomRightModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_right")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomRightOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_right_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopLeftModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_left")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopLeftOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_left_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopRightModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_right")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopRightOpenModel = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_right_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);

                var trapdoorBottom = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_bottom")), Optional.empty(), TextureSlot.TEXTURE);
                var trapdoorOpen = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_open")), Optional.empty(), TextureSlot.TEXTURE);
                var trapdoorTop = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_top")), Optional.empty(), TextureSlot.TEXTURE);

                var bookshelf = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/" + modelPrefix + "base_bookshelf")), Optional.empty(), TextureSlot.SIDE);

                var sign = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/sign/" + modelPrefix + "base_sign")), Optional.empty(), TextureSlot.PARTICLE);
                var hangingSign = new ModelTemplate(Optional.of(new ResourceLocation(ProductiveTrees.MODID, "block/sign/" + modelPrefix + "base_sign")), Optional.empty(), TextureSlot.PARTICLE);

                var plankTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + style.plankStyle())).put(TextureSlot.ALL, new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + style.plankStyle()));
                // planks
                planksModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + name), plankTextureMap, this.modelOutput);
                // button
                buttonModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + name + "_button"), plankTextureMap, this.modelOutput);
                buttonInventoryModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + name + "_button_inventory"), plankTextureMap, this.modelOutput);
                buttonPressedModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/button/" + name + "_button_pressed"), plankTextureMap, this.modelOutput);
                // fence
                fenceInventoryModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + name + "_fence_inventory"), plankTextureMap, this.modelOutput);
                fencePostModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + name + "_fence_post"), plankTextureMap, this.modelOutput);
                fenceSideModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence/" + name + "_fence_side"), plankTextureMap, this.modelOutput);
                // fence_gate
                fenceGateModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate"), plankTextureMap, this.modelOutput);
                fenceGateOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_open"), plankTextureMap, this.modelOutput);
                fenceGateWallModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_wall"), plankTextureMap, this.modelOutput);
                fenceGateWallOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_wall_open"), plankTextureMap, this.modelOutput);
                // pressure_plate
                pressurePlateModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + name + "_pressure_plate"), plankTextureMap, this.modelOutput);
                pressurePlateDownModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/pressure_plate/" + name + "_pressure_plate_down"), plankTextureMap, this.modelOutput);
                // slab
                slabModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + name + "_slab"), plankTextureMap, this.modelOutput);
                slabTopModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/slab/" + name + "_slab_top"), plankTextureMap, this.modelOutput);
                // stairs
                stairsModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs"), plankTextureMap, this.modelOutput);
                stairsInnerModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs_inner"), plankTextureMap, this.modelOutput);
                stairsOuterModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs_outer"), plankTextureMap, this.modelOutput);

                // leaves
                var leavesTextureMap = (new TextureMapping()).put(TextureSlot.ALL, new ResourceLocation(ProductiveTrees.MODID, "block/leaves/" + style.leafStyle()));
                leavesModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/leaves/" + name), leavesTextureMap, this.modelOutput);

                var logTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new ResourceLocation(ProductiveTrees.MODID, "block/log_top/" + style.woodStyle()))
                        .put(TextureSlot.SIDE, new ResourceLocation(ProductiveTrees.MODID, "block/log/" + style.woodStyle()));

                logModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_log"), logTextureMap, this.modelOutput);
                logHorizontalModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_log_horizontal"), logTextureMap, this.modelOutput);

                var strippedLogTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new ResourceLocation(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle() + "_top"))
                        .put(TextureSlot.SIDE, new ResourceLocation(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle()));

                cubeColumnModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_stripped_log"), strippedLogTextureMap, this.modelOutput);
                cubeColumnHorizontalModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_stripped_log_horizontal"), strippedLogTextureMap, this.modelOutput);

                var strippedWoodTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new ResourceLocation(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle()))
                        .put(TextureSlot.SIDE, new ResourceLocation(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle()));

                cubeColumnModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_stripped_wood"), strippedWoodTextureMap, this.modelOutput);

                var woodTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new ResourceLocation(ProductiveTrees.MODID, "block/log/" + style.woodStyle()))
                        .put(TextureSlot.SIDE, new ResourceLocation(ProductiveTrees.MODID, "block/log/" + style.woodStyle()));

                cubeColumnModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/log/" + name + "_wood"), woodTextureMap, this.modelOutput);

                var doorTextureMap = (new TextureMapping())
                        .put(TextureSlot.TOP, new ResourceLocation(ProductiveTrees.MODID, "block/door/" + style.doorStyle() + "_top"))
                        .put(TextureSlot.BOTTOM, new ResourceLocation(ProductiveTrees.MODID, "block/door/" + style.doorStyle() + "_bottom"));

                doorBottomLeftModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_bottom_left"), doorTextureMap, this.modelOutput);
                doorBottomLeftOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_bottom_left_open"), doorTextureMap, this.modelOutput);
                doorBottomRightModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_bottom_right"), doorTextureMap, this.modelOutput);
                doorBottomRightOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_bottom_right_open"), doorTextureMap, this.modelOutput);
                doorTopLeftModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_top_left"), doorTextureMap, this.modelOutput);
                doorTopLeftOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_top_left_open"), doorTextureMap, this.modelOutput);
                doorTopRightModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_top_right"), doorTextureMap, this.modelOutput);
                doorTopRightOpenModel.create(new ResourceLocation(ProductiveTrees.MODID, "block/door/" + name + "_top_right_open"), doorTextureMap, this.modelOutput);

                var trapdoorTextureMap = (new TextureMapping())
                        .put(TextureSlot.TEXTURE, new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + style.doorStyle()));

                trapdoorBottom.create(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + name + "_bottom"), trapdoorTextureMap, this.modelOutput);
                trapdoorOpen.create(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + name + "_open"), trapdoorTextureMap, this.modelOutput);
                trapdoorTop.create(new ResourceLocation(ProductiveTrees.MODID, "block/trapdoor/" + name + "_top"), trapdoorTextureMap, this.modelOutput);

                var bookshelfTextureMap = (new TextureMapping()).put(TextureSlot.SIDE, new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + style.plankStyle()));

                bookshelf.create(new ResourceLocation(ProductiveTrees.MODID, "block/bookshelf/" + name), bookshelfTextureMap, this.modelOutput);

                var signTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new ResourceLocation(ProductiveTrees.MODID, "block/planks/" + style.plankStyle()));
                var hangingSignTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new ResourceLocation(ProductiveTrees.MODID, "block/stripped/" + style.plankStyle()));

                sign.create(new ResourceLocation(ProductiveTrees.MODID, "block/sign/" + name), signTextureMap, this.modelOutput);
                hangingSign.create(new ResourceLocation(ProductiveTrees.MODID, "block/sign/hanging_" + name), hangingSignTextureMap, this.modelOutput);
            });
        }

        class WoodProvider
        {
            public WoodProvider() {}

            public WoodProvider wood(String style, Block block, boolean stripped) {
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createAxisAlignedPillarBlock(block, new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_wood" : "block/log/" + style + "_wood")));
                return this;
            }

            public WoodProvider logWithHorizontal(String style, Block block, boolean stripped) {
                return logWithHorizontal(style, block, stripped, "log");
            }

            public WoodProvider logWithHorizontal(String style, Block block, boolean stripped, String type) {
                ResourceLocation rLoc = new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_" + type : "block/log/" + style + "_" + type);
                ResourceLocation rLocHor = new ResourceLocation(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_" + type + "_horizontal" : "block/log/" + style + "_" + type + "_horizontal");
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createRotatedPillarWithHorizontalVariant(block, new ResourceLocation(ProductiveTrees.MODID, rLoc.getPath()), new ResourceLocation(ProductiveTrees.MODID, rLocHor.getPath())));
                return this;
            }
        }
    }
}
