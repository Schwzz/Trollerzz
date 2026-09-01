# 🎭 TrollSystem

[![Platform](https://img.shields.io/badge/Platform-Spigot%20%2F%20Paper-gold.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Minecraft-1.21%2B-brightgreen.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A professional, high-performance, and feature-rich Minecraft troll plugin for Spigot 1.21+. Created by **Swartzz**, this system provides admins with a GUI-based interface to prank/troll players with a variety of "horror" and "annoyance" themed trolls.

## ✨ Features

- **Advanced GUI Framework**: Paginated player list and expanded 54-slot toggleable menus for a seamless admin experience.
- **13 Unique Trolls**: Ranging from subtle psychological pranks to chaotic inventory management.
- **Smart Logic**: Automatic cleanup on player disconnect and sophisticated stalker AI for the "Haunt" troll.
- **Cross-Version Support**: Built using XSeries to ensure sound and material compatibility across versions.

## 🛠️ The Troll Arsenal

| Troll | Mechanic |
| :--- | :--- |
| **Flip** | Any block interaction rotates the player's view 180°. |
| **TNT Rain** | Spawns phantom TNT that vanishes before detonation. |
| **Bad Luck** | Ores drop Poisonous Potatoes instead of gems. |
| **Sticky Fingers** | Locks the player's inventory—no moving items allowed. |
| **Haunted Sounds** | Plays random, spooky ambient sounds every 30 seconds. |
| **Haunt** | A villager stalks the player; it vanishes with a scream when looked at. |
| **Hotbar Shuffle** | Randomly rearranges the player's hotbar items every 5 seconds. |
| **Shield Drop** | Forces the player to drop their shield when attempting to block. |
| **Gapple Trap** | Golden Apples give Blindness and Weakness instead of buffs. |
| **Vegetarian Curse** | Replaces all meat/fish items with Dried Kelp upon consumption. |
| **Fake Death** | Every 30 seconds broadcasts a random death message (10 templates) to every online player except the victim — they see nothing. |
| **Gravity Flip** | Every 8 seconds, if the player is on the ground, launches them into the air with a slight random sideways drift — enough height to deal fall damage on landing. |
| **Phantom Inventory** | Hooks InventoryOpenEvent — specifically CRAFTING type (the player’s own inventory), then closes it 1 tick later so it flickers shut every time they try to open it. |

## 🚀 Commands & Permissions

- `/troll <player>` - Open the troll menu for a specific player.
- `/troll open` - Open a paginated list of all online players.
- `/troll reset <player>` - Instantly stop all active trolls on a target.

**Permission:** `troll.use` (Default: OP)
---
Developed by **Swartzz**
