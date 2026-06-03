package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
 * A 2x2 conifer trunk with whorl branches off most levels, spiralling around the compass and tapering long-to-short
 * up the height, so the branch skeleton itself forms the cone (e.g. silver fir).
 */
public class ConiferTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<ConiferTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("branch_start", 0.12F).forGetter((placer) -> placer.branchStartFraction),
                Codec.intRange(1, 12).fieldOf("max_branch").forGetter((placer) -> placer.maxBranch)
        )).apply(instance, ConiferTrunkPlacer::new);
    });
    // eight compass directions the whorl branches fan out toward, including the four diagonals
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final float branchStartFraction;
    private final int maxBranch;

    public ConiferTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, float branchStartFraction, int maxBranch) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchStartFraction = branchStartFraction;
        this.maxBranch = maxBranch;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.CONIFER_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        // 2x2 central trunk so it grows from a 2x2 sapling patch
        for (int dx = 0; dx <= 1; ++dx) {
            for (int dz = 0; dz <= 1; ++dz) {
                setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                for (int h = 0; h < pFreeTreeHeight; ++h) {
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }
        }

        int branchStart = Math.round(pFreeTreeHeight * this.branchStartFraction);
        int whorlIndex = pRandom.nextInt(FAN.length);
        for (int h = branchStart; h < pFreeTreeHeight - 1; ++h) {
            // taper branch length to a point at the top so the radiating branches form a cone
            float t = (float) (h - branchStart) / (float) Math.max(1, pFreeTreeHeight - 1 - branchStart);
            int length = Math.max(1, Math.round(this.maxBranch * (1.0F - t)));
            // two branches per whorl (the second a diagonal) so the cone fills out in 3D; whorl spirals up 135 deg/level
            int branches = length > 1 ? 2 : 1;
            for (int b = 0; b < branches; ++b) {
                int[] dir = FAN[(whorlIndex + b * 3) % FAN.length];
                int branchLength = b == 0 ? length : Math.max(1, length - 1);
                // start at the trunk corner on the branch side (2x2 spans dx,dz in 0..1)
                int bx = pPos.getX() + (dir[0] > 0 ? 1 : 0);
                int by = pPos.getY() + h;
                int bz = pPos.getZ() + (dir[1] > 0 ? 1 : 0);
                for (int step = 1; step <= branchLength; ++step) {
                    bx += dir[0];
                    bz += dir[1];
                    if (step % 3 == 0) { // gentle upsweep
                        ++by;
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
                }
                attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
            }
            whorlIndex += 3;
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
