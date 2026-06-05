package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class PollinatedLeavesBlockEntity extends BlockEntity
{
    private ItemStack result = ItemStack.EMPTY;
    private Block leafA;
    private Block leafB;

    public PollinatedLeavesBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TreeRegistrator.POLLINATED_LEAVES_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public Block getLeafA() {
        return this.leafA;
    }

    public void setLeafA(Block leaf) {
        this.leafA = leaf;
    }

    public Block getLeafB() {
        return this.leafB;
    }

    public void setLeafB(Block leaf) {
        this.leafB = leaf;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.loadPacketNBT(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.savePacketNBT(output);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
        this.loadPacketNBT(input);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public void loadPacketNBT(ValueInput input) {
        input.getString("leafA").ifPresent(s -> this.leafA = BuiltInRegistries.BLOCK.get(Identifier.parse(s)).map(Holder::value).orElse(null));
        input.getString("leafB").ifPresent(s -> this.leafB = BuiltInRegistries.BLOCK.get(Identifier.parse(s)).map(Holder::value).orElse(null));
        this.result = input.read("result", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    public void savePacketNBT(ValueOutput output) {
        if (leafA != null) {
            output.putString("leafA", BuiltInRegistries.BLOCK.getKey(leafA).toString());
        }
        if (leafB != null) {
            output.putString("leafB", BuiltInRegistries.BLOCK.getKey(leafB).toString());
        }
        if (result != null && !result.isEmpty()) {
            output.store("result", ItemStack.CODEC, result);
        }
    }
}
