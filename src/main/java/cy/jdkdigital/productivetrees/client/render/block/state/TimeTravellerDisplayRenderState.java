package cy.jdkdigital.productivetrees.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class TimeTravellerDisplayRenderState extends BlockEntityRenderState
{
    public boolean hasItem;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
}
