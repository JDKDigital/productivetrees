package cy.jdkdigital.productivetrees.common.block.entity;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.inventory.StripperContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StripperBlockEntity extends CapabilityBlockEntity
{
    public static final UUID STRIPPER_UUID = UUID.nameUUIDFromBytes("pt_stripper".getBytes(StandardCharsets.UTF_8)); //
    protected int tickCounter = 0;

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;
    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(2, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == SLOT_IN && stack.is(ItemTags.LOGS)) {
                return true;
            }
            return slot == SLOT_OUT;
        }
    });

    public StripperBlockEntity(BlockPos pos, BlockState state) {
        super(ProductiveTrees.STRIPPER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(ProductiveTrees.STRIPPER.get().getDescriptionId());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StripperBlockEntity blockEntity) {
        if (++blockEntity.tickCounter % 7 == 0 && level instanceof ServerLevel serverLevel) {
            blockEntity.inventoryHandler.ifPresent(inv -> {
                ItemStack logs = inv.getStackInSlot(SLOT_IN);
                if (!logs.isEmpty()) {
                    var strippedLogItem = getStrippedItem(blockEntity, serverLevel, new ItemStack(logs.getItem()));
                    if (inv.insertItem(SLOT_OUT, strippedLogItem, false).isEmpty()) {
                        logs.shrink(1);
                    }
                }
            });
        }
    }

    private static ItemStack getStrippedItem(StripperBlockEntity blockEntity, ServerLevel level, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            var initialState = blockItem.getBlock().defaultBlockState();
            var stripState = AxeItem.getAxeStrippingState(initialState);
            if (stripState != null ) {
                return new ItemStack(stripState.getBlock());
            }
            ProductiveTrees.LOGGER.info("STRIPPER_UUID: " + STRIPPER_UUID);
            Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(STRIPPER_UUID, "stripper"));
            var pos = blockEntity.getBlockPos();
            var blockHit = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.DOWN, pos, false);
            UseOnContext context = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, blockHit);
            stripState = ForgeEventFactory.onToolUse(initialState, context, ToolActions.AXE_STRIP, true);
            return !stripState.equals(initialState) ? new ItemStack(initialState.getBlock()) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new StripperContainer(windowId, playerInventory, this);
    }
}
