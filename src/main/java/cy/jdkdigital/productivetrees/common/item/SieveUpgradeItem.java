package cy.jdkdigital.productivetrees.common.item;

import cy.jdkdigital.productivelib.common.item.AbstractUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SieveUpgradeItem extends AbstractUpgradeItem
{
    public SieveUpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);

        tooltip.add(Component.translatable("productivebees.information.upgrade.upgrade_pollen_sieve").withStyle(ChatFormatting.GOLD));
    }
}
