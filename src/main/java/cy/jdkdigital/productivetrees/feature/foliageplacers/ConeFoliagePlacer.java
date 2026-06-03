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

/** A dense smooth conifer cone: a filled diamond per layer, tapering from a point on top to the full radius at the base (cypress). */
public class ConeFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<ConeFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(0, 64).fieldOf("trunk_height").forGetter((placer) -> placer.trunkHeight)).apply(instance, ConeFoliagePlacer::new);
    });
    private final IntProvider trunkHeight;

    public ConeFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.CONE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos base = pAttachment.pos();
        int max = pFoliageRadius + pAttachment.radiusOffset();
        int total = pOffset + pFoliageHeight;
        for (int l = pOffset; l >= -pFoliageHeight; --l) {
            // a point on top, widening to the full radius at the base
            int fromTop = pOffset - l;
            int radius = total <= 0 ? max : Math.round((float) max * fromTop / total);
            this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base, radius, l, pAttachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.trunkHeight.sample(pRandom);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // diamond (Manhattan) layers
        return Math.abs(pLocalX) + Math.abs(pLocalZ) > pRange;
    }
}
