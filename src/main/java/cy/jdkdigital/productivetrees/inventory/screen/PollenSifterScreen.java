package cy.jdkdigital.productivetrees.inventory.screen;

import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.inventory.PollenSifterContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class PollenSifterScreen extends AbstractUpgradeableContainerScreen<PollenSifterContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveTrees.MODID, "textures/gui/container/pollen_sifter.png");

    public PollenSifterScreen(PollenSifterContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (isHovering(75, 33, 18, 18, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(
                    List.of(Component.translatable(ProductiveTrees.MODID + ".screen.progress", this.menu.getBlockEntity().progress + "/" + this.menu.getBlockEntity().recipeTime).getVisualOrderText()),
                    mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        int progress = (int) (18f * ((float) this.menu.getBlockEntity().progress / (float) this.menu.getBlockEntity().recipeTime));
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos + 75, this.topPos + 33, 202.0F, 0.0F, progress, 18, 256, 256);
    }
}
