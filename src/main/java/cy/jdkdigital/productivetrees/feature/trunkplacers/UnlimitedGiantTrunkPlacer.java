package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class UnlimitedGiantTrunkPlacer extends GiantTrunkPlacer
{
    public static final MapCodec<UnlimitedGiantTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).apply(instance, UnlimitedGiantTrunkPlacer::new);
    });

    public UnlimitedGiantTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.UNLIMITED_GIANT_TRUNK_PLACER.get();
    }
}
