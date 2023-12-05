package su.nightexpress.excellentchallenges.challenge.action;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.excellentchallenges.ExcellentChallengesPlugin;
import su.nightexpress.excellentchallenges.hooks.HookId;
import su.nightexpress.excellentchallenges.hooks.external.MythicMobsHook;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry extends AbstractManager<ExcellentChallengesPlugin> {

    private static final Map<String, ActionType<?, ?>> ACTION_TYPE_MAP = new HashMap<>();

    public ActionRegistry(@NotNull ExcellentChallengesPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.registerAction(InventoryClickEvent.class, EventPriority.HIGHEST, ActionTypes.ANVIL_RENAME);
        this.registerAction(InventoryClickEvent.class, EventPriority.HIGHEST, ActionTypes.ANVIL_REPAIR);

        // Block Material related
        this.registerAction(BlockBreakEvent.class, EventPriority.HIGHEST, ActionTypes.BLOCK_BREAK);
        this.registerAction(BlockFertilizeEvent.class, EventPriority.MONITOR, ActionTypes.BLOCK_FERTILIZE);
        this.registerAction(BlockPlaceEvent.class, EventPriority.MONITOR, ActionTypes.BLOCK_PLACE);

        // Damage Cause related
        this.registerAction(EntityDamageByEntityEvent.class, EventPriority.MONITOR, ActionTypes.DAMAGE_INFLICT);
        this.registerAction(EntityDamageEvent.class, EventPriority.MONITOR, ActionTypes.DAMAGE_RECEIVE);

        // Entity Type related
        this.registerAction(EntityBreedEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_BREED);
        this.registerAction(EntityDeathEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_KILL);
        this.registerAction(EntityDeathEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_SHOOT);
        this.registerAction(PlayerShearEntityEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_SHEAR);
        this.registerAction(EntityTameEvent.class, EventPriority.MONITOR, ActionTypes.ENTITY_TAME);
        this.registerAction(ProjectileLaunchEvent.class, EventPriority.MONITOR, ActionTypes.PROJECTILE_LAUNCH);

        // Item Material related
        this.registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_CONSUME);
        this.registerAction(CraftItemEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_CRAFT);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_DISENCHANT);
        this.registerAction(EnchantItemEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_ENCHANT);
        this.registerAction(PlayerFishEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_FISH);
        this.registerAction(FurnaceExtractEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_FURNACE);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ITEM_TRADE);

        // PotionEffectType related
        this.registerAction(BrewEvent.class, EventPriority.MONITOR, ActionTypes.POTION_BREW);
        this.registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, ActionTypes.POTION_DRINK);

        // Enchantment related
        this.registerAction(EnchantItemEvent.class, EventPriority.MONITOR, ActionTypes.ENCHANT_GET);
        this.registerAction(InventoryClickEvent.class, EventPriority.MONITOR, ActionTypes.ENCHANT_REMOVE);

        this.registerHooks();
    }

    private void registerHooks() {
        if (EngineUtils.hasPlugin(HookId.MYTHIC_MOBS)) {
            this.plugin.info("Found " + HookId.MYTHIC_MOBS + "! Registering new challenge types...");
            MythicMobsHook.register(this);
        }
    }

    @Override
    protected void onShutdown() {
        ACTION_TYPE_MAP.clear();
    }

    @Nullable
    public <E extends Event, O> ActionType<E, O> registerAction(@NotNull Class<E> eventClass,
                                                                @NotNull EventPriority priority,
                                                                @NotNull String name,
                                                                @NotNull ObjectFormatter<O> objectFormatter,
                                                                @NotNull EventHelper<E, O> dataGather) {
        return this.registerAction(eventClass, priority, ActionType.create(name, objectFormatter, dataGather));
    }

    @Nullable
    public <E extends Event, O> ActionType<E, O> registerAction(@NotNull Class<E> eventClass,
                                                                @NotNull EventPriority priority,
                                                                @NotNull ActionType<E, O> actionType) {

        if (!actionType.loadSettings(this.plugin)) return null;

        //for (EventPriority priority : EventPriority.values()) {
            WrappedEvent<E, O> event = new WrappedEvent<>(plugin, eventClass, actionType);
            plugin.getPluginManager().registerEvent(eventClass, event, priority, event, plugin, true);
        //}

        ACTION_TYPE_MAP.put(actionType.getName(), actionType);
        return actionType;
    }

    @Nullable
    public ActionType<?, ?> getActionType(@NotNull String name) {
        return ACTION_TYPE_MAP.get(name.toLowerCase());
    }
}
