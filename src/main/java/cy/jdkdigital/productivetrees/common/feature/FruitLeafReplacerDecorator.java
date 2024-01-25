package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class FruitLeafReplacerDecorator extends TreeDecorator
{
    public static final Codec<FruitLeafReplacerDecorator> CODEC = RecordCodecBuilder.create((decoratorInstance) -> decoratorInstance.group(Codec.FLOAT.fieldOf("density").orElse(0.5f).forGetter(FruitLeafReplacerDecorator::getDensity), BlockStateProvider.CODEC.fieldOf("fruit_provider").forGetter(FruitLeafReplacerDecorator::getFruitProvider)).apply(decoratorInstance, FruitLeafReplacerDecorator::new));

    private final float density;
    public final BlockStateProvider fruitProvider;

    public FruitLeafReplacerDecorator(float density, BlockStateProvider fruitProvider) {
        this.density = density;
        this.fruitProvider = fruitProvider;
    }

    public float getDensity() {
        return density;
    }

    public BlockStateProvider getFruitProvider() {
        return fruitProvider;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistrator.FRUIT_LEAF_REPLACER.get();
    }

    @Override
    public void place(Context context) {
        if (context.leaves().isEmpty()) {
            return;
        }

        var rand = context.random();
        context.leaves().forEach(blockPos -> {
            if (rand.nextFloat() < density) {
                context.setBlock(blockPos, fruitProvider.getState(rand, blockPos));
            }
        });
    }
}
