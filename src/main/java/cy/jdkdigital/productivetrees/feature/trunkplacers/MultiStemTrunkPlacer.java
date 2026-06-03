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
 * Several slender stems rising from a shared base and fanning outward as they climb — the coppiced,
 * multi-trunk form of a hazel. Each stem leans away from the centre a step at a time and ends in its own
 * foliage cluster, so the canopy is a cluster of small crowns rather than one block.
 */
public class MultiStemTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<MultiStemTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(1, 8).fieldOf("stem_count").forGetter((placer) -> placer.stemCount),
                IntProvider.codec(1, 8).fieldOf("lean_interval").forGetter((placer) -> placer.leanInterval)
        )).apply(instance, MultiStemTrunkPlacer::new);
    });
    // eight compass directions the stems fan out toward
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final IntProvider stemCount;
    private final IntProvider leanInterval;

    public MultiStemTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider stemCount, IntProvider leanInterval) {
        super(baseHeight, heightRandA, heightRandB);
        this.stemCount = stemCount;
        this.leanInterval = leanInterval;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.MULTI_STEM_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);

        int count = this.stemCount.sample(pRandom);
        int startFan = pRandom.nextInt(FAN.length);
        for (int s = 0; s < count; ++s) {
            // spread the stems evenly around the compass, then jitter so they don't look mechanical
            int[] dir = FAN[(startFan + s * (FAN.length / Math.max(1, count)) + pRandom.nextInt(2)) % FAN.length];
            int lean = this.leanInterval.sample(pRandom);
            // stems vary in height so the cluster of crowns sits at staggered levels
            int stemHeight = Math.max(2, pFreeTreeHeight - pRandom.nextInt(3));
            int x = pPos.getX();
            int z = pPos.getZ();
            for (int h = 0; h <= stemHeight; ++h) {
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(x, pPos.getY() + h, z), pConfig);
                if (h == 0) {
                    setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(x, pPos.getY() - 1, z), pConfig);
                }
                // lean a step outward every few blocks so the stem arcs away from the centre
                if (h > 0 && h % lean == 0) {
                    x += dir[0];
                    z += dir[1];
                }
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(x, pPos.getY() + stemHeight + 1, z), 0, false));
        }
        return attachments;
    }
}
