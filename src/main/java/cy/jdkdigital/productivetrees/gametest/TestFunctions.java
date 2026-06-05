package cy.jdkdigital.productivetrees.gametest;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Holds the gametest bodies and publishes them into {@link BuiltInRegistries#TEST_FUNCTION}.
 *
 * <p>In gameTestServer mode {@code Bootstrap.bootStrap()} runs before the mod constructor, so
 * {@code TEST_FUNCTION} is already populated and frozen by the time a {@code TestFunctionLoader}
 * hook would fire. {@link #init()} therefore unfreezes the registry, registers each recorded body,
 * and refreezes. {@code FunctionGameTestInstance.run} resolves the body by ResourceKey at test time.
 */
public final class TestFunctions
{
    private static final Map<String, Consumer<GameTestHelper>> FUNCTIONS = new LinkedHashMap<>();
    private static boolean published;

    private TestFunctions() {}

    public static ResourceKey<Consumer<GameTestHelper>> register(String name, Consumer<GameTestHelper> body) {
        if (published) {
            throw new IllegalStateException("TestFunctions.register called after init() — too late, registry is already published.");
        }
        if (FUNCTIONS.put(name, body) != null) {
            throw new IllegalStateException("Duplicate test function registration: " + name);
        }
        return key(name);
    }

    public static ResourceKey<Consumer<GameTestHelper>> key(String name) {
        return ResourceKey.create(Registries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, name));
    }

    public static Iterable<String> names() {
        return FUNCTIONS.keySet();
    }

    public static void init() {
        if (published) return;
        published = true;

        Registry<Consumer<GameTestHelper>> registry = BuiltInRegistries.TEST_FUNCTION;
        if (!(registry instanceof MappedRegistry<Consumer<GameTestHelper>> mapped)) {
            throw new IllegalStateException("BuiltInRegistries.TEST_FUNCTION is not a MappedRegistry — cannot unfreeze");
        }

        mapped.unfreeze(false);
        try {
            FUNCTIONS.forEach((name, body) -> Registry.register(mapped, key(name), body));
        } finally {
            mapped.freeze();
        }
    }
}
