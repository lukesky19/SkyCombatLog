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
package com.github.lukesky19.skycombatlog.configuration.manager;

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.configuration.record.Settings;
import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skylib.record.Time;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class loads the plugin's locale configuration.
 */
public class LocaleManager {
    private final SkyCombatLog skyCombatLog;
    private final SettingsManager settingsManager;
    private final Locale DEFAULT_LOCALE = new Locale(
            "1.0.0.0",
            "<red><bold>SkyCombatLog</bold></red><gray> ▪ </gray>",
            "<green>The plugin has reloaded successfully.</green>",
            "<red>You are now in combat! You will be unable to teleport and will be killed if you log out.",
            "<green>You are no longer in combat.</green>",
            "<dark_red>Player <aqua><player_name></aqua> logged out in combat and was killed!</dark_red>",
            "<dark_red>You cannot teleport while in combat.</dark_red>",
            "<yellow>Combat Timer: <white><time></white></yellow>",
            new Locale.TimeMessage(
                    "",
                    "<aqua><years></aqua> year(s)",
                    "<aqua><months></aqua> month(s)",
                    "<aqua><weeks></aqua> week(s)",
                    "<aqua><days></aqua> day(s)",
                    "<aqua><hours></aqua> hour(s)",
                    "<aqua><minutes></aqua> minute(s)",
                    "<aqua><seconds></aqua> second(s)",
                    "."));
    private Locale locale;

    /**
     * Constructor
     * @param skyCombatLog The SkyCombatLog plugin.
     * @param settingsManager A SettingsManager instance.
     */
    public LocaleManager(
            SkyCombatLog skyCombatLog,
            SettingsManager settingsManager)  {
        this.skyCombatLog = skyCombatLog;
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's locale if not null or the default locale otherwise.
     * @return The plugin's locale if not null or the default locale otherwise.
     */
    @NotNull
    public Locale getLocale() {
        if(locale == null) return DEFAULT_LOCALE;
        return locale;
    }

    /**
     * Reloads the plugin's locale.
     */
    public void reload() {
        ComponentLogger logger = skyCombatLog.getComponentLogger();
        locale = null;

        copyDefaultLocales();

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.error(FormatUtil.format("<red>Failed to load plugin's locale due to plugin settings being null.</red>"));
            return;
        }
        if(settings.locale() == null) {
            logger.error(FormatUtil.format("<red>Failed to load plugin's locale to use in settings.yml is null.</red>"));
            return;
        }

        String localeString = settings.locale();
        Path path = Path.of(skyCombatLog.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException exception) {
            throw new RuntimeException(exception);
        }

        validateLocale();
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     */
    private void copyDefaultLocales() {
        Path path = Path.of(skyCombatLog.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyCombatLog.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Checks if the locale configuration has any null-values.
     */
    private void validateLocale() {
        if(locale == null) return;

        if(locale.configVersion() == null
                || locale.prefix() == null
                || locale.reload() == null
                || locale.playerCombatLogged() == null
                || locale.teleportInCombat() == null) {
            locale = null;

            skyCombatLog.getComponentLogger().warn(FormatUtil.format("<yellow>One of the plugin's locale messages was null. Double-check your configuration."));
            skyCombatLog.getComponentLogger().info(FormatUtil.format("<white>The plugin will use the default config until the issue is resolved.</white>"));
        }
    }

    /**
     * Gets the time message to display in the boss bar.
     * @param time The time in seconds.
     * @return A String containing the time message.
     */
    @NotNull
    public String getTimeMessage(int time) {
        Locale locale = this.getLocale();
        Time timeRecord = FormatUtil.millisToTime(time * 1000L);

        List<TagResolver.Single> placeholders = List.of(
                Placeholder.parsed("years", String.valueOf(timeRecord.years())),
                Placeholder.parsed("months", String.valueOf(timeRecord.months())),
                Placeholder.parsed("weeks", String.valueOf(timeRecord.weeks())),
                Placeholder.parsed("days", String.valueOf(timeRecord.days())),
                Placeholder.parsed("hours", String.valueOf(timeRecord.hours())),
                Placeholder.parsed("minutes", String.valueOf(timeRecord.minutes())),
                Placeholder.parsed("seconds", String.valueOf(timeRecord.seconds())));

        StringBuilder stringBuilder = getStringBuilder(locale, timeRecord);

        return MiniMessage.miniMessage().serialize(FormatUtil.format(stringBuilder.toString(), placeholders));
    }

    /**
     * Builds the string by populating any non-zero individual time units.
     * @param locale The plugin's locale
     * @param timeRecord The record containing the individual time units to display.
     * @return A populated StringBuilder. May be empty if all time units were 0 and no suffix was configured.
     */
    private @NotNull StringBuilder getStringBuilder(Locale locale, Time timeRecord) {
        Locale.TimeMessage timeMessage = locale.timeMessage();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(timeMessage.prefix());

        boolean isFirstUnit = true;

        if (timeRecord.years() > 0) {
            stringBuilder.append(timeMessage.years());
            isFirstUnit = false;
        }

        if (timeRecord.months() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.months());
            isFirstUnit = false;
        }

        if (timeRecord.weeks() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.weeks());
            isFirstUnit = false;
        }

        if (timeRecord.days() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.days());
            isFirstUnit = false;
        }

        if (timeRecord.hours() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.hours());
            isFirstUnit = false;
        }

        if (timeRecord.minutes() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.minutes());
            isFirstUnit = false;
        }

        if (timeRecord.seconds() > 0) {
            if (!isFirstUnit) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(timeMessage.seconds());
            isFirstUnit = false;
        }

        if(isFirstUnit) {
            stringBuilder.append(timeMessage.seconds());
        }

        stringBuilder.append(timeMessage.suffix());
        return stringBuilder;
    }
}
