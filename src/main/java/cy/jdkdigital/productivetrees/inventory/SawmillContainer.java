package cy.jdkdigital.productivetrees.inventory;

import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import cy.jdkdigital.productivetrees.common.block.Sawmill;
import cy.jdkdigital.productivetrees.common.block.entity.SawmillBlockEntity;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SawmillContainer extends AbstractContainer
{
    public final SawmillBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public SawmillContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public SawmillContainer(final int windowId, final Inventory playerInventory, final SawmillBlockEntity blockEntity) {
        super(TreeRegistrator.SAWMILL_MENU.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.progress;
            }

            @Override
            public void set(int value) {
                blockEntity.progress = value;
            }
        });

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SawmillBlockEntity.SLOT_IN, 44 - 13, 34));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SawmillBlockEntity.SLOT_OUT, 116 - 13, 25));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SawmillBlockEntity.SLOT_SECONDARY, 107 - 13, 43));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SawmillBlockEntity.SLOT_TERTIARY, 125 - 13, 43));

        addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 178 - 13, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8 - 13, 84);
    }

    private static SawmillBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof SawmillBlockEntity) {
            return (SawmillBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Sawmill && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
