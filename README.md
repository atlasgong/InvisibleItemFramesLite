# InvisibleItemFramesLite

A lightweight, high-performance Paper plugin that brings craftable invisible item frames to your server. Designed to work out of the box with no setup required, this plugin keeps things simple with no commands and no permissions, leaving virtually no footprint on your server. Optional configuration configuration is available for customizing plugin behavior.

See a [video demo](https://youtu.be/2kLfRHyH5cc), and the [gallery](https://modrinth.com/plugin/invisibleitemframes/gallery).

<p align="center">
  <img src="https://raw.githubusercontent.com/atlasgong/InvisibleItemFramesLite/main/docs/images/demo.gif" alt="Invisibility Demo" width="600">
</p>
<p align="center">
  <em>Item frames become invisible when holding an item.</em>
</p>

## Obtaining
You can craft invisible item frames by placing 8 item frames (or glow item frames) around a phantom membrane.
| Ingredients | Crafting recipe |
|-------------|-----------------|
| [Item Frame](https://minecraft.wiki/w/Item_Frame) +<br>[Phantom Membrane](https://minecraft.wiki/w/Phantom_Membrane) | <img width="250" height="116" alt="The crafting recipe of an invisible item frame (8 item frames, glow or normal, surrounding a phantom membrane." src="https://raw.githubusercontent.com/atlasgong/InvisibleItemFramesLite/main/docs/images/crafting_recipe.png" /> |

This recipe can be configured in [config.yml](https://github.com/atlasgong/InvisibleItemFramesLite/blob/main/src/main/resources/config.yml).

## Features

- Item frames automatically become invisible when holding an item
- Supports both regular and glow item frames
- Container pass-through allows interacting with containers behind frames (sneak to rotate items instead)
- Configurable crafting recipes and plugin behavior
- No commands or permissions
- Supports Paper 26.3+

## Installation

1. Download the plugin JAR on [Modrinth](https://modrinth.com/plugin/invisibleitemframes)
2. Drop the plugin JAR into your `plugins/` folder
3. Restart your server
4. Craft your item frames :)

## Migrating from v2 to v3

Server admins are expected to update their configuration to replace any legacy chat formatting with Adventure's MiniMessage formatting: https://docs.advntr.dev/minimessage/format. Alternatively, if you wish, you may simply delete the config file and a fresh one will automatically be generated on the next server restart.

## Contributing

Code is licensed under MPLv2, as per our [upstream](https://github.com/tiffany352/InvisibleItemFrames/blob/main/LICENSE-MPL). See [LICENSE-MPL](./LICENSE-MPL) for details. Any contributions are assumed to be under this license.

### Prerequisites

- A UNIX-like system (Linux, macOS, WSL)
- JDK 25

### Getting Started

1. Clone the repository.
2. Run `./gradlew test` and update or add tests as needed.
3. Run `./gradlew build`, then test the plugin JAR from `build/libs/` on Paper.
4. Create a PR.

## bStats Metrics

<img src="https://bstats.org/signatures/bukkit/InvisibleItemFramesLite.svg" alt="bStats Metrics" width="100%">

## Credits
This project is forked from [InvisibleItemFrames](https://github.com/tiffany352/InvisibleItemFrames) by [Tiffany Bennett (Tiffnix)](https://tiffnix.com/contact). This fork updates the original plugin for newer Paper versions, fixes existing bugs, and strips down unnecessary functionality. 

