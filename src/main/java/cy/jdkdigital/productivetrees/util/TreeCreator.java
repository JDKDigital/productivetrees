package cy.jdkdigital.productivetrees.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import cy.jdkdigital.productivetrees.registry.TreeRegistrator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class TreeCreator
{
    public static TreeObject create(ResourceLocation id, JsonObject json) throws JsonSyntaxException {
        var treeOptional = TreeObject.codec(id).parse(JsonOps.INSTANCE, json).result();

        if (treeOptional.isPresent()) {
            var treeObject = treeOptional.get();

            // Validate colors
            ColorUtil.getCacheColor(treeObject.getLeafColor());
            ColorUtil.getCacheColor(treeObject.getLogColor());
            ColorUtil.getCacheColor(treeObject.getPlankColor());
            if (treeObject.hasFruit()) {
                ColorUtil.getCacheColor(treeObject.getFruit().ripeColor());
                ColorUtil.getCacheColor(treeObject.getFruit().unripeColor());
            }

            TreeRegistrator.registerTree(treeObject);

            return treeObject;
        } else {
            ProductiveTrees.LOGGER.info("failed to read tree configuration for " + id);
        }
        return null;
    }
}
