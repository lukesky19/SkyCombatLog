/*
    SkyCombatLog tracks players in combat, kills them if they disconnect in combat, and prevents plugins teleporting players in combat.
    Copyright (C) 2025  lukeskywlker19

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

import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import com.github.lukesky19.skylib.format.FormatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

/**
 * This class listens to when a player teleports and if they are in combat cancels it.
 */
public class PlayerTeleportListener implements Listener {
    private final LocaleManager localeManager;
    private final CombatManager combatManager;

    /**
     * Constructor
     * @param localeManager A LocaleManager instance.
     * @param combatManager A CombatManager instance.
     */
    public PlayerTeleportListener(LocaleManager localeManager, CombatManager combatManager) {
        this.localeManager = localeManager;
        this.combatManager = combatManager;
    }

    /**
     * Listens to when a player teleports and cancels it if they are in combat.
     * @param playerTeleportEvent A PlayerTeleportEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent playerTeleportEvent) {
        Player player = playerTeleportEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        if(playerTeleportEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)
                || playerTeleportEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)
                || playerTeleportEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN)) {
            if(combatManager.isPlayerInCombat(uuid)) {
                Locale locale = localeManager.getLocale();

                playerTeleportEvent.setCancelled(true);
                player.sendMessage(FormatUtil.format(locale.prefix() + locale.teleportInCombat()));
            }
        }
    }
}
