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
package com.github.lukesky19.skycombatlog;

import com.github.lukesky19.skycombatlog.command.SkyCombatLogCommand;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.manager.SettingsManager;
import com.github.lukesky19.skycombatlog.listener.PlayerDamageListener;
import com.github.lukesky19.skycombatlog.listener.PlayerDeathListener;
import com.github.lukesky19.skycombatlog.listener.PlayerQuitListener;
import com.github.lukesky19.skycombatlog.listener.PlayerTeleportListener;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * The main plugin class
 */
public final class SkyCombatLog extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private CombatManager combatManager;

    /**
     * The method ran on plugin startup.
     */
    @Override
    public void onEnable() {
        // Classes
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        combatManager = new CombatManager(this, settingsManager, localeManager);

        // Register plugin command
        SkyCombatLogCommand skyMinesCommand = new SkyCombatLogCommand(this, localeManager);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands ->
                        commands.registrar().register(skyMinesCommand.createCommand(),
                                "Command to manage and use the SkyCombatLog plugin.",
                                List.of("scl", "combatlog")));

        // Register Listeners.
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerQuitListener(this, localeManager, combatManager), this);
        pm.registerEvents(new PlayerTeleportListener(localeManager, combatManager), this);
        pm.registerEvents(new PlayerDamageListener(combatManager), this);
        pm.registerEvents(new PlayerDeathListener(combatManager), this);

        reload();
    }

    /**
     * The method ran on plugin disable.
     */
    @Override
    public void onDisable() {
        combatManager.stopTimerTask();
    }

    /**
     * Reloads all plugin data.
     */
    public void reload() {
        settingsManager.reload();
        localeManager.reload();
    }
}
