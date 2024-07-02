package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.ColoredParticleType;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ClientRegistration
{
    public static void init() {}

    public static final DeferredHolder<ParticleType<?>, ColoredParticleType> PETAL_PARTICLES = ProductiveTrees.PARTICLE_TYPES.register("petals", () -> new ColoredParticleType(0));
}
