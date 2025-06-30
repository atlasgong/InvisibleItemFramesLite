# InvisibleItemFramesLite

> Forked from [InvisibleItemFrames](https://github.com/tiffany352/InvisibleItemFrames) by [Tiffany Bennett (Tiffnix)](https://tiffnix.com/contact).

> [!IMPORTANT]
> Spigot support will be dropped in `v3.0.0`. The last build to support Spigot is [v2.0.0-beta.7](https://github.com/atlasgong/InvisibleItemFramesLite/releases/tag/v2.0.0-beta.7), and all subsequent builds will solely support Paper.

A fast, minimal Paper plugin that adds **craftable invisible item frames** to your server; no configuration required, no commands or permissions, just drop it in and go.

See a full video demo here: https://youtu.be/2kLfRHyH5cc

<table width="100%">
  <tr>
    <th style="text-align: center;">Item Frame Demo</th>
    <th style="text-align: center;">Crafting Recipe</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0672ce90-f42c-4f5d-8b7d-fe04adf94f1d" alt="Item Frame Demo" width="100%">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/04ab7b6a-1c56-4f89-805d-cde3a6230389" alt="Crafting Recipe"  width="100%"><br>
      This crafting recipe is configurable!
    </td>
  </tr>
</table>

## Features

- Adds invisible item frames you can craft and place like normal
- Frames are visible when empty, and automatically turn invisible once an item is added
- Perfect for clean builds, displays, or shops without visual clutter

## Why This Version?

This is an updated, stripped-down fork of the original [InvisibleItemFrames](https://www.spigotmc.org/resources/invisible-item-frames.85365/) plugin. Compared to the original:

- Lightweight performance; it's tiny, fast, and focused
- Works right out of the box; no additional configuration is needed
- Compatibility with newer Paper versions (1.21+)
- Plenty of bug fixes

No command trees. No permission spaghetti. Just craftable invisible item frames that feel like they could've been shipped natively with vanilla.

## Installation

1. Download the plugin JAR on [Hangar](https://hangar.papermc.io/atlasgong/InvisibleItemFramesLite)
2. Drop the plugin JAR into your `plugins/` folder
3. Restart your server
4. Craft your item frames :)

## Migrating from v2 to v3

Server admins are expected to update their configuration to replace any legacy chat formatting with Adventure's MiniMessage formatting: https://docs.advntr.dev/minimessage/format. Alternatively, if you wish, you may simply delete the config file and a fresh one will automatically be generated on the next server restart.

## Contributing

Code is licensed under MPLv2, as per the original. See [LICENSE-MPL](./LICENSE-MPL) for details. Any contributions are assumed to be under this license.

0. **Prerequisites**

- JDK 21
- Maven

1. Clone the repository:

```
git clone https://github.com/atlasgong/InvisibleItemFramesLite
```

2. Run, update, or write unit tests as necessary
3. Build the project with Maven, test manually in-game
4. Create a PR

## bStats Metrics
<img src="https://bstats.org/signatures/bukkit/InvisibleItemFramesLite.svg" alt="bStats Metrics" width="100%">