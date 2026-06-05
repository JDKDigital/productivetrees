package cy.jdkdigital.productivetrees.client.color;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public record PollenTintSource() implements ItemTintSource
{
    public static final MapCodec<PollenTintSource> MAP_CODEC = MapCodec.unit(PollenTintSource::new);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (stack.has(TreeRegistrator.POLLEN_BLOCK_COMPONENT)) {
            Identifier id = stack.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT);
            Block leaf = BuiltInRegistries.BLOCK.get(id).map(Holder::value).orElse(Blocks.AIR);
            return 0xFF000000 | TreeUtil.getLeafColor(leaf);
        }
        return 0xFF000000 | FoliageColor.FOLIAGE_DEFAULT;
    }

    @Override
    public MapCodec<PollenTintSource> type() {
        return MAP_CODEC;
    }
}
