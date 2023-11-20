package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.inventory.WoodWorkerContainer;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoodWorkerBlockEntity extends CapabilityBlockEntity
{
    protected int tickCounter = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public static int SLOT_AXE = 2;
    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(3, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            if (slot == SLOT_OUT && !stack.is(ItemTags.AXES)) {
                var currentOutStack = getStackInSlot(slot);
                if (currentOutStack.isEmpty() || currentOutStack.getCount() < currentOutStack.getMaxStackSize()) {
                    return ItemHandlerHelper.canItemStacksStack(stack, currentOutStack) && !canProcess(stack);
                }
            }
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            if ((slot == SLOT_IN && stack.is(ItemTags.LOGS)) || (slot == SLOT_AXE && stack.is(ItemTags.AXES))) {
                var currentOutStack = getStackInSlot(slot);
                return currentOutStack.isEmpty() || (currentOutStack.getCount() < currentOutStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(stack, currentOutStack));
            }
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
    });

    private boolean canProcess(ItemStack stack) {
        var stripped = TreeUtil.getStrippedItem(stack);

        return !stripped.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, stripped);
    }

    public WoodWorkerBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.WOOD_WORKER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.STRIPPER.get().getDescriptionId());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodWorkerBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 10 == 0 && level instanceof ServerLevel serverLevel) {
            blockEntity.inventoryHandler.ifPresent(inv -> {
            });
        }
    }

    public ItemStack getAxe() {
        return inventoryHandler.map(h -> h.getStackInSlot(SLOT_AXE)).orElse(ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new WoodWorkerContainer(windowId, playerInventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
