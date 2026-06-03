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

public class ConiferFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<ConiferFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(0, 64).fieldOf("trunk_height").forGetter((placer) -> placer.trunkHeight)).apply(instance, ConiferFoliagePlacer::new);
    });
    private final IntProvider trunkHeight;

    public ConiferFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.CONIFER_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos blockpos = pAttachment.pos();
        int i = pRandom.nextInt(2);
        int j = 1;
        int k = 0;
        for (int l = pOffset; l >= -pFoliageHeight; --l) {
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, blockpos, i, l, pAttachment.doubleTrunk());
            if (i >= j) {
                i = k;
                k = 1;
                j = Math.min(j + 1, pFoliageRadius + pAttachment.radiusOffset());
            } else {
                ++i;
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.trunkHeight.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        if (pRange <= 0) {
            return false;
        }
        // drop the corners and randomly thin the outer ring so the cone reads as irregular needled branches, not smooth tiers
        if (Math.abs(pLocalX) == pRange && Math.abs(pLocalZ) == pRange) {
            return true;
        }
        return Math.max(Math.abs(pLocalX), Math.abs(pLocalZ)) >= pRange && pRandom.nextInt(2) == 0;
    }
}
