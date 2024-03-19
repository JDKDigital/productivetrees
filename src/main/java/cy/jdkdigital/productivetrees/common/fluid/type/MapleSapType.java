package cy.jdkdigital.productivetrees.common.fluid.type;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class MapleSapType extends FluidType
{
    public MapleSapType() {
        super(FluidType.Properties.create()
                .descriptionId("block." + ProductiveTrees.MODID + ".maple_sap")
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .canConvertToSource(false)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(true));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation
                    UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png"),
                    WATER_STILL = new ResourceLocation("block/water_still"),
                    WATER_FLOW = new ResourceLocation("block/water_flow"),
                    WATER_OVERLAY = new ResourceLocation("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture()
            {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return WATER_FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return WATER_OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return UNDERWATER_LOCATION;
            }

            @Override
            public int getTintColor()
            {
                return 0xFFb57d21;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
            {
                return getTintColor();
            }
        });
    }
}
