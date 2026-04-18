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
package com.github.lukesky19.skycombatlog.configuration.manager;

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.record.Settings;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages everything related to handling the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param skyCombatLog A {@link SkyCombatLog} instance.
     */
    public SettingsManager(@NonNull SkyCombatLog skyCombatLog) {
        super(skyCombatLog, Path.of(skyCombatLog.getDataFolder() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public void saveDefaultConfiguration() {
        plugin.saveResource("settings.yml", false);
    }

    @Override
    public @Nullable Settings migrateConfiguration(@NonNull Settings settings) {
        if(settings.version() == 0) {
            return new Settings(1, settings.locale(), settings.combatTime());
        }

        return settings;
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings configuration) {
        return configuration != null;
    }
}