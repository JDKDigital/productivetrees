package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivetrees.inventory.PollenSifterContainer;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PollenSifterBlockEntity extends CapabilityBlockEntity implements MenuProvider, IUpgradeableBlockEntity
{
    protected int tickCounter = 0;
    public int tickRate = 10;
    public int recipeTime = 600;
    public int progress = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public final InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(2, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack, boolean fromAutomation) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            return slot != SLOT_IN && !canProcess(stack);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == SLOT_IN;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            if (slot == SLOT_IN && canProcess(stack)) {
                var currentOutStack = getStackInSlot(slot);
                return currentOutStack.isEmpty() || (currentOutStack.getCount() < currentOutStack.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, currentOutStack));
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
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get()
    ));

    public PollenSifterBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.POLLEN_SIFTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.POLLEN_SIFTER.get().getDescriptionId());
    }

    private boolean canProcess(ItemStack stack) {
        return stack.is(ModTags.POLLINATABLE_ITEM);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, PollenSifterBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % blockEntity.tickRate == 0 && level instanceof ServerLevel serverLevel) {
            var leaf = blockEntity.inventoryHandler.getStackInSlot(SLOT_IN);
            var output = blockEntity.inventoryHandler.getStackInSlot(SLOT_OUT);
            if (!leaf.isEmpty() && leaf.getItem() instanceof BlockItem blockItem && (output.isEmpty() || output.is(TreeRegistrator.POLLEN.get()))) {
                var pollenStack = TreeUtil.getPollen(blockItem.getBlock());
                if (output.isEmpty() || ItemStack.isSameItemSameComponents(output, pollenStack)) {
                    var speedModifier = 1 + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME.get()) + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2;
                    blockEntity.progress+= blockEntity.tickRate * speedModifier;
                    if (blockEntity.progress >= blockEntity.recipeTime) {
                        if (level.getRandom().nextInt(100) <= 10) {
                            blockEntity.inventoryHandler.insertItem(SLOT_OUT, pollenStack, false);
                        }
                        leaf.shrink(1);
                        blockEntity.inventoryHandler.setStackInSlot(SLOT_IN, leaf);
                        blockEntity.progress = 0;
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new PollenSifterContainer(windowId, playerInventory, this);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        this.progress = input.getIntOr("RecipeProgress", 0);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        output.putInt("RecipeProgress", this.progress);
    }
}
