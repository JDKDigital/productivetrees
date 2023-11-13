package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.render.block.StripperBlockEntityRenderer;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.inventory.screen.StripperScreen;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.util.ColorUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ProductiveTrees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ProductiveTrees.STRIPPER_MENU.get(), StripperScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ProductiveTrees.STRIPPER_BLOCK_ENTITY.get(), StripperBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (stack.getTag() != null && stack.getTag().contains("Block")) {
                return ColorUtil.getLeafColor(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTag().getString("Block"))));
            }
            return FoliageColor.getDefaultColor();
        }, ProductiveTrees.POLLEN.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.tintLeaves()) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getLeafColor()), treeObject.getLeafBlock().get());
            }
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
            }, treeObject.getSaplingBlock().get(), treeObject.getPottedSaplingBlock().get());

            if (treeObject.registerWood()) {
                if (treeObject.getTintStyle().equals(TreeObject.TintStyle.FULL)) {
                    event.register((stack, tintIndex) -> {
                        return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLogColor()) : ColorUtil.getCacheColor(treeObject.getPlankColor());
                    }, treeObject.getLogBlock().get(), treeObject.getWoodBlock().get());

                    event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                            treeObject.getStrippedLogBlock().get(),
                            treeObject.getStrippedWoodBlock().get(),
                            treeObject.getPlankBlock().get(),
                            treeObject.getSlabBlock().get(),
                            treeObject.getStairsBlock().get(),
                            treeObject.getPressurePlateBlock().get(),
                            treeObject.getButtonBlock().get(),
                            treeObject.getFenceBlock().get(),
                            treeObject.getFenceGateBlock().get(),
                            treeObject.getDoorBlock().get(),
                            treeObject.getTrapdoorBlock().get(),
                            treeObject.getBookshelfBlock().get(),
                            treeObject.getSignBlock().get(),
                            treeObject.getHangingSignBlock().get(),
                            treeObject.getWallSignBlock().get(),
                            treeObject.getWallHangingSignBlock().get()
                    );
                }
                if (treeObject.tintHives()) {
                    event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                            treeObject.getHiveBlock().get(),
                            treeObject.getExpansionBoxBlock().get()
                    );
                }
            }
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            event.register((stack, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                    woodObject.getStrippedLogBlock().get(),
                    woodObject.getStrippedWoodBlock().get(),
                    woodObject.getPlankBlock().get(),
                    woodObject.getSlabBlock().get(),
                    woodObject.getStairsBlock().get(),
                    woodObject.getPressurePlateBlock().get(),
                    woodObject.getButtonBlock().get(),
                    woodObject.getFenceBlock().get(),
                    woodObject.getFenceGateBlock().get(),
                    woodObject.getDoorBlock().get(),
                    woodObject.getTrapdoorBlock().get(),
                    woodObject.getBookshelfBlock().get(),
                    woodObject.getSignBlock().get(),
                    woodObject.getHangingSignBlock().get(),
                    woodObject.getWallSignBlock().get(),
                    woodObject.getWallHangingSignBlock().get()
            );

            if (woodObject.getHiveStyle() != null) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                        woodObject.getHiveBlock().get(),
                        woodObject.getExpansionBoxBlock().get()
                );
            }

            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(woodObject.getLogColor()) : ColorUtil.getCacheColor(woodObject.getPlankColor());
            }, woodObject.getLogBlock().get(), woodObject.getWoodBlock().get());
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((blockState, lightReader, pos, tintIndex) -> {
            if (lightReader != null && pos != null) {
                BlockEntity be = lightReader.getBlockEntity(pos);
                if (be instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                    int colorA = ColorUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafA(), lightReader, pos);
                    int colorB = ColorUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafB(), lightReader, pos);
                    return ColorUtil.blend(colorA, colorB, 0.5f);
                }
            }
            return FoliageColor.getDefaultColor();
        }, ProductiveTrees.POLLINATED_LEAVES.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.hasFruit()) {
                event.register((blockState, lightReader, pos, tintIndex) -> {
                    if (tintIndex == 0) {
                        return ColorUtil.getCacheColor(treeObject.getLeafColor());
                    }
                    int age = blockState.getValue(ProductiveFruitBlock.getAgeProperty());
                    if (age > 2) {
                        float agePercentage = (float) age / (float) ProductiveFruitBlock.getMaxAge();
                        int colorA = ColorUtil.getCacheColor(treeObject.getFruit().unripeColor());
                        int colorB = ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                        return ColorUtil.blend(colorA, colorB, agePercentage);
                    } else {
                        return ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                    }
                }, treeObject.getFruitBlock().get());
            }

            if (treeObject.tintLeaves()) {
                event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(treeObject.getLeafColor()), treeObject.getLeafBlock().get());
            }
            event.register((blockState, lightReader, pos, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
            }, treeObject.getSaplingBlock().get(), treeObject.getPottedSaplingBlock().get());

            if (treeObject.registerWood()) {
                if (treeObject.getTintStyle().equals(TreeObject.TintStyle.FULL)) {
                    event.register((blockState, lightReader, pos, tintIndex) -> {
                        return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLogColor()) : ColorUtil.getCacheColor(treeObject.getPlankColor());
                    }, treeObject.getLogBlock().get(), treeObject.getWoodBlock().get());

                    event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                            treeObject.getStrippedLogBlock().get(),
                            treeObject.getStrippedWoodBlock().get(),
                            treeObject.getPlankBlock().get(),
                            treeObject.getSlabBlock().get(),
                            treeObject.getStairsBlock().get(),
                            treeObject.getPressurePlateBlock().get(),
                            treeObject.getButtonBlock().get(),
                            treeObject.getFenceBlock().get(),
                            treeObject.getFenceGateBlock().get(),
                            treeObject.getDoorBlock().get(),
                            treeObject.getTrapdoorBlock().get(),
                            treeObject.getBookshelfBlock().get(),
                            treeObject.getSignBlock().get(),
                            treeObject.getHangingSignBlock().get(),
                            treeObject.getWallSignBlock().get(),
                            treeObject.getWallHangingSignBlock().get()
                    );
                }
                if (treeObject.tintHives()) {
                    event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(treeObject.getPlankColor()),
                            treeObject.getHiveBlock().get(),
                            treeObject.getExpansionBoxBlock().get()
                    );
                }
            }
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            event.register((blockState, lightReader, pos, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(woodObject.getLogColor()) : ColorUtil.getCacheColor(woodObject.getPlankColor());
            }, woodObject.getLogBlock().get(), woodObject.getWoodBlock().get());

            event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                    woodObject.getStrippedLogBlock().get(),
                    woodObject.getStrippedWoodBlock().get(),
                    woodObject.getPlankBlock().get(),
                    woodObject.getSlabBlock().get(),
                    woodObject.getStairsBlock().get(),
                    woodObject.getPressurePlateBlock().get(),
                    woodObject.getButtonBlock().get(),
                    woodObject.getFenceBlock().get(),
                    woodObject.getFenceGateBlock().get(),
                    woodObject.getDoorBlock().get(),
                    woodObject.getTrapdoorBlock().get(),
                    woodObject.getBookshelfBlock().get(),
                    woodObject.getSignBlock().get(),
                    woodObject.getHangingSignBlock().get(),
                    woodObject.getWallSignBlock().get(),
                    woodObject.getWallHangingSignBlock().get()
            );
            if (woodObject.getHiveStyle() != null) {
                event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                        woodObject.getHiveBlock().get(),
                        woodObject.getExpansionBoxBlock().get()
                );
            }
        });
    }
}
