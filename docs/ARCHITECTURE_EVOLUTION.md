# Shopping List App - Architecture Evolution

This document outlines the planned evolution of the shopping list app's sync architecture, moving from vendor lock-in to a self-hosted solution with real-time capabilities.

## Overview

The app will evolve through five iterations:
0. **Foundation**: Basic app structure with static UI, no database or persistence
1. **Local persistence**: Offline-first app with Room database (no sync)
2. **First sync implementation**: Firebase for multi-device sync (quick MVP)
3. **Independence**: Custom REST API with polling and FCM for background
4. **Real-time optimization**: WebSockets for foreground, FCM for background

**Current Status**: Iteration 1 - offline-first app with Room database for local persistence. The grocery list supports adding and checking off items, with data persisted locally.

## Iteration 0: Basic App Shell

**Status**: ✅ Complete

**Goal**: Establish the basic app structure, navigation, and static UI before introducing any database or persistence.

**Characteristics**:
- Static UI with hardcoded/in-memory data
- No database or persistence (data lost on app restart)
- Navigation between screens
- Learning Android fundamentals

**What Was Built**:
- Bottom navigation bar (Home, List, Settings)
- Static grocery list with checkboxes
- Basic MVVM structure
- XML layouts with ViewBinding

---

## Iteration 1: Offline-First with Room Database (Current)

**Status**: ✅ Complete

![Iteration 1 Architecture](iteration-1-offline-first.mermaid)

**Goal**: Add Room database for local persistence so the app actually saves data. Build a solid, working shopping list app that works perfectly offline before adding any sync complexity.

**Characteristics**:
- Single device only (no sync)
- Fully functional offline
- Clean architecture foundation
- Data persisted locally with Room
- Manual dependency creation (singleton pattern, direct instantiation)

**Key Components**:
- **Room Database**: Local SQLite database
  - `GroceryItem` entity (id: Int, name: String, isChecked: Boolean)
  - `GroceryDao` for CRUD operations
- **GroceryViewModel**: Business logic, state management (extends AndroidViewModel)
- **GroceryRepository**: Abstract data access (will make adding sync easier later)
- **GroceryListActivity + GroceryAdapter**: XML layouts with ViewBinding and RecyclerView

**Features**:
- ✅ Add items to the list
- ✅ Check off items as purchased
- ✅ Persist data locally with Room
- ✅ XML UI with ViewBinding and RecyclerView
- ✅ Light/dark theme toggle (Settings screen)

**Why This Matters**:
- Room database becomes the **single source of truth** for all future iterations
- Repository pattern makes adding sync sources (Firebase, REST API) easy later
- ViewModels stay clean - they don't care where data comes from
- You can use and test the app immediately without needing a backend
- Proves the app concept before investing in sync infrastructure

**Architecture**:
```
GroceryListActivity + GroceryAdapter (XML + ViewBinding)
    ↓
GroceryViewModel (business logic & state)
    ↓
GroceryRepository (abstraction layer)
    ↓
GroceryDao → Room Database (local storage)
```

This architecture makes it trivial to add Firebase or REST API later - you just add another data source behind the GroceryRepository.

---

## Iteration 2: Firebase Implementation (Next)

**Status**: 🔨 In Development

![Iteration 2 Architecture](iteration-2-firebase.mermaid)

**Characteristics**:
- Fully managed by Firebase
- Real-time sync out of the box
- Complete vendor lock-in
- Simple to implement
- Limited backend control
- **Perfect for learning Android development without backend complexity**

**Why Start Here**:
- Focus on Android development (MVVM, Room, etc.) without building backend
- Get working multi-device sync quickly to validate the app concept
- Learn how real-time sync should work before building your own
- Can always migrate later once you've proven the app is useful

**Key Components**:
- Firebase Realtime Database / Firestore for data storage
- Firebase Auth for authentication
- Firebase SDK handles all sync automatically
- Room database for local caching and offline support (already built!)

**Migration Path from Iteration 1**:
1. Set up Firebase project and add config files
2. Add Firebase dependencies to your app
3. Implement Firebase Auth (email/password or anonymous)
4. Create Firestore data structure matching your Room schema:
   ```
   users/{userId}/lists/{listId}
   users/{userId}/lists/{listId}/items/{itemId}
   ```
5. Update Repository to:
   - Write changes to both Room (immediate) and Firestore (sync)
   - Listen to Firestore changes and update Room
   - Room remains single source of truth for UI
6. Handle sync conflicts (last-write-wins is simplest)
7. Test with two devices

**Data Flow**:
1. User makes change → ViewModel → Repository
2. Repository writes to Room immediately (UI updates)
3. Repository writes to Firestore (background sync)
4. Firestore listener receives changes from other devices
5. Repository updates Room with remote changes
6. UI reactively updates from Room Flow/LiveData

**Design Decisions to Enable Later Migration**:
- Keep business logic in ViewModels, not tied to Firebase
- Use Repository pattern to abstract data source
- Structure Room database as single source of truth
- Firebase is just another data source behind the repository
- This makes swapping to REST API later much easier

---

## Iteration 3: REST API + Polling + FCM

**Status**: 🔮 Planned

![Iteration 3 Architecture](iteration-3-rest-polling-fcm.mermaid)

**Goals**:
- Gain control of backend and data
- Reduce vendor dependency (only FCM for notifications)
- Enable future self-hosting options

**Characteristics**:
- Custom REST API (AWS or local server)
- Polling every 10-30 seconds when app is in foreground
- FCM push notifications wake app for background updates
- ~30 second delay acceptable for shopping list use case

**Key Components**:
- **REST API**: Standard CRUD endpoints (GET/POST/PUT/DELETE)
  - `GET /lists` - Retrieve shopping lists
  - `GET /lists/{id}/items` - Get items for a list
  - `POST /lists/{id}/items` - Add new item
  - `PUT /items/{id}` - Update item
  - `DELETE /items/{id}` - Remove item
- **Database**: PostgreSQL, MySQL, or similar relational DB
- **FCM Integration**: Server-side service to send push notifications
- **Foreground Service**: Android service that polls for updates while app is visible
- **Room Database**: Local cache for offline capability

**Data Flow**:
1. User makes change → POST/PUT/DELETE to REST API
2. Server updates database
3. If app is foreground: discovers change on next poll (10-30s)
4. If app is background: server sends FCM notification → app wakes → fetches updates via REST

**Trade-offs**:
- ✅ Control your own data
- ✅ Can host anywhere (AWS, local server, VPS)
- ✅ Battery efficient
- ✅ Minimal data usage
- ✅ Simple to implement and debug
- ⚠️ 10-30 second delay in foreground
- ⚠️ Still depends on FCM for background notifications

**Migration Path from Iteration 2**:
1. Build REST API with same data model as Firebase
2. Add FCM token registration endpoint
3. Implement polling service in Android app
4. Test dual-write to both Firebase and REST API
5. Switch read operations to REST API
6. Once stable, remove Firebase dependency

---

## Iteration 4: WebSocket + FCM Hybrid

**Status**: 🔮 Future Enhancement

![Iteration 4 Architecture](iteration-4-websocket-fcm.mermaid)

**Goals**:
- Instant real-time updates when actively using the app
- Maintain battery efficiency when backgrounded
- Optimal user experience

**Characteristics**:
- WebSocket connection for instant bidirectional updates
- Connection only maintained while app is in foreground
- FCM still handles background notifications
- Best UX with reasonable battery trade-off

**Key Components**:
- **REST API**: Same as Iteration 2
- **WebSocket Server**: Real-time push server (Socket.IO, raw WebSockets, etc.)
- **Message Broker** (Optional): Redis or RabbitMQ to coordinate updates between REST API and WebSocket server
- **WebSocket Client**: Android library (OkHttp WebSocket, Scarlet, etc.)
- **FCM Integration**: Same as Iteration 2 for background
- **Lifecycle Management**: Connect WebSocket on `onResume()`, disconnect on `onPause()`

**Data Flow (Foreground)**:
1. User makes change → POST to REST API
2. Server updates database
3. Server publishes change to message broker
4. WebSocket server broadcasts to all connected clients
5. Other user's app receives instant update via WebSocket
6. Local Room database updated

**Data Flow (Background)**:
1. Same as Iteration 2 - FCM notification wakes app
2. App fetches updates via REST API
3. Does NOT establish WebSocket connection

**Trade-offs**:
- ✅ Instant updates when using the app
- ✅ Battery efficient (WS only when foreground)
- ✅ Still works offline with local cache
- ✅ Minimal data usage (only actual changes transmitted)
- ⚠️ More complex implementation
- ⚠️ Need to handle WebSocket reconnection
- ⚠️ Need message broker for coordination (or polling fallback)
- ⚠️ Still depends on FCM for background

**Migration Path from Iteration 3**:
1. Add WebSocket server to backend
2. Optional: Add Redis/RabbitMQ for message coordination
3. Implement WebSocket client in Android app
4. Add lifecycle hooks (connect on resume, disconnect on pause)
5. Add reconnection logic with exponential backoff
6. Keep polling as fallback if WebSocket fails
7. Gradually transition users to WebSocket

---

## Comparison Matrix

| Feature | Iter 0 (Shell) | Iter 1 (Room) | Iter 2 (Firebase) | Iter 3 (REST + Poll) | Iter 4 (WS + FCM) |
|---------|---------------|--------------|------------------|---------------------|-------------------|
| **Data Persistence** | ❌ No | ✅ Local | ✅ Local + Cloud | ✅ Local + Cloud | ✅ Local + Cloud |
| **Multi-device Sync** | ❌ No | ❌ No | ✅ Yes | ✅ Yes | ✅ Yes |
| **Vendor Lock-in** | None | None | Complete | Minimal (FCM only) | Minimal (FCM only) |
| **Update Speed** | N/A | N/A | Instant | 10-30s (foreground) | Instant (foreground) |
| **Offline Support** | ❌ No | ✅ Perfect | ✅ Yes (Firebase SDK) | ✅ Yes (Room cache) | ✅ Yes (Room cache) |
| **Battery Usage** | Minimal | Minimal | Very Low | Very Low | Low-Medium |
| **Implementation Complexity** | Easy | Easy | Very Easy | Medium | Complex |
| **Backend Control** | N/A | N/A | None | Full | Full |
| **Self-hosting Option** | N/A | N/A | No | Yes | Yes |
| **Good For** | Learning UI | Learning, Testing | MVP, Validation | Production, Independence | High-UX Production |

---

## Technology Stack Considerations

### Backend Options

**AWS Deployment**:
- **REST API**: API Gateway + Lambda, or ECS/Fargate
- **Database**: RDS (PostgreSQL/MySQL) or DynamoDB
- **WebSocket**: API Gateway WebSocket or ECS
- **Message Broker**: ElastiCache (Redis) or Amazon MQ

**Local Server**:
- **REST API**: Spring Boot, Express.js, FastAPI
- **Database**: PostgreSQL, MySQL
- **WebSocket**: Socket.IO, ws (Node.js), or Spring WebSocket
- **Message Broker**: Redis, RabbitMQ

### Android Libraries

- **REST**: Retrofit + OkHttp
- **WebSocket**: OkHttp WebSocket or Scarlet
- **FCM**: Firebase Cloud Messaging SDK
- **Local DB**: Room
- **Dependency Injection**: Manual (singleton pattern, direct instantiation)

---

## Recommended Timeline

1. **Phase 0** (Complete): Basic app shell
   - Set up Android project structure
   - Build navigation and static UI
   - XML layouts with ViewBinding

2. **Phase 1** (Current): Add Room database for local persistence
   - Implement Room database schema
   - Build Repository pattern and DAOs
   - Wire ViewModels to real data
   - Test offline functionality thoroughly

3. **Phase 2**: Implement Firebase sync (Iteration 2)
   - Add Firebase to project
   - Implement authentication
   - Add sync logic to Repository
   - Test multi-device sync
   - Use app with partner to validate

4. **Phase 3**: Build REST API backend, migrate to Iteration 3
   - Build backend (Spring Boot/Express/FastAPI)
   - Implement REST endpoints
   - Replace Firebase with REST + polling
   - Gain data independence

5. **Phase 4** (Optional): Implement Iteration 4
   - Add WebSocket server
   - Implement real-time foreground updates
   - Optimize UX if instant sync is important

**Alternative Fast Track**: Skip Firebase entirely and go straight from Iteration 1 → Iteration 3 if you're comfortable building the backend first.

---

## Key Decision Points

### Should you add sync at all (move from Iteration 1 to 2)?
**Yes if**:
- You and your partner want to share shopping lists
- One person often does the shopping while the other adds items
- You want to collaborate in real-time

**Stay on Iteration 1 if**:
- You're the only user
- You're still learning Android fundamentals
- The app isn't proven useful yet
- You want to focus on features, not infrastructure

### Should you move to Iteration 3?
**Yes if**:
- You've validated the app is useful (both using it regularly)
- You want control over your data
- You want to avoid ongoing Firebase costs at scale
- You want to learn backend development
- 10-30 second delay is acceptable

**Stay on Iteration 2 if**:
- Still experimenting with features
- Firebase free tier covers your usage
- You value simplicity over control
- Don't want to manage infrastructure
- App isn't mission-critical yet

### Should you implement Iteration 4?
**Yes if**:
- Instant sync is important for UX
- You're frequently making changes simultaneously
- You want to learn real-time architectures

**Stick with Iteration 3 if**:
- 10-30 second delay is fine
- You want to minimize complexity
- Battery life is a top priority

---

## Alternative Paths

### Skip Firebase? (Iteration 1 → Iteration 3 directly)

**You could go directly to Iteration 3 if**:
- You're already comfortable with backend development
- You want to learn full-stack development from the start
- You don't mind the extra initial complexity
- You're certain you'll want your own backend eventually

**Pros of skipping Firebase**:
- No vendor lock-in from day one
- No migration work later
- Learn backend + frontend together
- Complete control immediately

**Cons of skipping Firebase**:
- More initial complexity (two codebases to build)
- Slower time to working multi-device sync
- More moving parts to debug
- Need to learn backend patterns while learning Android

**Recommendation**: Start with Firebase (Iteration 2) if you're primarily learning Android development and want to focus on one thing at a time. Move to Iteration 3 once you've proven the app is useful and you're comfortable with Android patterns.

### Stay on Iteration 1?

**Totally valid if**:
- You're the only user (no need for sync)
- You're focused on learning Android fundamentals first
- You want to perfect the offline experience before adding complexity
- The app isn't mission-critical yet

Room database works perfectly for single-device use cases. Many apps start here and never need sync!

---

## Notes

- **Iteration 1 is crucial** - Room database remains the single source of truth through all future iterations
- All iterations from 1 onward maintain offline-first architecture via Room database
- Repository pattern in Iteration 1 makes adding any sync mechanism (Firebase, REST, WebSocket) straightforward
- FCM dependency in Iterations 3 & 4 can be replaced with self-hosted push service (more complex)
- WebSocket in Iteration 4 can fallback to polling if connection fails
- Message broker in Iteration 4 is optional - you can poll database from WebSocket server instead
- The app is fully functional at every iteration - each step adds capabilities without breaking existing functionality
