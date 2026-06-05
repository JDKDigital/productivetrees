package cy.jdkdigital.productivetrees.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.util.WoodSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class WoodObject
{
    private final Identifier id;
    private final boolean fireProof;
    private final TreeObject.TreeColors colors;
    private final Optional<Identifier> stripDrop;

    public WoodObject(Identifier id, boolean fireProof, TreeColors colors, Optional<Identifier> stripDrop) {
        this.id = id;
        this.fireProof = fireProof;
        this.colors = colors;
        this.stripDrop = stripDrop;
    }

    public Identifier getId() {
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

    public Optional<Identifier> getStripDrop() {
        return stripDrop;
    }

    public ItemStack getStripDropStack() {
        return new ItemStack(BuiltInRegistries.ITEM.get(stripDrop.get()).map(Holder::value).orElse(Items.AIR));
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