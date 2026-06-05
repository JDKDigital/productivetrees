package cy.jdkdigital.productivetrees.common.item;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class SaplingBlockItem extends BlockItem
{
    public SaplingBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pDisplay, Consumer<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
        if (getBlock() instanceof ProductiveSaplingBlock saplingBlock) {
            pTootipComponents.accept(Component.translatable("block." + ProductiveTrees.MODID + "." + saplingBlock.getTree().getId().getPath() + ".latin").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
            super.appendHoverText(pStack, pContext, pDisplay, pTootipComponents, pTooltipFlag);
            String configurations = "";
            if (!saplingBlock.getTree().getFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                configurations += "1x1 ";
            }
            if (!saplingBlock.getTree().getMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                configurations += switch (saplingBlock.getTree().getMegaConfiguration()) {
                    case 5 -> "5x5 ";
                    case 3 -> "3x3 ";
                    default -> "2x2 ";
                };
            }
            if (!saplingBlock.getTree().getLargeMegaFeature().equals(TreeRegistrator.NULL_FEATURE)) {
                int largeWidth = 2 * (saplingBlock.getTree().getMegaConfiguration() - 1) + 1;
                configurations += largeWidth + "x" + largeWidth + " ";
            }
            if (!configurations.isEmpty()) {
                pTootipComponents.accept(Component.translatable(ProductiveTrees.MODID + ".sapling.configurations", configurations).withStyle(ChatFormatting.GOLD));
            }
        } else {
            super.appendHoverText(pStack, pContext, pDisplay, pTootipComponents, pTooltipFlag);
        }
    }
}
