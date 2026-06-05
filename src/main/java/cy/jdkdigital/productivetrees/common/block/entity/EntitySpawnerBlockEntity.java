package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.feature.EntityPlacerDecorator;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.ArrayList;
import java.util.List;

public class EntitySpawnerBlockEntity extends BlockEntity
{
    public EntitySpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.ENTITY_SPAWNER_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EntitySpawnerBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            for (Direction dir : Direction.values()) {
                if (level.getBlockState(pos.relative(dir)).getBlock() instanceof ProductiveLogBlock pLog) {
                    var tree = TreeUtil.getTree(pLog);
                    if (tree != null) {
                        var feature = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(tree.getFeature()).orElse(null);
                        if (feature != null) {
                            feature.value().getSubFeatures().forEach(configuredFeature -> {
                                if (configuredFeature.value().config() instanceof TreeConfiguration treeConfig) {
                                    List<TreeDecorator> decorators = new ArrayList<>(treeConfig.decorators);
                                    for (TreeDecorator decorator : decorators) {
                                        if (decorator instanceof EntityPlacerDecorator entityPlacerDecorator) {
                                            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityPlacerDecorator.getEntity()).map(Holder::value).orElse(null);
                                            if (entityType != null) {
                                                entityType.spawn(serverLevel, pos, EntitySpawnReason.NATURAL);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_INVISIBLE);
        }
    }
}
