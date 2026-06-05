package cy.jdkdigital.productivetrees.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class StripperRenderState extends BlockEntityRenderState
{
    public boolean hasAxe;
    public final ItemStackRenderState axeStackState = new ItemStackRenderState();
}
