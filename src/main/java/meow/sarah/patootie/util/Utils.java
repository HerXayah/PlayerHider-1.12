package meow.sarah.patootie.util;

import meow.sarah.patootie.Sarah;
import net.labymod.main.LabyMod;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

public class Utils {

    static Sarah instance = Sarah.getInstance();
    static LabyMod labymod = LabyMod.getInstance();

    public static boolean isNPC(Entity entity) {
        if (!(entity instanceof EntityOtherPlayerMP)) {
            return true;
        }

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

        return entity.getUniqueID().version() != 2 || entityLivingBase.getHealth() != 20.0F || entityLivingBase.isPlayerSleeping();
    }

    public static UUID toUUID(String s) {
        return UUID.fromString(s);
    }

    public static void SetConfig(Boolean answer) {
        instance.setRenderPlayers(answer);
        instance.setMuted(answer);
        instance.saveConfig();
    }

    public static void sendMessage(String message) {
        try {
            labymod.displayMessageInChat(message);
        } catch (Exception e) {
            e.printStackTrace();
            labymod.displayMessageInChat(e.getMessage());
        }

    }


}
