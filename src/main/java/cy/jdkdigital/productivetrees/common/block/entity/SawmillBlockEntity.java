package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivetrees.inventory.SawmillContainer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SawmillBlockEntity extends CapabilityBlockEntity implements MenuProvider, IUpgradeableBlockEntity
{
    protected int tickCounter = 0;
    public int tickRate = 10;
    public int recipeTime = 200;
    public int progress = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public static int SLOT_SECONDARY = 2;
    public static int SLOT_TERTIARY = 3;
    public final InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(4, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack, boolean fromAutomation) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            return slot != SLOT_IN && !canProcess(stack);
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack stack) {
            if ((slot == SLOT_IN && canProcess(stack))) {
                var currentOutStack = getStackInSlot(slot);
                return currentOutStack.isEmpty() || (currentOutStack.getCount() < currentOutStack.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, currentOutStack));
            }
            return false;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == SLOT_IN;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return slot == SLOT_IN;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{SLOT_OUT, SLOT_SECONDARY, SLOT_TERTIARY};
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get()
    ));

    public SawmillBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.SAWMILL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.SAWMILL.get().getDescriptionId());
    }

    private boolean canProcess(ItemStack stack) {
        return TreeUtil.getSawmillRecipe(level, stack) != null;
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SawmillBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % blockEntity.tickRate == 0 && level instanceof ServerLevel) {
            var log = blockEntity.inventoryHandler.getStackInSlot(SLOT_IN);
            if (log.isEmpty()) {
                return;
            }
            var recipe = TreeUtil.getSawmillRecipe(level, log);
            if (recipe == null) {
                return;
            }
            var speedModifier = 1 + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME.get()) + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2;
            blockEntity.progress += blockEntity.tickRate * speedModifier;
            if (blockEntity.progress >= blockEntity.recipeTime) {
                var primary = recipe.value().output();
                var secondary = recipe.value().secondary();
                var tertiary = recipe.value().tertiary();
                boolean fits = blockEntity.inventoryHandler.insertItem(SLOT_OUT, primary.copy(), true).isEmpty()
                        && (secondary.isEmpty() || blockEntity.inventoryHandler.insertItem(SLOT_SECONDARY, secondary.copy(), true).isEmpty())
                        && (tertiary.isEmpty() || blockEntity.inventoryHandler.insertItem(SLOT_TERTIARY, tertiary.copy(), true).isEmpty());
                if (fits) {
                    blockEntity.inventoryHandler.insertItem(SLOT_OUT, primary.copy(), false);
                    if (!secondary.isEmpty()) {
                        blockEntity.inventoryHandler.insertItem(SLOT_SECONDARY, secondary.copy(), false);
                    }
                    if (!tertiary.isEmpty()) {
                        blockEntity.inventoryHandler.insertItem(SLOT_TERTIARY, tertiary.copy(), false);
                    }
                    log.shrink(1);
                    blockEntity.inventoryHandler.setStackInSlot(SLOT_IN, log);
                    blockEntity.progress = 0;
                } else {
                    blockEntity.progress = blockEntity.recipeTime;
                }
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new SawmillContainer(windowId, playerInventory, this);
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
