package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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
        // rotate around the structure's horizontal centre and offset so that centre lands on the sapling origin,
        // keeping the base (local y 0) at ground level
        BlockPos pivot = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setRotationPivot(pivot).setIgnoreEntities(true);
        BlockPos placePos = context.origin().offset(-pivot.getX(), 0, -pivot.getZ());
        return template.placeInWorld(level, placePos, placePos, settings, random, Block.UPDATE_CLIENTS);
    }
}
