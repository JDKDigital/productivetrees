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

public class LayeredTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<LayeredTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(0, 12).fieldOf("branch_count").forGetter((placer) -> placer.branchCount),
                IntProvider.codec(1, 16).fieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, LayeredTrunkPlacer::new);
    });
    private final IntProvider branchCount;
    private final IntProvider branchLength;

    public LayeredTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider branchCount, IntProvider branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.LAYERED_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        // single central trunk
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);
        for (int h = 0; h < pFreeTreeHeight; ++h) {
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, h, 0), pConfig);
        }

        // branches radiate near-horizontally over the upper half, forming a wide flat layered crown that caps the trunk
        int crownStart = pFreeTreeHeight / 2;
        int count = this.branchCount.sample(pRandom);
        for (int b = 0; b < count; ++b) {
            int startHeight = crownStart + pRandom.nextInt(Math.max(1, pFreeTreeHeight - crownStart));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            int length = this.branchLength.sample(pRandom);
            int bx = pPos.getX();
            int by = pPos.getY() + startHeight;
            int bz = pPos.getZ();
            for (int s = 0; s < length; ++s) {
                bx += direction.getStepX();
                bz += direction.getStepZ();
                if (s % 3 == 2) {
                    ++by;
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
            }
            // a block above the tip so leaves cap the end log
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by + 1, bz), 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
