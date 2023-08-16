package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.Tags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, ProductiveTrees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var axe = tag(BlockTags.MINEABLE_WITH_AXE);

        var dof = tag(Tags.DIRT_OR_FARMLAND);

        var flowers = tag(BlockTags.FLOWERS);
        var planks = tag(BlockTags.PLANKS);
        var logs = tag(BlockTags.LOGS);
        var logsThatBurn = tag(BlockTags.LOGS_THAT_BURN);
        var sapling = tag(BlockTags.SAPLINGS);
        var leaves = tag(BlockTags.LEAVES);
        var slabs = tag(BlockTags.WOODEN_SLABS);
        var pressurePlates = tag(BlockTags.WOODEN_PRESSURE_PLATES);
        var stairs = tag(BlockTags.WOODEN_STAIRS);
        var fences = tag(BlockTags.WOODEN_FENCES);
        var fenceGates = tag(BlockTags.FENCE_GATES);
        var buttons = tag(BlockTags.WOODEN_BUTTONS);
//        var trapdoors = tag(BlockTags.WOODEN_TRAPDOORS);

        var hives = tag(ModTags.HIVES_BLOCK);
        var boxes = tag(ModTags.BOXES_BLOCK);

        TreeFinder.trees.forEach((id, treeObject) -> {
            sapling.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSaplingBlock().get())));
            leaves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLeafBlock().get())));

            if (id.getPath().equals("ysabella_purpurea")) {
                flowers.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLeafBlock().get())));
            }

            if (treeObject.registerWood()) {
                axe.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getPlankBlock().get())));
                axe.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLogBlock().get())));
                axe.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWoodBlock().get())));
                axe.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedLogBlock().get())));
                axe.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedWoodBlock().get())));

                planks.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getPlankBlock().get())));
                if (treeObject.isFireProof()) {
                    logs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLogBlock().get())));
                    logs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWoodBlock().get())));
                    logs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedLogBlock().get())));
                    logs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedWoodBlock().get())));
                } else {
                    logsThatBurn.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLogBlock().get())));
                    logsThatBurn.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWoodBlock().get())));
                    logsThatBurn.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedLogBlock().get())));
                    logsThatBurn.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedWoodBlock().get())));
                }
                stairs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStairsBlock().get())));
                slabs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSlabBlock().get())));
                fences.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFenceBlock().get())));
                fenceGates.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFenceGateBlock().get())));
                pressurePlates.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getPressurePlateBlock().get())));
                buttons.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getButtonBlock().get())));

                hives.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getHiveBlock().get())));
                boxes.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getExpansionBoxBlock().get())));
            }
        });

        leaves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(ProductiveTrees.POLLINATED_LEAVES.get())));
        dof.addTag(BlockTags.DIRT).add(Blocks.FARMLAND);
    }

    @Override
    public String getName() {
        return "Productive Trees Block Tags Provider";
    }
}
