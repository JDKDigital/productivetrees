package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
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

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class TreeObject
{
    private final ResourceLocation id;
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;
    private final ResourceKey<ConfiguredFeature<?, ?>> megaFeature;
    private final String style;
    private final String hiveStyle;
    private final Boolean registerWood;
    private final ResourceLocation log;
    private final TreeColors colors;
    private final Fruit fruit;
    private final TagKey<Block> soil;
    private final Boolean canForceGrowth;
    private final Boolean fireProof;
    private final Boolean useTinting;
    private final Boolean fallingLeaves;
    private final GrowthConditions growthConditions;

    private Supplier<Block> saplingBlock;
    private Supplier<Block> pottedSaplingBlock;
    private Supplier<Block> leafBlock;
    private Supplier<Block> logBlock;
    private Supplier<Block> strippedLogBlock;
    private Supplier<Block> woodBlock;
    private Supplier<Block> strippedWoodBlock;
    private Supplier<Block> plankBlock;
    private Supplier<Block> fruitBlock;
    private Supplier<BlockEntityType<BlockEntity>> fruitBlockEntity;
    private Supplier<Block> stairsBlock;
    private Supplier<Block> slabBlock;
    private Supplier<Block> fenceBlock;
    private Supplier<Block> fenceGateBlock;
    private Supplier<Block> pressurePlate;
    private Supplier<Block> buttonBlock;
    private Supplier<Block> hiveBlock;
    private Supplier<Block> expansionBoxBlock;

    public TreeObject(ResourceLocation id, ResourceKey<ConfiguredFeature<?, ?>> feature, ResourceKey<ConfiguredFeature<?, ?>> megaFeature, String style, String hiveStyle, Boolean registerWood, ResourceLocation log,TreeColors colors, Fruit fruit, TagKey<Block> soil, Boolean canForceGrowth, Boolean fireProof, Boolean useTinting, Boolean fallingLeaves, GrowthConditions growthConditions) {
        this.id = id;
        this.feature = feature;
        this.megaFeature = megaFeature;
        this.style = style;
        this.hiveStyle = hiveStyle;
        this.registerWood = registerWood;
        this.log = log;
        this.colors = colors;
        this.fruit = fruit;
        this.soil = soil;
        this.canForceGrowth = canForceGrowth;
        this.fireProof = fireProof;
        this.useTinting = useTinting;
        this.fallingLeaves = fallingLeaves;
        this.growthConditions = growthConditions;
    }

    public static Codec<TreeObject> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").orElse(id).forGetter(TreeObject::getId),
                ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").orElse(null).forGetter(TreeObject::getFeature),
                ResourceLocation.CODEC.fieldOf("megaFeature").orElse(null).xmap((value) -> value != null ? ResourceKey.create(Registries.CONFIGURED_FEATURE, value) : Features.NULL, (value) -> value != null ? value.location() : null).forGetter(TreeObject::getMegaFeature),
                Codec.STRING.fieldOf("style").orElse("oak").forGetter(TreeObject::getStyle),
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

    public ResourceLocation getId() {
        return id;
    }

    public ResourceKey<ConfiguredFeature<?, ?>> getFeature() {
        return feature;
    }

    public ResourceKey<ConfiguredFeature<?, ?>> getMegaFeature() {
        return megaFeature;
    }

    public String getStyle() {
        return style;
    }

    public String getHiveStyle() {
        return hiveStyle;
    }

    public boolean registerWood() {
        return registerWood;
    }

    public ResourceLocation getLog() {
        return log;
    }

    public TreeColors getColors() {
        return colors;
    }

    public String getLeafColor() {
        return colors.leafColor;
    }

    public String getLogColor() {
        return colors.logColor;
    }

    public String getPlankColor() {
        return colors.plankColor;
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

    public Boolean canForceGrowth() {
        return canForceGrowth;
    }

    public Boolean isFireProof() {
        return fireProof;
    }

    public Boolean useTinting() {
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
            return logBlock;
        }
        return () -> ForgeRegistries.BLOCKS.getValue(log);
    }

    public void setLogBlock(Supplier<Block> logBlock) {
        this.logBlock = logBlock;
    }

    public Supplier<Block> getStrippedLogBlock() {
        return strippedLogBlock;
    }

    public void setStrippedLogBlock(Supplier<Block> strippedLogBlock) {
        this.strippedLogBlock = strippedLogBlock;
    }

    public Supplier<Block> getWoodBlock() {
        return woodBlock;
    }

    public void setWoodBlock(Supplier<Block> woodBlock) {
        this.woodBlock = woodBlock;
    }

    public Supplier<Block> getStrippedWoodBlock() {
        return strippedWoodBlock;
    }

    public void setStrippedWoodBlock(Supplier<Block> strippedWoodBlock) {
        this.strippedWoodBlock = strippedWoodBlock;
    }

    public Supplier<Block> getPlankBlock() {
        return plankBlock;
    }

    public void setPlankBlock(Supplier<Block> plankBlock) {
        this.plankBlock = plankBlock;
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

    public Supplier<Block> getStairsBlock() {
        return stairsBlock;
    }

    public void setStairsBlock(Supplier<Block> stairsBlock) {
        this.stairsBlock = stairsBlock;
    }

    public Supplier<Block> getSlabBlock() {
        return slabBlock;
    }

    public void setSlabBlock(Supplier<Block> slabBlock) {
        this.slabBlock = slabBlock;
    }

    public Supplier<Block> getFenceBlock() {
        return fenceBlock;
    }

    public void setFenceBlock(Supplier<Block> fenceBlock) {
        this.fenceBlock = fenceBlock;
    }

    public Supplier<Block> getFenceGateBlock() {
        return fenceGateBlock;
    }

    public void setFenceGateBlock(Supplier<Block> fenceGateBlock) {
        this.fenceGateBlock = fenceGateBlock;
    }

    public void setPressurePlateBlock(Supplier<Block> pressurePlate) {
        this.pressurePlate = pressurePlate;
    }

    public Supplier<Block> getPressurePlateBlock() {
        return pressurePlate;
    }

    public Supplier<Block> getButtonBlock() {
        return buttonBlock;
    }

    public void setButtonBlock(Supplier<Block> buttonBlock) {
        this.buttonBlock = buttonBlock;
    }

    public Supplier<Block> getHiveBlock() {
        return hiveBlock;
    }

    public void setHiveBlock(Supplier<Block> hiveBlock) {
        this.hiveBlock = hiveBlock;
    }

    public Supplier<Block> getExpansionBoxBlock() {
        return expansionBoxBlock;
    }

    public void setExpansionBoxBlock(Supplier<Block> expansionBoxBlock) {
        this.expansionBoxBlock = expansionBoxBlock;
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

    public record TreeColors(String leafColor, String logColor, String plankColor)
    {
        private static final TreeColors DEFAULT = new TreeColors("", "", "");
        public static Codec<TreeColors> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("leafColor").orElse("#1d7b00").forGetter(TreeColors::leafColor),
                    Codec.STRING.fieldOf("logColor").orElse("#917142").forGetter(TreeColors::logColor),
                    Codec.STRING.fieldOf("plankColor").orElse("#c29d62").forGetter(TreeColors::plankColor)
            ).apply(instance, TreeColors::new));
        }
    }
}