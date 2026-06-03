package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.Codec;
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

public class TaperedMegaTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<TaperedMegaTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.intRange(0, 8).fieldOf("flare_height").forGetter((placer) -> placer.baseHeight),
                IntProvider.codec(0, 12).fieldOf("branch_count").forGetter((placer) -> placer.branchCount),
                IntProvider.codec(1, 8).fieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, TaperedMegaTrunkPlacer::new);
    });
    private final int baseHeight;
    private final IntProvider branchCount;
    private final IntProvider branchLength;

    public TaperedMegaTrunkPlacer(int height, int heightRandA, int heightRandB, int baseHeight, IntProvider branchCount, IntProvider branchLength) {
        super(height, heightRandA, heightRandB);
        this.baseHeight = baseHeight;
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.TAPERED_MEGA_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        // a 2x2 root flare so it fills the sapling patch, but only for the first few blocks before it pinches into a single slender column
        for (int dx = 0; dx <= 1; ++dx) {
            for (int dz = 0; dz <= 1; ++dz) {
                setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                for (int h = 0; h < this.baseHeight; ++h) {
                    // each base row drops a corner so the 2x2 tapers off rather than ending abruptly
                    if (h > 0 && dx == 1 && dz == 1) {
                        continue;
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }
        }

        // the single trunk runs the full height
        for (int h = 0; h < pFreeTreeHeight; ++h) {
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, h, 0), pConfig);
        }

        // a scatter of short offshoots through the upper trunk, each carrying a small foliage tuft
        int crownStart = pFreeTreeHeight / 3;
        int count = this.branchCount.sample(pRandom);
        for (int b = 0; b < count; ++b) {
            int startHeight = crownStart + pRandom.nextInt(Math.max(1, pFreeTreeHeight - 1 - crownStart));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            int length = this.branchLength.sample(pRandom);
            int bx = pPos.getX();
            int by = pPos.getY() + startHeight;
            int bz = pPos.getZ();
            for (int s = 1; s <= length; ++s) {
                bx += direction.getStepX();
                bz += direction.getStepZ();
                if (s % 2 == 0) {
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
