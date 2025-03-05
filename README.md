# SkyCombatLog
## Description
* A combat log plugin that tracks players in combat, kills them if they disconnect in combat, and prevents plugins teleporting players while in combat.

## Dependencies
* SkyLib

## Commands
- /skycombatlog reload - Command to reload the plugin

## Permisisons
- `skycombatlog.commands.skycombatlog` - The permission to access the /skycombatlog command.
- `skycombatlog.commands.skycombatlog.reload` - The permission to access /skycombatlog reload.

## Issues, Bugs, or Suggestions
* Please create a new [Github Issue](https://github.com/lukesky19/SkyCombatLog/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyShop and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## FAQ
Q: What Minecraft versions does this plugin support?

A: 1.21.0, 1.21.1, 1.21.2, 1.21.3, 1.21.4

Q: Are there any plans to support any other versions?

A: No.

Q: Does this work on Spigot and Paper?

A: This will work on Paper and most likely forks of Paper.

Q: Is Folia supported?

A: There is no Folia support at this time. I may look into it in the future though.

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
