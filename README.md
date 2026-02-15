# Inventory Assistant

Inventory Assistant (Ivy) is an educational Android project built as a hands-on way to learn Android development by creating a practical application for day-to-day use.

## Goals

- **Learn by doing** — build a real app while picking up Android development skills in Kotlin
- **Testing in Android** — strengthen existing coding skills by learning to write and run tests in an Android environment
- **Sync across devices** — work towards a shopping list that stays up to date on multiple devices
- **Grow over time** — start with a shopping list, then extend into home inventory recording and management
- **Accessible from the start** — include simple accessibility features as a core part of the design, not an afterthought

## Current Features

- Grocery list with adding and checking off items
- Light and dark theme support
- Custom colour palette designed to reduce eye strain for users with Irlens Syndrome
- Bottom navigation between Home, Inventory, and Settings screens

## Tech Stack

- **Language:** Kotlin
- **UI:** Material Design components
- **Persistence:** Room for local storage of shopping list state
- **Data layer:** Repository pattern to abstract data access from the UI
- **Reactive state:** Kotlin Flow and StateFlow for real-time UI updates
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 16)

## Status

This is a work in progress. Data is persisted locally using Room. Planned next steps include device sync and additional list management features such as deleting and reordering items.
