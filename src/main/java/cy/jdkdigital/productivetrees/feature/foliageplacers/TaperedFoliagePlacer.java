package cy.jdkdigital.productivetrees.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class TaperedFoliagePlacer extends BlobFoliagePlacer
{
    public static final Codec<TaperedFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return blobParts(instance).apply(instance, TaperedFoliagePlacer::new);
    });

    public TaperedFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.TAPERED_FOLIAGE_PLACER.get();
    }

    protected void createFoliage(LevelSimulatedReader level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource randomSource, TreeConfiguration treeConfiguration, int pMaxFreeTreeHeight, FoliagePlacer.FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        ProductiveTrees.LOGGER.info("pos " + pAttachment.pos());
        for(int i = pOffset; i >= pOffset - pFoliageHeight; --i) {
            int j = pFoliageRadius + pAttachment.radiusOffset() - 1 - i;
            this.placeLeavesRow(level, foliageSetter, randomSource, treeConfiguration, pAttachment.pos(), j, i, pAttachment.doubleTrunk());
        }
    }

    protected boolean shouldSkipLocation(RandomSource randomSource, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return pLocalX == pRange && pLocalZ == pRange && randomSource.nextInt(2) == 0;
    }
}
