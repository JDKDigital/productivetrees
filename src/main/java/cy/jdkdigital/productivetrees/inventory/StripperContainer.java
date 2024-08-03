package cy.jdkdigital.productivetrees.inventory;

import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import cy.jdkdigital.productivetrees.common.block.Stripper;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StripperContainer extends AbstractContainer
{
    public final StripperBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public StripperContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public StripperContainer(final int windowId, final Inventory playerInventory, final StripperBlockEntity blockEntity) {
        super(TreeRegistrator.STRIPPER_MENU.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

            // Input slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, StripperBlockEntity.SLOT_IN, 44 - 13, 25));
        // Axe slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, StripperBlockEntity.SLOT_AXE, 44 - 13, 44));
        // Output slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, StripperBlockEntity.SLOT_OUT, 116 - 13, 25));
        // Bark slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, StripperBlockEntity.SLOT_BARK, 116 - 13, 44));

        addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 178 - 13, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8 - 13, 84);
    }

    private static StripperBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof StripperBlockEntity) {
            return (StripperBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Stripper && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
