package cy.jdkdigital.productivetrees;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config
{
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final Server SERVER = new Server(SERVER_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Server
    {
        public final ForgeConfigSpec.IntValue pollenChanceFromSieve;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            pollenChanceFromSieve = builder
                    .comment("Chance to get a pollen when using sieve upgrades in hives")
                    .defineInRange("pollenChanceFromSieve", 2, 1, Integer.MAX_VALUE);

            builder.pop();
        }
    }
}