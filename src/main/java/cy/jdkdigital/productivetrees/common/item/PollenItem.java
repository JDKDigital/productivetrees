package cy.jdkdigital.productivetrees.common.item;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import cy.jdkdigital.productivetrees.recipe.RecipeHelper;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PollenItem extends Item
{
    public PollenItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        if (stack.getTag() != null && stack.getTag().contains("Block")) {
            Block leaf = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTag().getString("Block")));
            if (leaf != null) {
                list.add(Component.translatable(ProductiveTrees.MODID + ".pollen.name", Component.translatable(leaf.getDescriptionId()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_BLUE));
            }
        }
        list.add(Component.translatable(ProductiveTrees.MODID + ".information.pollen").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (context.getPlayer() != null) {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            BlockState state = level.getBlockState(context.getClickedPos());
            if (stack.getTag() != null && stack.getTag().contains("Block") && state.is(BlockTags.LEAVES) && !state.is(ProductiveTrees.POLLINATED_LEAVES.get())) {
                Block leafB = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTag().getString("Block")));
                if (leafB != null) {
                    var recipe = RecipeHelper.getRecipe(level, state, leafB.defaultBlockState());
                    level.setBlock(context.getClickedPos(), ProductiveTrees.POLLINATED_LEAVES.get().defaultBlockState(), Block.UPDATE_ALL);
                    if (level.getBlockEntity(context.getClickedPos()) instanceof PollinatedLeavesBlockEntity pollinatedLeavesBlockEntity) {
                        pollinatedLeavesBlockEntity.setLeafA(state.getBlock());
                        pollinatedLeavesBlockEntity.setLeafB(leafB);

                        ItemStack result = ItemStack.EMPTY;
                        ResourceLocation backupItem = level.random.nextBoolean() ? ForgeRegistries.BLOCKS.getKey(state.getBlock()) : new ResourceLocation(stack.getTag().getString("Block"));
                        if (recipe == null && backupItem != null) {
                            var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(backupItem.getNamespace(), backupItem.getPath().replace("leaves", "sapling")));
                            if (item != null) {
                                result = new ItemStack(item);
                            }
                        } else if (recipe != null){
                            result = recipe.result;
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
        }
        return super.useOn(context);
    }
}
