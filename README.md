# SkyCombatLog
## Description
* A combat log plugin that tracks players in combat, kills them if they disconnect in combat, and prevents plugins teleporting players while in combat.

## Features
* Tracks players in combat.
* Displays an action bar when in combat.
* Kills the player if they disconnect while in combat.
* Prevents plugins teleporting players while in combat.
* Disables flight if enabled.

## Dependencies
* [SkyLib](https://github.com/lukesky19/SkyLib)

## Commands
- /skycombatlog reload - Command to reload the plugin

## Permisisons
- `skycombatlog.commands.skycombatlog` - The permission to access the /skycombatlog command.
- `skycombatlog.commands.skycombatlog.reload` - The permission to access /skycombatlog reload.

## Issues, Bugs, or Suggestions
* Please create a new [Github Issue](https://github.com/lukesky19/SkyCombatLog/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyCombatLog and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## FAQ
Q: What Minecraft versions does this plugin support?

A: 1.21.0, 1.21.1, 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, and 1.21.11.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot? Paper? (Insert other server software here)?

A: I only support Paper, but this will likely also work on forks of Paper (untested). There are no plans to support any other server software (i.e., Spigot or Folia).

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Go to [SkyFlight](https://github.com/lukesky19/SkyFlight) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
