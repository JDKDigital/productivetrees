package cy.jdkdigital.productivetrees.event;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.event.BeeReleaseEvent;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivetrees.Config;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ProductiveTrees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onServerStarting(AddReloadListenerEvent event) {
        TreeFinder.context = event.getConditionContext();
    }

    @SubscribeEvent
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level && event.getState().is(TreeRegistrator.POLLINATED_LEAVES.get()) && level.getBlockEntity(event.getPos()) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
            Block.popResource(level, event.getPos(), pollinatedLeavesBlockEntity.getResult());
        }
    }

    @SubscribeEvent
    public static void beeRelease(BeeReleaseEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
            if (event.getBee().getHivePos() != null) {
                // Scan for leaves blocks around the hive, 4 block radius + 2 per range upgrade
                var pos = event.getBee().getHivePos();
                int distance = 4 + (2 * advancedBeehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get()));
                List<BlockPos> leaves = BlockPos.betweenClosedStream(pos.offset(-distance, -distance, -distance), pos.offset(distance, distance, distance)).map(BlockPos::immutable).collect(Collectors.toList());
                // Build permutation map
                List<BlockState> uniqueLeaves = new ArrayList<>();
                Map<BlockState, BlockPos> leafMap = new HashMap<>();
                leaves.forEach(blockPos -> {
                    var state = level.getBlockState(blockPos);
                    if (state.is(ModTags.POLLINATABLE) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get())) {
                        leafMap.put(state, blockPos);
                        if (!uniqueLeaves.contains(state)) {
                            uniqueLeaves.add(state);
                        }
                    }
                });

                if (uniqueLeaves.size() > 0) {
                    // Check for pollen sieve upgrade and collect pollen from nearby leaf
                    int sieveUpgrades = advancedBeehiveBlockEntity.getUpgradeCount(TreeRegistrator.UPGRADE_POLLEN_SIEVE.get());
                    if (sieveUpgrades > 0 && level.random.nextInt(100) < Config.SERVER.pollenChanceFromSieve.get()) {
                        BlockState pollenLeaf = uniqueLeaves.get(level.random.nextInt(uniqueLeaves.size()));
                        advancedBeehiveBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
                            var pollenStack = new ItemStack(TreeRegistrator.POLLEN.get());
                            pollenStack.getOrCreateTag().putString("Block", ForgeRegistries.BLOCKS.getKey(pollenLeaf.getBlock()).toString());
                            ((InventoryHandlerHelper.ItemHandler) inv).addOutput(pollenStack);
                        });
                    }

                    // Pollinate leaves
                    Map<TreePollinationRecipe, Pair<BlockState, BlockState>> matchedRecipes = new HashMap<>();
                    var allRecipes = level.getRecipeManager().getAllRecipesFor(TreeRegistrator.TREE_POLLINATION_TYPE.get());
                    allRecipes.forEach(treePollinationRecipe -> {
                        uniqueLeaves.forEach(stateA -> {
                            uniqueLeaves.forEach(stateB -> {
                                if (!matchedRecipes.containsKey(treePollinationRecipe) && treePollinationRecipe.matches(stateA, stateB)) {
                                    matchedRecipes.put(treePollinationRecipe, Pair.of(stateA, stateB));
                                }
                            });
                        });
                    });

                    if (matchedRecipes.size() > 0) {
                        TreePollinationRecipe pickedRecipe = (TreePollinationRecipe) matchedRecipes.keySet().toArray()[level.random.nextInt(matchedRecipes.size())];
                        Pair<BlockState, BlockState> states = matchedRecipes.get(pickedRecipe);

                        BlockPos posA = level.random.nextBoolean() ? leafMap.get(states.getFirst()) : leafMap.get(states.getSecond());

                        if (level.random.nextInt(100) <= pickedRecipe.chance && level.getBlockState(posA).is(BlockTags.LEAVES)) {
                            level.setBlock(posA, TreeRegistrator.POLLINATED_LEAVES.get().defaultBlockState(), Block.UPDATE_ALL);
                            if (level.getBlockEntity(posA) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                                pollinatedLeavesBlockEntity.setLeafA(states.getFirst().getBlock());
                                pollinatedLeavesBlockEntity.setLeafB(states.getSecond().getBlock());
                                pollinatedLeavesBlockEntity.setResult(pickedRecipe.result);
                                pollinatedLeavesBlockEntity.setChanged();
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockGrow(SaplingGrowTreeEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            Block grownBlock = serverLevel.getBlockState(event.getPos()).getBlock();
            if (grownBlock instanceof ProductiveSaplingBlock saplingBlock && !saplingBlock.getTree().getMutationInfo().target().equals(ProductiveTrees.EMPTY_RL)) {
                if (saplingBlock.getTree().getMutationInfo().chance() >= event.getRandomSource().nextFloat()) {
                    event.setFeature(ResourceKey.create(Registries.CONFIGURED_FEATURE, saplingBlock.getTree().getMutationInfo().target()));
                }
            }
        }
    }
}
