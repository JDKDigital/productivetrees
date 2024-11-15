package cy.jdkdigital.productivetrees.common.block.entity;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivetrees.inventory.StripperContainer;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StripperBlockEntity extends CapabilityBlockEntity implements MenuProvider, UpgradeableBlockEntity
{
    protected int tickCounter = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public static int SLOT_AXE = 2;
    public static int SLOT_BARK = 3;
    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(4, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            } else if ((slot == SLOT_OUT || slot == SLOT_BARK) && !stack.is(ModTags.STRIPPER_TOOLS) && !canProcess(stack)) {
                var currentOutStack = getStackInSlot(slot);
                if (currentOutStack.isEmpty()) {
                    return true;
                }
                if (currentOutStack.getCount() < currentOutStack.getMaxStackSize()) {
                    return ItemStack.isSameItemSameComponents(stack, currentOutStack);
                }
            }
            return false;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == SLOT_AXE || slot == SLOT_IN;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            if ((slot == SLOT_IN && stack.is(ItemTags.LOGS) && canProcess(stack)) || (slot == SLOT_AXE && stack.is(ModTags.STRIPPER_TOOLS))) {
                var currentOutStack = getStackInSlot(slot);
                return currentOutStack.isEmpty() || (currentOutStack.getCount() < currentOutStack.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, currentOutStack));
            }
            return false;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{SLOT_OUT, SLOT_BARK};
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == SLOT_AXE && level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get()
    ));

    public StripperBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.STRIPPER_BLOCK_ENTITY.get(), pos, state);
    }

    private boolean canProcess(ItemStack stack) {
        var stripped = TreeUtil.getStrippedItem(stack);

        return !stripped.isEmpty() && !ItemStack.isSameItemSameComponents(stack, stripped);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.STRIPPER.get().getDescriptionId());
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StripperBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 10 == 0 && level instanceof ServerLevel serverLevel) {
            var log = blockEntity.inventoryHandler.getStackInSlot(SLOT_IN);
            var axe = blockEntity.inventoryHandler.getStackInSlot(SLOT_AXE);
            var output = blockEntity.inventoryHandler.getStackInSlot(SLOT_OUT);
            if (!log.isEmpty() && !axe.isEmpty() && (output.getCount() < output.getMaxStackSize())) {
                var strippedLogItem = TreeUtil.getStrippedItem(blockEntity, serverLevel, log);
                var speedModifier = 1 + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME.get()) + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2;
                var itemCount = Math.min(Math.min(speedModifier, log.getCount()), output.getMaxStackSize() - output.getCount());
                strippedLogItem.setCount(itemCount);
                if (!strippedLogItem.isEmpty() && blockEntity.inventoryHandler.insertItem(SLOT_OUT, strippedLogItem, false).isEmpty()) {
                    if (log.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ProductiveLogBlock logBlock) {
                        var treeObject = TreeUtil.getTree(logBlock);
                        if (treeObject != null && treeObject.getStripDrop().isPresent()) {
                            blockEntity.inventoryHandler.insertItem(SLOT_BARK, treeObject.getStripDropStack().copy(), false);
                        }
                    }
                    log.shrink(itemCount);
                    if (axe.isDamageableItem()) {
                        Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(TreeUtil.STRIPPER_UUID, "stripper"));
                        axe.hurtAndBreak(1, fakePlayer, EquipmentSlot.MAINHAND);
                    }
                }
            }
        }
    }

    public ItemStack getAxe() {
        return inventoryHandler.getStackInSlot(SLOT_AXE);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new StripperContainer(windowId, playerInventory, this);
    }
}
