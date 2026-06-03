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
 * A clean uniform sphere of leaves centred on the attachment — a lollipop ball on top of a straight trunk.
 * Each row's radius follows the circle equation so the result is round on every axis, not a squared blob.
 */
public class BallFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<BallFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            foliagePlacerParts(instance).apply(instance, BallFoliagePlacer::new));

    public BallFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.BALL_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos blockpos = pAttachment.pos();
        for (int dy = -pFoliageRadius; dy <= pFoliageRadius; ++dy) {
            int rowRadius = (int) Math.round(Math.sqrt((double) (pFoliageRadius * pFoliageRadius - dy * dy)));
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, blockpos, rowRadius, pOffset + dy, pAttachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.radius.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // round each row into a disk so the stack of rows reads as a smooth ball
        return pLocalX * pLocalX + pLocalZ * pLocalZ > pRange * pRange;
    }
}
