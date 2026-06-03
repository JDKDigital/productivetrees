package cy.jdkdigital.productivetrees.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class RoughFoliagePlacer extends BlobFoliagePlacer
{
    public static final MapCodec<RoughFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return blobParts(instance).apply(instance, RoughFoliagePlacer::new);
    });

    public RoughFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.ROUGH_FOLIAGE_PLACER.get();
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomSource, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // drop the corners, then chew up the outer rings so each clump reads as ragged foliage on a branch rather than a smooth ball
        if (Math.abs(pLocalX) == pRange && Math.abs(pLocalZ) == pRange) {
            return true;
        }
        int edge = Math.max(Math.abs(pLocalX), Math.abs(pLocalZ));
        if (edge >= pRange) {
            return randomSource.nextInt(2) == 0;
        }
        if (edge == pRange - 1) {
            return randomSource.nextInt(4) == 0;
        }
        return false;
    }
}
