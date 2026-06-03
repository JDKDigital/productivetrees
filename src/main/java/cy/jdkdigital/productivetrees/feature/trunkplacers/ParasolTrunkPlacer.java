package cy.jdkdigital.productivetrees.feature.trunkplacers;

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
 * A bare single column capped by a flat disk of spokes that radiate out almost level — the parasol/umbrella
 * crown of a dragon tree. The spokes spread evenly around the compass and each ends in a foliage cluster, so
 * the canopy sits flat on top of the trunk rather than as a rounded blob.
 */
public class ParasolTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<ParasolTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(2, 12).fieldOf("spoke_count").forGetter((placer) -> placer.spokeCount),
                IntProvider.codec(1, 8).fieldOf("spoke_length").forGetter((placer) -> placer.spokeLength)
        )).apply(instance, ParasolTrunkPlacer::new);
    });
    // eight compass directions the spokes fan out toward
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final IntProvider spokeCount;
    private final IntProvider spokeLength;

    public ParasolTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider spokeCount, IntProvider spokeLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.spokeCount = spokeCount;
        this.spokeLength = spokeLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.PARASOL_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, h, 0), pConfig);
        }

        int spokes = this.spokeCount.sample(pRandom);
        int startFan = pRandom.nextInt(FAN.length);
        int topY = pPos.getY() + pFreeTreeHeight - 1;
        for (int i = 0; i < spokes; ++i) {
            int[] dir = FAN[(startFan + i * Math.max(1, FAN.length / Math.max(1, spokes))) % FAN.length];
            int length = this.spokeLength.sample(pRandom);
            int bx = pPos.getX();
            int by = topY;
            int bz = pPos.getZ();
            for (int step = 1; step <= length; ++step) {
                bx += dir[0];
                bz += dir[1];
                // lift only the outermost block so the rim turns up slightly, keeping the canopy flat-topped
                if (step == length) {
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
