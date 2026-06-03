package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RootDecorator extends TreeDecorator
{
    public static final MapCodec<RootDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            BlockStateProvider.CODEC.fieldOf("root_provider").forGetter((decorator) -> decorator.rootProvider),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_chance").forGetter((decorator) -> decorator.placeChance),
            IntProvider.codec(1, 8).fieldOf("length").forGetter((decorator) -> decorator.length)
    ).apply(instance, RootDecorator::new));

    private final BlockStateProvider rootProvider;
    private final float placeChance;
    private final IntProvider length;

    public RootDecorator(BlockStateProvider rootProvider, float placeChance, IntProvider length) {
        this.rootProvider = rootProvider;
        this.placeChance = placeChance;
        this.length = length;
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return TreeRegistrator.ROOT_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        List<BlockPos> logs = context.logs();
        if (logs.isEmpty()) {
            return;
        }
        int baseY = logs.stream().mapToInt(BlockPos::getY).min().getAsInt();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (BlockPos log : logs) {
            if (log.getY() != baseY) {
                continue;
            }
            // flare a root spur out from the trunk base in each direction, hugging the ground until it runs into something
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() >= this.placeChance) {
                    continue;
                }
                int len = this.length.sample(random);
                mutable.set(log);
                for (int i = 0; i < len; ++i) {
                    mutable.move(direction);
                    if (!context.isAir(mutable)) {
                        break;
                    }
                    context.setBlock(mutable, this.rootProvider.getState(random, mutable));
                    if (random.nextInt(2) == 0) {
                        mutable.move(Direction.DOWN);
                    }
                }
            }
        }
    }
}
