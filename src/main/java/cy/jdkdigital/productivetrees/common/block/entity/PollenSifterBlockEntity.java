package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.inventory.PollenSifterContainer;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollenSifterBlockEntity extends CapabilityBlockEntity implements MenuProvider
{
    protected int tickCounter = 0;
    public int tickRate = 10;
    public int recipeTime = 600;
    public int progress = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.BlockEntityItemStackHandler(2, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            return slot != SLOT_IN;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == SLOT_IN;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            if (slot == SLOT_IN && canProcess(stack)) {
                var currentOutStack = getStackInSlot(slot);
                return currentOutStack.isEmpty() || (currentOutStack.getCount() < currentOutStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(stack, currentOutStack));
            }
            return false;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return slot == SLOT_IN;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{SLOT_OUT};
        }
    });

    private boolean canProcess(ItemStack stack) {
        return stack.is(ModTags.POLLINATABLE_ITEM);
    }

    public PollenSifterBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.POLLEN_SIFTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.POLLEN_SIFTER.get().getDescriptionId());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PollenSifterBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % blockEntity.tickRate == 0 && level instanceof ServerLevel serverLevel) {
            blockEntity.inventoryHandler.ifPresent(inv -> {
                var leaf = inv.getStackInSlot(SLOT_IN);
                    var output = inv.getStackInSlot(SLOT_OUT);
                    if (!leaf.isEmpty() && leaf.getItem() instanceof BlockItem blockItem && (output.isEmpty() || output.is(TreeRegistrator.POLLEN.get()))) {
                        var pollenStack = TreeUtil.getPollen(blockItem.getBlock());
                        if (output.isEmpty() || ItemHandlerHelper.canItemStacksStack(output, pollenStack)) {
                            blockEntity.progress += blockEntity.tickRate;
                            if (blockEntity.progress >= blockEntity.recipeTime) {
                                if (level.random.nextInt(100) <= 10) {
                                    inv.insertItem(SLOT_OUT, pollenStack, false);
                                }
                                leaf.shrink(1);
                                blockEntity.progress = 0;
                            }
                        }
                    }
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new PollenSifterContainer(windowId, playerInventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
        this.progress = tag.getInt("RecipeProgress");
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        tag.putInt("RecipeProgress", this.progress);
    }
}
