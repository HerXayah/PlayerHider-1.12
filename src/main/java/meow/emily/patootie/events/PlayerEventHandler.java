package meow.emily.patootie.events;

import meow.emily.patootie.Emily;
import meow.emily.patootie.util.MessageHandler;
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

    // We do be handling those messages
    MessageHandler out = MessageHandler.getInstance();

    // im gonna kms ngl
    Minecraft minecraft = Minecraft.getMinecraft();


    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Emily instance = Emily.getInstance();
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
        Emily instance = Emily.getInstance();
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
        Emily instance = Emily.getInstance();
        VoiceChat voiceChat = (VoiceChat) AddonLoader.getAddonByUUID(this.vcUuid12);
        if (!player.equals(minecraft.player)) {
            voiceChat.getPlayerVolumes().put(player.getUniqueID(), 0);
            voiceChat.savePlayersVolumes();
        }
    }

    public void RemovePlayer(String s) {
        // remove from the list
        Emily instance = Emily.getInstance();
        instance.getPlayersToRenderString().remove(s);
        instance.savePlayersToRenderString();
        //  playersToRenderString.removeIf(player -> player.equals(s));
        instance.saveConfig();
    }

    public void unmute(EntityPlayer player) {
        Emily instance = Emily.getInstance();
        VoiceChat voiceChat = (VoiceChat) AddonLoader.getAddonByUUID(this.vcUuid12);
        UUID uuid = player.getUniqueID();
        Map<UUID, Integer> volume = voiceChat.getPlayerVolumes();
        if (!player.equals(minecraft.player)) {
            volume.put(uuid, 100);
        }
        voiceChat.savePlayersVolumes();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        EntityPlayer enPlayer = minecraft.player;
        Emily instance = Emily.getInstance();
        LabyMod labymod = LabyMod.getInstance();
        if (instance.getKey() > -1) {
            if (Keyboard.isKeyDown(instance.getKey())) {
                if (instance.isModOn()) {
                    if (instance.isRenderPlayers()) {
                        instance.setRenderPlayers(false);
                        instance.setMuted(false);
                        instance.saveConfig();
                        if (instance.isVoiceexist() && instance.isPlayerUnmute()) {
                            minecraft.world.playerEntities.stream()
                                    .filter(entityPlayer ->
                                            Emily.getInstance().getPlayersToRenderString()
                                                    .contains(entityPlayer.getName())).
                                    forEach(this::unmute);
                            //removePlayer(enPlayer.getGameProfile().getName());
                        }
                        if (instance.isConfigMessage()) {
                            out.sendMessage("[PH] - On");
                        }
                    } else {
                        instance.setRenderPlayers(true);
                        instance.setMuted(true);
                        instance.saveConfig();
                        if (instance.isVoiceexist() && instance.isPlayerUnmute()) {
                            minecraft.world.playerEntities.stream()
                                    .filter(entityPlayer ->
                                            Emily.getInstance().getPlayersToRenderString()
                                                    .contains(entityPlayer.getName())).
                                    forEach(this::mute);
                        }
                        if (instance.isConfigMessage()) {
                            out.sendMessage("[PH] - Off");
                        }
                    }
                } else {
                    out.sendMessage("[PH] - Mod seems to be disabled. Check Config.");
                }
            }
        }
    }
}