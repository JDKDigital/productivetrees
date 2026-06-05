package cy.jdkdigital.productivetrees.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivetrees.client.render.block.state.PollinatedLeavesRenderState;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class PollinatedLeavesBlockEntityRenderer implements BlockEntityRenderer<PollinatedLeavesBlockEntity, PollinatedLeavesRenderState>
{
    public PollinatedLeavesBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PollinatedLeavesRenderState createRenderState() {
        return new PollinatedLeavesRenderState();
    }

    @Override
    public void extractRenderState(PollinatedLeavesBlockEntity be, PollinatedLeavesRenderState state, float partialTick, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.leaf = null;
        Block leafA = be.getLeafA();
        if (leafA != null && be.getLevel() instanceof ClientLevel level) {
            BlockPos pos = be.getBlockPos();
            MovingBlockRenderState leafState = new MovingBlockRenderState();
            leafState.randomSeedPos = pos;
            leafState.blockPos = pos;
            leafState.blockState = leafA.defaultBlockState();
            leafState.biome = level.getBiome(pos);
            leafState.cardinalLighting = level.cardinalLighting();
            leafState.lightEngine = level.getLightEngine();
            state.leaf = leafState;
        }
    }

    @Override
    public void submit(PollinatedLeavesRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.leaf != null) {
            collector.submitMovingBlock(poseStack, state.leaf);
        }
    }
}
