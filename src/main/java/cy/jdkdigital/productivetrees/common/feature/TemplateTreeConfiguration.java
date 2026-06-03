package cy.jdkdigital.productivetrees.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

/**
 * Config for a tree that is placed as an exact exported structure rather than grown procedurally. Holds the
 * list of structure variants (loaded from {@code data/productivetrees/structure/<name>.nbt}); one is picked at
 * random per placement.
 */
public record TemplateTreeConfiguration(List<ResourceLocation> templates) implements FeatureConfiguration
{
    public static final Codec<TemplateTreeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(TemplateTreeConfiguration::templates)
    ).apply(instance, TemplateTreeConfiguration::new));
}
