package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.loadPacketNBT(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        this.savePacketNBT(pTag, pRegistries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithId(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        super.onDataPacket(net, pkt, pRegistries);
        this.loadPacketNBT(pkt.getTag(), pRegistries);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (tag.contains("leafA")) {
            this.leafA = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("leafA")));
        }
        if (tag.contains("leafB")) {
            this.leafB = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("leafB")));
        }
        if (tag.contains("result")) {
            this.result = ItemStack.parse(pRegistries, tag.getCompound("result")).orElse(ItemStack.EMPTY);
        } else {
            this.result = ItemStack.EMPTY;
        }
    }

    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (leafA != null) {
            tag.putString("leafA", BuiltInRegistries.BLOCK.getKey(leafA).toString());
        }
        if (leafB != null) {
            tag.putString("leafB", BuiltInRegistries.BLOCK.getKey(leafB).toString());
        }
        if (result != null && !result.isEmpty()) {
            tag.put("result", result.save(pRegistries, new CompoundTag()));
        }
    }
}
