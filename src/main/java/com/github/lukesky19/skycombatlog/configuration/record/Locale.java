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
package com.github.lukesky19.skycombatlog.configuration.record;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;

/**
 * The plugin's locale.
 * @param configVersion The file's config version
 * @param prefix The plugin's prefix
 * @param reload The plugin's reload message
 * @param inCombat The message sent when a player is in combat
 * @param combatEnded The message sent when a player is no longer in combat.
 * @param playerCombatLogged The message sent when a player combat logs.
 * @param teleportInCombat The message sent when a player tries to teleport in combat.
 * @param actionBar The action bar message to send when in combat.
 * @param timeMessage Allows formatting the time message in the action bar.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        String reload,
        String inCombat,
        String combatEnded,
        String playerCombatLogged,
        String teleportInCombat,
        String actionBar,
        TimeMessage timeMessage) {
    /**
     *
     * @param prefix The text to display before the first time unit.
     * @param years The text to display when the player's time enters years.
     * @param months The text to display when the player's time enters months.
     * @param weeks The text to display when the player's time enters weeks.
     * @param days The text to display when the player's time enters days.
     * @param hours The text to display when the player's time enters hours.
     * @param minutes The text to display when the player's time enters minutes.
     * @param seconds The text to display when the player's time enters seconds.
     * @param suffix The text to display after the last time unit.
     */
    @ConfigSerializable
    public record TimeMessage(
            String prefix,
            String years,
            String months,
            String weeks,
            String days,
            String hours,
            String minutes,
            String seconds,
            String suffix) {}
}
