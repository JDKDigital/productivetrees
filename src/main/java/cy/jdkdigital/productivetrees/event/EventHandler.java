package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;

@EventBusSubscriber(modid = ProductiveTrees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onServerStarting(AddReloadListenerEvent event) {
        TreeFinder.context = event.getConditionContext();
    }

    @SubscribeEvent
    public static void collectValidUpgrades(CollectValidUpgradesEvent event) {
        if (ModList.get().isLoaded("productivebees")) {
            CompatHandler.collectValidUpgrades(event);
        }
    }

    @SubscribeEvent
    public static void addUpgradeTooltip(UpgradeTooltipEvent event) {
        var upgradeType = BuiltInRegistries.ITEM.getKey(event.getStack().getItem());

        String tPrefix = "productivetrees.information.upgrade." + upgradeType.getPath() + ".";
        switch (upgradeType.getPath()) {
            case "upgrade_time", "upgrade_time_2" -> {
                event.addValidBlock(Component.translatable("productivetrees.devices.stripper"), tPrefix + "stripper");
                event.addValidBlock(Component.translatable("productivetrees.devices.sawmill"), tPrefix + "sawmill");
                event.addValidBlock(Component.translatable("productivetrees.devices.pollen_sifter"), tPrefix + "pollen_sifter");
            }
            case "upgrade_pollen_sieve" -> {
                event.addValidBlock(Component.translatable("productivetrees.devices.advanced_beehive"), tPrefix + "advanced_beehive");
            }
        }
    }

    @SubscribeEvent
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level && event.getState().is(TreeRegistrator.POLLINATED_LEAVES.get()) && level.getBlockEntity(event.getPos()) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
            if (!pollinatedLeavesBlockEntity.getResult().isEmpty()) {
                Block.popResource(level, event.getPos(), pollinatedLeavesBlockEntity.getResult().copy());
            }
        }
    }

    @SubscribeEvent
    public static void blockToolModified(BlockEvent.BlockToolModificationEvent event) {
        if (!event.isSimulated() && event.getItemAbility().equals(ItemAbilities.AXE_STRIP) && event.getLevel() instanceof ServerLevel level) {
            if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ProductiveLogBlock logBlock) {
                var tree = TreeUtil.getTree(logBlock);
                if (tree != null && tree.getStripDrop().isPresent()) {
                    Block.popResource(level, event.getPos(), tree.getStripDropStack().copy());
                }
            }
        }
    }

    @SubscribeEvent
    public static void beeRelease(BeeReleaseEvent event) {
        if (ModList.get().isLoaded("productivebees")) {
            CompatHandler.beeRelease(event);
        } else if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof BeehiveBlockEntity && event.getBee().getHivePos() != null) {
            TreeUtil.pollinateLeaves(level, event.getBee().getHivePos(), 4, false, new ArrayList<>());
        }
    }

    @SubscribeEvent
    public static void onBlockGrow(BlockGrowFeatureEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            Block grownBlock = serverLevel.getBlockState(event.getPos()).getBlock();
            if (grownBlock instanceof ProductiveSaplingBlock saplingBlock && !saplingBlock.getTree().getMutationInfo().target().equals(ProductiveTrees.EMPTY_RL)) {
                if (saplingBlock.getTree().getMutationInfo().chance() >= event.getRandom().nextFloat()) {
                    event.setFeature(ResourceKey.create(Registries.CONFIGURED_FEATURE, saplingBlock.getTree().getMutationInfo().target()));
                }
            }
        }
    }
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
            event.accept(LibItems.UPGRADE_POLLEN_SIEVE.get());
            event.accept(LibItems.UPGRADE_TIME.get());
            event.accept(LibItems.UPGRADE_TIME_2.get());
        }
    }

    @SubscribeEvent
    public static void dynamicDatapack(AddPackFindersEvent event) {
//        ProductiveTrees.LOGGER.info("dynamicDatapack");
//        if (event.getPackType() == PackType.SERVER_DATA) {
//            event.addRepositorySource(new DataGenPackFinder(event.getPackType()));
//        }
//        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
//            event.addRepositorySource(new DataGenPackFinder(event.getPackType()));
//        }
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
