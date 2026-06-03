package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Smooths a tapering trunk. Where the trunk steps inward — a log column ends but the trunk carries on narrower
 * just above — this fills the dropped-out shoulder with the tree's wood block, so the silhouette eases in
 * instead of all four sides shrinking in one abrupt cliff.
 */
public class TaperDecorator extends TreeDecorator
{
    public static final MapCodec<TaperDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            BlockStateProvider.CODEC.fieldOf("wood_provider").forGetter((decorator) -> decorator.woodProvider),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_chance").forGetter((decorator) -> decorator.placeChance)
    ).apply(instance, TaperDecorator::new));

    private final BlockStateProvider woodProvider;
    private final float placeChance;

    public TaperDecorator(BlockStateProvider woodProvider, float placeChance) {
        this.woodProvider = woodProvider;
        this.placeChance = placeChance;
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return TreeRegistrator.TAPER_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        Set<BlockPos> logs = new HashSet<>(context.logs());
        // per-layer trunk centre, to tell a face middle (axis-aligned from centre) from a corner
        Map<Integer, double[]> centres = new HashMap<>();
        Map<Integer, Integer> counts = new HashMap<>();
        for (BlockPos log : logs) {
            double[] sum = centres.computeIfAbsent(log.getY(), y -> new double[2]);
            sum[0] += log.getX() + 0.5;
            sum[1] += log.getZ() + 0.5;
            counts.merge(log.getY(), 1, Integer::sum);
        }

        for (BlockPos log : context.logs()) {
            BlockPos above = log.above();
            // only at a taper step: this column ends but the trunk continues just above
            if (logs.contains(above)) {
                continue;
            }
            boolean adjacentToTrunk = false;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (logs.contains(above.relative(direction))) {
                    adjacentToTrunk = true;
                    break;
                }
            }
            if (!adjacentToTrunk || !context.isAir(above)) {
                continue;
            }

            double[] centre = centres.get(above.getY());
            int count = counts.getOrDefault(above.getY(), 1);
            double dx = above.getX() + 0.5 - centre[0] / count;
            double dz = above.getZ() + 0.5 - centre[1] / count;
            // ~1 at a face middle, ~0 at a corner
            double faceMiddle = Math.abs(Math.abs(dx) - Math.abs(dz)) / (Math.abs(dx) + Math.abs(dz) + 1.0E-4);

            // fill the dropped shoulder, favouring face middles so the taper eases there rather than at the corners
            if (random.nextFloat() < this.placeChance * (0.4F + 0.6F * faceMiddle)) {
                context.setBlock(above, this.woodProvider.getState(random, above));
            }
        }
    }
}
