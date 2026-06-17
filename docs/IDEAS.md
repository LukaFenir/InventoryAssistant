# Ideas

## Architecture
- Dependency injection (Hilt or Koin) — right now dependencies are manually constructed. DI would make the app more testable and is a natural next step.
    - The factory pisses me off. Would Hilt be better?
    - **Pre-requisite for guaranteed deletes** — injecting repositories into a WorkManager `Worker` is awkward without Hilt; you'd have to resolve dependencies manually inside `doWork()`.
- Guaranteed remote deletes — currently Firestore deletes are fire-and-forget after local removal. If the app is force-killed while offline, Firestore's queued write may be lost. 
  WorkManager would persist the delete job across process death and retry with backoff until the server acknowledges. This is where architecture gets genuinely hard — conflict resolution in particular (e.g. item deleted locally but modified remotely before the delete syncs). 
  **Pre-requisite: Hilt DI migration** (see above).
- Multiple lists — e.g. separate lists for different shops or weeks. Introduces relational data (one-to-many) in Room and more complex state management.
- Home screen widget — a quick-glance shopping list widget. Widgets have their own lifecycle and data access patterns, quite different from Activities.
- Multi-module project — splitting into :app, :data, :grocery, :settings modules. Enforces boundaries at the build level rather than just by convention.
- Undo/redo — undoing a checked-off or deleted item. Interesting state management challenge (command pattern or snackbar-based approach).

## Next

- Sync data between devices
- Deleting items
- Reordering items