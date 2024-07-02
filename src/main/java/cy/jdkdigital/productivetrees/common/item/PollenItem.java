package cy.jdkdigital.productivetrees.common.item;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.recipe.RecipeHelper;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PollenItem extends Item
{
    public PollenItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        if (pStack.has(TreeRegistrator.POLLEN_BLOCK_COMPONENT)) {
            Block leaf = BuiltInRegistries.BLOCK.get(pStack.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT));
            pTooltipComponents.add(Component.translatable(ProductiveTrees.MODID + ".pollen.name", Component.translatable(leaf.getDescriptionId()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_BLUE));
        }
        pTooltipComponents.add(Component.translatable(ProductiveTrees.MODID + ".information.pollen").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (context.getPlayer() != null) {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            BlockState state = level.getBlockState(context.getClickedPos());
            if (stack.has(TreeRegistrator.POLLEN_BLOCK_COMPONENT) && state.is(BlockTags.LEAVES) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get())) {
                Block leafB = BuiltInRegistries.BLOCK.get(stack.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT));
                var recipe = RecipeHelper.getPollinationRecipe(level, state, leafB.defaultBlockState());
                level.setBlock(context.getClickedPos(), TreeRegistrator.POLLINATED_LEAVES.get().defaultBlockState(), Block.UPDATE_ALL);
                if (level.getBlockEntity(context.getClickedPos()) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                    pollinatedLeavesBlockEntity.setLeafA(state.getBlock());
                    pollinatedLeavesBlockEntity.setLeafB(leafB);

                    ItemStack result = ItemStack.EMPTY;
                    ResourceLocation backupItem = level.random.nextBoolean() ? BuiltInRegistries.BLOCK.getKey(state.getBlock()) : stack.get(TreeRegistrator.POLLEN_BLOCK_COMPONENT);
                    if (recipe == null && backupItem != null) {
                        var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(backupItem.getNamespace(), backupItem.getPath().replace("leaves", "sapling")));
                        if (item != null) {
                            result = new ItemStack(item);
                        }
                    } else if (recipe != null){
                        result = recipe.value().result;
                    }
                    pollinatedLeavesBlockEntity.setResult(result);
                    pollinatedLeavesBlockEntity.setChanged();
                    if (!context.getPlayer().isCreative()) {
                        stack.shrink(1);
                    }
                    context.getPlayer().swing(context.getHand());
                    level.levelEvent(2005, context.getClickedPos(), 0);
                }
            }
        }
        return super.useOn(context);
    }
}
