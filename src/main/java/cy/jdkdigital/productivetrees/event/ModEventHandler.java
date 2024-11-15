package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.datagen.dynamic.DataGenPackFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ProductiveTrees.MODID)
public class ModEventHandler
{
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(TreeRegistrator.TAB_KEY)) {
            var hasBees = ModList.get().isLoaded("productivebees");
            for (DeferredHolder<Item, ? extends Item> item : ProductiveTrees.ITEMS.getEntries()) {
                if (item.getId().getPath().equals("pollen_sifter") && hasBees) {
                    continue;
                }
                if (item.getId().getPath().equals("upgrade_pollen_sieve") && !hasBees) {
                    continue;
                }
                event.accept(item.get());
            }
        }
    }

    @SubscribeEvent
    public static void dynamicDatapack(AddPackFindersEvent event) {
        ProductiveTrees.LOGGER.info("dynamicDatapack");
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(new DataGenPackFinder(event.getPackType()));
        }
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(new DataGenPackFinder(event.getPackType()));
        }
    }

    @SubscribeEvent
    public static void registerBlockEntityCapabilities(RegisterCapabilitiesEvent event) {
        // Stripper
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TreeRegistrator.STRIPPER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        // Sawmill
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TreeRegistrator.SAWMILL_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        // Wood worker
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TreeRegistrator.WOOD_WORKER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        // Pollen sifter
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TreeRegistrator.POLLEN_SIFTER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
        // Display
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TreeRegistrator.TIME_TRAVELLER_DISPLAY_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
    }
}
