# InvisibleItemFramesLite

> Forked from [InvisibleItemFrames](https://github.com/tiffany352/InvisibleItemFrames) by [Tiffany Bennett (Tiffnix)](https://tiffnix.com/contact).

A fast, minimal Paper plugin that adds **craftable invisible item frames** to your server; no configuration required, no commands or permissions, just drop it in and go.

See a [video demo](https://youtu.be/2kLfRHyH5cc), and the [gallery](https://modrinth.com/plugin/invisibleitemframes/gallery).

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
- Allows for container pass-through (you can still sneak to rotate items)
- Perfect for clean builds, displays, or shops without visual clutter

## Why This Version?

This is an updated, stripped-down fork of the original [InvisibleItemFrames](https://www.spigotmc.org/resources/invisible-item-frames.85365/) plugin. Compared to the original:

- Lightweight performance; it's tiny, fast, and focused
- Works right out of the box; no additional configuration is needed
- Compatibility with newer Paper versions (1.21+)
- Plenty of bug fixes

No command trees. No permission spaghetti. Just craftable invisible item frames that feel like they could've been shipped natively with vanilla.

## Installation

1. Download the plugin JAR on [Modrinth](https://modrinth.com/plugin/invisibleitemframes)
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

1. Clone the repository
2. Run, update, or write unit tests as necessary
3. Build the project with Maven, test manually in-game
4. Create a PR

## bStats Metrics
<img src="https://bstats.org/signatures/bukkit/InvisibleItemFramesLite.svg" alt="bStats Metrics" width="100%">
