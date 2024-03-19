package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class UnlimitedStraightTrunkPlacer extends StraightTrunkPlacer
{
    public static final Codec<UnlimitedStraightTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return TrunkPlacerCodecs.trunkPlacerParts(instance).apply(instance, UnlimitedStraightTrunkPlacer::new);
    });

    public UnlimitedStraightTrunkPlacer(int p_70248_, int p_70249_, int p_70250_) {
        super(p_70248_, p_70249_, p_70250_);
    }
    protected TrunkPlacerType<?> type() {
        return TreeRegistrator.UNLIMITED_STRAIGHT_TRUNK_PLACER.get();
    }
}
