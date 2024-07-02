package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.util.WoodSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Supplier;

public class WoodObject
{
    private final ResourceLocation id;
    private final boolean fireProof;
    private final TreeObject.TreeColors colors;
    private final Optional<ResourceLocation> stripDrop;

    public WoodObject(ResourceLocation id, boolean fireProof, TreeColors colors, Optional<ResourceLocation> stripDrop) {
        this.id = id;
        this.fireProof = fireProof;
        this.colors = colors;
        this.stripDrop = stripDrop;
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

    public Optional<ResourceLocation> getStripDrop() {
        return stripDrop;
    }

    public ItemStack getStripDropStack() {
        return new ItemStack(BuiltInRegistries.ITEM.get(stripDrop.get()));
    }

    public record TreeColors(String leafColor, String logColor, String plankColor)
    {
        static final TreeColors DEFAULT = new TreeColors("#000000", "#000000", "#000000");
        public static Codec<TreeColors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("leafColor").orElse("#1d7b00").forGetter(TreeColors::leafColor),
                Codec.STRING.fieldOf("logColor").orElse("#917142").forGetter(TreeColors::logColor),
                Codec.STRING.fieldOf("plankColor").orElse("#c29d62").forGetter(TreeColors::plankColor)
        ).apply(instance, TreeColors::new));
    }
}