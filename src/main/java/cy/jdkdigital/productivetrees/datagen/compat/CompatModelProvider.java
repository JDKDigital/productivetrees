package cy.jdkdigital.productivetrees.datagen.compat;

import com.mojang.math.Quadrant;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.data.models.BlockModelGenerators;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.client.color.TreeTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class CompatModelProvider extends ModelProvider
{
    public CompatModelProvider(PackOutput packOutput) {
        super(packOutput, ProductiveTrees.MODID);
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        List<Holder<Item>> known = new ArrayList<>();
        forEachHive((id, treeObject, hive, box) -> {
            known.add(hive.asItem().builtInRegistryHolder());
            known.add(box.asItem().builtInRegistryHolder());
        });
        return known.stream();
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        List<Holder<Block>> known = new ArrayList<>();
        forEachHive((id, treeObject, hive, box) -> {
            known.add(hive.builtInRegistryHolder());
            known.add(box.builtInRegistryHolder());
        });
        return known.stream();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        forEachHive((id, treeObject, hive, box) -> {
            HiveType type = new HiveType(false, treeObject.getPlankColor(), treeObject.getStyle().hiveStyle(), TreeUtil.getBlock(id, "_planks"), null);
            generateHiveAndBox(blockModels, itemModels, hive, box, id.getPath(), type, treeObject.tintHives());
        });
    }

    private interface HiveConsumer
    {
        void accept(Identifier id, TreeObject treeObject, Block hive, Block box);
    }

    private void forEachHive(HiveConsumer consumer) {
        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.getStyle().hiveStyle() != null) {
                Block hive = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive")).map(Holder::value).orElse(null);
                Block box = BuiltInRegistries.BLOCK.get(treeObject.getId().withPath(p -> "expansion_box_" + p)).map(Holder::value).orElse(null);
                if (hive != null && box != null) {
                    consumer.accept(id, treeObject, hive, box);
                }
            }
        });
    }

    private void generateHiveAndBox(BlockModelGenerators blockModels, ItemModelGenerators itemModels, Block hive, Block box, String name, HiveType type, boolean tintHives) {
        String modId = BuiltInRegistries.BLOCK.getKey(hive).getNamespace();
        ModelTemplate hiveTemplate = hiveModelTemplate();
        ModelTemplate boxTemplate = expansionModelTemplate();

        Identifier hiveBase = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/hives/advanced_" + name + "_beehive");
        Identifier boxBase = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/expansion_boxes/expansion_box_" + name);

        Identifier single = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.NONE, false, hiveBase);
        Identifier up = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.UP, false, hiveBase.withSuffix("_up"));
        Identifier down = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.DOWN, false, hiveBase.withSuffix("_down"));
        Identifier left = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.LEFT, false, hiveBase.withSuffix("_left"));
        Identifier right = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.RIGHT, false, hiveBase.withSuffix("_right"));
        Identifier back = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.BACK, false, hiveBase.withSuffix("_back"));
        Identifier singleHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.NONE, true, hiveBase.withSuffix("_honey"));
        Identifier upHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.UP, true, hiveBase.withSuffix("_up_honey"));
        Identifier downHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.DOWN, true, hiveBase.withSuffix("_down_honey"));
        Identifier leftHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.LEFT, true, hiveBase.withSuffix("_left_honey"));
        Identifier rightHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.RIGHT, true, hiveBase.withSuffix("_right_honey"));
        Identifier backHoney = hiveModel(blockModels, hiveTemplate, name, type, VerticalHive.BACK, true, hiveBase.withSuffix("_back_honey"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(hive)
                        .with(PropertyDispatch.initial(AdvancedBeehive.EXPANDED, BlockStateProperties.LEVEL_HONEY).generate((expanded, level) -> {
                            boolean honey = level >= 5;
                            return switch (expanded) {
                                case NONE -> plainVariant(honey ? singleHoney : single);
                                case UP -> plainVariant(honey ? upHoney : up);
                                case DOWN -> plainVariant(honey ? downHoney : down);
                                case LEFT -> plainVariant(honey ? leftHoney : left);
                                case RIGHT -> plainVariant(honey ? rightHoney : right);
                                case BACK -> plainVariant(honey ? backHoney : back);
                            };
                        }))
                        .with(PropertyDispatch.modify(BeehiveBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))));

        Identifier hiveItemParent = type.hasTexture()
                ? hiveBase
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/small");
        Identifier hiveItemModel = Identifier.fromNamespaceAndPath(modId, "item/advanced_" + name + "_beehive");
        itemTemplate(hiveItemParent).create(hiveItemModel, new TextureMapping(), blockModels.modelOutput);
        itemModels.itemModelOutput.accept(hive.asItem(), tintHives
                ? ItemModelUtils.tintedModel(hiveItemModel, new TreeTintSource(ColorUtil.getCacheColor(type.primary())))
                : ItemModelUtils.plainModel(hiveItemModel));

        Identifier boxSingle = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.NONE, boxBase);
        Identifier boxUp = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.UP, boxBase.withSuffix("_up"));
        Identifier boxDown = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.DOWN, boxBase.withSuffix("_down"));
        Identifier boxLeft = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.LEFT, boxBase.withSuffix("_left"));
        Identifier boxRight = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.RIGHT, boxBase.withSuffix("_right"));
        Identifier boxBack = expansionModel(blockModels, boxTemplate, name, type, VerticalHive.BACK, boxBase.withSuffix("_back"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(box)
                        .with(PropertyDispatch.initial(AdvancedBeehive.EXPANDED).generate(expanded -> switch (expanded) {
                            case NONE -> plainVariant(boxSingle);
                            case UP -> plainVariant(boxUp);
                            case DOWN -> plainVariant(boxDown);
                            case LEFT -> plainVariant(boxLeft);
                            case RIGHT -> plainVariant(boxRight);
                            case BACK -> plainVariant(boxBack);
                        }))
                        .with(PropertyDispatch.modify(BeehiveBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))));

        Identifier boxItemParent = type.hasTexture()
                ? boxBase
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/small");
        Identifier boxItemModel = Identifier.fromNamespaceAndPath(modId, "item/expansion_box_" + name);
        itemTemplate(boxItemParent).create(boxItemModel, new TextureMapping(), blockModels.modelOutput);
        itemModels.itemModelOutput.accept(box.asItem(), tintHives
                ? ItemModelUtils.tintedModel(boxItemModel, new TreeTintSource(ColorUtil.getCacheColor(type.primary())))
                : ItemModelUtils.plainModel(boxItemModel));
    }

    private Identifier hiveModel(BlockModelGenerators blockModels, ModelTemplate template, String name, HiveType type, VerticalHive expand, boolean honey, Identifier modelLocation) {
        if (!type.hasTexture()) {
            String suffix = switch (expand) {
                case NONE -> "small";
                default -> expand.getSerializedName();
            };
            return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_hive/" + type.style() + "/" + suffix + (honey ? "_honey" : ""));
        }
        return template.create(modelLocation, hiveTextureMap(expand, name, honey), blockModels.modelOutput);
    }

    private Identifier expansionModel(BlockModelGenerators blockModels, ModelTemplate template, String name, HiveType type, VerticalHive expand, Identifier modelLocation) {
        if (!type.hasTexture()) {
            String suffix = switch (expand) {
                case NONE -> "small";
                default -> expand.getSerializedName();
            };
            return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "block/tinted_expansion_box/" + type.style() + "/" + suffix);
        }
        return template.create(modelLocation, expansionTextureMap(expand, name), blockModels.modelOutput);
    }

    private static TextureMapping hiveTextureMap(VerticalHive expand, String type, boolean honey) {
        Material front = pbTex("block/advanced_beehive/" + type + "_beehive_front" + (honey ? "_honey" : ""));
        Material back = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material right = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material left = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material top = pbTex("block/advanced_beehive/" + type + "_beehive_end");
        Material bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_front_" + expand.getSerializedName() + (honey ? "_honey" : ""));
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
            }
            case LEFT, RIGHT -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_front_" + expand.opposite() + (honey ? "_honey" : ""));
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_" + expand.getSerializedName());
                bottom = top;
            }
            case BACK -> {
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_left");
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_right");
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_front");
                bottom = pbTex("block/advanced_beehive/" + type + "_beehive_end_back");
            }
            default -> {}
        }
        return new TextureMapping()
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    private static TextureMapping expansionTextureMap(VerticalHive expand, String type) {
        Material front = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material back = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material left = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material right = pbTex("block/advanced_beehive/" + type + "_beehive_side");
        Material top = pbTex("block/advanced_beehive/" + type + "_beehive_end");
        Material bottom = top;
        switch (expand) {
            case UP, DOWN -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
            }
            case LEFT, RIGHT -> {
                front = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.getSerializedName());
                back = pbTex("block/advanced_beehive/" + type + "_beehive_side_" + expand.opposite());
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_" + expand.opposite());
                bottom = top;
            }
            case BACK -> {
                left = pbTex("block/advanced_beehive/" + type + "_beehive_side_left");
                right = pbTex("block/advanced_beehive/" + type + "_beehive_side_right");
                top = pbTex("block/advanced_beehive/" + type + "_beehive_end_back");
                bottom = pbTex("block/advanced_beehive/" + type + "_beehive_end_front");
            }
            default -> {}
        }
        return new TextureMapping()
                .put(TextureSlot.EAST, right)
                .put(TextureSlot.WEST, left)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.BACK, back)
                .copySlot(TextureSlot.EAST, TextureSlot.PARTICLE);
    }

    private static ModelTemplate hiveModelTemplate() {
        return new ModelTemplate(Optional.of(pbId("block/advanced_beehive_template")), Optional.empty(),
                TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private static ModelTemplate expansionModelTemplate() {
        return new ModelTemplate(Optional.of(pbId("block/expansion_box_template")), Optional.empty(),
                TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.FRONT, TextureSlot.BACK);
    }

    private static ModelTemplate itemTemplate(Identifier parent) {
        return new ModelTemplate(Optional.of(parent), Optional.empty());
    }

    private static Identifier pbId(String path) {
        return Identifier.fromNamespaceAndPath(ProductiveBees.MODID, path);
    }

    private static Material pbTex(String path) {
        return new Material(pbId(path));
    }
}
