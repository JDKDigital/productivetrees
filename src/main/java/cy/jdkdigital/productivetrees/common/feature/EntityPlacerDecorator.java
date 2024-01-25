package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class EntityPlacerDecorator extends TreeDecorator
{
    public static final Codec<EntityPlacerDecorator> CODEC = RecordCodecBuilder.create((decoratorInstance) -> decoratorInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(EntityPlacerDecorator::getEntity), Codec.INT.fieldOf("count").orElse(1).forGetter(EntityPlacerDecorator::getCount)).apply(decoratorInstance, EntityPlacerDecorator::new));
    private final ResourceLocation entity;
    private final int count;

    public EntityPlacerDecorator(ResourceLocation entity, int count) {
        this.entity = entity;
        this.count = count;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistrator.ENTITY_PLACER.get();
    }

    @Override
    public void place(Context context) {
        if (context.logs().isEmpty()) {
            return;
        }
        for (var i = 0; i < count; i++) {
            var pos =  context.logs().get(context.random().nextInt( context.logs().size()));
            for (Direction dir: Direction.values()) {
                if (context.isAir(pos.relative(dir))) {
                    context.setBlock(pos.relative(dir), TreeRegistrator.ENTITY_SPAWNER.get().defaultBlockState());
                    break;
                }
            }
        }
    }

    public ResourceLocation getEntity() {
        return entity;
    }

    public int getCount() {
        return count;
    }
}
