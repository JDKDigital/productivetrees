package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import cy.jdkdigital.productivetrees.util.WoodSet;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class TreeObject extends WoodObject
{
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;
    private final ResourceKey<ConfiguredFeature<?, ?>> megaFeature;
    private final String style;
    private final Fruit fruit;
    private final MutationInfo mutationInfo;
    private final TagKey<Block> soil;
    private final TintStyle tintStyle;
    private final boolean fallingLeaves;
    private final GrowthCondition growthCondition;
    private final Decoration decoration;

    public TreeObject(ResourceLocation id, ResourceKey<ConfiguredFeature<?, ?>> feature, ResourceKey<ConfiguredFeature<?, ?>> megaFeature, String style, Supplier<ItemStack> stripDrop, TreeColors colors, Fruit fruit, MutationInfo mutationInfo, TagKey<Block> soil, boolean fireProof, TintStyle tintStyle, boolean fallingLeaves, GrowthCondition growthCondition, Decoration decoration) {
        super(id, fireProof, colors, stripDrop);
        this.feature = feature;
        this.megaFeature = megaFeature;
        this.style = style;
        this.fruit = fruit;
        this.mutationInfo = mutationInfo;
        this.soil = soil;
        this.tintStyle = tintStyle;
        this.fallingLeaves = fallingLeaves;
        this.growthCondition = growthCondition;
        this.decoration = decoration;
    }

    public static Codec<TreeObject> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").orElse(id).forGetter(TreeObject::getId),
                ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").orElse(TreeRegistrator.NULL_FEATURE).forGetter(TreeObject::getFeature),
                ResourceLocation.CODEC.fieldOf("megaFeature").orElse(null).xmap((value) -> value != null ? ResourceKey.create(Registries.CONFIGURED_FEATURE, value) : TreeRegistrator.NULL_FEATURE, (value) -> value != null ? value.location() : null).forGetter(TreeObject::getMegaFeature),
                Codec.STRING.fieldOf("style").orElse(id.getPath()).forGetter(TreeObject::getStyleName),
                TreeUtil.ITEM_STACK_CODEC.fieldOf("stripDrop").orElse(() -> ItemStack.EMPTY).forGetter(TreeObject::getStripDrop),
                TreeColors.CODEC.fieldOf("colors").orElse(TreeColors.DEFAULT).forGetter(TreeObject::getColors),
                Fruit.CODEC.fieldOf("fruit").orElse(Fruit.DEFAULT).forGetter(TreeObject::getFruit),
                MutationInfo.CODEC.fieldOf("mutation_info").orElse(MutationInfo.DEFAULT).forGetter(TreeObject::getMutationInfo),
                TagKey.hashedCodec(Registries.BLOCK).fieldOf("soil").orElse(ModTags.DIRT_OR_FARMLAND).forGetter(TreeObject::getSoil),
                Codec.BOOL.fieldOf("fireproof").orElse(false).forGetter(TreeObject::isFireProof),
                TintStyle.CODEC.fieldOf("tint").orElse(TintStyle.HIVES).forGetter(TreeObject::getTintStyle),
                Codec.BOOL.fieldOf("fallingLeaves").orElse(false).forGetter(TreeObject::hasFallingLeaves),
                GrowthCondition.CODEC.fieldOf("growthConditions").orElse(GrowthCondition.DEFAULT).forGetter(TreeObject::getGrowthConditions),
                Decoration.CODEC.fieldOf("decoration").orElse(Decoration.DEFAULT).forGetter(TreeObject::getDecoration)
        ).apply(instance, TreeObject::new));
    }

    public ResourceKey<ConfiguredFeature<?, ?>> getFeature() {
        return feature;
    }

    public ResourceKey<ConfiguredFeature<?, ?>> getMegaFeature() {
        return megaFeature;
    }

    public WoodSet getStyle() {
        return WoodSet.STYLES.get(style);
    }

    private String getStyleName() {
        return style;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public boolean hasFruit() {
        return !getFruit().fruitItem().equals(ProductiveTrees.EMPTY_RL);
    }

    public MutationInfo getMutationInfo() {
        return this.mutationInfo;
    }

    public TagKey<Block> getSoil() {
        return soil;
    }

    public boolean canForceGrowth() {
        return growthCondition.canForceGrowth();
    }

    public TintStyle getTintStyle() {
        return tintStyle;
    }

    public boolean tintHives() {
        return tintStyle.equals(TintStyle.FULL) || tintStyle.equals(TintStyle.LEAVES_HIVES) || tintStyle.equals(TintStyle.HIVES);
    }

    public boolean tintFruit() {
        return tintStyle.equals(TintStyle.FULL) || tintStyle.equals(TintStyle.FRUIT) || tintStyle.equals(TintStyle.FRUIT_HIVES);
    }

    public boolean hasFallingLeaves() {
        return fallingLeaves;
    }

    public GrowthCondition getGrowthConditions() {
        return growthCondition;
    }

    public Decoration getDecoration() {
        return decoration;
    }

    public record GrowthCondition(boolean canForceGrowth, int minLight, int maxLight, Fluid fluid, Optional<HolderSet<Biome>> biome)
    {
        private static final GrowthCondition DEFAULT = new GrowthCondition(true, 9, 15, Fluids.EMPTY, null);
        public static Codec<GrowthCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("canForceGrowth").orElse(true).forGetter(GrowthCondition::canForceGrowth),
                ExtraCodecs.POSITIVE_INT.fieldOf("minLight").orElse(9).forGetter(GrowthCondition::minLight),
                ExtraCodecs.POSITIVE_INT.fieldOf("maxLight").orElse(15).forGetter(GrowthCondition::maxLight),
                ForgeRegistries.FLUIDS.getCodec().fieldOf("fluid").orElse(Fluids.EMPTY).forGetter(GrowthCondition::fluid),
                Biome.LIST_CODEC.optionalFieldOf("biome").forGetter(GrowthCondition::biome)
        ).apply(instance, GrowthCondition::new));
    }

    public record Decoration(String vine, Integer lightLevel)
    {
        private static final Decoration DEFAULT = new Decoration("", 0);
        public static Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("vine").orElse("").forGetter(Decoration::vine),
                Codec.INT.fieldOf("lightLevel").orElse(0).forGetter(Decoration::lightLevel)
        ).apply(instance, Decoration::new));
    }

    public record Fruit(String style, ResourceLocation fruitItem, int count, float growthSpeed, String flowerColor, String unripeColor, String ripeColor)
    {
        private static final Fruit DEFAULT = new Fruit("", ProductiveTrees.EMPTY_RL, 1, 1.0F, "", "", "");
        public static Codec<Fruit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("style").orElse("default").forGetter(Fruit::style),
                ResourceLocation.CODEC.fieldOf("item").forGetter(Fruit::fruitItem),
                Codec.INT.fieldOf("count").orElse(1).forGetter(Fruit::count),
                Codec.FLOAT.fieldOf("growthSpeed").orElse(1.0F).forGetter(Fruit::growthSpeed),
                Codec.STRING.fieldOf("flowerColor").orElse("#ffffff").forGetter(Fruit::flowerColor),
                Codec.STRING.fieldOf("unripeColor").orElse("#1aa000").forGetter(Fruit::unripeColor),
                Codec.STRING.fieldOf("ripeColor").orElse("#ff9d00").forGetter(Fruit::ripeColor)
        ).apply(instance, Fruit::new));

        public ItemStack getItem() {
            var item = ForgeRegistries.ITEMS.getValue(fruitItem);
            return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
        }
    }

    public record MutationInfo(ResourceLocation target, float chance)
    {
        private static final MutationInfo DEFAULT = new MutationInfo(ProductiveTrees.EMPTY_RL, 0f);
        public static Codec<MutationInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("target").forGetter(MutationInfo::target),
                Codec.FLOAT.fieldOf("chance").orElse(1.0F).forGetter(MutationInfo::chance)
        ).apply(instance, MutationInfo::new));
    }

    public enum TintStyle implements StringRepresentable
    {
        NONE("none"), LEAVES("leaves"), LEAVES_HIVES("leaves_hives"), FRUIT("fruit"), FRUIT_HIVES("fruit_hives"), HIVES("hives"), FULL("full");

        public static final StringRepresentable.EnumCodec<TintStyle> CODEC = StringRepresentable.fromEnum(TintStyle::values);

        private final String name;

        TintStyle(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}