# PlayerHider-1.12.2

This addon allows you to talk in the LabyMod VoiceChat continuously!

[![Build Addon](https://github.com/PrincessAkira/PlayerHider-1.8/actions/workflows/release.yml/badge.svg)](https://github.com/PrincessAkira/PlayerHider-1.8/actions/workflows/release.yml)

## Help needed

```
- Mute Players in Voice when they are hidden
I wasnt able to edit the "mutedPlayers" variable in the VoiceChat Config File.
If anyone is able to do this, it would be so much appreciated as a commit!
```

```
- Getting the mute func of labymod to work.
- Make this work on non-Forge Builds of Labymod. Thanks Laby....
```

## Setup Workspace

```
gradlew setupDecompWorkspace 
```

#### Setup for InteliJ

```
gradlew idea
```

#### Setup for Eclipse

```
gradlew eclipse
```

#### Build Addon

```
gradlew build 
```

#### Debug Addon

```
-Dfml.coreMods.load=net.labymod.core.asm.LabyModCoreMod -DdebugMode=true -Daddonresources=addon.json
```

For more information you can check out the
LabyMod [documentation](https://docs.labymod.net/pages/create-addons/introduction/).
