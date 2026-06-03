package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A short column topped with several wood arms that radiate from the crown and curve as they climb — each arm
 * turns a steady eighth-turn every couple of blocks, all the same way, so together they read as a rising
 * pinwheel spiral. Built mostly from the tree's "wood" (all-bark) block, the way the structure is.
 */
public class SpiralTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<SpiralTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(2, 10).fieldOf("arm_count").forGetter((placer) -> placer.armCount),
                IntProvider.codec(2, 12).fieldOf("arm_length").forGetter((placer) -> placer.armLength)
        )).apply(instance, SpiralTrunkPlacer::new);
    });
    // eight compass directions; an arm advances through them in order to curve a steady eighth-turn at a time
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final IntProvider armCount;
    private final IntProvider armLength;

    public SpiralTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider armCount, IntProvider armLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.armCount = armCount;
        this.armLength = armLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.SPIRAL_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, h, 0), pConfig);
        }

        int arms = this.armCount.sample(pRandom);
        int startFan = pRandom.nextInt(FAN.length);
        int top = pPos.getY() + pFreeTreeHeight - 1;
        for (int i = 0; i < arms; ++i) {
            int idx = startFan + i * Math.max(1, FAN.length / Math.max(1, arms));
            int length = this.armLength.sample(pRandom);
            int bx = pPos.getX();
            int by = top;
            int bz = pPos.getZ();
            for (int step = 1; step <= length; ++step) {
                int[] dir = FAN[idx % FAN.length];
                bx += dir[0];
                bz += dir[1];
                // climb a block each step while turning an eighth-circle every other step, so the arm spirals upward
                ++by;
                if (step % 2 == 0) {
                    ++idx;
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig, this::woodState);
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }

    // the "wood" (all-bark) counterpart of the trunk's log block; falls back to the log if absent
    private BlockState woodState(BlockState log) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(log.getBlock());
        Block wood = BuiltInRegistries.BLOCK.get(key.withPath((path) -> path.replace("_log", "_wood")));
        return wood == Blocks.AIR ? log : wood.defaultBlockState();
    }
}
