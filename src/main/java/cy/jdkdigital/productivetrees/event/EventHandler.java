package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;

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
        if (event.getStack().is(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get())) {
            event.getTooltipComponents().add(Component.translatable("productivetrees.information.upgrade.upgrade_pollen_sieve").withStyle(ChatFormatting.GOLD));
        }

        String upgradeType = BuiltInRegistries.ITEM.getKey(event.getStack().getItem()).getPath();
        switch (upgradeType) {
            case "upgrade_time" -> {
                event.addValidBlock(Component.literal("Stripper"));
                event.addValidBlock(Component.literal("Sawmill"));
                if (!ModList.get().isLoaded("productivebees")) {
                    event.addValidBlock(Component.literal("Pollen Sifter"));
                }
            }
            case "upgrade_pollen_sieve" -> {
                if (ModList.get().isLoaded("productivebees")) {
                    event.addValidBlock(Component.literal("Advanced Beehive"));
                }
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
}
