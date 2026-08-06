package cy.jdkdigital.productivetrees.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.Set;

/**
 * Tolerant variant of vanilla {@code LootItemBlockStatePropertyCondition}. The block holder is decoded with an
 * AIR fallback and the vanilla state-definition validation is dropped, so the condition still parses when minimal
 * mode leaves the referenced block unregistered (vanilla's required block holder hard-fails, which drops the whole
 * loot table on load). Runtime behaviour is identical when the block is present; the door tables this is used in
 * are never rolled while the door block is absent, so the AIR fallback is only ever exercised at parse time.
 */
public record OptionalBlockStatePropertyCondition(Holder<Block> block, Optional<StatePropertiesPredicate> properties) implements LootItemCondition {
    public static final MapCodec<OptionalBlockStatePropertyCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").orElse(Blocks.AIR.builtInRegistryHolder()).forGetter(OptionalBlockStatePropertyCondition::block),
                    StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(OptionalBlockStatePropertyCondition::properties)
            ).apply(instance, OptionalBlockStatePropertyCondition::new));

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return TreeRegistrator.OPTIONAL_BLOCK_STATE_PROPERTY.get();
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext context) {
        BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        return state != null && state.is(this.block) && (this.properties.isEmpty() || this.properties.get().matches(state));
    }

    public static LootItemCondition.Builder of(Block block, StatePropertiesPredicate.Builder properties) {
        Optional<StatePropertiesPredicate> built = properties.build();
        Holder<Block> holder = block.builtInRegistryHolder();
        return () -> new OptionalBlockStatePropertyCondition(holder, built);
    }
}
