package cy.jdkdigital.productivetrees.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * A sunburst of leaves: a small dense core with straight rays shooting out from it in assorted 3D directions
 * (cardinal and diagonal), each a line of leaves. Reads as a shining sun. The diagonal rays connect leaves
 * cornerwise, so trees using this should be in the diagonal-leaf-decay set and skip the orthogonal leaf trim.
 */
public class RayFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<RayFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(
                IntProvider.codec(2, 16).fieldOf("ray_count").forGetter((placer) -> placer.rayCount),
                IntProvider.codec(1, 8).fieldOf("ray_length").forGetter((placer) -> placer.rayLength)
        )).apply(instance, RayFoliagePlacer::new);
    });
    // the 26 surrounding directions a ray can shoot toward
    private static final int[][] DIRS;
    static {
        int[][] d = new int[26][];
        int i = 0;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        d[i++] = new int[]{dx, dy, dz};
                    }
                }
            }
        }
        DIRS = d;
    }
    private final IntProvider rayCount;
    private final IntProvider rayLength;

    public RayFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider rayCount, IntProvider rayLength) {
        super(radius, offset);
        this.rayCount = rayCount;
        this.rayLength = rayLength;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.RAY_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos centre = pAttachment.pos().above(pOffset);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        // dense core
        int core = pFoliageRadius;
        for (int dx = -core; dx <= core; ++dx) {
            for (int dy = -core; dy <= core; ++dy) {
                for (int dz = -core; dz <= core; ++dz) {
                    if (dx * dx + dy * dy + dz * dz <= core * core + core) {
                        tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, cursor.set(centre.getX() + dx, centre.getY() + dy, centre.getZ() + dz));
                    }
                }
            }
        }
        // rays shooting out in random directions
        int rays = this.rayCount.sample(pRandom);
        int start = pRandom.nextInt(DIRS.length);
        for (int i = 0; i < rays; ++i) {
            int[] dir = DIRS[(start + i * 7) % DIRS.length];
            int length = core + this.rayLength.sample(pRandom);
            for (int s = core + 1; s <= length; ++s) {
                tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, cursor.set(centre.getX() + dir[0] * s, centre.getY() + dir[1] * s, centre.getZ() + dir[2] * s));
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.radius.sample(pRandom) + this.rayLength.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return false;
    }
}
