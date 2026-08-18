package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/** Prevents template trees from replacing blocks that vanilla trees would preserve. */
public final class TreeReplaceableProcessor extends StructureProcessor
{
    public static final TreeReplaceableProcessor INSTANCE = new TreeReplaceableProcessor();
    public static final MapCodec<TreeReplaceableProcessor> CODEC = MapCodec.unit(INSTANCE);

    private TreeReplaceableProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos,
                                                              StructureTemplate.StructureBlockInfo originalInfo,
                                                              StructureTemplate.StructureBlockInfo currentInfo,
                                                              StructurePlaceSettings settings) {
        BlockState destination = level.getBlockState(currentInfo.pos());
        return destination.isAir() || destination.is(BlockTags.REPLACEABLE_BY_TREES) ? currentInfo : null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ProductiveTrees.TREE_REPLACEABLE_PROCESSOR.get();
    }
}
