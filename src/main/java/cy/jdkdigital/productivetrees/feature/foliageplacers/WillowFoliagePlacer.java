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

public class WillowFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<WillowFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(
                Codec.intRange(0, 16).fieldOf("height").forGetter((placer) -> placer.height),
                IntProvider.codec(0, 16).fieldOf("droop").forGetter((placer) -> placer.droop)
        )).apply(instance, WillowFoliagePlacer::new);
    });
    private final int height;
    private final IntProvider droop;

    public WillowFoliagePlacer(IntProvider radius, IntProvider offset, int height, IntProvider droop) {
        super(radius, offset);
        this.height = height;
        this.droop = droop;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.WILLOW_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos base = pAttachment.pos();
        int max = pFoliageRadius + pAttachment.radiusOffset();
        // a compact 3-row clump at the branch tip (not a dome); the trunk's many branches spread these into one broad weeping canopy
        this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base, max, pOffset, pAttachment.doubleTrunk());
        this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base, Math.max(1, max - 1), pOffset + 1, pAttachment.doubleTrunk());
        this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base, Math.max(1, max - 1), pOffset - 1, pAttachment.doubleTrunk());
        // hang strands from the underside, densest at the rim, thinning toward the centre
        for (int dx = -max; dx <= max; ++dx) {
            for (int dz = -max; dz <= max; ++dz) {
                if (dx * dx + dz * dz > max * max) {
                    continue;
                }
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                float dripChance = ring >= max ? 0.7F : (ring >= max - 1 ? 0.3F : 0.1F);
                if (pRandom.nextFloat() >= dripChance) {
                    continue;
                }
                int length = this.droop.sample(pRandom);
                for (int k = 1; k <= length; ++k) {
                    if (k > length - 2 && pRandom.nextInt(3) == 0) { // ragged tip
                        break;
                    }
                    this.tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base.offset(dx, pOffset - k, dz));
                }
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // round the canopy rows into a disk
        return pLocalX * pLocalX + pLocalZ * pLocalZ > (pRange + 1) * (pRange + 1);
    }
}
