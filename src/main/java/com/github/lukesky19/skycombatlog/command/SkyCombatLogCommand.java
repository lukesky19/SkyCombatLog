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
package com.github.lukesky19.skycombatlog.command;

import com.github.lukesky19.skycombatlog.SkyCombatLog;
import com.github.lukesky19.skycombatlog.configuration.manager.LocaleManager;
import com.github.lukesky19.skycombatlog.configuration.record.Locale;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * This class handles the creation of the SkyCombatLog command.
 */
public class SkyCombatLogCommand {
    private final SkyCombatLog skyCombatLog;
    private final LocaleManager localeManager;

    /**
     * Constructor
     * @param skyCombatLog The SkyCombatLog plugin
     * @param localeManager A LocaleManager instance/
     */
    public SkyCombatLogCommand(SkyCombatLog skyCombatLog, LocaleManager localeManager) {
        this.skyCombatLog = skyCombatLog;
        this.localeManager = localeManager;
    }

    /**
     * Creates a command to be passed into the LifeCycleAPI.
     * @return A LiteralCommandNode of a CommandSourceStack.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skycombatlog")
                .requires(ctx -> ctx.getSender().hasPermission("skycombatlog.commands.skycombatlog"));

        builder.then(Commands.literal("reload")
            .requires(ctx -> ctx.getSender().hasPermission("skycombatlog.commands.skycombatlog.reload"))
            .executes(ctx -> {
                Locale locale = localeManager.getLocale();

                skyCombatLog.reload();

                ctx.getSource().getSender().sendMessage(FormatUtil.format(locale.prefix() + locale.reload()));

                return 1;
            })
        );

        return builder.build();
    }
}
