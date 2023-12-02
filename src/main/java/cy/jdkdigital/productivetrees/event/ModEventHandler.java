package cy.jdkdigital.productivetrees.event;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.client.particle.PetalParticle;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ProductiveTrees.MODID)
public class ModEventHandler
{
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TreeRegistrator.PETAL_PARTICLES.get(), PetalParticle.Provider::new);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(TreeRegistrator.TAB_KEY)) {
            for (RegistryObject<Item> item : ProductiveTrees.ITEMS.getEntries()) {
                event.accept(item);
            }
        }
    }
}
