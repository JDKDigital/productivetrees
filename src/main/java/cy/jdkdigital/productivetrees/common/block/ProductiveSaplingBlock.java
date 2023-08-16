package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProductiveSaplingBlock extends SaplingBlock
{
    private final TreeObject treeObject;

    public ProductiveSaplingBlock(AbstractTreeGrower grower, Properties properties, TreeObject treeObject) {
        super(grower, properties);
        this.treeObject = treeObject;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(treeObject.getSoil());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p_55994_) {
        return treeObject.canForceGrowth();
    }

    public TreeObject getTree() {
        return treeObject;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> tooltips, TooltipFlag flag) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveSaplingBlock saplingBlock) {
            tooltips.add(Component.translatable("block." + ProductiveTrees.MODID + "." + saplingBlock.treeObject.getId().getPath() + ".latin").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, blockGetter, tooltips, flag);
    }
}
