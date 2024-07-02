package cy.jdkdigital.productivetrees.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;

public class ColoredParticleType extends ParticleType<ColoredParticleType> implements ParticleOptions
{
    private final MapCodec<ColoredParticleType> codec = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Codec.INT.fieldOf("color").forGetter(particle -> particle.color)
                    )
                    .apply(builder, ColoredParticleType::new)
    );
    private final StreamCodec<RegistryFriendlyByteBuf, ColoredParticleType> streamCodec = StreamCodec.unit(this);

    private int color = 0;

    @Override
    public MapCodec<ColoredParticleType> codec() {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ColoredParticleType> streamCodec() {
        return streamCodec;
    }

    public ColoredParticleType(int color) {
        super(false);
        this.color = color;
    }

    public ColoredParticleType() {
        super(false);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    @Nonnull
    @Override
    public ColoredParticleType getType() {
        return this;
    }
}
