# aiotpa - All-in-one TPA solution

**Aiotpa** is a lightweight, efficient, and feature-rich teleport request (`/tpa`) plugin for spigot based servers.  
It provides flexible teleportation options with cooldowns, economy integration, and fully customizable language support.

---

## ✨ Features
- **Teleport Requests**
    - `/tpa <player>` – Request to teleport to a player
    - `/tpahere <player>` – Request a player to teleport to you
    - `/tpaccept` – Accept an incoming request
    - `/tpadeny` – Deny an incoming request
    - `/tpcancel` – Cancel your outgoing request
    - `/tpatoggle` – Enable or disable receiving TPA requests
    - `/tpasettings` – Manage personal TPA settings
    - `/tpalist` – View incoming/outgoing requests
- **Cooldowns & Warmup** – Prevent abuse with configurable delays
- **Economy Support** – Vault integration for charging teleport costs
- **Auto Accept / Auto Deny** – Player-configurable preferences
- **Multi-Language Support** – Easily editable YAML language files
- **Crossworld Restrictions** – Optional disallowance of cross-world teleports
- **Actionbar & Title Messages** – Enhanced player feedback

---

## 📦 Installation
1. Build the source or download latest .jar file from [releases](https://github.com/dtzdev/aiotpa/releases/tag/main).
2. Place the `.jar` file into your server’s `plugins` folder.
3. Start the server to generate default configuration and language files.
4. Edit `config.yml` and `lang/lang_en_us.yml` to your liking.
5. Reload or restart your server.

---

## ⚙ Configuration
The plugin generates a `config.yml` with:
- **language** – Language file to use (default: `en_us`)
- **tpaEnabled** – Toggle global TPA availability
- **cooldowns** – Set per-player request cooldowns
- **economy settings** – Enable, disable, or adjust teleport costs
- **warmup** – Delay before teleportation happens
- **messages** – Toggle actionbar/title messages

---

## 🔌 Dependencies
- **Vault** – Required for economy features  
  *(Without Vault, economy features will be disabled automatically)*

---

## 📜 Credits
- [dtzdev](https://github.com/dtzdev)