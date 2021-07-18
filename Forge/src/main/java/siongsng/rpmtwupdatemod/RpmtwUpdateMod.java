package siongsng.rpmtwupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import siongsng.rpmtwupdatemod.CosmicChat.GetMessage;
import siongsng.rpmtwupdatemod.commands.noticeCMD;
import siongsng.rpmtwupdatemod.config.Config;
import siongsng.rpmtwupdatemod.config.ConfigScreen;
import siongsng.rpmtwupdatemod.config.Configer;
import siongsng.rpmtwupdatemod.crowdin.TokenCheck;
import siongsng.rpmtwupdatemod.crowdin.key;
import siongsng.rpmtwupdatemod.function.AFK;
import siongsng.rpmtwupdatemod.function.PackVersionCheck;
import siongsng.rpmtwupdatemod.notice.notice;

import java.io.IOException;


@Mod("rpmtw_update_mod")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RpmtwUpdateMod {

    public static final Logger LOGGER = LogManager.getLogger(); //註冊紀錄器
    public final static String Mod_ID = "rpmtw_update_mod"; //模組ID
    public final static String PackDownloadUrl = "https://github.com/RPMTW/ResourcePack-Mod-zh_tw/raw/Translated/RPMTW-1.16.zip";

    public RpmtwUpdateMod() {

        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init); //註冊監聽事件
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT, "rpmtw_update_mod-client.toml"); //註冊組態
        Config.loadConfig(Config.CLIENT); //載入客戶端組態

        LOGGER.info("Hello RPMTW world!");
        if (!ping.isConnect()) { //判斷是否有網路
            LOGGER.error("你目前處於無網路狀態，因此無法使用 RPMTW 翻譯自動更新模組，請連結網路後重新啟動此模組。");
        }
        if (FMLEnvironment.dist == Dist.CLIENT && Configer.isChinese.get()) {
            Minecraft.getInstance().gameSettings.language = "zh_tw"; //將語言設定為繁體中文
        }
        new PackVersionCheck(); //資源包版本檢查
        try {
            new TokenCheck().Check(Configer.Token.get()); //開始檢測權杖
        } catch (IOException e) {
            LOGGER.error("檢測權杖時發生未知錯誤：" + e);
        }

    }

    @SubscribeEvent
    public void init(final FMLClientSetupEvent e) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(new key());  //快捷鍵註冊
        MinecraftForge.EVENT_BUS.register(new noticeCMD()); //noticeCMD指令註冊
        if (Configer.notice.get()) { //判斷Config
            MinecraftForge.EVENT_BUS.register(new notice()); //玩家加入事件註冊
        }
        MinecraftForge.EVENT_BUS.register(new AFK()); //掛機事件註冊

        ModLoadingContext.get().registerExtensionPoint( //註冊組態螢幕至Forge模組設定
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (mc, screen) -> new ConfigScreen()
        );
        if (Configer.isChat.get()) {
            new GetMessage();
        }
    }

}