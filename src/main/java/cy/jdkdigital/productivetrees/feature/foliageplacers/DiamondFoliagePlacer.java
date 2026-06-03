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
 * A vertically symmetric diamond (bipyramid) of leaves: widest at the centre, tapering linearly to a point at
 * both the top and the bottom, so the top half mirrors the bottom. Each layer is a rounded disk.
 */
public class DiamondFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<DiamondFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            foliagePlacerParts(instance).apply(instance, DiamondFoliagePlacer::new));

    public DiamondFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.DIAMOND_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos blockpos = pAttachment.pos();
        for (int dy = -pFoliageRadius; dy <= pFoliageRadius; ++dy) {
            // radius shrinks a block per layer away from the centre, the same above and below
            int rowRadius = pFoliageRadius - Math.abs(dy);
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, blockpos, rowRadius, pOffset + dy, pAttachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.radius.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // round each layer into a disk so the diamond reads smooth rather than square
        return pLocalX * pLocalX + pLocalZ * pLocalZ > pRange * pRange;
    }
}
