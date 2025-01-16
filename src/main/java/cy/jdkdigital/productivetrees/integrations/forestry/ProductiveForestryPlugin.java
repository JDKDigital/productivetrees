package cy.jdkdigital.productivetrees.integrations.forestry;

import cy.jdkdigital.productivetrees.ProductiveTrees;
import forestry.api.plugin.IForestryPlugin;
import forestry.api.plugin.IPollenRegistration;
import net.minecraft.resources.ResourceLocation;

public class ProductiveForestryPlugin implements IForestryPlugin
{
    static ResourceLocation ID = new ResourceLocation(ProductiveTrees.MODID, ProductiveTrees.MODID);

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void registerPollen(IPollenRegistration pollen) {
        pollen.registerPollenType(new ProductiveTreePollenType());
    }
}
