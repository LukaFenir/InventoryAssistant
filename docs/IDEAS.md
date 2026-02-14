# Ideas

## Architecture
- Dependency injection (Hilt or Koin) — right now dependencies are manually constructed. DI would make the app more testable and is a natural next step.
- Multiple lists — e.g. separate lists for different shops or weeks. Introduces relational data (one-to-many) in Room and more complex state management.
- Offline-first sync — using WorkManager to queue changes and sync to a remote backend when online. This is where architecture gets genuinely hard (conflict resolution, retry strategies).
- Home screen widget — a quick-glance shopping list widget. Widgets have their own lifecycle and data access patterns, quite different from Activities.
- Multi-module project — splitting into :app, :data, :grocery, :settings modules. Enforces boundaries at the build level rather than just by convention.
- Undo/redo — undoing a checked-off or deleted item. Interesting state management challenge (command pattern or snackbar-based approach).

## Next

- Sync data between devices
- Deleting items
- Reordering items