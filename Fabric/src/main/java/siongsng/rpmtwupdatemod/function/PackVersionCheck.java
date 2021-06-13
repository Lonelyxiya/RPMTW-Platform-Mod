package siongsng.rpmtwupdatemod.function;

import net.minecraft.resource.ResourcePackProvider;
import org.apache.commons.io.FileUtils;
import siongsng.rpmtwupdatemod.RpmtwUpdateMod;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class PackVersionCheck {
    public static Path PackDir = Paths.get(System.getProperty("user.home") + "/.rpmtw/1.16"); //資源包的放置根目錄
    public static Path PackFile = PackDir.resolve("RPMTW-1.16.zip"); //資源包檔案位置

    public PackVersionCheck(Set<ResourcePackProvider> providers) throws IOException {
        if (!Files.isDirectory(PackDir)) {
            Files.createDirectories(PackDir);
        }
        RpmtwUpdateMod.LOGGER.info("正在準備檢測資源包版本。");
        try {
            FileUtils.copyURLToFile(new URL(RpmtwUpdateMod.PackDownloadUrl), PackFile.toFile()); //下載資源包檔案
            Class.forName("siongsng.rpmtwupdatemod.packs.LoadPack").getMethod("init", Set.class).invoke(null, providers);
        } catch (Exception e) {
            RpmtwUpdateMod.LOGGER.error("發生未知錯誤: " + e);
        }
    }
}
