package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;

import java.util.List;
import java.util.function.BiConsumer;

public class CenteredUpwardsBranchingTrunkPlacer extends UpwardsBranchingTrunkPlacer
{
    public static final MapCodec<CenteredUpwardsBranchingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return trunkPlacerParts(instance).and(instance.group(IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter((trunkPlacer) -> trunkPlacer.extraBranchSteps), Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter((trunkPlacer) -> trunkPlacer.placeBranchPerLogProbability), IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter((trunkPlacer) -> trunkPlacer.extraBranchLength))).apply(instance, CenteredUpwardsBranchingTrunkPlacer::new);
    });
    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;

    public CenteredUpwardsBranchingTrunkPlacer(int height, int randA, int randB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength) {
        super(height, randA, randB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength, HolderSet.direct());
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.CENTERED_UPWARDS_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        int bleed = pFreeTreeHeight / 3;
        for (int i = bleed; i < pFreeTreeHeight - bleed; ++i) {
            int j = pPos.getY() + i;
            if (this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(pPos.getX(), j, pPos.getZ()), pConfig) && i < pFreeTreeHeight - 1 && pRandom.nextFloat() < this.placeBranchPerLogProbability) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
                int k = this.extraBranchLength.sample(pRandom);
                int l = Math.max(0, k - this.extraBranchLength.sample(pRandom) - 1);
                int i1 = this.extraBranchSteps.sample(pRandom);
                this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pConfig, list, mutableBlockPos, j, direction, l, i1);
            }

            if (i == pFreeTreeHeight - 1) {
                list.add(new FoliagePlacer.FoliageAttachment(mutableBlockPos.set(pPos.getX(), j + 1, pPos.getZ()), 0, false));
            }
        }

        return list;
    }
}
