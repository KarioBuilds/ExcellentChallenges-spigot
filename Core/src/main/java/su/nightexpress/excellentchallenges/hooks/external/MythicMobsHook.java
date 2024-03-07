package su.nightexpress.excellentchallenges.hooks.external;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentchallenges.challenge.action.ActionRegistry;
import su.nightexpress.excellentchallenges.challenge.action.ActionType;
import su.nightexpress.excellentchallenges.challenge.action.EventHelper;
import su.nightexpress.excellentchallenges.challenge.action.ObjectFormatter;

public class MythicMobsHook {

    private static final MythicBukkit MYTHIC_MOBS = MythicBukkit.inst();

    public static void register(@NotNull ActionRegistry registry) {
        registry.registerAction(MythicMobDeathEvent.class, EventPriority.MONITOR, ACTION_TYPE);
    }

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

    public static final EventHelper<MythicMobDeathEvent, MythicMob> EVENT_HELPER = (plugin, event, processor) -> {
        LivingEntity killer = event.getKiller();
        if (!(killer instanceof Player player)) return false;

        processor.progressChallenge(player, event.getMobType(), 1);
        return true;
    };

    public static final ObjectFormatter<MythicMob> OBJECT_FORMATTER = new ObjectFormatter<>() {
        @NotNull
        @Override
        public String getName(@NotNull MythicMob object) {
            return object.getInternalName();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull MythicMob object) {
            return object.getDisplayName().get();
        }

        @Nullable
        @Override
        public MythicMob parseObject(@NotNull String name) {
            return getMobConfig(name);
        }
    };

    public static final ActionType<MythicMobDeathEvent, MythicMob> ACTION_TYPE = ActionType.create(
        "kill_mythic_mob", Material.WITHER_SKELETON_SKULL, OBJECT_FORMATTER, EVENT_HELPER
    );
}
