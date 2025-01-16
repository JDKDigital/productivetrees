package cy.jdkdigital.productivetrees.integrations.forestry;

import net.minecraft.resources.ResourceLocation;

import forestry.api.plugin.IForestryPlugin;
import forestry.api.plugin.IPollenRegistration;

import cy.jdkdigital.productivetrees.ProductiveTrees;

public class ProductiveTreesForestryPlugin implements IForestryPlugin {
	public static final ResourceLocation ID = new ResourceLocation(ProductiveTrees.MODID, "plugin");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void registerPollen(IPollenRegistration pollen) {
		pollen.registerPollenType(new ProductiveTreesPollenType());
	}
}
