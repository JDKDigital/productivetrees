package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * A lightning-bolt tree: a near-vertical trunk that jogs sideways now and then, with several jagged forks that
 * zigzag outward and up — each step kicking out, climbing, and occasionally kinking perpendicular — ending in a
 * foliage cluster at the tip. Reads as a forked bolt of lightning rather than a smooth branching tree.
 */
public class LightningTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<LightningTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(1, 8).fieldOf("fork_count").forGetter((placer) -> placer.forkCount),
                IntProvider.codec(1, 12).fieldOf("fork_length").forGetter((placer) -> placer.forkLength)
        )).apply(instance, LightningTrunkPlacer::new);
    });
    private final IntProvider forkCount;
    private final IntProvider forkLength;

    public LightningTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider forkCount, IntProvider forkLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.forkCount = forkCount;
        this.forkLength = forkLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.LIGHTNING_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);

        // the main bolt: climbs straight but jogs a block sideways now and then
        List<BlockPos> bolt = new ArrayList<>();
        int cx = pPos.getX();
        int cz = pPos.getZ();
        for (int h = 0; h < pFreeTreeHeight; ++h) {
            BlockPos p = new BlockPos(cx, pPos.getY() + h, cz);
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(p), pConfig);
            bolt.add(p);
            if (h > 1 && h < pFreeTreeHeight - 1 && pRandom.nextInt(4) == 0) {
                if (pRandom.nextBoolean()) {
                    cx += pRandom.nextBoolean() ? 1 : -1;
                } else {
                    cz += pRandom.nextBoolean() ? 1 : -1;
                }
                BlockPos jog = new BlockPos(cx, pPos.getY() + h, cz);
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(jog), pConfig);
                bolt.add(jog);
            }
        }

        // forks: jagged branches kicking out from the upper part of the bolt
        int forks = this.forkCount.sample(pRandom);
        for (int i = 0; i < forks; ++i) {
            BlockPos start = bolt.get(bolt.size() / 3 + pRandom.nextInt(Math.max(1, bolt.size() * 2 / 3)));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            int length = this.forkLength.sample(pRandom);
            int bx = start.getX();
            int by = start.getY();
            int bz = start.getZ();
            for (int s = 0; s < length; ++s) {
                bx += direction.getStepX();
                bz += direction.getStepZ();
                // climb every other step so the fork forks upward in jagged steps
                if (s % 2 == 0) {
                    ++by;
                }
                // occasional perpendicular kink — the zigzag of a lightning bolt
                if (pRandom.nextInt(3) == 0) {
                    Direction kink = direction.getClockWise();
                    bx += kink.getStepX();
                    bz += kink.getStepZ();
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
