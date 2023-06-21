package su.nightexpress.excellentchallenges.hooks.external;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicMobsHook {

    private static final MythicBukkit MYTHIC_MOBS = MythicBukkit.inst();

    public static boolean isMythicMob(@NotNull Entity entity) {
        return MYTHIC_MOBS.getAPIHelper().isMythicMob(entity);
    }

    @Nullable
    public static ActiveMob getMobInstance(@NotNull Entity entity) {
        return MYTHIC_MOBS.getAPIHelper().getMythicMobInstance(entity);
    }

    @Nullable
    public static MythicMob getMobConfig(@NotNull Entity entity) {
        ActiveMob mob = getMobInstance(entity);
        return mob != null ? mob.getType() : null;
    }

    @Nullable
    public static MythicMob getMobConfig(@NotNull String mobId) {
        return MYTHIC_MOBS.getAPIHelper().getMythicMob(mobId);
    }

    @NotNull
    public static String getMobInternalName(@NotNull Entity entity) {
        MythicMob mythicMob = getMobConfig(entity);
        return mythicMob != null ? mythicMob.getInternalName() : "null";
    }

    @NotNull
    public static String getMobDisplayName(@NotNull String mobId) {
        MythicMob mythicMob = getMobConfig(mobId);
        PlaceholderString string = mythicMob != null ? mythicMob.getDisplayName() : null;
        return string != null ? string.get() : mobId;
    }
}
