package cy.jdkdigital.productivetrees.datagen.dynamic;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cy.jdkdigital.productivetrees.ProductiveTrees;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class DynamicDataPack extends PathPackResources
{
    private final Path rootPath;
    private final PackType packType;
    private final PackLocationInfo packInfo;

    public DynamicDataPack(Path rootPath, PackType packType, PackLocationInfo packInfo) {
        super(packInfo, rootPath);
        ProductiveTrees.generateData();
        this.rootPath = rootPath;
        this.packType = packType;
        this.packInfo = packInfo;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionType<T> metadataSectionType) throws IOException {
        JsonObject jsonobject = new JsonObject();
        JsonObject packObject = new JsonObject();
        packObject.addProperty("pack_format", packType.equals(PackType.SERVER_DATA) ? 48 : 34);
        packObject.addProperty("description", ProductiveTrees.MODID);
        jsonobject.add("pack", packObject);
        if (!jsonobject.has(metadataSectionType.name())) {
            return null;
        } else {
            return metadataSectionType.codec()
                    .parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(jsonobject, metadataSectionType.name()))
                    .result()
                    .orElse(null);
        }
    }
}
