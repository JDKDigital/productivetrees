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

public class SpreadingTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<SpreadingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(0, 20).fieldOf("branch_count").forGetter((placer) -> placer.branchCount),
                IntProvider.codec(1, 16).fieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, SpreadingTrunkPlacer::new);
    });
    private final IntProvider branchCount;
    private final IntProvider branchLength;

    public SpreadingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider branchCount, IntProvider branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.SPREADING_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        // a 2x2 trunk so it grows correctly from a 2x2 sapling patch
        for (int dx = 0; dx <= 1; ++dx) {
            for (int dz = 0; dz <= 1; ++dz) {
                setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                for (int h = 0; h < pFreeTreeHeight; ++h) {
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }
        }

        // long branches sweep out and up through the upper crown, each ending in a foliage cluster that supports its own leaves
        int crownStart = pFreeTreeHeight / 2;
        int count = this.branchCount.sample(pRandom);
        for (int b = 0; b < count; ++b) {
            int startHeight = crownStart + pRandom.nextInt(Math.max(1, pFreeTreeHeight - crownStart));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            int length = this.branchLength.sample(pRandom);
            int bx = pPos.getX() + (direction.getStepX() > 0 ? 1 : 0);
            int by = pPos.getY() + startHeight;
            int bz = pPos.getZ() + (direction.getStepZ() > 0 ? 1 : 0);
            for (int s = 0; s < length; ++s) {
                bx += direction.getStepX();
                bz += direction.getStepZ();
                // sweep up but stay a few blocks below the trunk top, so the central crown peaks above the branch ring (domed, not flat)
                if (s % 2 == 1 && by < pPos.getY() + pFreeTreeHeight - 3) {
                    ++by;
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
            }
            // a block above the tip so leaves cap the end log
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by + 1, bz), 0, false));
        }

        // a central column above the branch ring → a rounded peak
        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, true));
        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight + 2), 0, false));
        return attachments;
    }
}
