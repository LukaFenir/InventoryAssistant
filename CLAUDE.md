# Inventory Assistant ("Ivy") — Claude Context

## Project overview

Android grocery list / home inventory app. Kotlin, MVVM, Room DB, Firestore sync. Built as a hands-on Android learning project.

**3 screens:** Home (hub), Grocery List (main feature), Settings (light/dark theme toggle).

## Architecture

Repository pattern — `GroceryRepository` interface with three implementations:
- `RoomGroceryRepository` — local SQLite via Room/DAO (source of truth)
- `FirestoreGroceryRepository` — remote Firestore (secondary, write-only for now)
- `SyncedGroceryRepository` — wraps both, fans out every operation to local first then remote. The ViewModel uses this one. `allItems` Flow comes only from local.

Iteration roadmap (from `ARCHITECTURE_EVOLUTION.md`):
1. ✅ Offline-first with Room
2. 🔄 Firebase sync (in progress)
3. Custom REST API + polling + FCM
4. WebSocket for instant sync
5. Multiple lists

---

## Current branch: `main`

*No feature branch in progress — next branch will document its scope here.*

### Known bugs

- **Offline delete lost on app close** — if the user deletes an item while offline then force-kills the app, Firestore's queued write is not guaranteed to flush when back online. Fix requires WorkManager (see `docs/IDEAS.md` — Guaranteed remote deletes).
- **App crashes on checkbox click** — `setChecked()` is `TODO` in both `FirestoreGroceryRepository` and `SyncedGroceryRepository`

---

## Next priorities

1. **Fix checkbox crash** — implement `setChecked()` in `FirestoreGroceryRepository` and `SyncedGroceryRepository`
2. **Remaining repository TODOs** — `update()`, `setChecked()`, `allItems` Flow in `FirestoreGroceryRepository` and `SyncedGroceryRepository`
