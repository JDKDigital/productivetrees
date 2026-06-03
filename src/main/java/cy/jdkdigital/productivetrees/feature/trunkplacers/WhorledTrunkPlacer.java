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

public class WhorledTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<WhorledTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.intRange(0, 4).fieldOf("radius").forGetter((placer) -> placer.radius),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("branch_start", 0.1F).forGetter((placer) -> placer.branchStartFraction),
                IntProvider.codec(1, 16).fieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, WhorledTrunkPlacer::new);
    });
    // eight compass directions the whorl branches fan out toward, including the four diagonals
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final int radius;
    private final float branchStartFraction;
    private final IntProvider branchLength;

    public WhorledTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int radius, float branchStartFraction, IntProvider branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.radius = radius;
        this.branchStartFraction = branchStartFraction;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.WHORLED_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        // branches begin partway up; the trunk below is a bare column
        int branchStart = Math.round(pFreeTreeHeight * this.branchStartFraction);
        // step 135 deg per whorl so tufts spiral through all eight compass points, not just the cardinals
        int whorlIndex = pRandom.nextInt(FAN.length);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            for (int dx = -this.radius; dx <= this.radius; ++dx) {
                for (int dz = -this.radius; dz <= this.radius; ++dz) {
                    // round off the corners of a thick trunk so a 3x3 core reads rounded rather than blocky
                    if (this.radius >= 2 && Math.abs(dx) == this.radius && Math.abs(dz) == this.radius) {
                        continue;
                    }
                    if (h == 0) {
                        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }

            // one short branch per crown level, direction stepping 135 deg each time
            if (h >= branchStart && h < pFreeTreeHeight - 1) {
                int[] dir = FAN[whorlIndex % FAN.length];
                whorlIndex += 3;
                int length = this.branchLength.sample(pRandom);
                int bx = pPos.getX() + dir[0] * this.radius;
                int by = pPos.getY() + h;
                int bz = pPos.getZ() + dir[1] * this.radius;
                for (int step = 1; step <= length; ++step) {
                    bx += dir[0];
                    bz += dir[1];
                    if (step % 2 == 0) { // tilt up as it extends
                        ++by;
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
                }
                attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
            }
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
