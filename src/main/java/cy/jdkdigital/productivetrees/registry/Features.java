package cy.jdkdigital.productivetrees.registry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.common.feature.EntityPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafPlacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.FruitLeafReplacerDecorator;
import cy.jdkdigital.productivetrees.common.feature.TrunkVineDecorator;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.RegistryObject;

public class Features
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> NULL = FeatureUtils.createKey(ProductiveTrees.MODID + ":null");

    public static RegistryObject<TreeDecoratorType<FruitLeafReplacerDecorator>> FRUIT_LEAF_REPLACER = ProductiveTrees.TREE_DECORATORS.register("fruit_leaf_replacer", () -> new TreeDecoratorType<>(FruitLeafReplacerDecorator.CODEC));
    public static RegistryObject<TreeDecoratorType<FruitLeafPlacerDecorator>> FRUIT_LEAF_PLACER = ProductiveTrees.TREE_DECORATORS.register("fruit_leaf_placer", () -> new TreeDecoratorType<>(FruitLeafPlacerDecorator.CODEC));
    public static RegistryObject<TreeDecoratorType<EntityPlacerDecorator>> ENTITY_PLACER = ProductiveTrees.TREE_DECORATORS.register("entity_placer", () -> new TreeDecoratorType<>(EntityPlacerDecorator.CODEC));
    public static RegistryObject<TreeDecoratorType<TrunkVineDecorator>> TRUNK_VINE = ProductiveTrees.TREE_DECORATORS.register("trunk_vine", () -> new TreeDecoratorType<>(TrunkVineDecorator.CODEC));
}
