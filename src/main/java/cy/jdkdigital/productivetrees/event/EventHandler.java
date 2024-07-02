package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;

@EventBusSubscriber(modid = ProductiveTrees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onServerStarting(AddReloadListenerEvent event) {
        TreeFinder.context = event.getConditionContext();
    }

//    @SubscribeEvent
//    public static void onCommonSetup(FMLCommonSetupEvent event) {
//        // TODO 1.21 move to datamap
//        event.enqueueWork(() -> {
//            TreeFinder.trees.forEach((id, treeObject) -> {
//                ComposterBlock.COMPOSTABLES.put(TreeUtil.getBlock(id, "_sapling"), 0.3f);
//                ComposterBlock.COMPOSTABLES.put(TreeUtil.getBlock(id, "_leaves"), 0.3f);
//                if (treeObject.hasFruit()) {
//                    ComposterBlock.COMPOSTABLES.put(treeObject.getFruit().getItem().getItem(), 0.65F);
//                }
//            });
//        });
//    }

    @SubscribeEvent
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level && event.getState().is(TreeRegistrator.POLLINATED_LEAVES.get()) && level.getBlockEntity(event.getPos()) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
            Block.popResource(level, event.getPos(), pollinatedLeavesBlockEntity.getResult().copy());
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
