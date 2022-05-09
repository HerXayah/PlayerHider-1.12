# PlayerHider-1.12.2

An Labymod Addon to hide players from view and VoiceChat.
Doesnt work on Vanilla Version, due to the event not implemented. Use with Labymod + Forge.

[![Build Addon](https://github.com/PrincessAkira/PlayerHider-1.12/actions/workflows/release.yml/badge.svg)](https://github.com/PrincessAkira/PlayerHider-1.12/actions/workflows/release.yml)


## Help needed

```
- Hide Voicechat Icon
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
