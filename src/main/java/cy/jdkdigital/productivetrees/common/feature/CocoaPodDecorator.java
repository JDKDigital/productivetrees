package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/** Hangs cocoa pods on the lower-trunk sides — no per-tree skip (unlike vanilla), {@code height} sets how far up the bole they climb. */
public class CocoaPodDecorator extends TreeDecorator
{
    public static final MapCodec<CocoaPodDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance").orElse(0.5f).forGetter((decorator) -> decorator.chance),
            Codec.intRange(1, 16).fieldOf("height").orElse(3).forGetter((decorator) -> decorator.height)
    ).apply(instance, CocoaPodDecorator::new));

    private final float chance;
    private final int height;

    public CocoaPodDecorator(float chance, int height) {
        this.chance = chance;
        this.height = height;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistrator.COCOA_POD.get();
    }

    @Override
    public void place(Context context) {
        if (context.logs().isEmpty()) {
            return;
        }
        RandomSource random = context.random();
        int baseY = context.logs().stream().mapToInt(BlockPos::getY).min().orElse(0);
        context.logs().forEach(log -> {
            if (log.getY() - baseY >= this.height) { // only the lower bole bears pods
                return;
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos at = log.relative(direction);
                if (context.isAir(at) && random.nextFloat() < this.chance) {
                    // FACING points away from the trunk
                    context.setBlock(at, Blocks.COCOA.defaultBlockState()
                            .setValue(CocoaBlock.AGE, random.nextInt(3))
                            .setValue(CocoaBlock.FACING, direction));
                }
            }
        });
    }
}
