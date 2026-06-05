package cy.jdkdigital.productivetrees.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Quadrant;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.client.color.PollenTintSource;
import cy.jdkdigital.productivetrees.client.color.TreeTintSource;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import cy.jdkdigital.productivetrees.util.WoodSet;
import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.DelegatedModel;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.Count;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModelProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<Identifier, ModelInstance> models = new HashMap<>();

    public ModelProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Block, BlockModelDefinitionGenerator> blockModels = Maps.newHashMap();
        Consumer<BlockModelDefinitionGenerator> blockStateOutput = (blockStateGenerator) -> {
            Block block = blockStateGenerator.block();
            BlockModelDefinitionGenerator blockstategenerator = blockModels.put(block, blockStateGenerator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        Map<Identifier, ModelInstance> itemModels = Maps.newHashMap();
        BiConsumer<Identifier, ModelInstance> modelOutput = (resourceLocation, elementSupplier) -> {
            ModelInstance supplier = itemModels.put(resourceLocation, elementSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };
        Map<Identifier, ClientItem> itemInfos = Maps.newHashMap();
        BiConsumer<Item, ItemModel.Unbaked> itemInfoOutput = (item, unbaked) -> {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            ClientItem previous = itemInfos.put(id, new ClientItem(unbaked, ClientItem.Properties.DEFAULT));
            if (previous != null) {
                throw new IllegalStateException("Duplicate client item definition for " + id);
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
        PackOutput.PathProvider itemInfoPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");

        TreeFinder.trees.forEach((id, treeObject) -> {
            Item saplingItem = TreeUtil.getBlock(id, "_sapling").asItem();
            if (saplingItem != Items.AIR) {
                Identifier saplingModel = ModelLocationUtils.getModelLocation(saplingItem);
                TreeTintSource leafTint = new TreeTintSource(ColorUtil.getCacheColor(treeObject.getLeafColor()));
                TreeTintSource logTint = new TreeTintSource(ColorUtil.getCacheColor(treeObject.getLogColor()));
                if (treeObject.hasFruit()) {
                    itemInfoOutput.accept(saplingItem, ItemModelUtils.tintedModel(saplingModel, leafTint, logTint, new TreeTintSource(ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()))));
                } else {
                    itemInfoOutput.accept(saplingItem, ItemModelUtils.tintedModel(saplingModel, leafTint, logTint));
                }
            }
            addBlockItemModel(TreeUtil.getBlock(id, "_leaves"), "leaves/" + treeObject.getStyle().leafStyle(), itemModels);
            addBlockItemModel(TreeUtil.getBlock(id, "_planks"), "planks/" + treeObject.getStyle().plankStyle(), itemModels);
            addBlockItemModel(TreeUtil.getBlock(id, "_log"), "log/" + treeObject.getStyle().woodStyle() + "_log", itemModels);
            addBlockItemModel(TreeUtil.getBlock(id, "_stripped_log"), "log/" + treeObject.getStyle().woodStyle() + "_stripped_log", itemModels);
            addBlockItemModel(TreeUtil.getBlock(id, "_wood"), "log/" + treeObject.getStyle().woodStyle() + "_wood", itemModels);
            addBlockItemModel(TreeUtil.getBlock(id, "_stripped_wood"), "log/" + treeObject.getStyle().woodStyle() + "_stripped_wood", itemModels);
            if (!ProductiveTrees.isMinimal) {
                addBlockItemModel(TreeUtil.getBlock(id, "_slab"), "slab/" + treeObject.getStyle().plankStyle() + "_slab", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_stairs"), "stairs/" + treeObject.getStyle().plankStyle() + "_stairs", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_button"), "button/" + treeObject.getStyle().plankStyle() + "_button_inventory", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_pressure_plate"), "pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_fence"), "fence/" + treeObject.getStyle().plankStyle() + "_fence_inventory", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_fence_gate"), "fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate", itemModels);
                ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(TreeUtil.getBlock(id, "_door").asItem()), (new TextureMapping()).put(TextureSlot.LAYER0, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "item/door/" + treeObject.getStyle().doorStyle()))), modelOutput);
                addBlockItemModel(TreeUtil.getBlock(id, "_trapdoor"), "trapdoor/" + treeObject.getStyle().doorStyle() + "_bottom", itemModels);
                addBlockItemModel(TreeUtil.getBlock(id, "_bookshelf"), "bookshelf/" + treeObject.getStyle().plankStyle(), itemModels);
                generateFlatItem(TreeUtil.getBlock(id, "_sign").asItem(), "item/sign/", modelOutput);
                generateFlatItem(TreeUtil.getBlock(id, "_hanging_sign").asItem(), "item/hanging_sign/", modelOutput);
            }
        });

        generateFruitItem(TreeRegistrator.COFFEE_BEAN.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CAROB.get(), modelOutput);
        generateFruitItem(TreeRegistrator.ALLSPICE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CLOVE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.CINNAMON.get(), modelOutput);
        generateFruitItem(TreeRegistrator.NUTMEG.get(), modelOutput);
        generateFruitItem(TreeRegistrator.STAR_ANISE.get(), modelOutput);
        generateFruitItem(TreeRegistrator.PLANET_PEACH.get(), modelOutput);

        generateMultiItem(TreeRegistrator.FUSTIC.get(), "item/", modelOutput);
        Item fustic = TreeRegistrator.FUSTIC.get();
        Identifier fusticModel = ModelLocationUtils.getModelLocation(fustic);
        itemInfoOutput.accept(fustic, ItemModelUtils.rangeSelect(
                new Count(false),
                ItemModelUtils.plainModel(fusticModel),
                ItemModelUtils.override(ItemModelUtils.plainModel(fusticModel.withSuffix("_two")), 2.0F),
                ItemModelUtils.override(ItemModelUtils.plainModel(fusticModel.withSuffix("_multiple")), 3.0F)));

        itemInfoOutput.accept(TreeRegistrator.POLLEN.get(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(TreeRegistrator.POLLEN.get()), new PollenTintSource()));

        generateFlatItem(TreeRegistrator.BAY_LEAF.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.CORK.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.HAEMATOXYLIN.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.DRACAENA_SAP.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.RUBBER.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.CURED_RUBBER.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.MAPLE_SAP_BUCKET.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.MAPLE_SYRUP.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.DATE_PALM_JUICE.get(), "item/", modelOutput);
        generateFlatItem(TreeRegistrator.SANDALWOOD_OIL.get(), "item/", modelOutput);

        TreeRegistrator.BERRIES.forEach(cropConfig ->  {
            generateFruitItem(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())), modelOutput);
        });
        TreeRegistrator.FRUITS.forEach(cropConfig ->  {
            generateFruitItem(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())), modelOutput);
        });
        TreeRegistrator.NUTS.forEach(cropConfig ->  {
            generateFruitItem(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())), modelOutput);
        });
        TreeRegistrator.ROASTED_NUTS.forEach(cropConfig ->  {
            generateFruitItem(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, cropConfig.name())), modelOutput);
        });

        TreeRegistrator.CRATED_CROPS.forEach(crate -> {
            addBlockItemParentModel(BuiltInRegistries.BLOCK.getValue(crate), "crates/", itemModels);
        });

        addBlockItemParentModel(TreeRegistrator.SAWMILL.get(), "", itemModels);
        addBlockItemParentModel(TreeRegistrator.STRIPPER.get(), "", itemModels);
        addBlockItemParentModel(TreeRegistrator.POLLEN_SIFTER.get(), "", itemModels);
        addBlockItemParentModel(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get(), "", itemModels);
        addBlockItemParentModel(TreeRegistrator.WOOD_WORKER.get(), "", itemModels);

        // Every item needs a client-item definition (assets/<ns>/items/<id>.json) pointing at its model,
        // or it renders without a model. Fill in a plain definition for each item this provider built a
        // model for that hasn't already been given a tinted/dispatched one above.
        for (var holder : ProductiveTrees.ITEMS.getEntries()) {
            Item item = holder.get();
            Identifier modelLocation = ModelLocationUtils.getModelLocation(item);
            if (itemModels.containsKey(modelLocation)) {
                itemInfos.computeIfAbsent(BuiltInRegistries.ITEM.getKey(item), key -> new ClientItem(ItemModelUtils.plainModel(modelLocation), ClientItem.Properties.DEFAULT));
            }
        }

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, blockGenerator) -> {
            JsonElement json = BlockStateModelDispatcher.CODEC.encodeStart(JsonOps.INSTANCE, blockGenerator.create()).getOrThrow();
            output.add(DataProvider.saveStable(cache, json, blockstatePathProvider.json(BuiltInRegistries.BLOCK.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });
        itemInfos.forEach((rLoc, clientItem) -> {
            JsonElement json = ClientItem.CODEC.encodeStart(JsonOps.INSTANCE, clientItem).getOrThrow();
            output.add(DataProvider.saveStable(cache, json, itemInfoPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    private void generateFlatItem(Item item, String prefix, BiConsumer<Identifier, ModelInstance> modelOutput) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFlatItemTextureMap(item, prefix), modelOutput);
    }

    private void generateFruitItem(Item item, BiConsumer<Identifier, ModelInstance> modelOutput) {
        generateFlatItem(item, "item/fruit/", modelOutput);
    }

    private void generateMultiItem(Item item, BiConsumer<Identifier, ModelInstance> modelOutput) {
        generateMultiItem(item, "item/fruit/", modelOutput);
    }
    private void generateMultiItem(Item item, String suffix, BiConsumer<Identifier, ModelInstance> modelOutput) {
        var tLocation = BuiltInRegistries.ITEM.getKey(item).withPrefix("item/").withSuffix("_two");
        ModelTemplates.FLAT_ITEM.create(tLocation, getFlatItemTextureMap(item, suffix, "_two"), modelOutput);
        var mLocation = BuiltInRegistries.ITEM.getKey(item).withPrefix("item/").withSuffix("_multiple");
        ModelTemplates.FLAT_ITEM.create(mLocation, getFlatItemTextureMap(item, suffix, "_multiple"), modelOutput);
        Identifier base = ModelLocationUtils.getModelLocation(item);
        modelOutput.accept(base, createFruitTemplate(base, getFlatItemTextureMap(item, suffix)));
    }

    public ModelInstance createFruitTemplate(Identifier resourceLocation, TextureMapping textureMapping) {
        return () -> {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("parent", "minecraft:item/generated");
            JsonObject textureLocations = new JsonObject();
            textureMapping.getForced().forEach(slot -> textureLocations.addProperty(slot.getId(), textureMapping.get(slot).sprite().toString()));
            if (textureLocations.size() > 0) {
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
        };
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix) {
        return getFlatItemTextureMap(item, prefix, "");
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix, String suffix) {
        Identifier resourcelocation = BuiltInRegistries.ITEM.getKey(item);
        return (new TextureMapping()).put(TextureSlot.LAYER0, new Material(resourcelocation.withPrefix(prefix).withSuffix(suffix)));
    }

    private void addItemModel(Item item, ModelInstance supplier, Map<Identifier, ModelInstance> itemModels) {
        if (item != null) {
            Identifier resourcelocation = ModelLocationUtils.getModelLocation(item);
            if (!itemModels.containsKey(resourcelocation)) {
                itemModels.put(resourcelocation, supplier);
            }
        }
    }

    private void addBlockItemModel(Block block, String base, Map<Identifier, ModelInstance> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            addItemModel(item, new DelegatedModel(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + base)), itemModels);
        }
    }

    private void addBlockItemParentModel(Block block, String prefix, Map<Identifier, ModelInstance> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            var rl = BuiltInRegistries.BLOCK.getKey(block);
            addItemModel(item, new DelegatedModel(Identifier.fromNamespaceAndPath(rl.getNamespace(), "block/" + prefix + rl.getPath())), itemModels);
        }
    }

    @Override
    public String getName() {
        return "Productive Trees Blockstate and Model generator";
    }

    static class ModelGenerator
    {
        Consumer<BlockModelDefinitionGenerator> blockStateOutput;
        BiConsumer<Identifier, ModelInstance> modelOutput;

        protected void registerStatesAndModels(Consumer<BlockModelDefinitionGenerator> blockStateOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
            this.blockStateOutput = blockStateOutput;
            this.modelOutput = modelOutput;

            TreeFinder.trees.forEach((id, treeObject) -> {
                this.createSapling(treeObject);
                this.createBaseBlock(TreeUtil.getBlock(id, "_leaves"), "leaves/" + treeObject.getStyle().leafStyle());
                if (treeObject.hasFruit()) {
                    this.createFruitBlock(treeObject);
                }
                if (treeObject.getId().getPath().equals("cinnamon")) {
                    createCrate(treeObject, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "cinnamon"));
                }
                if (treeObject.getId().getPath().equals("monkey_puzzle")) {
                    // the bending branch-leaf segments
                    createBranchLeaves(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "monkey_puzzle_small_leaves")), 2);
                    createBranchLeaves(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "monkey_puzzle_medium_leaves")), 4);
                }
                new WoodProvider().logWithHorizontal(treeObject.getStyle().woodStyle(), TreeUtil.getBlock(id, "_log"), false).wood(treeObject.getStyle().woodStyle(), TreeUtil.getBlock(id, "_wood"), false);
                new WoodProvider().logWithHorizontal(treeObject.getStyle().woodStyle(), TreeUtil.getBlock(id, "_stripped_log"), true).wood(treeObject.getStyle().woodStyle(), TreeUtil.getBlock(id, "_stripped_wood"), true);
                this.createBaseBlock(TreeUtil.getBlock(id, "_planks"), "planks/" + treeObject.getStyle().plankStyle());
                if (!ProductiveTrees.isMinimal) {
                    this.createStairsBlock(treeObject);
                    this.createSlabBlock(treeObject);
                    this.createPressurePlateBlock(treeObject);
                    this.createButtonBlock(treeObject);
                    this.createFenceGateBlock(treeObject);
                    this.createFenceBlock(treeObject);
                    this.createDoorBlock(treeObject);
                    this.createTrapdoorBlock(treeObject);
                    this.blockStateOutput.accept(createSimpleBlock(TreeUtil.getBlock(id, "_bookshelf"), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/bookshelf/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(TreeUtil.getBlock(id, "_sign"), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(TreeUtil.getBlock(id, "_wall_sign"), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(TreeUtil.getBlock(id, "_hanging_sign"), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/hanging_" + treeObject.getStyle().plankStyle())));
                    this.blockStateOutput.accept(createSimpleBlock(TreeUtil.getBlock(id, "_wall_hanging_sign"), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/hanging_" + treeObject.getStyle().plankStyle())));
                }
            });

            createBaseModels();
        }

        // a connecting branch leaf: a node + an arm per connected side, via a multipart blockstate (radius = half-thickness)
        private void createBranchLeaves(Block block, int radius) {
            Identifier texture = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/monkey_puzzle");
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            Identifier node = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + name + "_node");
            Identifier arm = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + name + "_arm");
            int r = radius;
            this.modelOutput.accept(node, boxModel(null, texture, new int[][]{{8 - r, 8 - r, 8 - r, 8 + r, 8 + r, 8 + r}}));
            // arm: a uniform segment from the node out toward north; the blockstate rotates it to the other sides
            this.modelOutput.accept(arm, boxModel(null, texture, new int[][]{
                    {8 - r, 8 - r, 0, 8 + r, 8 + r, 8 - r}
            }));
            this.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
                    .with(plainVariant(node))
                    .with(new ConditionBuilder().term(PipeBlock.NORTH, true), plainVariant(arm))
                    .with(new ConditionBuilder().term(PipeBlock.EAST, true), plainVariant(arm).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    .with(new ConditionBuilder().term(PipeBlock.SOUTH, true), plainVariant(arm).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                    .with(new ConditionBuilder().term(PipeBlock.WEST, true), plainVariant(arm).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                    .with(new ConditionBuilder().term(PipeBlock.UP, true), plainVariant(arm).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))
                    .with(new ConditionBuilder().term(PipeBlock.DOWN, true), plainVariant(arm).with(VariantMutator.X_ROT.withValue(Quadrant.R90))));
            // item icon: a straight segment
            Identifier itemModel = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "item/" + name);
            this.modelOutput.accept(itemModel, boxModel("minecraft:block/block", texture, new int[][]{
                    {8 - r, 8 - r, 0, 8 + r, 8 + r, 16}
            }));
        }

        private static ModelInstance boxModel(String parent, Identifier texture, int[][] boxes) {
            return () -> {
                JsonObject model = new JsonObject();
                if (parent != null) {
                    model.addProperty("parent", parent);
                }
                JsonObject textures = new JsonObject();
                textures.addProperty("texture", texture.toString());
                textures.addProperty("particle", texture.toString());
                model.add("textures", textures);
                JsonArray elements = new JsonArray();
                for (int[] box : boxes) {
                    JsonObject element = new JsonObject();
                    element.add("from", intArray(box[0], box[1], box[2]));
                    element.add("to", intArray(box[3], box[4], box[5]));
                    JsonObject faces = new JsonObject();
                    for (String face : new String[]{"down", "up", "north", "south", "west", "east"}) {
                        JsonObject faceObj = new JsonObject();
                        // uv from the box extents so the texture maps 1:1 (no squashing)
                        faceObj.add("uv", faceUv(face, box));
                        faceObj.addProperty("texture", "#texture");
                        faces.add(face, faceObj);
                    }
                    element.add("faces", faces);
                    elements.add(element);
                }
                model.add("elements", elements);
                return model;
            };
        }

        // the texture region matching a face's box extents (1:1 texel mapping)
        private static JsonArray faceUv(String face, int[] box) {
            int x1 = box[0], y1 = box[1], z1 = box[2], x2 = box[3], y2 = box[4], z2 = box[5];
            return switch (face) {
                case "down", "up" -> intArray(x1, z1, x2, z2);
                case "north", "south" -> intArray(x1, 16 - y2, x2, 16 - y1);
                default -> intArray(z1, 16 - y2, z2, 16 - y1); // west, east
            };
        }

        private static JsonArray intArray(int... values) {
            JsonArray array = new JsonArray();
            for (int value : values) {
                array.add(value);
            }
            return array;
        }

        private void createSapling(TreeObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_sapling");
            Block pottedBlock = TreeUtil.getBlock(treeObject.getId(), "_potted_sapling");

            String baseSapling = "";
            if (treeObject.hasFruit()) {
                baseSapling = "fruiting_";
            }

            Item item = block.asItem();
            if (item != Items.AIR) {
                this.modelOutput.accept(ModelLocationUtils.getModelLocation(item), new DelegatedModel(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "item/sapling/base_" + baseSapling + treeObject.getStyle().saplingStyle())));
            }
            this.blockStateOutput.accept(createSimpleBlock(pottedBlock, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sapling/base_potted_sapling_" + treeObject.getStyle().saplingStyle())));
            this.blockStateOutput.accept(createSimpleBlock(block, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sapling/base_" + baseSapling + treeObject.getStyle().saplingStyle())));
        }

        static ModelTemplate crateModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/base_crate")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.CROP);
        private void createCrate(TreeObject treeObject, Block block) {
            if (treeObject.getStyle().crateStyle() == null) {
                throw new RuntimeException(treeObject.getId() + " is missing a crate style");
            }
            Identifier top = BuiltInRegistries.BLOCK.getKey(block).withPath((p) -> "block/crate/" + p.replace("_crate", ""));
            var textureMapping = (new TextureMapping())
                    .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/crate/" + treeObject.getStyle().crateStyle() + "/side")))
                    .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/crate/" + treeObject.getStyle().crateStyle() + "/top")))
                    .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/crate/" + treeObject.getStyle().crateStyle() + "/bottom")))
                    .put(TextureSlot.CROP, new Material(top));
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(crateModel.create(BuiltInRegistries.BLOCK.getKey(block).withPath((p) -> "block/crates/" + p), textureMapping, this.modelOutput))));
        }

        static MultiVariantGenerator createSimpleBlock(Block block, Identifier resourceLocation) {
            return MultiVariantGenerator.dispatch(block, plainVariant(resourceLocation));
        }

        static BlockModelDefinitionGenerator createAxisAlignedPillarBlock(Block block, Identifier resourceLocation) {
            return MultiVariantGenerator.dispatch(block, plainVariant(resourceLocation)).with(createRotatedPillar());
        }

        static BlockModelDefinitionGenerator createRotatedPillarWithHorizontalVariant(Block p_124925_, Identifier p_124926_, Identifier resourceLocation) {
            return MultiVariantGenerator.dispatch(p_124925_).with(PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.Y, plainVariant(p_124926_)).select(Direction.Axis.Z, plainVariant(resourceLocation).with(VariantMutator.X_ROT.withValue(Quadrant.R90))).select(Direction.Axis.X, plainVariant(resourceLocation).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90))));
        }

        private static PropertyDispatch<VariantMutator> createRotatedPillar() {
            return PropertyDispatch.modify(BlockStateProperties.AXIS).select(Direction.Axis.Y, BlockModelGenerators.NOP).select(Direction.Axis.Z, VariantMutator.X_ROT.withValue(Quadrant.R90)).select(Direction.Axis.X, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R90)));
        }

        private void createBaseBlock(Block block, String baseName) {
            this.blockStateOutput.accept(createSimpleBlock(block, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + baseName)));
        }

        private void createFruitBlock(TreeObject treeObject) {
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(TreeUtil.getBlock(treeObject.getId(), "_fruit")).with(PropertyDispatch.initial(ProductiveFruitBlock.getAgeProperty()).generate(age -> {
                String fruitStyle = treeObject.getFruit().style();
                if (fruitStyle.equals("default")) {
                    var template = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fruit/base_fruit")), Optional.empty(), TextureSlot.ALL, TextureSlot.PLANT);
                    return plainVariant(template.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fruit/" + treeObject.getId().getPath() + "/stage_" + age), (new TextureMapping()).put(TextureSlot.ALL, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/" + treeObject.getStyle().leafStyle()))).put(TextureSlot.PLANT, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fruit/" + treeObject.getId().getPath() + "/stage_" + age))), modelOutput));
                }
                var template = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fruit/" + fruitStyle + "/fruit_" + age)), Optional.empty(), TextureSlot.ALL);
                return plainVariant(template.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fruit/" + treeObject.getId().getPath() + "/" + age), (new TextureMapping()).put(TextureSlot.ALL, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/" + treeObject.getStyle().leafStyle()))), modelOutput)
                );
            })));

            createCrate(treeObject, treeObject.getFruit().fruitItem());
        }

        private void createCrate(TreeObject treeObject, Identifier item) {
            var cratePath = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, item.withPath(p -> p.equals("apple") ? "red_delicious_apple_crate" : p + "_crate").getPath());
            if (TreeRegistrator.CRATED_CROPS.contains(cratePath) && !treeObject.getId().getPath().contains("copper_beech") && !treeObject.getId().getPath().contains("purple_blackthorn")) {
                createCrate(treeObject, BuiltInRegistries.BLOCK.getValue(cratePath));
            }
            var roastedCratePath = item.withPath(p -> "roasted_" + p + "_crate");
            if (TreeRegistrator.CRATED_CROPS.contains(roastedCratePath) && !treeObject.getId().getPath().contains("copper_beech") && !treeObject.getId().getPath().contains("purple_blackthorn")) {
                createCrate(treeObject, BuiltInRegistries.BLOCK.getValue(roastedCratePath));
            };
        }

        private static Quadrant quadrant(int yRotValue) {
            return switch (yRotValue) {
                case 90 -> Quadrant.R90;
                case 180 -> Quadrant.R180;
                case 270 -> Quadrant.R270;
                default -> Quadrant.R0;
            };
        }

        private void createStairsBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_stairs");
            Identifier stairs = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs");
            Identifier stairsInner = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs_inner");
            Identifier stairsOuter = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + treeObject.getStyle().plankStyle() + "_stairs_outer");
            this.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(HorizontalDirectionalBlock.FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).generate((facing, half, shape) -> {
                        int yRotValue = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
                        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                            yRotValue += 270; // Left facing stairs are rotated 90 degrees clockwise
                        }
                        if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                            yRotValue += 90; // Top stairs are rotated 90 degrees clockwise
                        }
                        yRotValue %= 360;
                        boolean uvLock = yRotValue != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation

                        var yRot = quadrant(yRotValue);

                        var variant = plainVariant(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter);

                        if (half != Half.BOTTOM) {
                            variant = variant.with(VariantMutator.X_ROT.withValue(Quadrant.R180));
                        }
                        if (!yRot.equals(Quadrant.R0)) {
                            variant = variant.with(VariantMutator.Y_ROT.withValue(yRot));
                        }
                        if (uvLock) {
                            variant = variant.with(VariantMutator.UV_LOCK.withValue(true));
                        }
                        return variant;
                    }))
            );
        }

        private void createSlabBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_slab");
            Identifier slab = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + treeObject.getStyle().plankStyle() + "_slab");
            Identifier planks = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + treeObject.getStyle().plankStyle());
            Identifier slabTop = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + treeObject.getStyle().plankStyle() + "_slab_top");
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE).generate(slabType -> plainVariant(slabType == SlabType.BOTTOM ? slab : slabType == SlabType.TOP ? slabTop : planks))));
        }

        private void createPressurePlateBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_pressure_plate");
            Identifier plate = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate");
            Identifier plateDown = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + treeObject.getStyle().plankStyle() + "_pressure_plate_down");
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.POWERED).generate(powered -> plainVariant(powered ? plateDown : plate))));
        }

        private void createButtonBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_button");
            Identifier button = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + treeObject.getStyle().plankStyle() + "_button");
            Identifier buttonPressed = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + treeObject.getStyle().plankStyle() + "_button_pressed");
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(ButtonBlock.FACING, ButtonBlock.FACE, ButtonBlock.POWERED)
                    .generate((facing, face, powered) -> {
                        var variant = plainVariant(powered ? buttonPressed : button);

                        int yRotValue = (int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot();
                        var yRot = quadrant(yRotValue);

                        if (face != AttachFace.FLOOR) {
                            variant = variant.with(VariantMutator.X_ROT.withValue(face == AttachFace.WALL ? Quadrant.R90 : Quadrant.R180));
                        }
                        if (!yRot.equals(Quadrant.R0)) {
                            variant = variant.with(VariantMutator.Y_ROT.withValue(yRot));
                        }
                        if (face == AttachFace.WALL) {
                            variant = variant.with(VariantMutator.UV_LOCK.withValue(true));
                        }

                        return variant;
                    })));
        }

        private void createFenceGateBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_fence_gate");
            Identifier fenceGate = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate");
            Identifier fenceGateOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_open");
            Identifier fenceGateWall = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_wall");
            Identifier fenceGateWallOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + treeObject.getStyle().plankStyle() + "_fence_gate_wall_open");
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(FenceGateBlock.FACING, FenceGateBlock.IN_WALL, FenceGateBlock.OPEN)
                    .generate((facing, inWall, open) -> {
                        var variant = plainVariant(open ? (inWall ? fenceGateWallOpen : fenceGateOpen) : (inWall ? fenceGateWall : fenceGate));

                        int yRotValue = (int) facing.toYRot();
                        var yRot = quadrant(yRotValue);

                        if (!yRot.equals(Quadrant.R0)) {
                            variant = variant.with(VariantMutator.Y_ROT.withValue(yRot));
                        }
                        variant = variant.with(VariantMutator.UV_LOCK.withValue(true));

                        return variant;
                    })));
        }

        private void createFenceBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_fence");
            Identifier fencePost = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + treeObject.getStyle().plankStyle() + "_fence_post");
            Identifier fenceSide = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + treeObject.getStyle().plankStyle() + "_fence_side");
            this.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(block)
                            .with(plainVariant(fencePost))
                            .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), plainVariant(fenceSide).with(VariantMutator.UV_LOCK.withValue(true)))
                            .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), plainVariant(fenceSide).with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), plainVariant(fenceSide).with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), plainVariant(fenceSide).with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
            );
        }

        private void createDoorBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_door");
            Identifier bottomLeft = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_bottom_left");
            Identifier bottomLeftOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_bottom_left_open");
            Identifier bottomRight = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_bottom_right");
            Identifier bottomRightOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_bottom_right_open");
            Identifier topLeft = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_top_left");
            Identifier topLeftOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_top_left_open");
            Identifier topRight = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_top_right");
            Identifier topRightOpen = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + treeObject.getStyle().doorStyle() + "_top_right_open");

            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(configureDoorHalf(configureDoorHalf(PropertyDispatch.initial(DoorBlock.FACING, DoorBlock.HALF, DoorBlock.HINGE, DoorBlock.OPEN), DoubleBlockHalf.LOWER, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen), DoubleBlockHalf.UPPER, topLeft, topLeftOpen, topRight, topRightOpen)));
        }

        private static PropertyDispatch.C4<MultiVariant, Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(PropertyDispatch.C4<MultiVariant, Direction, DoubleBlockHalf, DoorHingeSide, Boolean> p_236305_, DoubleBlockHalf half, Identifier left, Identifier leftOpen, Identifier right, Identifier rightOpen) {
            return p_236305_
                    .select(Direction.EAST, half, DoorHingeSide.LEFT, false, plainVariant(left))
                    .select(Direction.EAST, half, DoorHingeSide.RIGHT, false, plainVariant(right))
                    .select(Direction.EAST, half, DoorHingeSide.LEFT, true, plainVariant(leftOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    .select(Direction.EAST, half, DoorHingeSide.RIGHT, true, plainVariant(rightOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))

                    .select(Direction.SOUTH, half, DoorHingeSide.LEFT, false, plainVariant(left)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    .select(Direction.SOUTH, half, DoorHingeSide.RIGHT, false, plainVariant(right)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    .select(Direction.SOUTH, half, DoorHingeSide.LEFT, true, plainVariant(leftOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                    .select(Direction.SOUTH, half, DoorHingeSide.RIGHT, true, plainVariant(rightOpen))

                    .select(Direction.WEST, half, DoorHingeSide.LEFT, false, plainVariant(left)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                    .select(Direction.WEST, half, DoorHingeSide.RIGHT, false, plainVariant(right)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                    .select(Direction.WEST, half, DoorHingeSide.LEFT, true, plainVariant(leftOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                    .select(Direction.WEST, half, DoorHingeSide.RIGHT, true, plainVariant(rightOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))

                    .select(Direction.NORTH, half, DoorHingeSide.LEFT, false, plainVariant(left)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                    .select(Direction.NORTH, half, DoorHingeSide.RIGHT, false, plainVariant(right)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                    .select(Direction.NORTH, half, DoorHingeSide.LEFT, true, plainVariant(leftOpen))
                    .select(Direction.NORTH, half, DoorHingeSide.RIGHT, true, plainVariant(rightOpen)
                            .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)));
        }

        private void createTrapdoorBlock(WoodObject treeObject) {
            Block block = TreeUtil.getBlock(treeObject.getId(), "_trapdoor");
            Identifier top = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_top");
            Identifier bottom = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_bottom");
            Identifier open = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + treeObject.getStyle().doorStyle() + "_open");

            this.blockStateOutput.accept(createTrapdoor(block, top, bottom, open));
        }

        private static BlockModelDefinitionGenerator createTrapdoor(Block block, Identifier top, Identifier bottom, Identifier open) {
            return MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN)
                            .select(Direction.NORTH, Half.BOTTOM, false, plainVariant(bottom))
                            .select(Direction.SOUTH, Half.BOTTOM, false, plainVariant(bottom)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.EAST, Half.BOTTOM, false, plainVariant(bottom)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.WEST, Half.BOTTOM, false, plainVariant(bottom)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.NORTH, Half.TOP, false, plainVariant(top))
                            .select(Direction.SOUTH, Half.TOP, false, plainVariant(top)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.EAST, Half.TOP, false, plainVariant(top)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.WEST, Half.TOP, false, plainVariant(top)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.NORTH, Half.BOTTOM, true, plainVariant(open))
                            .select(Direction.SOUTH, Half.BOTTOM, true, plainVariant(open)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.EAST, Half.BOTTOM, true, plainVariant(open)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.WEST, Half.BOTTOM, true, plainVariant(open)
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.NORTH, Half.TOP, true, plainVariant(open)
                                    .with(VariantMutator.X_ROT.withValue(Quadrant.R180))
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.SOUTH, Half.TOP, true, plainVariant(open)
                                    .with(VariantMutator.X_ROT.withValue(Quadrant.R180))
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R0)))
                            .select(Direction.EAST, Half.TOP, true, plainVariant(open)
                                    .with(VariantMutator.X_ROT.withValue(Quadrant.R180))
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.WEST, Half.TOP, true, plainVariant(open)
                                    .with(VariantMutator.X_ROT.withValue(Quadrant.R180))
                                    .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    );
        }

        private void createBaseModels() {
            WoodSet.STYLES.forEach((name, style) -> {
                String modelPrefix = TreeUtil.isTranslucentTree(name) ? "translucent_" : "";

                var planksModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + modelPrefix + "base_planks")), Optional.empty(), TextureSlot.ALL);

                var buttonModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button")), Optional.empty(), TextureSlot.TEXTURE);
                var buttonInventoryModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button_inventory")), Optional.empty(), TextureSlot.TEXTURE);
                var buttonPressedModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + modelPrefix + "base_button_pressed")), Optional.empty(), TextureSlot.TEXTURE);

                var fenceInventoryModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_inventory")), Optional.empty(), TextureSlot.TEXTURE);
                var fencePostModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_post")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceSideModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + modelPrefix + "base_fence_side")), Optional.empty(), TextureSlot.TEXTURE);

                var fenceGateModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_open")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateWallModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_wall")), Optional.empty(), TextureSlot.TEXTURE);
                var fenceGateWallOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + modelPrefix + "base_fence_gate_wall_open")), Optional.empty(), TextureSlot.TEXTURE);

                var pressurePlateModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + modelPrefix + "base_pressure_plate")), Optional.empty(), TextureSlot.TEXTURE);
                var pressurePlateDownModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + modelPrefix + "base_pressure_plate_down")), Optional.empty(), TextureSlot.TEXTURE);

                var slabModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + modelPrefix + "base_slab")), Optional.empty(), TextureSlot.TEXTURE);
                var slabTopModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + modelPrefix + "base_slab_top")), Optional.empty(), TextureSlot.TEXTURE);

                var stairsModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs")), Optional.empty(), TextureSlot.TEXTURE);
                var stairsInnerModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs_inner")), Optional.empty(), TextureSlot.TEXTURE);
                var stairsOuterModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + modelPrefix + "base_stairs_outer")), Optional.empty(), TextureSlot.TEXTURE);

                var leavesModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/" + modelPrefix + "base_leaves")), Optional.empty(), TextureSlot.ALL);

                var logModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + modelPrefix + "base_log")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var logHorizontalModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + modelPrefix + "base_log_horizontal")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var cubeColumnModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + modelPrefix + "cube_column")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
                var cubeColumnHorizontalModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + modelPrefix + "cube_column_horizontal")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);

                var doorBottomLeftModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_left")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomLeftOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_left_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomRightModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_right")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorBottomRightOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "bottom_right_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopLeftModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_left")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopLeftOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_left_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopRightModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_right")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);
                var doorTopRightOpenModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + modelPrefix + "top_right_open")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP);

                var trapdoorBottom = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_bottom")), Optional.empty(), TextureSlot.TEXTURE);
                var trapdoorOpen = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_open")), Optional.empty(), TextureSlot.TEXTURE);
                var trapdoorTop = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + modelPrefix + "template_orientable_trapdoor_top")), Optional.empty(), TextureSlot.TEXTURE);

                var bookshelf = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/" + modelPrefix + "base_bookshelf")), Optional.empty(), TextureSlot.SIDE);

                var sign = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/" + modelPrefix + "base_sign")), Optional.empty(), TextureSlot.PARTICLE);
                var hangingSign = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/" + modelPrefix + "base_sign")), Optional.empty(), TextureSlot.PARTICLE);

                var plankTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + style.plankStyle()))).put(TextureSlot.ALL, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + style.plankStyle())));
                // planks
                planksModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + name), plankTextureMap, this.modelOutput);
                // button
                buttonModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + name + "_button"), plankTextureMap, this.modelOutput);
                buttonInventoryModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + name + "_button_inventory"), plankTextureMap, this.modelOutput);
                buttonPressedModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/button/" + name + "_button_pressed"), plankTextureMap, this.modelOutput);
                // fence
                fenceInventoryModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + name + "_fence_inventory"), plankTextureMap, this.modelOutput);
                fencePostModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + name + "_fence_post"), plankTextureMap, this.modelOutput);
                fenceSideModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence/" + name + "_fence_side"), plankTextureMap, this.modelOutput);
                // fence_gate
                fenceGateModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate"), plankTextureMap, this.modelOutput);
                fenceGateOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_open"), plankTextureMap, this.modelOutput);
                fenceGateWallModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_wall"), plankTextureMap, this.modelOutput);
                fenceGateWallOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/fence_gate/" + name + "_fence_gate_wall_open"), plankTextureMap, this.modelOutput);
                // pressure_plate
                pressurePlateModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + name + "_pressure_plate"), plankTextureMap, this.modelOutput);
                pressurePlateDownModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/pressure_plate/" + name + "_pressure_plate_down"), plankTextureMap, this.modelOutput);
                // slab
                slabModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + name + "_slab"), plankTextureMap, this.modelOutput);
                slabTopModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/slab/" + name + "_slab_top"), plankTextureMap, this.modelOutput);
                // stairs
                stairsModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs"), plankTextureMap, this.modelOutput);
                stairsInnerModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs_inner"), plankTextureMap, this.modelOutput);
                stairsOuterModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stairs/" + name + "_stairs_outer"), plankTextureMap, this.modelOutput);

                // leaves
                var leavesTextureMap = (new TextureMapping()).put(TextureSlot.ALL, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/" + style.leafStyle())));
                leavesModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/leaves/" + name), leavesTextureMap, this.modelOutput);

                var logTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log_top/" + style.woodStyle())))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + style.woodStyle())));

                logModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_log"), logTextureMap, this.modelOutput);
                logHorizontalModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_log_horizontal"), logTextureMap, this.modelOutput);

                var strippedLogTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle() + "_top")))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle())));

                cubeColumnModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_stripped_log"), strippedLogTextureMap, this.modelOutput);
                cubeColumnHorizontalModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_stripped_log_horizontal"), strippedLogTextureMap, this.modelOutput);

                var strippedWoodTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle())))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stripped/" + style.woodStyle())));

                cubeColumnModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_stripped_wood"), strippedWoodTextureMap, this.modelOutput);

                var woodTextureMap = (new TextureMapping())
                        .put(TextureSlot.END, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + style.woodStyle())))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + style.woodStyle())));

                cubeColumnModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/log/" + name + "_wood"), woodTextureMap, this.modelOutput);

                var doorTextureMap = (new TextureMapping())
                        .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + style.doorStyle() + "_top")))
                        .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + style.doorStyle() + "_bottom")));

                doorBottomLeftModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_bottom_left"), doorTextureMap, this.modelOutput);
                doorBottomLeftOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_bottom_left_open"), doorTextureMap, this.modelOutput);
                doorBottomRightModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_bottom_right"), doorTextureMap, this.modelOutput);
                doorBottomRightOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_bottom_right_open"), doorTextureMap, this.modelOutput);
                doorTopLeftModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_top_left"), doorTextureMap, this.modelOutput);
                doorTopLeftOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_top_left_open"), doorTextureMap, this.modelOutput);
                doorTopRightModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_top_right"), doorTextureMap, this.modelOutput);
                doorTopRightOpenModel.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/door/" + name + "_top_right_open"), doorTextureMap, this.modelOutput);

                var trapdoorTextureMap = (new TextureMapping())
                        .put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + style.doorStyle())));

                trapdoorBottom.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + name + "_bottom"), trapdoorTextureMap, this.modelOutput);
                trapdoorOpen.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + name + "_open"), trapdoorTextureMap, this.modelOutput);
                trapdoorTop.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/trapdoor/" + name + "_top"), trapdoorTextureMap, this.modelOutput);

                var bookshelfTextureMap = (new TextureMapping()).put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + style.plankStyle())));

                bookshelf.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/bookshelf/" + name), bookshelfTextureMap, this.modelOutput);

                var signTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/planks/" + style.plankStyle())));
                var hangingSignTextureMap = (new TextureMapping()).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/stripped/" + style.plankStyle())));

                sign.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/" + name), signTextureMap, this.modelOutput);
                hangingSign.create(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "block/sign/hanging_" + name), hangingSignTextureMap, this.modelOutput);
            });
        }

        class WoodProvider
        {
            public WoodProvider() {}

            public WoodProvider wood(String style, Block block, boolean stripped) {
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createAxisAlignedPillarBlock(block, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_wood" : "block/log/" + style + "_wood")));
                return this;
            }

            public WoodProvider logWithHorizontal(String style, Block block, boolean stripped) {
                return logWithHorizontal(style, block, stripped, "log");
            }

            public WoodProvider logWithHorizontal(String style, Block block, boolean stripped, String type) {
                Identifier rLoc = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_" + type : "block/log/" + style + "_" + type);
                Identifier rLocHor = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, stripped ? "block/log/" + style + "_stripped_" + type + "_horizontal" : "block/log/" + style + "_" + type + "_horizontal");
                ModelGenerator.this.blockStateOutput.accept(ModelGenerator.createRotatedPillarWithHorizontalVariant(block, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, rLoc.getPath()), Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, rLocHor.getPath())));
                return this;
            }
        }
    }
}
