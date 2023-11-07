package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.util.WoodSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class WoodObject implements TreeWoodProvider
{
    private final ResourceLocation id;
    private final boolean fireProof;
    private final TreeObject.TreeColors colors;
    private final String hiveStyle;

    private Supplier<Block> logBlock;
    private Supplier<Block> strippedLogBlock;
    private Supplier<Block> woodBlock;
    private Supplier<Block> strippedWoodBlock;
    private Supplier<Block> plankBlock;
    private Supplier<Block> stairsBlock;
    private Supplier<Block> slabBlock;
    private Supplier<Block> fenceBlock;
    private Supplier<Block> fenceGateBlock;
    private Supplier<Block> pressurePlate;
    private Supplier<Block> buttonBlock;
    private Supplier<Block> hiveBlock;
    private Supplier<Block> expansionBoxBlock;

    public WoodObject(ResourceLocation id, boolean fireProof, TreeColors colors, String hiveStyle) {
        this.id = id;
        this.fireProof = fireProof;
        this.colors = colors;
        this.hiveStyle = hiveStyle;
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean isFireProof() {
        return fireProof;
    }

    public WoodSet getStyle() {
        return WoodSet.STYLES.get(id.getPath());
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

    public String getHiveStyle() {
        return hiveStyle;
    }

    public Supplier<Block> getLogBlock() {
        return logBlock;
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

    public record TreeColors(String leafColor, String logColor, String plankColor)
    {
        static final TreeColors DEFAULT = new TreeColors("#000000", "#000000", "#000000");
        public static Codec<TreeColors> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("leafColor").orElse("#1d7b00").forGetter(TreeColors::leafColor),
                    Codec.STRING.fieldOf("logColor").orElse("#917142").forGetter(TreeColors::logColor),
                    Codec.STRING.fieldOf("plankColor").orElse("#c29d62").forGetter(TreeColors::plankColor)
            ).apply(instance, TreeColors::new));
        }
    }
}