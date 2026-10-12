# PolySprint

[![Modrinth downloads](https://img.shields.io/modrinth/dt/i9xRThb3?style=flat&logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/polysprint)
[![CurseForge downloads](https://img.shields.io/curseforge/dt/1147262?style=flat&logo=curseforge&label=CurseForge&color=f16436)](https://www.curseforge.com/minecraft/mc-mods/polysprint)

PolySprint lets you toggle sprint and sneak, so you press the key once and do not have to hold it. A line of text on your HUD shows whether you are sprinting, sneaking, flying or riding.

## Features

### Toggle sprint and sneak

- Press the sprint key once to keep sprinting, and press it again to stop. Sneak works the same way.
- PolySprint saves the toggle, so it is still on after you restart the game.
- You can bind a separate key for each toggle. The normal sprint or sneak key then works as a hold key again. Both separate keys are off and unbound by default.
- "Disable W-Tap Sprint" stops a double tap of the forward key from starting a sprint. It is off by default.

On Minecraft 1.21.1 and newer, "Enable Toggle Sprint" and "Enable Toggle Sneak" are the same settings as the Sprint and Sneak options in Minecraft's Controls menu. A change in one place shows up in the other. On 1.8.9, toggle sprint is on by default and toggle sneak is off.

### State HUD

The HUD shows one line, such as `[Sprinting (toggled)]`. It hides while you stand or walk.

- For sprinting, sneaking and descending in flight, the line says whether you hold the key, toggled it, or the game started it.
- It also shows when you fly or ride.
- You can rewrite each of the 12 texts, and one button resets them all.
- You move and scale the line in the OneConfig HUD editor.

![The state HUD while you hold sneak to descend in flight](https://i.imgur.com/RsBcnpT.png)

### Flying

- The HUD adds your sprint state to the flying text. You can turn that off.
- "Unsneak on Flight Start" switches a toggled sneak off when you start to fly, so you do not sink. It is on by default.
- "Fly Boost" multiplies your creative flight speed by 1 to 10 while you hold the sprint key. The default is 4. It is off by default, and it only works in singleplayer.

### Command

`/polysprint` opens the settings screen.

## Supported versions

- Minecraft 1.8.9 on Ornithe.
- Minecraft 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 and 26.3 on Fabric.

PolySprint needs [OneConfig](https://modrinth.com/mod/oneconfig) and Fabric Language Kotlin. The Fabric builds also need Fabric API.

## Support

- Report bugs and ask questions on the [Polyfrost Discord](https://polyfrost.org/discord).
- Check [Polyfrost Status](https://status.polyfrost.org/) if a Polyfrost service is down.

## Building

```sh
./gradlew ":26.3:build"
```

Replace `26.3` with any supported Minecraft version. The build needs JDK 21 and JDK 25 installed.
