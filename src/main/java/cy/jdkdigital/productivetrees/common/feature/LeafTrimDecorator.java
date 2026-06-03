package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeafTrimDecorator extends TreeDecorator
{
    public static final MapCodec<LeafTrimDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.intRange(1, 7).optionalFieldOf("max_distance", 6).forGetter((decorator) -> decorator.maxDistance)
    ).apply(instance, LeafTrimDecorator::new));

    private final int maxDistance;

    public LeafTrimDecorator(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return TreeRegistrator.LEAF_TRIM.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        List<BlockPos> logs = context.logs();
        if (logs.isEmpty()) {
            return;
        }
        Set<BlockPos> leaves = new HashSet<>(context.leaves());
        // flood out from the logs through connected leaves, exactly like vanilla leaf-decay distance, up to maxDistance
        Set<BlockPos> reached = new HashSet<>();
        Set<BlockPos> frontier = new HashSet<>(logs);
        for (int d = 1; d <= this.maxDistance; ++d) {
            Set<BlockPos> next = new HashSet<>();
            for (BlockPos p : frontier) {
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = p.relative(direction);
                    if (leaves.contains(neighbor) && reached.add(neighbor)) {
                        next.add(neighbor);
                    }
                }
            }
            if (next.isEmpty()) {
                break;
            }
            frontier = next;
        }
        // any leaf the flood never reached would decay after growth, so drop it now
        for (BlockPos leaf : leaves) {
            if (!reached.contains(leaf)) {
                context.setBlock(leaf, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
