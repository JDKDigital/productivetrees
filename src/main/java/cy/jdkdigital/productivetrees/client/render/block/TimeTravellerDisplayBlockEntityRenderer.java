package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivetrees.client.render.block.state.TimeTravellerDisplayRenderState;
import cy.jdkdigital.productivetrees.common.block.entity.TimeTravellerDisplayBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class TimeTravellerDisplayBlockEntityRenderer implements BlockEntityRenderer<TimeTravellerDisplayBlockEntity, TimeTravellerDisplayRenderState>
{
    private final ItemModelResolver itemModelResolver;

    public TimeTravellerDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public TimeTravellerDisplayRenderState createRenderState() {
        return new TimeTravellerDisplayRenderState();
    }

    @Override
    public void extractRenderState(TimeTravellerDisplayBlockEntity be, TimeTravellerDisplayRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.hasItem = !be.getItem().isEmpty();
        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemRenderState, be.getItem(), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(TimeTravellerDisplayRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.hasItem) {
            var tick = System.currentTimeMillis() / 800.0D;
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) ((tick * 30.0D) % 360)));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            state.itemRenderState.submit(poseStack, collector, 15728880, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
