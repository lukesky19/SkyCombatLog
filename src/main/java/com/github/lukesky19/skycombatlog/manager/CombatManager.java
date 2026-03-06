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
package com.github.lukesky19.skycombatlog.manager;

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.manager.SettingsManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.configuration.record.Settings;
import com.github.lukesky19.skycombatlog.integration.HookManager;
import com.github.lukesky19.skycombatlog.integration.hooks.SkyFlightHook;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Manages when a player is in combat.
 */
public class CombatManager {
    private final @NonNull SkyCombatLog skyCombatLog;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull HookManager hookManager;

    private final @NonNull Map<UUID, Integer> playersInCombat = new HashMap<>();
    private final @NonNull List<UUID> killedPlayers = new ArrayList<>();
    private @Nullable BukkitTask timerTask;

    /**
     * Constructor
     * @param skyCombatLog A {@link SkyCombatLog} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public CombatManager(
            @NonNull SkyCombatLog skyCombatLog,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull HookManager hookManager) {
        this.skyCombatLog = skyCombatLog;
        this.logger = skyCombatLog.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.hookManager = hookManager;

        startTimerTask();
    }

    /**
     * Marks a player as in combat and sends the action bar timer.
     * @param player The {@link Player}.
     * @param uuid The Player's {@link UUID}.
     */
    public void addPlayerInCombat(@NonNull Player player, @NonNull UUID uuid) {
        Locale locale = localeManager.getConfiguration();
        Settings settings = settingsManager.getConfiguration();

        if(settings != null) {
            playersInCombat.put(uuid, settings.combatTime());
            sendActionBar(player, uuid);

            // Disable Flight if the player can fly
            if(player.getAllowFlight()) {
                SkyFlightHook skyFlightHook = hookManager.getHook(SkyFlightHook.class);
                if(skyFlightHook.isHooked()) {
                    boolean result = skyFlightHook.disableFlight(player);
                    if(result) {
                        player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
                    } else {
                        logger.warn(AdventureUtil.deserialize("Failed to disable flight from SkyFlight when the player " + player.getName() + " was placed into combat."));
                    }
                } else {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.setFallDistance(0);

                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
                }
            }
        } else {
            logger.error(AdventureUtil.deserialize("Unable to put player into combat due to invalid plugin settings."));
        }
    }

    /**
     * Removes a player that was marked in combat and clears the action bar.
     * @param player A Player
     * @param uuid A Player's UUID
     */
    public void removePlayerInCombat(@NonNull Player player, @NonNull UUID uuid) {
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
    public boolean isPlayerInCombat(@NonNull UUID uuid) {
        return playersInCombat.containsKey(uuid);
    }

    /**
     * Marks a player was killed for combat logging.
     * @param uuid The UUID of the player.
     */
    public void addPlayerKilled(@NonNull UUID uuid) {
        killedPlayers.add(uuid);
    }

    /**
     * Removes a player that was marked as being killed for combat logging.
     * @param uuid The UUID of the player.
     */
    public void removePlayerKilled(@NonNull UUID uuid) {
        killedPlayers.remove(uuid);
    }

    /**
     * Checks if a player was killed for combat logging.
     * @param uuid The UUID of the player.
     * @return true if the player was killed, false if not.
     */
    public boolean wasPlayerKilled(@NonNull UUID uuid) {
        return killedPlayers.contains(uuid);
    }

    /**
     * Gets the player's combat time ends.
     * @param uuid The UUID of the player.
     * @return An integer of their combat time or null if not in combat.
     */
    public @Nullable Integer getPlayerCombatTimer(@NonNull UUID uuid) {
        return playersInCombat.get(uuid);
    }

    /**
     * Sends the player an action bar with their combat timer.
     * @param player The Player
     * @param uuid The Player's UUID
     */
    private void sendActionBar(@NonNull Player player, @NonNull UUID uuid) {
        Locale locale = localeManager.getConfiguration();
        int time = playersInCombat.get(uuid);
        String timeMessage = localeManager.getTimeMessage(time);
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("time", timeMessage));

        Component actionBar = AdventureUtil.deserialize(locale.actionBar(), placeholders);
        player.sendActionBar(actionBar);
    }

    /**
     * Clears the player's action bar with their combat timer
     * @param player The Player
     */
    private void removeActionBar(@NonNull Player player) {
        Component actionBar = AdventureUtil.deserialize("");
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