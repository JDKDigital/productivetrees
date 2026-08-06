package cy.jdkdigital.productivetrees;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_CONFIG;
    public static final Server SERVER = new Server(SERVER_BUILDER);

    private static final ModConfigSpec.Builder STARTUP_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec STARTUP_CONFIG;
    public static final Startup STARTUP = new Startup(STARTUP_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
        STARTUP_CONFIG = STARTUP_BUILDER.build();
    }

    public static class Server
    {
        public final ModConfigSpec.IntValue pollenChanceFromSieve;

        public Server(ModConfigSpec.Builder builder) {
            builder.push("General");

            pollenChanceFromSieve = builder
                    .comment("Chance to get a pollen when using sieve upgrades in hives")
                    .defineInRange("pollenChanceFromSieve", 2, 1, 100);

            builder.pop();
        }
    }

    public static class Startup
    {
        public final ModConfigSpec.BooleanValue minimal;

        public Startup(ModConfigSpec.Builder builder) {
            builder.push("General");

            minimal = builder
                    .comment(
                            "Minimal mode: skip registering the decorative and functional wood blocks for every tree",
                            "(stairs, slabs, fences, fence gates, pressure plates, buttons, doors, trapdoors, bookshelves and signs).",
                            "Logs, wood, planks, leaves, saplings and fruit are always registered.",
                            "This greatly reduces the number of registered blockstates and lowers RAM usage.",
                            "IMPORTANT: this changes which blocks exist, so it MUST be set to the same value on the server and on every",
                            "connecting client - a mismatch will prevent clients from joining.")
                    .define("minimal", false);

            builder.pop();
        }
    }
}