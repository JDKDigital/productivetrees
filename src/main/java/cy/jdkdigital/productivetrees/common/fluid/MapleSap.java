package cy.jdkdigital.productivetrees.common.fluid;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class MapleSap extends ForgeFlowingFluid
{
    protected MapleSap() {
        super(new ForgeFlowingFluid.Properties(
                TreeRegistrator.MAPLE_SAP_TYPE,
                TreeRegistrator.MAPLE_SAP,
                TreeRegistrator.MAPLE_SAP_FLOWING
        ).bucket(TreeRegistrator.MAPLE_SAP_BUCKET));
    }

    @Override
    public Fluid getSource() {
        return super.getSource();
    }

    @Override
    public Fluid getFlowing() {
        return super.getFlowing();
    }

    @Override
    public Item getBucket() {
        return super.getBucket();
    }

    public static class Flowing extends MapleSap
    {
        public Flowing() {
        }

        @Override
        public boolean isSource(FluidState pState) {
            return false;
        }

        @Override
        public int getAmount(FluidState pState) {
            return pState.getValue(BlockStateProperties.LEVEL_FLOWING);
        }
    }

    public static class Source extends MapleSap
    {
        public Source() {
        }

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
