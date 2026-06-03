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
 * A flame-shaped crown: a sharp point at the top widening into a bulging body lower down and tucking back in
 * at the base, with the rows drifting and the rim randomly thinned so the edges lick like fire. Built from the
 * top down off the attachment so the tip sits above the trunk.
 */
public class FlameFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<FlameFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(1, 32).fieldOf("flame_height").forGetter((placer) -> placer.flameHeight)).apply(instance, FlameFoliagePlacer::new);
    });
    private final IntProvider flameHeight;

    public FlameFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider flameHeight) {
        super(radius, offset);
        this.flameHeight = flameHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.FLAME_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos base = pAttachment.pos();
        int driftX = 0;
        int driftZ = 0;
        for (int i = 0; i <= pFoliageHeight; ++i) {
            float t = (float) i / (float) pFoliageHeight;
            // teardrop profile: a sharp tip at the top, widening to full radius, then tucking back in toward the base
            float s;
            if (t < 0.55F) {
                s = Math.max(0.0F, (t - 0.05F) / 0.5F);
            } else {
                s = 1.0F - (t - 0.55F) / 0.45F * 0.5F;
            }
            int radius = Math.round(pFoliageRadius * s);
            // flicker: occasionally pull a row in by a block so the body isn't a smooth lobe
            if (pRandom.nextInt(4) == 0) {
                radius = Math.max(0, radius - 1);
            }
            // lick: let the centre wander a block at a time, bounded, so the flame leans and waves
            if (pRandom.nextInt(3) == 0) {
                driftX = Math.max(-1, Math.min(1, driftX + pRandom.nextInt(3) - 1));
            }
            if (pRandom.nextInt(3) == 0) {
                driftZ = Math.max(-1, Math.min(1, driftZ + pRandom.nextInt(3) - 1));
            }
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base.offset(driftX, 0, driftZ), radius, pOffset - i, pAttachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.flameHeight.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        if (pRange <= 0) {
            return false;
        }
        int d = pLocalX * pLocalX + pLocalZ * pLocalZ;
        // round each row to a disk, then randomly thin the outer rim so the edges read as ragged flames
        if (d > pRange * pRange) {
            return true;
        }
        return d >= (pRange - 1) * (pRange - 1) && pRandom.nextInt(3) == 0;
    }
}
