package com.github.lukesky19.skycombatlog.listener;

import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jspecify.annotations.NonNull;

/**
 * Listens for when a player toggles their flight and prevents flying if in combat.
 */
public class PlayerToggleFlightListener implements Listener {
    private final @NonNull LocaleManager localeManager;
    private final @NonNull CombatManager combatManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param combatManager A {@link CombatManager} instance.
     */
    public PlayerToggleFlightListener(
            @NonNull LocaleManager localeManager,
            @NonNull CombatManager combatManager) {
        this.localeManager = localeManager;
        this.combatManager = combatManager;
    }

    /**
     * Listens for when a player toggles their flight and prevents flying if in combat.
     * @param playerToggleFlightEvent A {@link PlayerToggleFlightEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFlightEnable(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Player player = playerToggleFlightEvent.getPlayer();

        if(combatManager.isPlayerInCombat(player.getUniqueId()) && playerToggleFlightEvent.isFlying()) {
            playerToggleFlightEvent.setCancelled(true);

            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFallDistance(0);

            Locale locale = localeManager.getConfiguration();
            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.flightNotAllowed()));
        }
    }
}