# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SexyFingers** — a Godot 4 Android plugin providing a unified messaging and calling API with guarded number enforcement. Supports WhatsApp, SMS (open or direct send), dialer, direct call, and share chooser. Designed for agent/LLM-driven automation with safe defaults.

## Architecture

- **Single-class plugin pattern** — `SexyFingersPlugin.java` extends `GodotPlugin`, exposes a unified `send()` method via `@UsedByGodot`.
- **Namespace:** `com.example.sexyfingers`
- **Language:** Java
- **Guard numbers** — hardcoded allowlist; the `send()` method silently blocks any number not in the list.
- **Dual-mode dispatch** — `direct=false` (default, Play Store safe) uses intents requiring user confirmation. `direct=true` attempts automated actions (direct SMS send, direct call) if runtime permissions are granted.
- **Intent resolution** — all intents are checked with `resolveActivity()` before launching to prevent crashes on devices missing target apps.

### Directory Layout

```
android/plugins/SexyFingers/
├── build.gradle
└── src/main/
    ├── AndroidManifest.xml
    └── java/com/example/sexyfingers/SexyFingersPlugin.java
```

## Public API (exposed to GDScript)

### `send(String type, String number, String message)` / `send(..., boolean direct)`

Single entry point for all actions. `type` determines the channel:

| Type | `direct=false` (default) | `direct=true` (if permission granted) |
|---|---|---|
| `"whatsapp"` | Open WhatsApp chat via wa.me link | Same (no direct WhatsApp API) |
| `"sms"` | Open SMS app with pre-filled message | Send SMS silently via SmsManager |
| `"dialer"` | Open dialer with number | Same |
| `"call"` | Open dialer | Place direct call (ACTION_CALL) |
| `"share"` | Open Android share chooser | Same |
| any other | Falls back to share chooser | Same |

Number format: country code + digits, no `+` or spaces (e.g. `61412345678`).

## Permissions

- `CALL_PHONE` — required for `direct=true` with `"call"` type
- `SEND_SMS` — required for `direct=true` with `"sms"` type
- Both declared in manifest; runtime permission requests handled automatically via `hasCallPermission()` / `hasSmsPermission()`

## Build Configuration

- **Min SDK:** 21 / **Target SDK:** 34 / **Compile SDK:** 34
- Gradle Android library plugin (`com.android.library`)
- Built as part of Godot's Gradle export — enable "Use Gradle Build" and "Custom Build" in Godot Android export settings.

## GDScript Integration

Plugin registers as `"SexyFingers"` singleton:

```gdscript
if Engine.has_singleton("SexyFingers"):
    var sf = Engine.get_singleton("SexyFingers")
    sf.send("sms", "61412345678", "Hello")
```

Plugin config in `android/plugins.cfg`:
```ini
[plugin]
name="SexyFingers"
binary="SexyFingersPlugin"
```
