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

/** A columnar crown of separate leaf sections stacked down from the crown top with bare gaps between them (aspen). */
public class AspenFoliagePlacer extends FoliagePlacer
{
    public static final MapCodec<AspenFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(
                Codec.intRange(1, 12).fieldOf("section_count").forGetter((placer) -> placer.sectionCount),
                Codec.intRange(1, 8).fieldOf("section_height").forGetter((placer) -> placer.sectionHeight),
                Codec.intRange(0, 8).fieldOf("section_gap").forGetter((placer) -> placer.sectionGap)
        )).apply(instance, AspenFoliagePlacer::new);
    });
    private final int sectionCount;
    private final int sectionHeight;
    private final int sectionGap;

    public AspenFoliagePlacer(IntProvider radius, IntProvider offset, int sectionCount, int sectionHeight, int sectionGap) {
        super(radius, offset);
        this.sectionCount = sectionCount;
        this.sectionHeight = sectionHeight;
        this.sectionGap = sectionGap;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistrator.ASPEN_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos base = pAttachment.pos();
        int max = pFoliageRadius + pAttachment.radiusOffset();
        int y = pOffset;
        for (int section = 0; section < this.sectionCount; ++section) {
            for (int row = 0; row < this.sectionHeight; ++row) {
                // narrow the section's end rows so each reads as a rounded tuft
                int radius = (row == 0 || row == this.sectionHeight - 1) ? Math.max(1, max - 1) : max;
                this.placeLeavesRow(pLevel, pBlockSetter, pRandom, pConfig, base, radius, y, pAttachment.doubleTrunk());
                --y;
            }
            y -= this.sectionGap; // bare gap before the next section
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return this.sectionCount * this.sectionHeight + (this.sectionCount - 1) * this.sectionGap;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        // round each row into a disk
        return (pLocalX * pLocalX + pLocalZ * pLocalZ) > pRange * pRange;
    }
}
