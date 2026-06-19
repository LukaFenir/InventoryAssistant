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

## Current branch: `delete-items-manual` (Issue #1)

### What IS fully implemented

- **Selection mode UI:** long-click an item → selection bar replaces normal header; clicking items in selection mode toggles selection; item count updates live
- **ViewModel:** `_selectedIds` StateFlow, `isInSelectionMode` derived state, `toggleSelection(id)`, `deleteItem(item)`, `deleteSelected()` (filters allItems by selectedIds, deletes each, clears selectedIds)
- **Activity:** delete button wired up via `setupDeleteSelectedItems()` → calls `viewModel.deleteSelected()`
- **`RoomGroceryRepository.delete()`** — implemented, delegates to DAO
- **`SyncedGroceryRepository.delete(item)`** — calls `local.delete(item)` then `remote.delete(item)`
- **`FirestoreGroceryRepository.delete(item)`** — calls `itemsCollection.document(item.id.toString()).delete().await()`

### What is NOT yet implemented

1. **Cancel (X) button** in selection bar — no click listener wired yet; ViewModel also has no `clearSelection()` method

### Known bugs

- **Offline delete lost on app close** — if the user deletes an item while offline then force-kills the app, Firestore's queued write is not guaranteed to flush when back online. Fix requires WorkManager (see `docs/IDEAS.md` — Guaranteed remote deletes).
- **App crashes on checkbox click** — `setChecked()` is `TODO` in both `FirestoreGroceryRepository` and `SyncedGroceryRepository`

### Tests written

- `SyncedGroceryRepositoryTest`: `delete_removedFromLocalAndRemote`, `delete_localFailure_doesNotDeleteFromRemote` — both use a shared `setupItems()` helper
- `FirestoreGroceryRepositoryTest`: `delete_removesItemFromFirestore` — uses a shared `setupItems()` helper

---

## Next priorities

1. **Wire cancel button** — add `clearSelection()` to ViewModel, hook up X button in activity
2. **Fix checkbox crash** — implement `setChecked()` in `FirestoreGroceryRepository` and `SyncedGroceryRepository`
3. **Remaining repository TODOs** — `update()`, `setChecked()`, `allItems` Flow in `FirestoreGroceryRepository` and `SyncedGroceryRepository`
