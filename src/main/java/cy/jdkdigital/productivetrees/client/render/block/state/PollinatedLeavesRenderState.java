package cy.jdkdigital.productivetrees.client.render.block.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

import javax.annotation.Nullable;

public class PollinatedLeavesRenderState extends BlockEntityRenderState
{
    @Nullable
    public MovingBlockRenderState leaf;
}
