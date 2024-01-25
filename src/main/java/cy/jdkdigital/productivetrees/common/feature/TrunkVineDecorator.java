package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class TrunkVineDecorator extends TreeDecorator
{
    public static final Codec<TrunkVineDecorator> CODEC = RecordCodecBuilder.create((decoratorInstance) -> decoratorInstance.group(BlockStateProvider.CODEC.fieldOf("vine_provider").forGetter(TrunkVineDecorator::getVineProvider)).apply(decoratorInstance, TrunkVineDecorator::new));

    public final BlockStateProvider vineProvider;

    public TrunkVineDecorator(BlockStateProvider vineProvider) {
        this.vineProvider = vineProvider;
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return TreeRegistrator.TRUNK_VINE.get();
    }

    public BlockStateProvider getVineProvider() {
        return vineProvider;
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource randomsource = context.random();
        context.logs().forEach((logPos) -> {
            if (randomsource.nextInt(3) > 0) {
                BlockPos blockpos = logPos.west();
                if (context.isAir(blockpos)) {
                    context.setBlock(blockpos, vineProvider.getState(randomsource, blockpos).setValue(VineBlock.EAST, true));
                }
            }
            if (randomsource.nextInt(3) > 0) {
                BlockPos blockpos = logPos.east();
                if (context.isAir(blockpos)) {
                    context.setBlock(blockpos, vineProvider.getState(randomsource, blockpos).setValue(VineBlock.WEST, true));
                }
            }
            if (randomsource.nextInt(3) > 0) {
                BlockPos blockpos = logPos.north();
                if (context.isAir(blockpos)) {
                    context.setBlock(blockpos, vineProvider.getState(randomsource, blockpos).setValue(VineBlock.SOUTH, true));
                }
            }
            if (randomsource.nextInt(3) > 0) {
                BlockPos blockpos = logPos.south();
                if (context.isAir(blockpos)) {
                    context.setBlock(blockpos, vineProvider.getState(randomsource, blockpos).setValue(VineBlock.NORTH, true));
                }
            }
        });
    }
}