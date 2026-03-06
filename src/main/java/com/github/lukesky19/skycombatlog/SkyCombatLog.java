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
package com.github.lukesky19.skycombatlog;

import com.github.lukesky19.skycombatlog.command.SkyCombatLogCommand;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.manager.SettingsManager;
import com.github.lukesky19.skycombatlog.integration.HookManager;
import com.github.lukesky19.skycombatlog.integration.hooks.SkyFlightHook;
import com.github.lukesky19.skycombatlog.listener.*;
import com.github.lukesky19.skycombatlog.manager.CombatManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
 * The main plugin class
 */
public final class SkyCombatLog extends SkyPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private CombatManager combatManager;

    /**
     * Constructor
     */
    public SkyCombatLog() {}

    /**
     * The method ran on plugin startup.
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;

        // Classes
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        HookManager hookManager = new HookManager(this);
        combatManager = new CombatManager(this, settingsManager, localeManager, hookManager);

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

        SkyFlightHook skyFlightHook = hookManager.getHook(SkyFlightHook.class);
        if(skyFlightHook.isHooked()) {
            pm.registerEvents(new FlightEnableListener(localeManager, combatManager), this);
        } else {
            pm.registerEvents(new PlayerToggleFlightListener(localeManager, combatManager), this);
        }

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
    @Override
    public void reload() {
        settingsManager.loadConfiguration();
        localeManager.loadConfiguration();
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if(skyLib != null && skyLib.isEnabled()) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 5) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.deserialize("SkyLib Version 1.5.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}