package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.color.PollenTintSource;
import cy.jdkdigital.productivetrees.client.color.PollinatedLeavesTintSource;
import cy.jdkdigital.productivetrees.client.color.TreeTintSource;
import cy.jdkdigital.productivetrees.client.particle.PetalParticle;
import cy.jdkdigital.productivetrees.client.render.block.PollinatedLeavesBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.StripperBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.TimeTravellerDisplayBlockEntityRenderer;
import cy.jdkdigital.productivetrees.inventory.screen.PollenSifterScreen;
import cy.jdkdigital.productivetrees.inventory.screen.SawmillScreen;
import cy.jdkdigital.productivetrees.inventory.screen.StripperScreen;
import cy.jdkdigital.productivetrees.inventory.screen.WoodworkerScreen;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.List;

@EventBusSubscriber(modid = ProductiveTrees.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ClientRegistration.PETAL_PARTICLES.get(), PetalParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(0xFFb57d21)),
                TreeRegistrator.MAPLE_SAP.get(), TreeRegistrator.MAPLE_SAP_FLOWING.get());
    }

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(TreeRegistrator.STRIPPER_MENU.get(), StripperScreen::new);
        event.register(TreeRegistrator.SAWMILL_MENU.get(), SawmillScreen::new);
        event.register(TreeRegistrator.WOOD_WORKER_MENU.get(), WoodworkerScreen::new);
        event.register(TreeRegistrator.POLLEN_SIFTER_MENU.get(), PollenSifterScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TreeRegistrator.POLLINATED_LEAVES_BLOCK_ENTITY.get(), PollinatedLeavesBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TreeRegistrator.STRIPPER_BLOCK_ENTITY.get(), StripperBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TreeRegistrator.TIME_TRAVELLER_DISPLAY_BLOCK_ENTITY.get(), TimeTravellerDisplayBlockEntityRenderer::new);

        // SIGN_BE/HANGING_SIGN_BE are not registered in minimal mode (no sign blocks exist)
        if (TreeRegistrator.SIGN_BE != null) {
            event.registerBlockEntityRenderer(TreeRegistrator.SIGN_BE.get(), StandingSignRenderer::new);
            event.registerBlockEntityRenderer(TreeRegistrator.HANGING_SIGN_BE.get(), HangingSignRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerItemTintSources(final RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "pollen"), PollenTintSource.MAP_CODEC);
        event.register(Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "tree"), TreeTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerBlockTintSources(final RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(PollinatedLeavesTintSource.INSTANCE), TreeRegistrator.POLLINATED_LEAVES.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            BlockTintSource leafTint = BlockTintSources.constant(ColorUtil.getCacheColor(treeObject.getLeafColor()));
            BlockTintSource logTint = BlockTintSources.constant(ColorUtil.getCacheColor(treeObject.getLogColor()));
            BlockTintSource fruitTint = treeObject.hasFruit()
                    ? BlockTintSources.constant(ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()))
                    : logTint;
            List<BlockTintSource> saplingTints = List.of(leafTint, logTint, fruitTint);
            event.register(saplingTints,
                    TreeUtil.getBlock(treeObject.getId(), "_sapling"),
                    TreeUtil.getBlock(treeObject.getId(), "_potted_sapling"));

            if (ModList.get().isLoaded("productivebees") && treeObject.getStyle().hiveStyle() != null && treeObject.tintHives()) {
                BlockTintSource plankTint = BlockTintSources.constant(ColorUtil.getCacheColor(treeObject.getPlankColor()));
                event.register(List.of(plankTint),
                        getBlock(id.withPath(p -> "advanced_" + p + "_beehive")),
                        getBlock(id.withPath(p -> "expansion_box_" + p)));
            }
        });
    }

    private static Block getBlock(Identifier id) {
        return BuiltInRegistries.BLOCK.get(id).map(Holder::value).orElse(Blocks.AIR);
    }
}
