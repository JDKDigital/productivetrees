package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.concurrent.atomic.AtomicInteger;

public class FruitLeafPlacerDecorator extends TreeDecorator
{
    public static final Codec<FruitLeafPlacerDecorator> CODEC = RecordCodecBuilder.create((decoratorInstance) -> decoratorInstance.group(Codec.FLOAT.fieldOf("density").orElse(0.5f).forGetter(FruitLeafPlacerDecorator::getDensity), Codec.INT.fieldOf("max_fruits").orElse(100).forGetter(FruitLeafPlacerDecorator::getMaxFruits), BlockStateProvider.CODEC.fieldOf("fruit_provider").forGetter(FruitLeafPlacerDecorator::getFruitProvider)).apply(decoratorInstance, FruitLeafPlacerDecorator::new));

    private final float density;
    private final int maxFruits;
    public final BlockStateProvider fruitProvider;

    public FruitLeafPlacerDecorator(float density, int maxFruits, BlockStateProvider fruitProvider) {
        this.density = density;
        this.maxFruits = maxFruits;
        this.fruitProvider = fruitProvider;
    }

    public float getDensity() {
        return density;
    }

    public int getMaxFruits() {
        return maxFruits;
    }

    public BlockStateProvider getFruitProvider() {
        return fruitProvider;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistrator.FRUIT_LEAF_PLACER.get();
    }

    @Override
    public void place(Context context) {
        if (context.leaves().isEmpty()) {
            return;
        }
        AtomicInteger count = new AtomicInteger();
        var rand = context.random();
        context.leaves().forEach(blockPos -> {
            if (count.get() < maxFruits && context.isAir(blockPos.below()) && rand.nextFloat() < density) {
                context.setBlock(blockPos.below(), fruitProvider.getState(rand, blockPos.below()));
                count.getAndIncrement();
            }
        });
    }
}
