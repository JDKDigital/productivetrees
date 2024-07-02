package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.inventory.SawmillContainer;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SawmillBlockEntity extends CapabilityBlockEntity implements MenuProvider
{
    protected int tickCounter = 0;
    public int tickRate = 10;
    public int recipeTime = 200;
    public int progress = 0;
    private ItemStack buffer = ItemStack.EMPTY;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    public static int SLOT_SECONDARY = 2;
    public static int SLOT_TERTIARY = 3;
    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(4, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isInputSlotItem(slot, stack)) {
                return true;
            }
            return slot != SLOT_IN;
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

    private boolean canProcess(ItemStack stack) {
        var recipe = TreeUtil.getSawmillRecipe(level, stack);

        return recipe != null;
    }

    public SawmillBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.SAWMILL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.SAWMILL.get().getDescriptionId());
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SawmillBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % blockEntity.tickRate == 0 && level instanceof ServerLevel serverLevel) {
            var log = blockEntity.inventoryHandler.getStackInSlot(SLOT_IN);
            var output = blockEntity.inventoryHandler.getStackInSlot(SLOT_OUT);
            if (!blockEntity.buffer.isEmpty() && blockEntity.inventoryHandler.insertItem(SLOT_OUT, blockEntity.buffer.copy(), true).isEmpty()) {
                blockEntity.inventoryHandler.insertItem(SLOT_OUT, blockEntity.buffer.copy(), false);
                blockEntity.buffer = ItemStack.EMPTY;
            } else if (!log.isEmpty() && (output.isEmpty() || output.getCount() < output.getMaxStackSize())) {
                blockEntity.progress+= blockEntity.tickRate;
                if (blockEntity.progress >= blockEntity.recipeTime) {
                    var recipe = TreeUtil.getSawmillRecipe(level, log);
                    if (recipe != null) {
                        var leftOver = blockEntity.inventoryHandler.insertItem(SLOT_OUT, recipe.value().planks.copy(), false);
                        if (!leftOver.isEmpty()) {
                            blockEntity.buffer = leftOver;
                        }
                        if (!recipe.value().secondary.isEmpty()) {
                            var tLeftover = blockEntity.inventoryHandler.insertItem(SLOT_SECONDARY, recipe.value().secondary.get().copy(), false);

                            if (!recipe.value().tertiary.isEmpty()) {
                                blockEntity.inventoryHandler.insertItem(SLOT_TERTIARY, recipe.value().tertiary.get().copy(), false);
                            } else if(!tLeftover.isEmpty()) {
                                // Insert secondary output into tertiary slot
                                blockEntity.inventoryHandler.insertItem(SLOT_TERTIARY, tLeftover, false);
                            }
                        }
                        log.shrink(1);
                    }
                    blockEntity.progress = 0;
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
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        this.progress = tag.getInt("RecipeProgress");
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        tag.putInt("RecipeProgress", this.progress);
    }
}
