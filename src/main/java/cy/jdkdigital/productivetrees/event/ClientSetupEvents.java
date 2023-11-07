package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.inventory.screen.StripperScreen;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.util.ColorUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
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
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (stack.getTag() != null && stack.getTag().contains("Block")) {
                return ColorUtil.getLeafColor(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTag().getString("Block"))));
            }
            return FoliageColor.getDefaultColor();
        }, ProductiveTrees.POLLEN.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting()) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(treeObject.getLeafColor()), treeObject.getLeafBlock().get());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting()) {
                event.register((stack, tintIndex) -> {
                    return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
                }, treeObject.getSaplingBlock().get(), treeObject.getPottedSaplingBlock().get());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting() && treeObject.registerWood()) {
                event.register((stack, tintIndex) -> {
                    return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLogColor()) : ColorUtil.getCacheColor(treeObject.getPlankColor());
                }, treeObject.getLogBlock().get(), treeObject.getWoodBlock().get());
            }
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(woodObject.getLogColor()) : ColorUtil.getCacheColor(woodObject.getPlankColor());
            }, woodObject.getLogBlock().get(), woodObject.getWoodBlock().get());
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting() && treeObject.registerWood()) {
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
                        treeObject.getHiveBlock().get(),
                        treeObject.getExpansionBoxBlock().get()
                );
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
                    woodObject.getFenceGateBlock().get()
            );
            if (woodObject.getHiveStyle() != null) {
                event.register((stack, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                        woodObject.getHiveBlock().get(),
                        woodObject.getExpansionBoxBlock().get()
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
                    int colorA = ColorUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafA(), lightReader, pos);
                    int colorB = ColorUtil.getLeafColor(pollinatedLeavesBlockEntity.getLeafB(), lightReader, pos);
                    return ColorUtil.blend(colorA, colorB, 0.5f);
                }
            }
            return FoliageColor.getDefaultColor();
        }, ProductiveTrees.POLLINATED_LEAVES.get());

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting()) {
                event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(treeObject.getLeafColor()), treeObject.getLeafBlock().get());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting() && treeObject.hasFruit()) {
                event.register((blockState, lightReader, pos, tintIndex) -> {
                    if (tintIndex == 0) {
                        return ColorUtil.getCacheColor(treeObject.getLeafColor());
                    }
                    int colorA = ColorUtil.getCacheColor(treeObject.getFruit().unripeColor());
                    int colorB = ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                    float age = (float)blockState.getValue(ProductiveFruitBlock.getAgeProperty()) / (float)ProductiveFruitBlock.getMaxAge();
                    return ColorUtil.blend(colorA, colorB, age);
                }, treeObject.getFruitBlock().get());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting()) {
                event.register((blockState, lightReader, pos, tintIndex) -> {
                    return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLeafColor()) : (treeObject.hasFruit() && tintIndex == 2 ? ColorUtil.getCacheColor(treeObject.getFruit().ripeColor()) : ColorUtil.getCacheColor(treeObject.getLogColor()));
                }, treeObject.getSaplingBlock().get(), treeObject.getPottedSaplingBlock().get());
            }
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting() && treeObject.registerWood()) {
                event.register((blockState, lightReader, pos, tintIndex) -> {
                    return tintIndex == 0 ? ColorUtil.getCacheColor(treeObject.getLogColor()) : ColorUtil.getCacheColor(treeObject.getPlankColor());
                }, treeObject.getLogBlock().get(), treeObject.getWoodBlock().get());
            }
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            event.register((blockState, lightReader, pos, tintIndex) -> {
                return tintIndex == 0 ? ColorUtil.getCacheColor(woodObject.getLogColor()) : ColorUtil.getCacheColor(woodObject.getPlankColor());
            }, woodObject.getLogBlock().get(), woodObject.getWoodBlock().get());
        });

        TreeFinder.trees.forEach((id, treeObject) -> {
            if (treeObject.useTinting() && treeObject.registerWood()) {
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
                        treeObject.getHiveBlock().get(),
                        treeObject.getExpansionBoxBlock().get()
                );
            }
        });

        TreeFinder.woods.forEach((id, woodObject) -> {
            event.register((blockState, lightReader, pos, tintIndex) -> ColorUtil.getCacheColor(woodObject.getPlankColor()),
                    woodObject.getStrippedLogBlock().get(),
                    woodObject.getStrippedWoodBlock().get(),
                    woodObject.getPlankBlock().get(),
                    woodObject.getSlabBlock().get(),
                    woodObject.getStairsBlock().get(),
                    woodObject.getPressurePlateBlock().get(),
                    woodObject.getButtonBlock().get(),
                    woodObject.getFenceBlock().get(),
                    woodObject.getFenceGateBlock().get()
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
