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

import com.github.lukesky19.skycombatlog.manager.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * This class listens to when a player takes damage and if that damage came from another player, mark them both as in combat.
 */
public class PlayerDamageListener implements Listener {
    private final CombatManager combatManager;

    /**
     * Constructor
     * @param combatManager A CombatManager instance.
     */
    public PlayerDamageListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    /**
     * Listens to when two players takes damage and marks them in combat.
     * @param entityDamageByEntityEvent A EntityDamageByEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if(entityDamageByEntityEvent.getEntity() instanceof Player targetPlayer && entityDamageByEntityEvent.getDamageSource().getCausingEntity() instanceof Player sourcePlayer) {
            combatManager.addPlayerInCombat(sourcePlayer, sourcePlayer.getUniqueId());
            combatManager.addPlayerInCombat(targetPlayer, targetPlayer.getUniqueId());
        }
    }
}
