package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A towering rainforest emergent (kapok / Ceiba). A wide buttressed base — a Manhattan diamond cross-section —
 * holds for the lower part of the trunk, steps in to a square mid-section, then runs up as a slender column.
 * At the very top several long, nearly horizontal arms radiate out across two tiers, each ending in a foliage
 * cluster, so the crown reads as the flat, wide umbrella held high above a bare trunk rather than a cone.
 */
public class EmergentTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<EmergentTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.intRange(1, 4).fieldOf("radius").forGetter((placer) -> placer.radius),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("wide_fraction", 0.45F).forGetter((placer) -> placer.wideFraction),
                IntProvider.codec(2, 10).fieldOf("arm_count").forGetter((placer) -> placer.armCount),
                IntProvider.codec(1, 16).fieldOf("arm_length").forGetter((placer) -> placer.armLength)
        )).apply(instance, EmergentTrunkPlacer::new);
    });
    // eight compass directions the crown arms fan out toward
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final int radius;
    private final float wideFraction;
    private final IntProvider armCount;
    private final IntProvider armLength;

    public EmergentTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int radius, float wideFraction, IntProvider armCount, IntProvider armLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.radius = radius;
        this.wideFraction = wideFraction;
        this.armCount = armCount;
        this.armLength = armLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.EMERGENT_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        // diamond buttress below, then a square mid-section; keep the square high so the single-log top spike is short
        int diamondEnd = Math.round(pFreeTreeHeight * this.wideFraction);
        int columnStart = Math.min(pFreeTreeHeight - 1, Math.round(pFreeTreeHeight * (this.wideFraction + 0.45F)));

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            int r;
            boolean diamond = false;
            boolean cross = false;
            if (h < diamondEnd) {
                r = this.radius;
                diamond = true;
            } else if (h < columnStart) {
                r = Math.max(0, this.radius - 1); // square (3x3 at radius 2)
            } else if (h < columnStart + 2) {
                r = Math.max(0, this.radius - 1); // plus tier: eases the square down to the single-log column
                cross = true;
            } else {
                r = 0;
            }
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    // diamond clips to a Manhattan radius (buttress flares as a diamond)
                    if (diamond && Math.abs(dx) + Math.abs(dz) > r) {
                        continue;
                    }
                    // plus keeps only the axis arms
                    if (cross && dx != 0 && dz != 0) {
                        continue;
                    }
                    if (h == 0) {
                        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }
        }

        // crown: long near-horizontal arms radiating out across two tiers so the canopy spreads flat and wide
        int arms = this.armCount.sample(pRandom);
        int startFan = pRandom.nextInt(FAN.length);
        for (int i = 0; i < arms; ++i) {
            int[] dir = FAN[(startFan + i * Math.max(1, FAN.length / Math.max(1, arms))) % FAN.length];
            int length = this.armLength.sample(pRandom);
            // alternate arms between the top and one tier down to give the umbrella some depth
            int tierDrop = (i % 2 == 0) ? 0 : 2;
            int bx = pPos.getX();
            int by = pPos.getY() + pFreeTreeHeight - 1 - tierDrop;
            int bz = pPos.getZ();
            for (int step = 1; step <= length; ++step) {
                bx += dir[0];
                bz += dir[1];
                // rise only gently so the arm stays near-horizontal and the crown reads flat-topped
                if (step % 3 == 0) {
                    ++by;
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
