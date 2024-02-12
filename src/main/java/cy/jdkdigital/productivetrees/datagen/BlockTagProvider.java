package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
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
        var axeMineable = tag(BlockTags.MINEABLE_WITH_AXE);
        var storageBlocks = tag(Tags.Blocks.STORAGE_BLOCKS);

        var dof = tag(ModTags.DIRT_OR_FARMLAND);
        var pollinatable = tag(ModTags.POLLINATABLE);

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
        var doors = tag(BlockTags.WOODEN_DOORS);
        var trapdoors = tag(BlockTags.WOODEN_TRAPDOORS);
        var bookshelves = tag(Tags.Blocks.BOOKSHELVES);
        var enchantment = tag(BlockTags.ENCHANTMENT_POWER_PROVIDER);
        var signs = tag(BlockTags.STANDING_SIGNS);
        var hangingSigns = tag(BlockTags.CEILING_HANGING_SIGNS);
        var wallHangingSigns = tag(BlockTags.WALL_HANGING_SIGNS);
        var wallSigns = tag(BlockTags.WALL_SIGNS);

        // PBees
        var hives = tag(cy.jdkdigital.productivebees.init.ModTags.HIVES_BLOCK);
        var boxes = tag(cy.jdkdigital.productivebees.init.ModTags.BOXES_BLOCK);

        // Serene seasons
        var spring_crops = tag(BlockTags.create(new ResourceLocation("sereneseasons:spring_crops")));
        var summer_crops = tag(BlockTags.create(new ResourceLocation("sereneseasons:summer_crops")));
        var autumn_crops = tag(BlockTags.create(new ResourceLocation("sereneseasons:autumn_crops")));
        var winter_crops = tag(BlockTags.create(new ResourceLocation("sereneseasons:winter_crops")));

        TreeFinder.trees.forEach((id, treeObject) -> {
            sapling.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSaplingBlock().get())));
            spring_crops.add(treeObject.getSaplingBlock().get());
            summer_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSaplingBlock().get())));
            autumn_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSaplingBlock().get())));
            winter_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSaplingBlock().get())));
            leaves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLeafBlock().get())));
            if (treeObject.hasFruit()) {
                leaves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFruitBlock().get())));
                spring_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFruitBlock().get())));
                summer_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFruitBlock().get())));
                autumn_crops.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getFruitBlock().get())));
            }

            if (id.getPath().equals("purple_spiral")) {
                flowers.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLeafBlock().get())));
            }

            axeMineable.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getPlankBlock().get())));
            axeMineable.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getLogBlock().get())));
            axeMineable.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWoodBlock().get())));
            axeMineable.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedLogBlock().get())));
            axeMineable.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getStrippedWoodBlock().get())));

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
            doors.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getDoorBlock().get())));
            trapdoors.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getTrapdoorBlock().get())));
            signs.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getSignBlock().get())));
            bookshelves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getBookshelfBlock().get())));
            enchantment.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getBookshelfBlock().get())));
            hangingSigns.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getHangingSignBlock().get())));
            wallSigns.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWallSignBlock().get())));
            wallHangingSigns.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getWallHangingSignBlock().get())));

            if (treeObject.getStyle().hiveStyle() != null) {
                hives.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getHiveBlock().get())));
                boxes.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(treeObject.getExpansionBoxBlock().get())));
            }
        });

        leaves.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(TreeRegistrator.POLLINATED_LEAVES.get())));
        dof.addTag(BlockTags.DIRT).add(Blocks.FARMLAND);

        pollinatable.addTag(BlockTags.LEAVES).add(Blocks.WARPED_WART_BLOCK, Blocks.NETHER_WART_BLOCK);

        TreeRegistrator.CRATED_CROPS.forEach(cratePath ->  {
            var crateBlock = ForgeRegistries.BLOCKS.getValue(cratePath);
            var blockTagKey = BlockTags.create(new ResourceLocation("forge", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            tag(blockTagKey).add(crateBlock);
            storageBlocks.addTag(blockTagKey);
            axeMineable.addTag(blockTagKey);
        });
    }

    @Override
    public String getName() {
        return "Productive Trees Block Tags Provider";
    }
}
