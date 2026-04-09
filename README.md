# SexyFingers

A Godot 4 Android plugin providing unified messaging and calling through a single API — with guarded numbers, safe defaults, and agent-ready automation.

## What it does

One `send()` call handles WhatsApp, SMS, dialer, direct call, and the Android share chooser. Numbers must be on the guard list or the action is silently blocked.

## Install

1. Copy `android/plugins/SexyFingers/` into your Godot project's `android/plugins/` directory
2. In Godot: **Project → Export → Android** → enable **Use Gradle Build** and **Custom Build**
3. Create/edit `android/plugins.cfg`:

```ini
[plugin]
name="SexyFingers"
binary="SexyFingersPlugin"
```

## Usage

```gdscript
if Engine.has_singleton("SexyFingers"):
    var sf = Engine.get_singleton("SexyFingers")

    # WhatsApp
    sf.send("whatsapp", "61412345678", "Hello WhatsApp")

    # SMS (opens app)
    sf.send("sms", "61412345678", "Hello SMS")

    # SMS (direct send — requires SEND_SMS permission)
    sf.send("sms", "61412345678", "Hello SMS", true)

    # Share chooser
    sf.send("share", "61412345678", "Hello from SexyFingers!")

    # Open dialer
    sf.send("dialer", "61412345678", "")

    # Direct call (requires CALL_PHONE permission)
    sf.send("call", "61412345678", "", true)
```

## API Reference

### `send(type, number, message, direct=false)`

| Type | Default behavior | With `direct=true` |
|---|---|---|
| `"whatsapp"` | Opens WhatsApp chat | Same |
| `"sms"` | Opens SMS app | Sends via SmsManager |
| `"dialer"` | Opens phone dialer | Same |
| `"call"` | Opens dialer | Places direct call |
| `"share"` | Opens share chooser | Same |
| any other | Falls back to share chooser | Same |

## Guard Numbers

Numbers are hardcoded in `SexyFingersPlugin.java`. Only whitelisted numbers can receive messages or calls. Edit the list to match your needs:

```java
private final List<String> guardNumbers = Arrays.asList(
    "61412345678",
    "61498765432",
    "61455511223"
);
```

Number format: country code + digits, no `+` or spaces.

## Permissions

| Permission | When needed |
|---|---|
| `CALL_PHONE` | `direct=true` with `"call"` |
| `SEND_SMS` | `direct=true` with `"sms"` |

Both are declared in the manifest. Runtime permission requests are handled automatically. Default mode (`direct=false`) requires no permissions and is Play Store safe.

## Build

- Min SDK 21 / Target SDK 34
- Android library module built via Godot's Gradle export
- No external dependencies
