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

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import com.github.lukesky19.skylib.format.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * This class listens to when a player disconnects and if they are in combat kills them.
 */
public class PlayerQuitListener implements Listener {
    private final SkyCombatLog skyCombatLog;
    private final LocaleManager localeManager;
    private final CombatManager combatManager;

    /**
     * Constructor
     * @param skyCombatLog The SkyCombatLog plugin
     * @param localeManager A LocaleManager instance.
     * @param combatManager A CombatManager instance.
     */
    public PlayerQuitListener(SkyCombatLog skyCombatLog, LocaleManager localeManager, CombatManager combatManager) {
        this.skyCombatLog = skyCombatLog;
        this.localeManager = localeManager;
        this.combatManager = combatManager;
    }

    /**
     * Listens to when a player disconnects and kills them if they are in combat.
     * @param playerQuitEvent A PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        @NotNull Locale locale = localeManager.getLocale();
        Player player = playerQuitEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        if(combatManager.isPlayerInCombat(uuid)) {
            combatManager.removePlayerInCombat(player, uuid);

            combatManager.addPlayerKilled(uuid);

            player.setHealth(0);

            List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", player.getName()));
            Component message = FormatUtil.format(locale.prefix() + locale.playerCombatLogged(), placeholders);
            for(Player p : skyCombatLog.getServer().getOnlinePlayers()) {
                p.sendMessage(message);
            }
        }
    }
}
