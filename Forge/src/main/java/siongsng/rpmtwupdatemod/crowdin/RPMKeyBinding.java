package siongsng.rpmtwupdatemod.crowdin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import siongsng.rpmtwupdatemod.config.ConfigScreen;
import siongsng.rpmtwupdatemod.config.Configer;
import siongsng.rpmtwupdatemod.function.SendMsg;
import siongsng.rpmtwupdatemod.gui.*;
import siongsng.rpmtwupdatemod.packs.PacksManerger;

public final class RPMKeyBinding {
    public static final KeyBinding reloadpack = new KeyBinding("key.rpmtw_update_mod.reloadpack", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.rpmtw");
    public static final KeyBinding open_config = new KeyBinding("key.rpmtw_update_mod.open_config", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.rpmtw");
    public static final KeyBinding Crowdin = new KeyBinding("key.rpmtw_update_mod.open_crowdin", GLFW.GLFW_KEY_UNKNOWN, "key.categories.rpmtw");
    public static final KeyBinding cosmic_chat_send = new KeyBinding("key.rpmtw_update_mod.cosmic_chat_send", GLFW.GLFW_KEY_G, "key.categories.rpmtw");
    public static final KeyBinding translate = new KeyBinding("key.rpmtw_update_mod.translate", GLFW.GLFW_KEY_N, "key.categories.rpmtw");

    public static boolean updateLock = false;
    private boolean showed = false;

    public RPMKeyBinding() {
        ClientRegistry.registerKeyBinding(reloadpack);
        ClientRegistry.registerKeyBinding(open_config);
        ClientRegistry.registerKeyBinding(Crowdin);
        ClientRegistry.registerKeyBinding(cosmic_chat_send);
        ClientRegistry.registerKeyBinding(translate);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity p = mc.player;
        if (showed) { //防止重複開啟
            try {
                if (!reloadpack.isKeyDown() && !open_config.isKeyDown() && !Crowdin.isKeyDown() && !cosmic_chat_send.isKeyDown()) {
                    showed = false;
                }
            } catch (IndexOutOfBoundsException ex) {
                showed = false;
            }
            return;
        }
        if (Crowdin.isPressed()) {
            assert p != null;
            Item item = p.getHeldItemMainhand().getItem(); //拿的物品
            String item_key = item.getTranslationKey(); //物品的命名空間

            if (item_key.equals("block.minecraft.air")) {
                SendMsg.send("§4請手持物品後再使用此功能。");
                return;
            } else if (!Configer.isCheck.get()) {
                Minecraft.getInstance().displayGuiScreen(new CrowdinLoginScreen());
                return;
            } else {
                SendMsg.send("請稍後，正在開啟物品翻譯界面中...");
                Thread thread = new Thread(() -> {
                    if (CorwidnProcedure.getText() == null && Configer.isCheck.get()) {
                        SendMsg.send("§6由於你目前手持想要翻譯的物品，數據不在資料庫內\n因此無法進行翻譯，想了解更多資訊請前往RPMTW官方Discord群組:https://discord.gg/5xApZtgV2u");
                        return;
                    }
                    Minecraft.getInstance().displayGuiScreen(new CrowdinScreen());
                });
                thread.start();
            }

        }

        if (open_config.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new ConfigScreen());
        }
        if (Configer.rpmtw_reloadpack.get()) {
            if (reloadpack.isPressed() && !updateLock) {
                updateLock = true;
                PacksManerger.ReloadPack();
            }
        }
        if (cosmic_chat_send.isPressed()) {
            if (!Configer.isChat.get()) return;
            if (Configer.isEULA.get()) {
                Minecraft.getInstance().displayGuiScreen(new CosmicChatScreen());
            } else {
                Minecraft.getInstance().displayGuiScreen(new EULAScreen());
            }
        }
    }
}