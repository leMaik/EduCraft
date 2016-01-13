# EduCraft
[![Build Status](https://ci.wertarbyte.com/job/EduCraft/badge/icon)](https://ci.wertarbyte.com/job/EduCraft/)

EduCraft is a Bukkit plugin for learning how to program. The players can write programs in Lua, using a simple API based on [Minecraft Hour of Code][hoc].  
These programs are then executed in fully-customizable environments with an armor stand as a bot that executes the commands. You can even configure various checks
that are run after the program to see if it reached a goal.

What does it look like? Consider the following program, it moves the bot and then
shears the sheep.

```lua
moveForward() -- move to the sheep
moveForward()
shear() -- shear the sheep
```
![Demo environment](/demo-environment.png)

## Usage

### Run programs
Programs for the bot need to be written in books. While holding a book with code, use the command `/ec run <environment> [delay]` to run that code.
`<environment>` needs to be replaced with the name of an environment.

The `[delay]` parameter is optional and can be used to specify the time to wait after executing a function (in milliseconds).
Lower values will make the program run faster. Default is 1000 ms.

Only one player can run a program in an environment at a time.

### Stop programs
To stop all your running programs, use `/ec stop`. To stop a program in a specific environment, use `/ec stopo <environment>`. You can
only stop your own programs unless you have the `educraft.stop.any` permission.

### Reset an environment
Environments are automatically reset when running a program.  
To reset an environment manually, use `/ec reset <environment>`. Note that an environment can only be reset if no program is running
in it or if the program that runs in it was started by you, unless you have the `educraft.stop.any` permission.

## Programming the bot
EduCraft provides a set of functions to control the bot.
See [the API documentation][the-api] for details.

## Setup
### Installation
Grab the plugin .jar from our [CI server][ci] or compile it yourself and put it into the plugin directory. Start your server and you're done.

### Permissions
| Permission          | Default value | Description                                                         |
| ------------------- | ------------- | ------------------------------------------------------------------- |
| `educraft.run`      | `true`        | Allows players to run programs using `/ec run`.                     |
| `educraft.stop`     | `true`        | Allows players to stop their own programs.                          |
| `educraft.stop.any` | `op`          | Allows players to stop programs that were started by other players. |
| `educraft.reset`    | `true`        | Allows players to reset unused environments.                        |

### Setup environments
*Coming soon...*

### Reminder
You should not use this plugin in a survival world or in any world where players could
benefit from bots placing diamond ore and then farming it. ;)  
For example, you could put the environments into their own world or even on a different server instance.

[hoc]: https://studio.code.org/s/mc/
[the-api]: https://github.com/leMaik/EduCraft/wiki/The-API
[ci]: https://ci.wertarbyte.com/job/EduCraft/lastStableBuild/

## License
EduCraft is licensed under the MIT license. Read the [license file][license] for more
information.

EduCraft includes JNBT, Copyright (c) 2010 Graham Edgecombe. Read the [notice file][notice] for more information.

[license]: /LICENSE
[notice]: /NOTICE
