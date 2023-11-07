package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.WoodSet;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class TreeObject extends WoodObject
{
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;
    private final ResourceKey<ConfiguredFeature<?, ?>> megaFeature;
    private final String style;
    private final boolean registerWood;
    private final ResourceLocation log;
    private final Fruit fruit;
    private final TagKey<Block> soil;
    private final boolean canForceGrowth;
    private final boolean useTinting;
    private final boolean fallingLeaves;
    private final GrowthConditions growthConditions;

    private Supplier<Block> saplingBlock;
    private Supplier<Block> pottedSaplingBlock;
    private Supplier<Block> leafBlock;
    private Supplier<Block> fruitBlock;
    private Supplier<BlockEntityType<BlockEntity>> fruitBlockEntity;

    public TreeObject(ResourceLocation id, ResourceKey<ConfiguredFeature<?, ?>> feature, ResourceKey<ConfiguredFeature<?, ?>> megaFeature, String style, String hiveStyle, boolean registerWood, ResourceLocation log,TreeColors colors, Fruit fruit, TagKey<Block> soil, boolean canForceGrowth, boolean fireProof, boolean useTinting, boolean fallingLeaves, GrowthConditions growthConditions) {
        super(id, fireProof, colors, hiveStyle);
        this.feature = feature;
        this.megaFeature = megaFeature;
        this.style = style;
        this.registerWood = registerWood;
        this.log = log;
        this.fruit = fruit;
        this.soil = soil;
        this.canForceGrowth = canForceGrowth;
        this.useTinting = useTinting;
        this.fallingLeaves = fallingLeaves;
        this.growthConditions = growthConditions;
    }

    public static Codec<TreeObject> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").orElse(id).forGetter(TreeObject::getId),
                ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").orElse(null).forGetter(TreeObject::getFeature),
                ResourceLocation.CODEC.fieldOf("megaFeature").orElse(null).xmap((value) -> value != null ? ResourceKey.create(Registries.CONFIGURED_FEATURE, value) : Features.NULL, (value) -> value != null ? value.location() : null).forGetter(TreeObject::getMegaFeature),
                Codec.STRING.fieldOf("style").orElse("oak").forGetter(TreeObject::getStyleName),
                Codec.STRING.fieldOf("hiveStyle").orElse("oak").forGetter(TreeObject::getHiveStyle),
                Codec.BOOL.fieldOf("registerWood").orElse(true).forGetter(TreeObject::registerWood),
                ResourceLocation.CODEC.fieldOf("log").orElse(new ResourceLocation("oak_log")).forGetter(TreeObject::getLog),
                TreeColors.codec().fieldOf("colors").orElse(TreeColors.DEFAULT).forGetter(TreeObject::getColors),
                Fruit.codec().fieldOf("fruit").orElse(Fruit.DEFAULT).forGetter(TreeObject::getFruit),
                TagKey.hashedCodec(Registries.BLOCK).fieldOf("soil").orElse(Tags.DIRT_OR_FARMLAND).forGetter(TreeObject::getSoil),
                Codec.BOOL.fieldOf("canForceGrowth").orElse(true).forGetter(TreeObject::canForceGrowth),
                Codec.BOOL.fieldOf("fireproof").orElse(false).forGetter(TreeObject::isFireProof),
                Codec.BOOL.fieldOf("useTinting").orElse(true).forGetter(TreeObject::useTinting),
                Codec.BOOL.fieldOf("fallingLeaves").orElse(false).forGetter(TreeObject::hasFallingLeaves),
                GrowthConditions.codec().fieldOf("growthConditions").orElse(GrowthConditions.DEFAULT).forGetter(TreeObject::getGrowthConditions)
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

    public boolean registerWood() {
        return registerWood;
    }

    public ResourceLocation getLog() {
        return log;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public boolean hasFruit() {
        return !getFruit().fruitItem().equals(ProductiveTrees.EMPTY_RL);
    }

    public TagKey<Block> getSoil() {
        return soil;
    }

    public boolean canForceGrowth() {
        return canForceGrowth;
    }

    public boolean useTinting() {
        return useTinting;
    }

    public boolean hasFallingLeaves() {
        return fallingLeaves;
    }

    public GrowthConditions getGrowthConditions() {
        return growthConditions;
    }

    public Supplier<Block> getSaplingBlock() {
        return saplingBlock;
    }

    public void setSaplingBlock(Supplier<Block> saplingBlock) {
        this.saplingBlock = saplingBlock;
    }

    public Supplier<Block> getPottedSaplingBlock() {
        return pottedSaplingBlock;
    }

    public void setPottedSaplingBlock(Supplier<Block> pottedSaplingBlock) {
        this.pottedSaplingBlock = pottedSaplingBlock;
    }

    public Supplier<Block> getLeafBlock() {
        return leafBlock;
    }

    public void setLeafBlock(Supplier<Block> leafBlock) {
        this.leafBlock = leafBlock;
    }

    public Supplier<Block> getLogBlock() {
        if (registerWood) {
            return super.getLogBlock();
        }
        return () -> ForgeRegistries.BLOCKS.getValue(log);
    }

    public Supplier<Block> getFruitBlock() {
        return fruitBlock;
    }

    public Supplier<BlockEntityType<BlockEntity>> getFruitBlockEntity() {
        return fruitBlockEntity;
    }

    public void setFruitBlock(Supplier<Block> fruitBlock, Supplier<BlockEntityType<BlockEntity>> fruitBlockEntity) {
        this.fruitBlock = fruitBlock;
        this.fruitBlockEntity = fruitBlockEntity;
    }

    public record GrowthConditions(int minLight, int maxLight, Fluid fluid, Optional<HolderSet<Biome>> biome)
    {
        private static final GrowthConditions DEFAULT = new GrowthConditions(9, 15, Fluids.EMPTY, null);
        public static Codec<GrowthConditions> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("minLight").orElse(9).forGetter(GrowthConditions::minLight),
                    ExtraCodecs.POSITIVE_INT.fieldOf("maxLight").orElse(15).forGetter(GrowthConditions::maxLight),
                    ForgeRegistries.FLUIDS.getCodec().fieldOf("fluid").orElse(Fluids.EMPTY).forGetter(GrowthConditions::fluid),
                    Biome.LIST_CODEC.optionalFieldOf("biome").forGetter(GrowthConditions::biome)
            ).apply(instance, GrowthConditions::new));
        }
    }

    public record Fruit(String style, ResourceLocation fruitItem, int count, float growthSpeed, String unripeColor, String ripeColor)
    {
        private static final Fruit DEFAULT = new Fruit("", ProductiveTrees.EMPTY_RL, 1, 1.0F, "", "");
        public static Codec<Fruit> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("style").orElse("medium").forGetter(Fruit::style),
                    ResourceLocation.CODEC.fieldOf("item").forGetter(Fruit::fruitItem),
                    Codec.INT.fieldOf("count").orElse(1).forGetter(Fruit::count),
                    Codec.FLOAT.fieldOf("growthSpeed").orElse(1.0F).forGetter(Fruit::growthSpeed),
                    Codec.STRING.fieldOf("unripeColor").orElse("#1aa000").forGetter(Fruit::unripeColor),
                    Codec.STRING.fieldOf("ripeColor").orElse("#ff9d00").forGetter(Fruit::ripeColor)
            ).apply(instance, Fruit::new));
        }

        public ItemStack getItem() {
            var item = ForgeRegistries.ITEMS.getValue(fruitItem);
            return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
        }
    }
}