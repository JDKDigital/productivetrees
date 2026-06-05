package cy.jdkdigital.productivetrees.client.color;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Constant tree tint for a single tint slot of a tree item model (leaf, log, or ripe-fruit colour).
 * The colour is resolved at datagen time from the tree definition and referenced from the
 * per-item-model {@code tints} list.
 */
public record TreeTintSource(int color) implements ItemTintSource
{
    public static final MapCodec<TreeTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(TreeTintSource::color)
    ).apply(i, TreeTintSource::new));

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return 0xFF000000 | color;
    }

    @Override
    public MapCodec<TreeTintSource> type() {
        return MAP_CODEC;
    }
}
