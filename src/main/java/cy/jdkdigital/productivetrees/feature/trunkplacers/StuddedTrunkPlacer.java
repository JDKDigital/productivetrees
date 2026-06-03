package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * A knobbly column built from a log plus (centre + four short arms) on every layer, studded with the trunk's
 * "wood" (all-bark) block. The studs alternate position each layer — sitting on the diagonal corners of a tight
 * 3x3 on even layers and out at the tips of a wider plus on odd layers — so the studs read as a pattern climbing
 * the trunk. A small spreading crown of branches caps it.
 */
public class StuddedTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<StuddedTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                IntProvider.codec(0, 8).fieldOf("branch_count").forGetter((placer) -> placer.branchCount),
                IntProvider.codec(1, 8).fieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, StuddedTrunkPlacer::new);
    });
    private static final Direction[] ORTHO = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final int[][] DIAGONAL = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private final IntProvider branchCount;
    private final IntProvider branchLength;

    public StuddedTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider branchCount, IntProvider branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.STUDDED_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, 0, -1, 0), pConfig);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            BlockPos center = pPos.above(h);
            // the log plus: centre column plus a one-block arm in each of the four cardinal directions
            this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(center), pConfig);
            for (Direction d : ORTHO) {
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(center, d.getStepX(), 0, d.getStepZ()), pConfig);
            }
            // wood studs: diagonal corners of the 3x3 on even layers, tips of the wider plus on odd layers
            if (h % 2 == 0) {
                for (int[] c : DIAGONAL) {
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(center, c[0], 0, c[1]), pConfig, this::woodState);
                }
            } else {
                for (Direction d : ORTHO) {
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(center, d.getStepX() * 2, 0, d.getStepZ() * 2), pConfig, this::woodState);
                }
            }
        }

        // a small spreading crown of branches lifting off the top of the column
        int count = this.branchCount.sample(pRandom);
        for (int i = 0; i < count; ++i) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            int length = this.branchLength.sample(pRandom);
            int bx = pPos.getX();
            int by = pPos.getY() + pFreeTreeHeight - 1;
            int bz = pPos.getZ();
            for (int step = 1; step <= length; ++step) {
                bx += direction.getStepX();
                bz += direction.getStepZ();
                if (step % 2 == 0) {
                    ++by;
                }
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by, bz), 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }

    // the "wood" (all-bark) counterpart of the trunk's log block, used for the studs; falls back to the log if absent
    private BlockState woodState(BlockState log) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(log.getBlock());
        Block wood = BuiltInRegistries.BLOCK.get(key.withPath((path) -> path.replace("_log", "_wood")));
        return wood == Blocks.AIR ? log : wood.defaultBlockState();
    }
}
