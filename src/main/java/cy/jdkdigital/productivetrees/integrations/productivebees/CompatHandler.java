package cy.jdkdigital.productivetrees.integrations.productivebees;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import cy.jdkdigital.productivetrees.Config;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public class CompatHandler
{
    private static List<DeferredHolder<Block, Block>> HIVES = new ArrayList<>();
    private static List<DeferredHolder<Block, Block>> BOXES = new ArrayList<>();
    public static void createHive(String name, WoodObject woodObject, ToIntFunction<BlockState> lightLevel) {
        ModBlocks.HIVES.put("advanced_" + name + "_beehive", TreeRegistrator.registerBlock("advanced_" + name + "_beehive", () -> new AdvancedBeehive(Block.Properties.ofFullCopy(Blocks.BEEHIVE).lightLevel(lightLevel)), true));
        ModBlocks.EXPANSIONS.put("expansion_box_" + name, TreeRegistrator.registerBlock("expansion_box_" + name, () -> new ExpansionBox(Block.Properties.ofFullCopy(Blocks.BEEHIVE).lightLevel(lightLevel)), true));
    }

    public static void collectValidUpgrades(CollectValidUpgradesEvent event) {
        if (event.getBlockEntity() instanceof AdvancedBeehiveBlockEntity) {
            event.addValidUpgrade(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get());
        }
    }

    public static void beeRelease(BeeReleaseEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof BeehiveBlockEntity beehiveBlockEntity && event.getBee().getHivePos() != null) {
            // Scan for leaves blocks around the hive, 4 block radius + 2 per range upgrade
            int distance = 4 + (beehiveBlockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity ? (2 * advancedBeehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get())) : 0);
            boolean isSpecialPollinator = event.getBee() instanceof ProductiveBee pBee && pBee.getBeeName().equals("allergy");
            List<BlockState> uniqueLeaves = new ArrayList<>();
            TreeUtil.pollinateLeaves(level, event.getBee().getHivePos(), distance, isSpecialPollinator, uniqueLeaves);

            if (!uniqueLeaves.isEmpty()) {
                // Check for pollen sieve upgrade and collect pollen from nearby leaf
                if (beehiveBlockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
                    int sieveUpgrades = advancedBeehiveBlockEntity.getUpgradeCount(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get());
                    if (sieveUpgrades > 0 && level.random.nextInt(100) < (Config.SERVER.pollenChanceFromSieve.get() * (isSpecialPollinator ? 5 : 1))) {
                        BlockState pollenLeaf = uniqueLeaves.get(level.random.nextInt(uniqueLeaves.size()));
                        var pollenStack = TreeUtil.getPollen(pollenLeaf.getBlock());
                        ((InventoryHandlerHelper.BlockEntityItemStackHandler) advancedBeehiveBlockEntity.inventoryHandler).addOutput(pollenStack);
                    }
                }
            }
        }
    }
}
