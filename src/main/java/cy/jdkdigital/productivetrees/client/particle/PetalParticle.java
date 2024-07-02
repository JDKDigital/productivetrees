package cy.jdkdigital.productivetrees.client.particle;

import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CherryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class PetalParticle extends CherryParticle
{
    protected PetalParticle(ClientLevel level, double xo, double y0, double z0, SpriteSet sprites) {
        super(level, xo, y0, z0, sprites);
    }

    public static class Provider implements ParticleProvider<ColoredParticleType>
    {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(ColoredParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            PetalParticle petalParticle = new PetalParticle(level, x, y, z, this.spriteSet);
            var color = ColorUtil.getCacheColor(particleType.getColor());
            petalParticle.setColor(color[0], color[1], color[2]);
            return petalParticle;
        }
    }
}
