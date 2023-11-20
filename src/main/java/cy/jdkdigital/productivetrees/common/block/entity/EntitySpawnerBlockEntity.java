package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.feature.EntityPlacerDecorator;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraftforge.registries.ForgeRegistries;

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
                if (level.getBlockState(pos.relative(dir)).getBlock() instanceof ProductiveLogBlock pLog && pLog.getTree() instanceof TreeObject treeObject) {
                    var feature = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(treeObject.getFeature()).orElse(null);
                    if (feature != null) {
                        feature.value().getFeatures().forEach(configuredFeature -> {
                            if (configuredFeature.config() instanceof TreeConfiguration treeConfig) {
                                List<TreeDecorator> decorators = new ArrayList<>(treeConfig.decorators);
                                for (TreeDecorator decorator : decorators) {
                                    if (decorator instanceof EntityPlacerDecorator entityPlacerDecorator) {
                                        var entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityPlacerDecorator.getEntity());
                                        if (entityType != null) {
                                            entityType.spawn(serverLevel, pos, MobSpawnType.NATURAL);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_INVISIBLE);
        }
    }
}
