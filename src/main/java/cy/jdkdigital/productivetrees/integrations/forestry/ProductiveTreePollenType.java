package cy.jdkdigital.productivetrees.integrations.forestry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import forestry.api.genetics.pollen.IPollen;
import forestry.api.genetics.pollen.IPollenType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class ProductiveTreePollenType implements IPollenType<TreeObject>
{
    static ResourceLocation ID = new ResourceLocation(ProductiveTrees.MODID, "tree");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public boolean canPollinate(LevelAccessor levelAccessor, BlockPos blockPos, @Nullable Object o) {
        return levelAccessor.getBlockState(blockPos).getBlock() instanceof ProductiveLeavesBlock;
    }

    @Nullable
    @Override
    public IPollen<TreeObject> tryCollectPollen(LevelAccessor levelAccessor, BlockPos blockPos, @Nullable Object o) {
        if (levelAccessor.getBlockState(blockPos).getBlock() instanceof ProductiveLeavesBlock leavesBlock) {
            return new Pollen(leavesBlock.getTree());
        }
        return null;
    }

    @Override
    public boolean tryPollinate(LevelAccessor levelAccessor, BlockPos blockPos, TreeObject treeObject, @Nullable Object o) {
        var state = levelAccessor.getBlockState(blockPos);
        if (levelAccessor instanceof Level level && state.is(BlockTags.LEAVES) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get())) {
            TreeUtil.tryPollinatePosition(level, blockPos, TreeUtil.getBlock(treeObject.getId(), "_leaves"));
        }
        return false;
    }

    @Override
    public IPollen<TreeObject> readNbt(Tag tag) {
        if (tag instanceof StringTag stringTag) {
            return new Pollen(TreeFinder.trees.get(ResourceLocation.tryParse(stringTag.getAsString())));
        }
        return null;
    }

    public class Pollen implements IPollen<TreeObject>
    {
        private final TreeObject tree;

        public Pollen(TreeObject tree) {
            this.tree = tree;
        }

        @Override
        public IPollenType<TreeObject> getType() {
            return ProductiveTreePollenType.this;
        }

        @Override
        public TreeObject getPollen() {
            return this.tree;
        }

        @Nullable
        @Override
        public Tag writeNbt() {
            return StringTag.valueOf(this.tree.getId().toString());
        }

        @Override
        public ItemStack createStack() {
            return TreeUtil.getPollen(TreeUtil.getBlock(this.tree.getId(), "_leaves"));
        }
    }
}
