package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.ColoredParticleType;
import net.minecraftforge.registries.RegistryObject;

public class ClientRegistration
{
    public static void init() {}

    public static final RegistryObject<ColoredParticleType> PETAL_PARTICLES = ProductiveTrees.PARTICLE_TYPES.register("petals", ColoredParticleType::new);
}
