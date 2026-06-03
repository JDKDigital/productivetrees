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
 * A dense, tall plume of leaves: rows ease up to a point on top then hold at full radius for a solid body,
 * with every other full row recessed so it reads as tiered layers rather than a smooth cylinder. Suits
 * columnar and conical dense crowns (ginkgo, aspen, clove, juniper, poplar, cinnamon, cypress, holly).
 */
public class PlumeFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<PlumeFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(0, 64).fieldOf("trunk_height").forGetter((placer) -> placer.trunkHeight)).apply(instance, PlumeFoliagePlacer::new);
    });
    private final IntProvider trunkHeight;

    public PlumeFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.PLUME_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos blockpos = pAttachment.pos();
        int max = pFoliageRadius + pAttachment.radiusOffset();
        // place rows top-down: the radius eases up (~half a block per row) to a point on top, then holds at max for a dense body
        for (int i = 0; i <= pFoliageHeight; ++i) {
            int radius = Math.min(max, (i + 1) / 2);
            // recess every other full-width row so the dense body shows tiered layers instead of a smooth cylinder
            if (radius == max && i % 2 == 1) {
                radius = Math.max(0, radius - 1);
            }
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, blockpos, radius, pOffset - i, pAttachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.trunkHeight.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // round each layer into a tight disk (corners trimmed) so it reads rounded rather than square
        return (pLocalX * pLocalX + pLocalZ * pLocalZ) > pRange * pRange;
    }
}
