package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nonnull;

public class StripperBlockEntityRenderer implements BlockEntityRenderer<StripperBlockEntity>
{
    public StripperBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(StripperBlockEntity blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (!blockEntity.getAxe().isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.17f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (30.0D % 360)));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (180.0D % 360)));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getAxe(), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}