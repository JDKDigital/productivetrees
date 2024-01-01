package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.TimeTravellerDisplayBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nonnull;

public class TimeTravellerDisplayBlockEntityRenderer implements BlockEntityRenderer<TimeTravellerDisplayBlockEntity>
{
    public TimeTravellerDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TimeTravellerDisplayBlockEntity blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (!blockEntity.getItem().isEmpty()) {
            var tick = System.currentTimeMillis() / 800.0D;
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) ((tick * 30.0D) % 360)));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, 15728880, combinedOverlayIn, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}