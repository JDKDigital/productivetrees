package cy.jdkdigital.productivetrees.feature.trunkplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TrunkPlacerCodecs
{
    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> trunkPlacerParts(RecordCodecBuilder.Instance<P> pInstance) {
        return pInstance.group(Codec.intRange(0, 200).fieldOf("base_height").forGetter((trunkPlacer) -> {
            return trunkPlacer.baseHeight;
        }), Codec.intRange(0, 100).fieldOf("height_rand_a").forGetter((trunkPlacer) -> {
            return trunkPlacer.heightRandA;
        }), Codec.intRange(0, 100).fieldOf("height_rand_b").forGetter((trunkPlacer) -> {
            return trunkPlacer.heightRandB;
        }));
    }
}
