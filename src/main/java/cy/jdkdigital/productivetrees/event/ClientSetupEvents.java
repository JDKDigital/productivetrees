package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.PetalParticle;
import cy.jdkdigital.productivetrees.client.render.block.PollinatedLeavesBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.StripperBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.TimeTravellerDisplayBlockEntityRenderer;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.inventory.screen.PollenSifterScreen;
import cy.jdkdigital.productivetrees.inventory.screen.SawmillScreen;
import cy.jdkdigital.productivetrees.inventory.screen.StripperScreen;
import cy.jdkdigital.productivetrees.inventory.screen.WoodworkerScreen;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ProductiveTrees.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ClientRegistration.PETAL_PARTICLES.get(), PetalParticle.Provider::new);
    }

    @SubscribeEvent
    public static void clientSetupEvent(final RegisterMenuScreensEvent event) {
        event.register(TreeRegistrator.STRIPPER_MENU.get(), StripperScreen::new);
        event.register(TreeRegistrator.SAWMILL_MENU.get(), SawmillScreen::new);
        event.register(TreeRegistrator.WOOD_WORKER_MENU.get(), WoodworkerScreen::new);
        event.register(TreeRegistrator.POLLEN_SIFTER_MENU.get(), PollenSifterScreen::new);
    }

    @SubscribeEvent
    public static void clientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Fruits with multi stack overrides
            ItemProperties.register(TreeRegistrator.FUSTIC.get(), ResourceLocation.withDefaultNamespace("count"), (stack, world, entity, i) -> stack.getCount());
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TreeRegistrator.POLLINATED_LEAVES_BLOCK_ENTITY.get(), PollinatedLeavesBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TreeRegistrator.STRIPPER_BLOCK_ENTITY.get(), StripperBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TreeRegistrator.TIME_TRAVELLER_DISPLAY_BLOCK_ENTITY.get(), TimeTravellerDisplayBlockEntityRenderer::new);

        TreeFinder.trees.forEach((id, treeObject) -> {
            event.registerBlockEntityRenderer(TreeRegistrator.SIGN_BE.get(), SignRenderer::new);
            event.registerBlockEntityRenderer(TreeRegistrator.HANGING_SIGN_BE.get(), HangingSignRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (stack.has(TreeRegistrator.POLLEN_BLOCK_COMPONENT)) {
                return TreeUtil.getLeafColor(BuiltInRegistries.BLOCK.get(stack.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT)));
            }
            return FoliageColor.getDefaultColor();
        }, TreeRegistrator.POLLEN.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
            }, TreeUtil.getBlock(treeObject.getId(), "_sapling"), TreeUtil.getBlock(treeObject.getId(), "_potted_sapling"));

            if (ModList.get().isLoaded("productivebees") && treeObject.getStyle().hiveStyle() != null && treeObject.tintHives()) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                        BuiltInRegistries.BLOCK.get(id.withPath(p -> "advanced_" + p + "_beehive")),
                        BuiltInRegistries.BLOCK.get(id.withPath(p -> "expansion_box_" + p))
                );
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((blockState, lightReader, pos, tintIndex) -> {
            if (lightReader != null && pos != null) {
                BlockEntity be = lightReader.getBlockEntity(pos);
                if (be instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                    int colorA = TreeUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafA(), lightReader, pos);
                    int colorB = TreeUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafB(), lightReader, pos);
                    return ColorUtil.blend(colorA, colorB, 0.5f);
                }
            }
            return FoliageColor.getDefaultColor();
        }, TreeRegistrator.POLLINATED_LEAVES.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            event.register((blockState, lightReader, pos, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
            }, TreeUtil.getBlock(treeObject.getId(), "_sapling"), TreeUtil.getBlock(treeObject.getId(), "_potted_sapling"));

            if (ModList.get().isLoaded("productivebees") && treeObject.getStyle().hiveStyle() != null && treeObject.tintHives()) {
                event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                        BuiltInRegistries.BLOCK.get(id.withPath(p -> "advanced_" + p + "_beehive")),
                        BuiltInRegistries.BLOCK.get(id.withPath(p -> "expansion_box_" + p))
                );
            }
        });
    }
}
