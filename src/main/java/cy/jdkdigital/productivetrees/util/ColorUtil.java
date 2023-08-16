package cy.jdkdigital.productivetrees.util;

import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.entity.PollinatedLeavesBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil
{
    // Color calc cache
    private static final Map<Integer, float[]> colorCache = new HashMap<>();
    private static final Map<String, Integer> stringColorCache = new HashMap<>();

    public static Integer getCacheColor(String color) {
        if (!stringColorCache.containsKey(color)) {
            stringColorCache.put(color, TextColor.parseColor(color).getValue());
        }
        return stringColorCache.get(color);
    }

    public static float[] getCacheColor(int color) {
        if (!colorCache.containsKey(color)) {
            colorCache.put(color, ColorUtil.getComponents(color));
        }
        return colorCache.get(color);
    }

    public static float[] getComponents(int color) {
        float[] f = new float[4];
        f[0] = (float) ((color >> 16) & 0xFF)/255f;
        f[1] = (float) ((color >> 8) & 0xFF)/255f;
        f[2] = (float) (color & 0xFF)/255f;
        f[3] = (float) ((color >> 24) & 0xff)/255f;

        return f;
    }

    public static int getLeafColor(Block leaf) {
        return getLeafColor(leaf, null, null);
    }

    public static int getLeafColor(Block leaf, BlockAndTintGetter lightReader, BlockPos pos) {
        if (leaf != null) {
            if (leaf instanceof ProductiveLeavesBlock leafBlock) {
                return ColorUtil.getCacheColor(leafBlock.getTree().getLeafColor());
            }
            if (leaf.equals(Blocks.SPRUCE_LEAVES)) {
                return FoliageColor.getEvergreenColor();
            }
            if (leaf.equals(Blocks.BIRCH_LEAVES)) {
                return FoliageColor.getBirchColor();
            }
        }
        if (lightReader != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(lightReader, pos);
        }
        return FoliageColor.getDefaultColor();
    }

    public static int blend(int a, int b, float f) {
        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int A = (int)((aA * (1.0f - f)) + (bA * f));
        int R = (int)((aR * (1.0f - f)) + (bR * f));
        int G = (int)((aG * (1.0f - f)) + (bG * f));
        int B = (int)((aB * (1.0f - f)) + (bB * f));

        return A << 24 | R << 16 | G << 8 | B;
    }
}
