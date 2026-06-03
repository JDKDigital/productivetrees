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
import java.util.Optional;
import java.util.function.BiConsumer;

public class WideTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<WideTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.intRange(0, 8).fieldOf("radius").forGetter((placer) -> placer.radius),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("branch_start", 0.33F).forGetter((placer) -> placer.branchStartFraction),
                IntProvider.codec(1, 16).optionalFieldOf("branch_length").forGetter((placer) -> placer.branchLength)
        )).apply(instance, WideTrunkPlacer::new);
    });
    // eight compass directions the branches fan out toward, including the four diagonals
    private static final int[][] FAN = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final int radius;
    private final float branchStartFraction;
    private final Optional<IntProvider> branchLength;

    public WideTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int radius, float branchStartFraction, Optional<IntProvider> branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.radius = radius;
        this.branchStartFraction = branchStartFraction;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.WIDE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        // branches and foliage only start above this fraction of the trunk (e.g. a tall bare baobab trunk crowns near the top)
        int branchStart = Math.round(pFreeTreeHeight * this.branchStartFraction);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            // taper: full radius at the base, shrinking a step at a time to a single column by the top
            int r = Math.round((float) this.radius * (1.0F - (float) h / (float) pFreeTreeHeight));
            if (r < 0) {
                r = 0;
            }
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    // thick trunks (r>=3) use a round cross-section; smaller ones just clip the extreme corners
                    if (r >= 3) {
                        if (dx * dx + dz * dz > r * r) {
                            continue;
                        }
                    } else if (r >= 2 && Math.abs(dx) == r && Math.abs(dz) == r) {
                        continue;
                    }
                    if (h == 0) {
                        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }

            // throw a branch out from the upper trunk; lower branches reach further so the canopy reads as a cone
            if (h >= branchStart && h < pFreeTreeHeight - 1 && pRandom.nextFloat() < 0.75F) {
                int[] dir = FAN[pRandom.nextInt(FAN.length)]; // diagonals included, so the crown isn't two flat planes
                // an explicit branch length gives pronounced branches (e.g. a baobab crown); otherwise branches taper toward the top into a cone
                int length;
                if (this.branchLength.isPresent()) {
                    length = this.branchLength.get().sample(pRandom);
                } else {
                    int maxLength = 1 + Math.round((float) (pFreeTreeHeight - h) / (float) pFreeTreeHeight * 6.0F);
                    length = 1 + pRandom.nextInt(Math.max(1, maxLength));
                }
                int bx = pPos.getX();
                int by = pPos.getY() + h;
                int bz = pPos.getZ();
                for (int step = 1; step <= length; ++step) {
                    bx += dir[0];
                    bz += dir[1];
                    if (step % 2 == 0) {
                        ++by;
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, by, bz), pConfig);
                }
                // attach a block above the tip so leaves cap the end log
                attachments.add(new FoliagePlacer.FoliageAttachment(new BlockPos(bx, by + 1, bz), 0, false));
            }
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }
}
