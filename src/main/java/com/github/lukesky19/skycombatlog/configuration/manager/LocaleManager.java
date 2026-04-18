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
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skycombatlog.configuration.record.Settings;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.time.Time;
import com.github.lukesky19.skylib.common.api.time.TimeUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class loads the plugin's locale configuration.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Locale DEFAULT_LOCALE = new Locale(
            1,
            "<red><bold>SkyCombatLog</bold></red><gray> ▪ </gray>",
            "<green>The plugin has reloaded successfully.</green>",
            "<red>You are now in combat! You will be unable to teleport and will be killed if you log out.",
            "<green>You are no longer in combat.</green>",
            "<dark_red>Player <aqua><player_name></aqua> logged out in combat and was killed!</dark_red>",
            "<dark_red>You cannot teleport while in combat.</dark_red>",
            "<dark_red>Flight disabled because you entered combat.</dark_red>",
            "<dark_red>Flight is not allowed while in combat.</dark_red>",
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

    /**
     * Constructor
     * @param skyCombatLog A {@link SkyCombatLog} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(
            @NonNull SkyCombatLog skyCombatLog,
            @NonNull SettingsManager settingsManager)  {
        super(skyCombatLog, Locale.class);
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's locale if not null or the default locale otherwise.
     * @return The plugin's locale if not null or the default locale otherwise.
     */
    @Override
    public @NonNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.plain("Failed to load plugin's locale due to plugin settings being null."));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtility.plain("Failed to load plugin's locale to use in settings.yml is null."));
            return;
        }

        String localeString = settings.locale();
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + (localeString + ".yml"));
        setConfigurationPath(path);

        super.loadConfiguration();
    }

    @Override
    public void saveDefaultConfiguration() {
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Migrate the locale.
     * @param locale The {@link Locale} to migrate.
     * @return The migrated {@link Locale} or null if migration failed.
     */
    @Override
    public @Nullable Locale migrateConfiguration(@NonNull Locale locale) {
        if(locale.version() == 0) {
            return new Locale(
                    1,
                    locale.prefix(),
                    locale.reload(),
                    locale.inCombat(),
                    locale.combatEnded(),
                    locale.playerCombatLogged(),
                    locale.teleportInCombat(),
                    locale.actionBar(),
                    "<dark_red>Flight disabled because you entered combat.</dark_red>",
                    "<dark_red>Flight is not allowed while in combat.</dark_red>",
                    locale.timeMessage());
        }

        return locale;
    }

    /**
     * Validates if the locale is missing any strings.
     */
    @Override
    public boolean validateConfiguration(@Nullable Locale configuration) {
        if(configuration == null) return false;

        if(configuration.prefix() == null
                || configuration.reload() == null
                || configuration.playerCombatLogged() == null
                || configuration.teleportInCombat() == null) {
            logger.error(AdventureUtility.deserialize("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtility.deserialize("You can regenerate your locale file by deleting it or adding the missing messages to resolve the issue."));

            return false;
        }

        return true;
    }

    /**
     * Gets the time message to display in the boss bar.
     * @param time The time in seconds.
     * @return A String containing the time message.
     */
    public @NonNull String getTimeMessage(int time) {
        Locale locale = this.getConfiguration();
        Time timeRecord = TimeUtil.millisToTime(time * 1000L);

        List<TagResolver.Single> placeholders = List.of(
                Placeholder.parsed("years", String.valueOf(timeRecord.years())),
                Placeholder.parsed("months", String.valueOf(timeRecord.months())),
                Placeholder.parsed("weeks", String.valueOf(timeRecord.weeks())),
                Placeholder.parsed("days", String.valueOf(timeRecord.days())),
                Placeholder.parsed("hours", String.valueOf(timeRecord.hours())),
                Placeholder.parsed("minutes", String.valueOf(timeRecord.minutes())),
                Placeholder.parsed("seconds", String.valueOf(timeRecord.seconds())));

        StringBuilder stringBuilder = getStringBuilder(locale, timeRecord);

        return MiniMessage.miniMessage().serialize(AdventureUtility.deserialize(stringBuilder.toString(), placeholders));
    }

    /**
     * Builds the string by populating any non-zero individual time units.
     * @param locale The plugin's locale
     * @param timeRecord The record containing the individual time units to display.
     * @return A populated StringBuilder. May be empty if all time units were 0 and no suffix was configured.
     */
    private @NonNull StringBuilder getStringBuilder(Locale locale, Time timeRecord) {
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