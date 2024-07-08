package cy.jdkdigital.productivetrees.datagen;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.ModTags;
import cy.jdkdigital.productivetrees.registry.TreeFinder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import cy.jdkdigital.productivetrees.util.TreeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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
        var pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);
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
        var woodenFenceGates = tag(Tags.Blocks.FENCE_GATES_WOODEN);
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
        var spring_crops = tag(BlockTags.create(ResourceLocation.parse("sereneseasons:spring_crops")));
        var summer_crops = tag(BlockTags.create(ResourceLocation.parse("sereneseasons:summer_crops")));
        var autumn_crops = tag(BlockTags.create(ResourceLocation.parse("sereneseasons:autumn_crops")));
        var winter_crops = tag(BlockTags.create(ResourceLocation.parse("sereneseasons:winter_crops")));

        TreeFinder.trees.forEach((id, treeObject) -> {
            sapling.add(TreeUtil.getBlock(id, "_sapling"));

            spring_crops.add(TreeUtil.getBlock(id, "_sapling"));
            summer_crops.add(TreeUtil.getBlock(id, "_sapling"));
            autumn_crops.add(TreeUtil.getBlock(id, "_sapling"));
            winter_crops.add(TreeUtil.getBlock(id, "_sapling"));
            leaves.add(TreeUtil.getBlock(id, "_leaves"));
            if (treeObject.hasFruit()) {
                leaves.add(TreeUtil.getBlock(id, "_fruit"));
                spring_crops.add(TreeUtil.getBlock(id, "_fruit"));
                summer_crops.add(TreeUtil.getBlock(id, "_fruit"));
                autumn_crops.add(TreeUtil.getBlock(id, "_fruit"));
            }

            if (id.getPath().equals("purple_spiral")) {
                flowers.add(TreeUtil.getBlock(id, "_leaves"));
            }

            axeMineable.add(TreeUtil.getBlock(id, "_planks"));
            axeMineable.add(TreeUtil.getBlock(id, "_log"));
            axeMineable.add(TreeUtil.getBlock(id, "_wood"));
            axeMineable.add(TreeUtil.getBlock(id, "_stripped_log"));
            axeMineable.add(TreeUtil.getBlock(id, "_stripped_wood"));

            planks.add(TreeUtil.getBlock(id, "_planks"));

            var logTag = BlockTags.create(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, id.getPath() + "_logs"));
            tag(logTag).add(TreeUtil.getBlock(id, "_log"), TreeUtil.getBlock(id, "_wood"), TreeUtil.getBlock(id, "_stripped_log"), TreeUtil.getBlock(id, "_stripped_wood"));
            if (treeObject.isFireProof()) {
                logs.addTag(logTag);
            } else {
                logs.addTag(logTag);
            }
            stairs.add(TreeUtil.getBlock(id, "_stairs"));
            slabs.add(TreeUtil.getBlock(id, "_slab"));
            fences.add(TreeUtil.getBlock(id, "_fence"));
            fenceGates.add(TreeUtil.getBlock(id, "_fence_gate"));
            woodenFenceGates.add(TreeUtil.getBlock(id, "_fence_gate"));
            pressurePlates.add(TreeUtil.getBlock(id, "_pressure_plate"));
            buttons.add(TreeUtil.getBlock(id, "_button"));
            doors.add(TreeUtil.getBlock(id, "_door"));
            trapdoors.add(TreeUtil.getBlock(id, "_trapdoor"));
            signs.add(TreeUtil.getBlock(id, "_sign"));
            bookshelves.add(TreeUtil.getBlock(id, "_bookshelf"));
            enchantment.add(TreeUtil.getBlock(id, "_bookshelf"));
            hangingSigns.add(TreeUtil.getBlock(id, "_hanging_sign"));
            wallSigns.add(TreeUtil.getBlock(id, "_wall_sign"));
            wallHangingSigns.add(TreeUtil.getBlock(id, "_wall_hanging_sign"));

            if (treeObject.getStyle().hiveStyle() != null) {
                hives.addOptional(Objects.requireNonNull(treeObject.getId().withPath(p -> "advanced_" + p + "_beehive")));
                boxes.addOptional(Objects.requireNonNull(treeObject.getId().withPath(p ->  "expansion_box_" + p)));
            }
        });

        leaves.add(TreeRegistrator.POLLINATED_LEAVES.get());
        dof.addTag(BlockTags.DIRT).add(Blocks.FARMLAND);

        pollinatable.addTag(BlockTags.LEAVES).add(Blocks.WARPED_WART_BLOCK, Blocks.NETHER_WART_BLOCK);

        TreeRegistrator.CRATED_CROPS.forEach(cratePath ->  {
            var crateBlock = BuiltInRegistries.BLOCK.get(cratePath);
            var blockTagKey = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + cratePath.getPath().replace("_crate", "")));
            tag(blockTagKey).add(crateBlock);
            storageBlocks.addTag(blockTagKey);
            axeMineable.addTag(blockTagKey);
        });

        axeMineable.add(TreeRegistrator.TIME_TRAVELLER_DISPLAY.get());
        pickaxeMineable.add(TreeRegistrator.STRIPPER.get());
        pickaxeMineable.add(TreeRegistrator.SAWMILL.get());
        pickaxeMineable.add(TreeRegistrator.WOOD_WORKER.get());
        pickaxeMineable.add(TreeRegistrator.POLLEN_SIFTER.get());
    }

    @Override
    public String getName() {
        return "Productive Trees Block Tags Provider";
    }
}
