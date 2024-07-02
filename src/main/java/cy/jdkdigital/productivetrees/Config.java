package cy.jdkdigital.productivetrees;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_CONFIG;
    public static final Server SERVER = new Server(SERVER_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
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
}