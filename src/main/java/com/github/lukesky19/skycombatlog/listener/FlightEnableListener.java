/*
    SkyCombatLog tracks players in combat, kills them if they disconnect in combat, and prevents plugins teleporting players in combat.
    Copyright (C) 2025 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skycombatlog.listener;

import com.github.lukesky19.skyFlight.api.event.FlightEnableEvent;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

/**
 * Listens for when a player toggles their flight through SkyFlight and prevents flying if in combat.
 */
public class FlightEnableListener implements Listener {
    private final @NonNull LocaleManager localeManager;
    private final @NonNull CombatManager combatManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param combatManager A {@link CombatManager} instance.
     */
    public FlightEnableListener(
            @NonNull LocaleManager localeManager,
            @NonNull CombatManager combatManager) {
        this.localeManager = localeManager;
        this.combatManager = combatManager;
    }

    /**
     * Listens for when a player toggles their flight through SkyFlight and prevents flying if in combat.
     * @param flightEnableEvent A {@link FlightEnableEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFlightEnable(@NonNull FlightEnableEvent flightEnableEvent) {
        Player player = flightEnableEvent.getPlayer();

        if(combatManager.isPlayerInCombat(player.getUniqueId())) {
            flightEnableEvent.setCancelled(true);

            Locale locale = localeManager.getConfiguration();
            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNotAllowed()));
        }
    }
}
