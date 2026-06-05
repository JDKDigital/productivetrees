package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivetrees.client.render.block.state.StripperRenderState;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
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

public class StripperBlockEntityRenderer implements BlockEntityRenderer<StripperBlockEntity, StripperRenderState>
{
    private final ItemModelResolver itemModelResolver;

    public StripperBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public StripperRenderState createRenderState() {
        return new StripperRenderState();
    }

    @Override
    public void extractRenderState(StripperBlockEntity be, StripperRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.hasAxe = !be.getAxe().isEmpty();
        if (state.hasAxe) {
            this.itemModelResolver.updateForTopItem(state.axeStackState, be.getAxe(), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(StripperRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.hasAxe) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.17f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (30.0D % 360)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (180.0D % 360)));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            state.axeStackState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
