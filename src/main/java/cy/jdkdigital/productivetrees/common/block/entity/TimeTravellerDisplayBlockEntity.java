package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class TimeTravellerDisplayBlockEntity extends CapabilityBlockEntity
{
    public static int SLOT_IN = 0;
    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(1, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };

    public TimeTravellerDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(TreeRegistrator.TIME_TRAVELLER_DISPLAY_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get().getDescriptionId());
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    public ItemStack getItem() {
        return inventoryHandler.getStackInSlot(SLOT_IN);
    }
}
