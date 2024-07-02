//package cy.jdkdigital.productivetrees.datagen;
//
//import cy.jdkdigital.productivetrees.ProductiveTrees;
//import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
//import net.minecraft.advancements.Advancement;
//import net.minecraft.advancements.DisplayInfo;
//import net.minecraft.advancements.AdvancementType;
//import net.minecraft.advancements.critereon.*;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.data.PackOutput;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.ItemLike;
//import net.neoforged.neoforge.common.data.ExistingFileHelper;
//import net.neoforged.neoforge.common.data.internal.NeoForgeAdvancementProvider;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Consumer;
//
//public class AdvancementProvider extends NeoForgeAdvancementProvider
//{
//    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
//        super(output, registries, existingFileHelper, List.of(AdvancementProvider::generate));
//    }
//
//    private static void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
////        ResourceLocation amateurArcheologist = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "adv1");
////        Advancement parent = advancement(amateurArcheologist, Items.EGG)
////                .parent(new ResourceLocation("adventure/root"))
////                .addCriterion("find_artifact", InventoryChangeTrigger.TriggerInstance.hasItems(
////                        ItemPredicate.Builder.item().of(ItemTags.AXOLOTL_TEMPT_ITEMS).build()
////                )).save(saver, amateurArcheologist, existingFileHelper);
//
//        // TODO advancements for hidden trees
//
//        var coconutTag = new CompoundTag();
//        coconutTag.put("BlockState", NbtUtils.writeBlockState(TreeRegistrator.COCONUT_SPROUT.get().defaultBlockState()));
//        ResourceLocation chestSlayer = ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "adv2");
//        advancement(chestSlayer, BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveTrees.MODID, "coconut")))
//                .parent(ResourceLocation.withDefaultNamespace("adventure/root"))
//                .addCriterion("death_by_coconut", KilledTrigger.TriggerInstance.entityKilledPlayer(
//                        EntityPredicate.Builder.entity().of(EntityType.FALLING_BLOCK).nbt(new NbtPredicate(coconutTag))
//                )).save(saver, chestSlayer, existingFileHelper);
//    }
//
//    private static Advancement.Builder advancement(ResourceLocation id, ItemLike icon) {
//        return Advancement.Builder.advancement().display(display(id.getPath(), icon));
//    }
//
//    private static DisplayInfo display(String title, ItemLike icon) {
//        return new DisplayInfo(new ItemStack(icon),
//                Component.translatable("%s.advancements.%s.title".formatted(ProductiveTrees.MODID, title)),
//                Component.translatable("%s.advancements.%s.description".formatted(ProductiveTrees.MODID, title)),
//                null, AdvancementType.TASK, true, true, false
//        );
//    }
//}
