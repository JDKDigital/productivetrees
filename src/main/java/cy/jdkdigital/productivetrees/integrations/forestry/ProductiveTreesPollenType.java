package cy.jdkdigital.productivetrees.integrations.forestry;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.genetics.pollen.IPollen;
import forestry.api.genetics.pollen.IPollenType;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import org.jetbrains.annotations.Nullable;

class ProductiveTreesPollenType implements IPollenType<TreeObject> {
	public static final ResourceLocation ID = new ResourceLocation(ProductiveTrees.MODID, "pollen");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean canPollinate(LevelAccessor level, BlockPos pos, @Nullable Object pollinator) {
		return level.getBlockState(pos).getBlock() instanceof ProductiveLeavesBlock;
	}

	@Nullable
	@Override
	public IPollen<TreeObject> tryCollectPollen(LevelAccessor level, BlockPos pos, @Nullable Object pollinator) {
		if (level.getBlockState(pos).getBlock() instanceof ProductiveLeavesBlock leaves) {
			return new Pollen(leaves.getTree());
		}

		return null;
	}

	@Override
	public boolean tryPollinate(LevelAccessor levelAccessor, BlockPos pos, TreeObject tree, @Nullable Object pollinator) {
		BlockState state = levelAccessor.getBlockState(pos);
		if (levelAccessor instanceof Level level && state.is(BlockTags.LEAVES) && !state.is(TreeRegistrator.POLLINATED_LEAVES.get())) {
			TreeUtil.tryPollinatePosition(level, pos, TreeUtil.getBlock(tree.getId(), "_leaves"));
		}
		return false;
	}

	@Override
	public IPollen<TreeObject> readNbt(Tag nbt) {
		if (nbt instanceof StringTag string) {
			return new Pollen(TreeFinder.trees.get(ResourceLocation.tryParse(string.getAsString())));
		}

		return null;
	}

	private class Pollen implements IPollen<TreeObject> {
		private final TreeObject tree;

		private Pollen(TreeObject tree) {
			this.tree = tree;
		}

		@Override
		public IPollenType<TreeObject> getType() {
			return ProductiveTreesPollenType.this;
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
