package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.PetalParticle;
import cy.jdkdigital.productivetrees.client.render.block.PollinatedLeavesBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.StripperBlockEntityRenderer;
import cy.jdkdigital.productivetrees.client.render.block.TimeTravellerDisplayBlockEntityRenderer;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveHangingSignBlockEntity;
import cy.jdkdigital.productivetrees.common.block.entity.ProductiveSignBlockEntity;
import cy.jdkdigital.productivetrees.inventory.screen.PollenSifterScreen;
import cy.jdkdigital.productivetrees.inventory.screen.SawmillScreen;
import cy.jdkdigital.productivetrees.inventory.screen.StripperScreen;
import cy.jdkdigital.productivetrees.inventory.screen.WoodworkerScreen;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ProductiveTrees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ClientRegistration.PETAL_PARTICLES.get(), PetalParticle.Provider::new);
    }

    @SubscribeEvent
    public static void clientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(TreeRegistrator.STRIPPER_MENU.get(), StripperScreen::new);
            MenuScreens.register(TreeRegistrator.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(TreeRegistrator.WOOD_WORKER_MENU.get(), WoodworkerScreen::new);
            MenuScreens.register(TreeRegistrator.POLLEN_SIFTER_MENU.get(), PollenSifterScreen::new);
            // Fruits with multi stack overrides
            ItemProperties.register(TreeRegistrator.FUSTIC.get(), new ResourceLocation("count"), (stack, world, entity, i) -> stack.getCount());
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
            if (stack.getTag() != null && stack.getTag().contains("Block")) {
                return TreeUtil.getLeafColor(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTag().getString("Block"))));
            }
            return FoliageColor.getDefaultColor();
        }, TreeRegistrator.POLLEN.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
            }, TreeUtil.getBlock(treeObject.getId(), "_sapling"), TreeUtil.getBlock(treeObject.getId(), "_potted_sapling"));

            if (ModList.get().isLoaded("productivebees") && treeObject.getStyle().hiveStyle() != null && treeObject.tintHives()) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                        ForgeRegistries.BLOCKS.getValue(id.withPath(p -> "advanced_" + p + "_beehive")),
                        ForgeRegistries.BLOCKS.getValue(id.withPath(p -> "expansion_box_" + p))
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
                        ForgeRegistries.BLOCKS.getValue(id.withPath(p -> "advanced_" + p + "_beehive")),
                        ForgeRegistries.BLOCKS.getValue(id.withPath(p -> "expansion_box_" + p))
                );
            }
        });
    }
}
