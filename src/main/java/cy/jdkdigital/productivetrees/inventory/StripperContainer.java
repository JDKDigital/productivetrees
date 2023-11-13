package cy.jdkdigital.productivetrees.inventory;

import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.container.ManualSlotItemHandler;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.Stripper;
import cy.jdkdigital.productivetrees.common.block.entity.StripperBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StripperContainer extends cy.jdkdigital.productivebees.container.AbstractContainer
{
    public final StripperBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public StripperContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public StripperContainer(final int windowId, final Inventory playerInventory, final StripperBlockEntity blockEntity) {
        super(ProductiveTrees.STRIPPER_MENU.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            // Input slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, StripperBlockEntity.SLOT_IN, 44, 25));
            // Axe slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, StripperBlockEntity.SLOT_AXE, 44, 44));
            // Output slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, StripperBlockEntity.SLOT_OUT, 116, 34));
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
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
