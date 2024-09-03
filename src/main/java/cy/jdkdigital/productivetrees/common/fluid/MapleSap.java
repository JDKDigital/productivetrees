package cy.jdkdigital.productivetrees.common.fluid;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class MapleSap extends BaseFlowingFluid
{
    protected MapleSap() {
        super(new BaseFlowingFluid.Properties(
                TreeRegistrator.MAPLE_SAP_TYPE,
                TreeRegistrator.MAPLE_SAP,
                TreeRegistrator.MAPLE_SAP_FLOWING
        ).bucket(TreeRegistrator.MAPLE_SAP_BUCKET));
    }

    public static class Flowing extends MapleSap
    {
        @Override
        public boolean isSource(FluidState pState) {
            return false;
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(BlockStateProperties.LEVEL_FLOWING);
        }

        @Override
        public int getAmount(FluidState pState) {
            return pState.getValue(BlockStateProperties.LEVEL_FLOWING);
        }
    }

    public static class Source extends MapleSap
    {
        @Override
        public boolean isSource(FluidState pState) {
            return true;
        }

        @Override
        public int getAmount(FluidState pState) {
            return BlockStateProperties.MAX_LEVEL_8;
        }
    }
}
