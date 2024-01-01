package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class PollinatedLeavesBlockEntityRenderer implements BlockEntityRenderer<PollinatedLeavesBlockEntity>
{
    public PollinatedLeavesBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(PollinatedLeavesBlockEntity blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (blockEntity.getLeafA() != null && Minecraft.getInstance().level != null) {
            poseStack.pushPose();
            var pState = blockEntity.getLeafA().defaultBlockState();
            var renderType = TreeUtil.isTranslucentTree(ForgeRegistries.BLOCKS.getKey(blockEntity.getLeafA()).getPath()) ? RenderType.translucent() : RenderType.cutout();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(pState, poseStack, bufferSource, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType);
            poseStack.popPose();
        }
    }
}