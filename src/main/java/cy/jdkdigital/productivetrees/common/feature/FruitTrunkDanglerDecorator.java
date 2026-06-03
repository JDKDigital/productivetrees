package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/** Hangs fruit beneath leaves within {@code reach} blocks of the trunk, so it bunches near the bole rather than on the outer fronds. */
public class FruitTrunkDanglerDecorator extends TreeDecorator
{
    public static final MapCodec<FruitTrunkDanglerDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.FLOAT.fieldOf("density").orElse(0.5f).forGetter((decorator) -> decorator.density),
            Codec.INT.fieldOf("max_fruits").orElse(100).forGetter((decorator) -> decorator.maxFruits),
            Codec.INT.fieldOf("reach").orElse(1).forGetter((decorator) -> decorator.reach),
            BlockStateProvider.CODEC.fieldOf("fruit_provider").forGetter((decorator) -> decorator.fruitProvider)
    ).apply(instance, FruitTrunkDanglerDecorator::new));

    private final float density;
    private final int maxFruits;
    private final int reach;
    private final BlockStateProvider fruitProvider;

    public FruitTrunkDanglerDecorator(float density, int maxFruits, int reach, BlockStateProvider fruitProvider) {
        this.density = density;
        this.maxFruits = maxFruits;
        this.reach = reach;
        this.fruitProvider = fruitProvider;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistrator.FRUIT_TRUNK_DANGLER.get();
    }

    @Override
    public void place(Context context) {
        if (context.leaves().isEmpty() || context.logs().isEmpty()) {
            return;
        }
        // the trunk's (x,z) columns
        Set<Long> logColumns = new HashSet<>();
        for (BlockPos log : context.logs()) {
            logColumns.add(BlockPos.asLong(log.getX(), 0, log.getZ()));
        }

        AtomicInteger count = new AtomicInteger();
        var rand = context.random();
        context.leaves().forEach(leaf -> {
            if (count.get() >= maxFruits || !context.isAir(leaf.below()) || !nearTrunk(logColumns, leaf)) {
                return;
            }
            if (rand.nextFloat() < density) {
                context.setBlock(leaf.below(), fruitProvider.getState(rand, leaf.below()));
                count.getAndIncrement();
            }
        });
    }

    private boolean nearTrunk(Set<Long> logColumns, BlockPos leaf) {
        for (int dx = -reach; dx <= reach; ++dx) {
            for (int dz = -reach; dz <= reach; ++dz) {
                if (logColumns.contains(BlockPos.asLong(leaf.getX() + dx, 0, leaf.getZ() + dz))) {
                    return true;
                }
            }
        }
        return false;
    }
}
