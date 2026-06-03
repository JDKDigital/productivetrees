package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.Codec;
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
 * A tall NxN trunk that carves a notch out of one face on every layer — a fluted cross-section like a
 * rainbow eucalyptus. The notch direction rotates a quarter turn every {@code twist} blocks so the flute
 * spirals gently up the trunk, and the column tapers to a point near the top where a few branches crown it.
 */
public class FlutedTrunkPlacer extends TrunkPlacer
{
    public static final MapCodec<FlutedTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).and(instance.group(
                Codec.intRange(1, 4).fieldOf("radius").forGetter((placer) -> placer.radius),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("branch_start", 0.8F).forGetter((placer) -> placer.branchStartFraction),
                IntProvider.codec(1, 16).fieldOf("branch_length").forGetter((placer) -> placer.branchLength),
                Codec.intRange(0, 32).optionalFieldOf("twist", 10).forGetter((placer) -> placer.twist)
        )).apply(instance, FlutedTrunkPlacer::new);
    });
    private final int radius;
    private final float branchStartFraction;
    private final IntProvider branchLength;
    private final int twist;

    public FlutedTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int radius, float branchStartFraction, IntProvider branchLength, int twist) {
        super(baseHeight, heightRandA, heightRandB);
        this.radius = radius;
        this.branchStartFraction = branchStartFraction;
        this.branchLength = branchLength;
        this.twist = twist;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.FLUTED_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int branchStart = Math.round(pFreeTreeHeight * this.branchStartFraction);
        Direction[] turns = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        int fluteStart = pRandom.nextInt(4);

        placeRoots(pLevel, pBlockSetter, pRandom, pPos, pConfig, mutableBlockPos);

        for (int h = 0; h < pFreeTreeHeight; ++h) {
            // ease the full base radius down toward a single column over the trunk's height, then collapse to a point at the very top
            int r = Math.max(1, Math.round(this.radius - (this.radius - 1) * ((float) h / Math.max(1, pFreeTreeHeight - 1))));
            int fromTop = pFreeTreeHeight - 1 - h;
            if (fromTop < r) {
                r = fromTop;
            }
            // the notch carved out of the trunk this layer; its facing rotates a quarter turn every `twist` blocks
            Direction flute = turns[(fluteStart + (this.twist > 0 ? h / this.twist : 0)) % 4];
            int notchX = flute.getStepX() * r;
            int notchZ = flute.getStepZ() * r;

            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    // round off the corners of a thick ring
                    if (r >= 2 && Math.abs(dx) == r && Math.abs(dz) == r) {
                        continue;
                    }
                    // carve the flute: drop the mid-edge cell on the notch side (only while the trunk is wide enough to have one)
                    if (r >= 1 && dx == notchX && dz == notchZ) {
                        continue;
                    }
                    if (h == 0) {
                        setDirtAt(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, -1, dz), pConfig);
                    }
                    this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.setWithOffset(pPos, dx, h, dz), pConfig);
                }
            }

            // a few branches sweep out of the crown near the top, each carrying a foliage cluster
            if (h >= branchStart && h < pFreeTreeHeight - 1 && pRandom.nextFloat() < 0.6F) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
                int length = this.branchLength.sample(pRandom);
                int bx = pPos.getX() + direction.getStepX() * r;
                int by = pPos.getY() + h;
                int bz = pPos.getZ() + direction.getStepZ() * r;
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
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));
        return attachments;
    }

    // buttress roots: a short flare climbs a block or two up each side of the trunk, then a low spur creeps out along the ground
    private void placeRoots(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig, BlockPos.MutableBlockPos mutableBlockPos) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int ground = pPos.getY();
        for (int[] d : dirs) {
            if (pRandom.nextFloat() >= 0.7F) {
                continue;
            }
            boolean diagonal = d[0] != 0 && d[1] != 0;
            // sit against the bark: the clipped corner cell for diagonals, one step out for cardinals (both face-adjacent to the trunk)
            int bx = pPos.getX() + d[0] * this.radius + (diagonal ? 0 : d[0]);
            int bz = pPos.getZ() + d[1] * this.radius + (diagonal ? 0 : d[1]);
            int flare = 1 + pRandom.nextInt(this.radius); // climb up to widen the base
            for (int up = 0; up <= flare; ++up) {
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(bx, ground + up, bz), pConfig, this::woodState);
            }
            // a short ground spur from the foot of the buttress
            int rx = bx;
            int rz = bz;
            int length = 1 + pRandom.nextInt(this.radius + 1);
            for (int s = 0; s < length; ++s) {
                rx += d[0];
                rz += d[1];
                if (pRandom.nextInt(3) == 0) { // sideways kink
                    if (d[1] == 0) {
                        rz += pRandom.nextBoolean() ? 1 : -1;
                    } else {
                        rx += pRandom.nextBoolean() ? 1 : -1;
                    }
                }
                int ry = ground - (pRandom.nextInt(3) == 0 ? 1 : 0);
                this.placeLog(pLevel, pBlockSetter, pRandom, mutableBlockPos.set(rx, ry, rz), pConfig, this::woodState);
            }
        }
    }

    // the "wood" (all-bark) counterpart of the trunk's log block, used for the roots; falls back to the log if absent
    private BlockState woodState(BlockState log) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(log.getBlock());
        Block wood = BuiltInRegistries.BLOCK.get(key.withPath((path) -> path.replace("_log", "_wood")));
        return wood == Blocks.AIR ? log : wood.defaultBlockState();
    }
}
