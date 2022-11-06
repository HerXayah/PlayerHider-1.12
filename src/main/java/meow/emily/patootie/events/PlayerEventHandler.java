package meow.emily.patootie.events;

import meow.emily.patootie.Emily;
import meow.emily.patootie.util.Utils;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerEventHandler {

    // UUID VoiceCHat 1.12
    private final UUID vcUuid12 = UUID.fromString("24c0644d-ad56-4609-876d-6e9da3cc9794");
    // UUID VoiceChat 1.8
    private final UUID vcUuid8 = UUID.fromString("43152d5b-ca80-4b29-8f48-39fd63e48dee");

    boolean keyPressed = false;
    int keycount = 0;

    // im gonna kms ngl
    Emily instance = Emily.getInstance();
    LabyMod labymod = LabyMod.getInstance();
    Minecraft minecraft = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!instance.isVoiceexist()) {
            LabyModAddon addon = AddonLoader.getAddonByUUID(UUID.fromString(String.valueOf(vcUuid12)));
            if (addon instanceof VoiceChat && addon.about.name.equals("VoiceChat")) {
                VoiceChat voiceChat = (VoiceChat) addon;
                instance.setVoiceexist(true);
            } else {
                instance.setVoiceexist(false);
            }
        }
    }

    @SubscribeEvent
    public void onPrePlayerRender(RenderPlayerEvent.Pre e) {
        EntityPlayer enPlayer = e.getEntityPlayer();
        if (instance.isRenderPlayers() && instance.isModOn()) {
            if (instance.isRenderPlayers() && !enPlayer.equals(minecraft.player)) {
                List<String> localPlayersToRender = instance.getPlayersToRenderString();
                if (Utils.isNPC(enPlayer)) {
                    e.setCanceled(false);
                    for (String s : localPlayersToRender) {
                        if (s.equals(enPlayer.getGameProfile().getName())) {
                            e.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    public void mute(EntityPlayer player) {
        VoiceChat voiceChat = (VoiceChat) AddonLoader.getAddonByUUID(this.vcUuid12);
        if (!player.equals(minecraft.player)) {
            voiceChat.getPlayerVolumes().put(player.getUniqueID(), 0);
            voiceChat.savePlayersVolumes();
        }
    }

    public void RemovePlayer(String s) {
        // remove from the list
        instance.getPlayersToRenderString().remove(s);
        instance.savePlayersToRenderString();
        //  playersToRenderString.removeIf(player -> player.equals(s));
        instance.saveConfig();
    }

    public void unmute(EntityPlayer player) {
        VoiceChat voiceChat = (VoiceChat) AddonLoader.getAddonByUUID(this.vcUuid12);
        UUID uuid = player.getUniqueID();
        Map<UUID, Integer> volume = voiceChat.getPlayerVolumes();
        if (!player.equals(minecraft.player)) {
            volume.put(uuid, 100);
        }
        voiceChat.savePlayersVolumes();
    }

    public void sendMessage(String message) {
        try {
            labymod.displayMessageInChat(message);
        } catch (Exception e) {
            e.printStackTrace();
            labymod.displayMessageInChat(e.getMessage());
        }

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        EntityPlayer enPlayer = minecraft.player;
        if (instance.getKey() > -1) {
            if (Keyboard.isKeyDown(instance.getKey())) {
                while (keyPressed || Keyboard.isRepeatEvent()) {
                    return;
                }
                if (instance.isModOn() && !keyPressed) {
                    keyPressed = true;
                    if (instance.isRenderPlayers()) {
                        instance.setRenderPlayers(false);
                        instance.setMuted(false);
                        instance.saveConfig();
                        if (instance.isVoiceexist() && instance.isPlayerUnmute()) {
                            minecraft.world.playerEntities.stream()
                                    .filter(entityPlayer ->
                                            instance.getPlayersToRenderString()
                                                    .contains(entityPlayer.getName())).
                                    forEach(this::unmute);
                        }
                        if (instance.isConfigMessage()) {
                            sendMessage("[PH] - On");
                        }
                    } else {
                        instance.setRenderPlayers(true);
                        instance.setMuted(true);
                        instance.saveConfig();
                        if (instance.isVoiceexist() && instance.isPlayerUnmute()) {
                            minecraft.world.playerEntities.stream()
                                    .filter(entityPlayer ->
                                            instance.getPlayersToRenderString()
                                                    .contains(entityPlayer.getName())).
                                    forEach(this::mute);
                        }
                        if (instance.isConfigMessage()) {
                            sendMessage("[PH] - Off");
                        }
                    }
                } else {
                    sendMessage("[PH] - Mod seems to be disabled. Check Config.");
                }
                keyPressed = false;
            }
        }
    }
}