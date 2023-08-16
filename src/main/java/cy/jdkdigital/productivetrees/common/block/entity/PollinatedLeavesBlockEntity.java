package cy.jdkdigital.productivetrees.common.block.entity;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class PollinatedLeavesBlockEntity extends BlockEntity
{
    private ItemStack result = ItemStack.EMPTY;
    private Block leafA;
    private Block leafB;

    public PollinatedLeavesBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ProductiveTrees.POLLINATED_LEAVES_BLOCK_ENTITY.get(), blockPos, blockState);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        this.loadPacketNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.savePacketNBT(tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithId();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.loadPacketNBT(pkt.getTag());
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public void loadPacketNBT(CompoundTag tag) {
        if (tag.contains("leafA")) {
            this.leafA = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("leafA")));
        }
        if (tag.contains("leafB")) {
            this.leafB = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("leafB")));
        }
        if (tag.contains("result")) {
            this.result = ItemStack.of(tag.getCompound("result"));
        }
    }

    public void savePacketNBT(CompoundTag tag) {
        if (leafA != null) {
            tag.putString("leafA", ForgeRegistries.BLOCKS.getKey(leafA).toString());
        }
        if (leafB != null) {
            tag.putString("leafB", ForgeRegistries.BLOCKS.getKey(leafB).toString());
        }
        if (result != null) {
            tag.put("result", result.save(new CompoundTag()));
        }
    }
}
