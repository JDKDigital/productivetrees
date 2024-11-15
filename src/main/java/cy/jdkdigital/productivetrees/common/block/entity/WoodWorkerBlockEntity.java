package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.inventory.WoodWorkerContainer;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoodWorkerBlockEntity extends CapabilityBlockEntity implements MenuProvider
{
    protected int tickCounter = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public static int SLOT_AXE = 2;
    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(3, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{SLOT_OUT};
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == SLOT_AXE && level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };

    private boolean canProcess(ItemStack stack) {
        var stripped = TreeUtil.getStrippedItem(stack);

        return !stripped.isEmpty() && !ItemStack.isSameItemSameComponents(stack, stripped);
    }

    public WoodWorkerBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.WOOD_WORKER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.STRIPPER.get().getDescriptionId());
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodWorkerBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 10 == 0 && level instanceof ServerLevel serverLevel) {
        }
    }

    public ItemStack getAxe() {
        return inventoryHandler.getStackInSlot(SLOT_AXE);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new WoodWorkerContainer(windowId, playerInventory, this);
    }
}
