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

## Implemented features

- Management mode (long-click or manage button) with per-item delete — `onDeleteItem` wired to `viewModel.deleteItem(item)`
- Checkbox toggling — `setChecked()` works end-to-end (local + remote) via `SyncedGroceryRepository`

## In-progress work

- **Editing an item's name** — long-click in management mode should let the user edit the item, not just delete it. Not wired up yet: no edit UI (dialog/inline field), no `viewModel.editItem(...)` or equivalent, no repository-level update call from the UI. Check `GroceryListActivity.setupRecyclerView()`'s `onLongClick` handler for the current state of this.
- **Repository `update()` TODOs** — `update()` is still `TODO("Not yet implemented")` in both `FirestoreGroceryRepository` and `SyncedGroceryRepository`. Editing won't sync remotely until these are implemented.

### Known bugs

- **Offline delete lost on app close** — if the user deletes an item while offline then force-kills the app, Firestore's queued write is not guaranteed to flush when back online. Fix requires WorkManager (see `docs/IDEAS.md` — Guaranteed remote deletes).
