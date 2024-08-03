package cy.jdkdigital.productivetrees.integrations.productivebees;

import com.mojang.datafixers.util.Pair;
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
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.registry.WoodObject;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
            if (event.getBee().getHivePos() != null) {
                // Scan for leaves blocks around the hive, 4 block radius + 2 per range upgrade
                var pos = event.getBee().getHivePos();
                int distance = 4 + (2 * advancedBeehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get()));
                List<BlockPos> leaves = BlockPos.betweenClosedStream(pos.offset(-distance, -distance, -distance), pos.offset(distance, distance, distance)).map(BlockPos::immutable).toList();
                // Build permutation map
                List<BlockState> uniqueLeaves = new ArrayList<>();
                Map<BlockState, BlockPos> leafMap = new HashMap<>();
                leaves.forEach(blockPos -> {
                    var state = level.getBlockState(blockPos);
                    if (state.is(ModTags.POLLINATABLE) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get()) && !(state.getBlock() instanceof ProductiveFruitBlock)) {
                        leafMap.put(state, blockPos);
                        if (!uniqueLeaves.contains(state)) {
                            uniqueLeaves.add(state);
                        }
                    }
                });

                if (!uniqueLeaves.isEmpty()) {
                    boolean isSpecialPollinator = event.getBee() instanceof ProductiveBee pBee && pBee.getBeeName().equals("allergy");
                    // Check for pollen sieve upgrade and collect pollen from nearby leaf
                    int sieveUpgrades = advancedBeehiveBlockEntity.getUpgradeCount(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get());
                    if (sieveUpgrades > 0 && level.random.nextInt(100) < (Config.SERVER.pollenChanceFromSieve.get() * (isSpecialPollinator ? 5 : 1))) {
                        BlockState pollenLeaf = uniqueLeaves.get(level.random.nextInt(uniqueLeaves.size()));
                        var pollenStack = TreeUtil.getPollen(pollenLeaf.getBlock());
                        ((InventoryHandlerHelper.BlockEntityItemStackHandler) advancedBeehiveBlockEntity.inventoryHandler).addOutput(pollenStack);
                    }

                    // Pollinate leaves
                    Map<RecipeHolder<TreePollinationRecipe>, Pair<BlockState, BlockState>> matchedRecipes = new HashMap<>();
                    var allRecipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
                    allRecipes.forEach(treePollinationRecipe -> {
                        if (!matchedRecipes.containsKey(treePollinationRecipe)) {
                            uniqueLeaves.forEach(stateA -> {
                                uniqueLeaves.forEach(stateB -> {
                                    if (treePollinationRecipe.value().matches(stateA, stateB)) {
                                        matchedRecipes.put(treePollinationRecipe, Pair.of(stateA, stateB));
                                    }
                                });
                            });
                        }
                    });

                    if (matchedRecipes.size() > 0) {
                        RecipeHolder<TreePollinationRecipe> pickedRecipe = (RecipeHolder<TreePollinationRecipe>) matchedRecipes.keySet().toArray()[level.random.nextInt(matchedRecipes.size())];
                        Pair<BlockState, BlockState> states = matchedRecipes.get(pickedRecipe);

                        BlockPos posA = level.random.nextBoolean() ? leafMap.get(states.getFirst()) : leafMap.get(states.getSecond());

                        if (level.random.nextInt(100) <= (pickedRecipe.value().chance * (isSpecialPollinator ? 5 : 1)) && level.getBlockState(posA).is(BlockTags.LEAVES)) {
                            level.setBlock(posA, TreeRegistrator.POLLINATED_LEAVES.get().defaultBlockState(), Block.UPDATE_ALL);
                            if (level.getBlockEntity(posA) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                                pollinatedLeavesBlockEntity.setLeafA(states.getFirst().getBlock());
                                pollinatedLeavesBlockEntity.setLeafB(states.getSecond().getBlock());
                                pollinatedLeavesBlockEntity.setResult(pickedRecipe.value().result);
                                pollinatedLeavesBlockEntity.setChanged();
                            }
                        }
                    }
                }
            }
        }
    }
}
