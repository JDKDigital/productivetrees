package cy.jdkdigital.productivetrees.datagen.recipe;

import net.minecraft.world.item.Item;

public record TreeDropSpec(Item item, int min, int max, float chance)
{
}
