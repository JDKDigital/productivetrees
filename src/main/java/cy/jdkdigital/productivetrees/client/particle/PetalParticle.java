package cy.jdkdigital.productivetrees.client.particle;

import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

public class PetalParticle extends SingleQuadParticle
{
    private final float rotSpeed;
    private final float particleRandom;
    private final SpriteSet sprites;

    protected PetalParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.gravity = 0.0125F;
        this.quadSize *= 1.0F;
        this.lifetime = Mth.randomBetweenInclusive(this.random, 50, 80);
        this.rotSpeed = ((float) Math.random() - 0.5F) * 0.05F;
        this.particleRandom = (float) Math.random();
        this.setSpriteFromAge(sprites);
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.setSpriteFromAge(this.sprites);

            this.oRoll = this.roll;
            if (!this.onGround) {
                float scaledTime = (this.age + this.particleRandom) * 0.03F;
                this.xd += (Mth.sin(scaledTime) * 0.001D);
                this.zd += (Mth.cos(scaledTime) * 0.001D);
                this.roll += this.rotSpeed;
            } else {
                this.remove();
            }

            this.xd *= 0.99F;
            this.yd *= 0.99F;
            this.zd *= 0.99F;
        }
    }

    public static class Provider implements ParticleProvider<ColoredParticleType>
    {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@Nonnull ColoredParticleType particleType, @Nonnull ClientLevel level, double x, double y, double z, double dx, double dy, double dz, @Nonnull RandomSource random) {
            PetalParticle petalParticle = new PetalParticle(level, x, y, z, this.spriteSet);
            var color = ColorUtil.getCacheColor(particleType.getColor());
            petalParticle.setColor(color[0], color[1], color[2]);
            return petalParticle;
        }
    }
}
