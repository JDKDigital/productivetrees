package cy.jdkdigital.productivetrees.feature.foliageplacers;

import com.mojang.serialization.Codec;
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
 * A palm/screwpine frond crown: a small core with strands radiating out, arching up then either drooping
 * ({@code droop}, banana/coconut) or continuing to climb (upright, plantain). Strands connect diagonally
 * (pair with the connected-leaf decay + no trim).
 */
public class FrondFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<FrondFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(
                IntProvider.codec(2, 16).fieldOf("frond_count").forGetter((placer) -> placer.frondCount),
                IntProvider.codec(1, 12).fieldOf("frond_length").forGetter((placer) -> placer.frondLength),
                Codec.BOOL.fieldOf("droop").forGetter((placer) -> placer.droop)
        )).apply(instance, FrondFoliagePlacer::new);
    });
    // eight compass directions the fronds fan out toward
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final IntProvider frondCount;
    private final IntProvider frondLength;
    private final boolean droop;

    public FrondFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider frondCount, IntProvider frondLength, boolean droop) {
        super(radius, offset);
        this.frondCount = frondCount;
        this.frondLength = frondLength;
        this.droop = droop;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.FROND_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos centre = pAttachment.pos().above(pOffset);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
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

        int fronds = this.frondCount.sample(pRandom);
        int start = pRandom.nextInt(FAN.length);
        for (int i = 0; i < fronds; ++i) {
            // spread fronds evenly around the compass
            int[] dir = FAN[(start + (i * FAN.length) / fronds) % FAN.length];
            int length = this.frondLength.sample(pRandom);
            int bx = centre.getX();
            int by = centre.getY();
            int bz = centre.getZ();
            int rise = Math.max(1, length / 3);
            for (int s = 1; s <= length; ++s) {
                bx += dir[0];
                bz += dir[1];
                if (s <= rise) {
                    ++by;                          // arch up out of the crown
                } else if (droop) {
                    if (s % 2 == 0) --by;          // then droop toward the tip
                } else {
                    if (s % 2 == 0) ++by;          // or keep climbing (upright)
                }
                tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, cursor.set(bx, by, bz));
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.radius.sample(pRandom) + 1;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return false;
    }
}
