package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

/**
 * Places one of a tree's exported structure variants verbatim, with a random rotation, centred on the origin
 * with its base at ground level. Used for hand-built trees whose exact form must be preserved (e.g. a
 * walk-through arching trunk base) that a procedural trunk/foliage placer can't reproduce.
 */
public class TemplateTreeFeature extends Feature<TemplateTreeConfiguration>
{
    public TemplateTreeFeature(Codec<TemplateTreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TemplateTreeConfiguration> context) {
        List<ResourceLocation> templates = context.config().templates();
        if (templates.isEmpty()) {
            return false;
        }
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ResourceLocation id = templates.get(random.nextInt(templates.size()));
        StructureTemplateManager manager = level.getLevel().getStructureManager();
        Optional<StructureTemplate> loaded = manager.get(id);
        if (loaded.isEmpty()) {
            return false;
        }
        StructureTemplate template = loaded.get();
        Vec3i size = template.getSize();
        Rotation rotation = Rotation.getRandom(random);
        // Anchor and rotate around the trunk base 
        BlockPos pivot = findTrunkBase(template, id).orElseGet(() -> new BlockPos(size.getX() / 2, 0, size.getZ() / 2));
        // Skip structure_void positions
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setRotationPivot(pivot).setIgnoreEntities(true)
                .addProcessor(new BlockIgnoreProcessor(List.of(Blocks.STRUCTURE_VOID)))
                .addProcessor(TreeReplaceableProcessor.INSTANCE);
        BlockPos placePos = context.origin().offset(-pivot.getX(), 0, -pivot.getZ());
        return template.placeInWorld(level, placePos, placePos, settings, random, Block.UPDATE_CLIENTS);
    }

    // Centre (x,z at y 0) of the lowest layer of the tree's logs
    private static Optional<BlockPos> findTrunkBase(StructureTemplate template, ResourceLocation templateId) {
        Block log = resolveLogBlock(templateId);
        if (log == Blocks.AIR) {
            return Optional.empty();
        }
        List<StructureTemplate.StructureBlockInfo> logs = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), log);
        if (logs.isEmpty()) {
            return Optional.empty();
        }
        int minY = logs.stream().mapToInt(info -> info.pos().getY()).min().orElse(0);
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        for (StructureTemplate.StructureBlockInfo info : logs) {
            if (info.pos().getY() != minY) {
                continue;
            }
            minX = Math.min(minX, info.pos().getX());
            maxX = Math.max(maxX, info.pos().getX());
            minZ = Math.min(minZ, info.pos().getZ());
            maxZ = Math.max(maxZ, info.pos().getZ());
        }
        return Optional.of(new BlockPos((minX + maxX) / 2, 0, (minZ + maxZ) / 2));
    }

    // The tree whose id is the longest prefix of the template path (templates are named "<tree>[_mega|_giga]_<n>").
    private static Block resolveLogBlock(ResourceLocation templateId) {
        String path = templateId.getPath();
        ResourceLocation best = null;
        for (ResourceLocation treeId : TreeFinder.trees.keySet()) {
            String treePath = treeId.getPath();
            if ((path.equals(treePath) || path.startsWith(treePath + "_"))
                    && (best == null || treePath.length() > best.getPath().length())) {
                best = treeId;
            }
        }
        return best == null ? Blocks.AIR : TreeUtil.getBlock(best, "_log");
    }
}
