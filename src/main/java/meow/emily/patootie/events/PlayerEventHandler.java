package meow.emily.patootie.events;

import com.mojang.realmsclient.gui.ChatFormatting;
import meow.emily.patootie.Emily;
import meow.emily.patootie.util.Utils;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PlayerEventHandler {

    @SubscribeEvent
    public void onPrePlayerRender(RenderPlayerEvent.Pre e) {
        Emily instance = Emily.getInstance();
        if (instance.isRenderPlayers()) {
            EntityPlayer enPlayer = e.getEntityPlayer();
            if (instance.isRenderPlayers() && !enPlayer.equals(Minecraft.getMinecraft().player)) {
                List<String> localPlayersToRender = instance.getPlayersToRenderString();
                if (!Utils.isNPC(enPlayer)) {
                    e.setCanceled(false);
                    for (String s : localPlayersToRender) {
                        if (s.equals(enPlayer.getGameProfile().getName())) {
                            e.setCanceled(true);
                            //LabyMod.getInstance().displayMessageInChat("§a" + instance.isVoiceexist() + "§7 " + instance.isMuted());
                           /* if (instance.isVoiceexist()) {
                                if (instance.isMuted()) {
                                    LabyMod.getInstance().displayMessageInChat("§a" + "here");
                                    mute(enPlayer);
                                }
                            } else if(!instance.isMuted()) {
                                unmute(enPlayer);
                            } */
                        }

                    }
                }
            }
        }
    }

// Needs fixing
    /*
    public void mute(EntityPlayer player) {
        Emily instance = Emily.getInstance();
        instance.getVoiceChat().getPlayerVolumes().put(UUID.fromString(instance.getPlayersToRender().toString()), 0);
        instance.getVoiceChat().savePlayersVolumes();
    }

    public void unmute(EntityPlayer player) {
        Emily instance = Emily.getInstance();
        UUID uuid = player.getGameProfile().getId();
        Map<UUID, Integer> volume = instance.getVoiceChat().getPlayerVolumes();
        volume.put(uuid, 100);
        if (volume.containsKey(uuid)) {
            volume.put(uuid, instance.getVoiceChat().getVolume(uuid));
        } else {
            volume.put(uuid, 100);
        }
        instance.getVoiceChat().savePlayersVolumes();
        instance.savePlayersToRender();
    } */

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        Emily instance = Emily.getInstance();
        if (instance.getKey() > -1) {
            if (Keyboard.isKeyDown(instance.getKey())) {
                LabyMod labymod = LabyMod.getInstance();
                if (instance.isRenderPlayers()) {
                    instance.setRenderPlayers(false);
                    instance.setMuted(false);
                    if (instance.isConfigMessage()) {
                        labymod.displayMessageInChat(ChatFormatting.GRAY + ">>" + "[" + ChatFormatting.AQUA + "PH" + ChatFormatting.WHITE + "]" + ChatFormatting.BOLD + ChatFormatting.GREEN + " on");
                    }
                } else {
                    instance.setRenderPlayers(true);
                    instance.setMuted(true);
                    if (instance.isConfigMessage()) {
                        labymod.displayMessageInChat(ChatFormatting.GRAY + ">>" + "[" + ChatFormatting.AQUA + "PH" + ChatFormatting.WHITE + "]" + ChatFormatting.BOLD + ChatFormatting.DARK_RED + " off");
                    }
                }
            }
        }
    }
}
