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
package com.github.lukesky19.skycombatlog.manager;

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.manager.SettingsManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.configuration.record.Settings;
import com.github.lukesky19.skylib.format.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages when a player is in combat.
 */
public class CombatManager {
    private final SkyCombatLog skyCombatLog;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;

    private final HashMap<UUID, Integer> playersInCombat = new HashMap<>();
    private final List<UUID> killedPlayers = new ArrayList<>();
    private BukkitTask timerTask;

    /**
     * Constructor
     * @param skyCombatLog The SkyCombatLog plugin
     * @param settingsManager A SettingsManager instance
     * @param localeManager A LocaleManager instance
     */
    public CombatManager(SkyCombatLog skyCombatLog, SettingsManager settingsManager, LocaleManager localeManager) {
        this.skyCombatLog = skyCombatLog;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;

        startTimerTask();
    }

    /**
     * Marks a player as in combat and sends the action bar timer.
     * @param player A Player
     * @param uuid A Player's UUID
     */
    public void addPlayerInCombat(@NotNull Player player, @NotNull UUID uuid) {
        Settings settings = settingsManager.getSettings();

        if(settings != null) {
            playersInCombat.put(uuid, settings.combatTime());
            sendActionBar(player, uuid);
        } else {
            skyCombatLog.getComponentLogger().error("<red>Unable to put player into combat due to invalid plugin settings.</red>");
        }
    }

    /**
     * Removes a player that was marked in combat and clears the action bar.
     * @param player A Player
     * @param uuid A Player's UUID
     */
    public void removePlayerInCombat(@NotNull Player player, @NotNull UUID uuid) {
        playersInCombat.remove(uuid);

        if(player.isOnline() && player.isConnected()) {
           removeActionBar(player);
        }
    }

    /**
     * Checks if a player is in combat.
     * @param uuid The UUID of the player.
     * @return true if in combat, false if not.
     */
    public boolean isPlayerInCombat(@NotNull UUID uuid) {
        return playersInCombat.containsKey(uuid);
    }

    /**
     * Marks a player was killed for combat logging.
     * @param uuid The UUID of the player.
     */
    public void addPlayerKilled(@NotNull UUID uuid) {
        killedPlayers.add(uuid);
    }

    /**
     * Removes a player that was marked as being killed for combat logging.
     * @param uuid The UUID of the player.
     */
    public void removePlayerKilled(@NotNull UUID uuid) {
        killedPlayers.remove(uuid);
    }

    /**
     * Checks if a player was killed for combat logging.
     * @param uuid The UUID of the player.
     * @return true if the player was killed, false if not.
     */
    public boolean wasPlayerKilled(@NotNull UUID uuid) {
        return killedPlayers.contains(uuid);
    }

    /**
     * Gets the player's combat time ends.
     * @param uuid The UUID of the player.
     * @return An integer of their combat time or null if not in combat.
     */
    @Nullable
    public Integer getPlayerCombatTimer(@NotNull UUID uuid) {
        return playersInCombat.get(uuid);
    }

    /**
     * Sends the player an action bar with their combat timer.
     * @param player The Player
     * @param uuid The Player's UUID
     */
    private void sendActionBar(@NotNull Player player, @NotNull UUID uuid) {
        @NotNull Locale locale = localeManager.getLocale();
        int time = playersInCombat.get(uuid);
        String timeMessage = localeManager.getTimeMessage(time);
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("time", timeMessage));

        Component actionBar = FormatUtil.format(locale.actionBar(), placeholders);
        player.sendActionBar(actionBar);
    }

    /**
     * Clears the player's action bar with their combat timer
     * @param player The Player
     */
    private void removeActionBar(@NotNull Player player) {
        Component actionBar = FormatUtil.format("");
        player.sendActionBar(actionBar);
    }

    /**
     * Starts the timer task that handles player's timers and sending action bars.
     */
    private void startTimerTask() {
        timerTask = skyCombatLog.getServer().getScheduler().runTaskTimer(skyCombatLog, () -> {
            Iterator<Map.Entry<UUID, Integer>> iterator = playersInCombat.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                UUID uuid = entry.getKey();
                Player player = skyCombatLog.getServer().getPlayer(uuid);

                if(player != null && player.isOnline() && player.isConnected()) {
                    int time = entry.getValue() - 1;

                    if(time <= 0) {
                        iterator.remove();

                        removeActionBar(player);
                    } else {
                        playersInCombat.put(uuid, time);

                        sendActionBar(player, uuid);
                    }
                }
            }
        }, 0L, 20L);
    }

    /**
     * Stops the timer task that handles player's timers and sending action bars.
     */
    public void stopTimerTask() {
        if(timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
