package cy.jdkdigital.productivetrees.inventory;

import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import cy.jdkdigital.productivetrees.common.block.PollenSifter;
import cy.jdkdigital.productivetrees.common.block.entity.PollenSifterBlockEntity;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PollenSifterContainer extends AbstractContainer
{
    public final PollenSifterBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public PollenSifterContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public PollenSifterContainer(final int windowId, final Inventory playerInventory, final PollenSifterBlockEntity blockEntity) {
        super(TreeRegistrator.POLLEN_SIFTER_MENU.get(), windowId);

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

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) inv, PollenSifterBlockEntity.SLOT_IN, 44, 34));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) inv, PollenSifterBlockEntity.SLOT_OUT, 109, 34));
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static PollenSifterBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof PollenSifterBlockEntity) {
            return (PollenSifterBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof PollenSifter && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
