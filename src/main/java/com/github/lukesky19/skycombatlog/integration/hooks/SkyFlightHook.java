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
package com.github.lukesky19.skycombatlog.integration.hooks;

import com.github.lukesky19.skyFlight.api.SkyFlightAPI;
import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skylib.common.api.integration.Hook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages interfacing with the SkyFlight plugin.
 */
public class SkyFlightHook implements Hook {
    private final @NonNull SkyCombatLog plugin;
    private @Nullable SkyFlightAPI skyFlightAPI;

    /**
     * Constructor
     * @param plugin A {@link SkyCombatLog} instance.
     */
    public SkyFlightHook(@NonNull SkyCombatLog plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to get the {@link SkyFlightAPI} from SkyFlight.
     */
    @Override
    public void initialize() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("SkyFlight");
        if(plugin != null && plugin.isEnabled()) {
            RegisteredServiceProvider<SkyFlightAPI> rsp = this.plugin.getServer().getServicesManager().getRegistration(SkyFlightAPI.class);
            if(rsp != null) {
                skyFlightAPI = rsp.getProvider();
            }
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return skyFlightAPI != null;
    }

    /**
     * Disable flight for the player.
     * @apiNote Only disables flight if SkyFlight is hooked into.
     * @param player The {@link Player}.
     * @return true if disabled, false if not.
     */
    public boolean disableFlight(@NonNull Player player) {
        if(skyFlightAPI == null) return false;

        return skyFlightAPI.disableFlight(player, false);
    }
}