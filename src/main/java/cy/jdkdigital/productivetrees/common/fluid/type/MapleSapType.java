package cy.jdkdigital.productivetrees.common.fluid.type;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

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
}
