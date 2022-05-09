package meow.emily.patootie.util;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

public class Utils {
    /**
     * Checks if the given entity is an NPC
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is an NPC, {@code false} otherwise
     */
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
}
