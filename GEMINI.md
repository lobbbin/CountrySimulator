# Country Simulator - Project Context

## Project Overview
**Country Simulator** is a text-based strategy and simulation Android game. Players take on the role of a national leader, managing their country's economy, military, diplomacy, and domestic policies through turn-based gameplay. The game features complex systems including government types, random events, resource management, and various game-over conditions.

- **Status**: Version 2.0 (Major Update)
- **Primary Tech Stack**: Kotlin 1.9.x, Jetpack Compose, Material 3, MVVM Architecture.
- **Core Logic**: Turn-based progression with randomized event triggers and state-based consequences.

## Architecture & Design
The project follows a clean **MVVM (Model-View-ViewModel)** architectural pattern:

- **Domain Layer (`app/src/main/kotlin/com/countrysimulator/game/domain/`)**: 
    - `Models.kt`: Pure Kotlin data classes representing the game state (`Country`, `CountryStats`, `Resources`, `GameEvent`, etc.).
    - `GameLogic.kt`: Centralized business logic for turn processing, income calculation, and event effects.
- **Data Layer (`app/src/main/kotlin/com/countrysimulator/game/data/`)**: 
    - `GameRepository.kt`: Manages persistent game state using **DataStore Preferences**.
- **Presentation Layer (`app/src/main/kotlin/com/countrysimulator/game/presentation/`)**:
    - `GameViewModel.kt`: Manages UI state using `StateFlow` and handles user interactions.
    - `GameScreen.kt`: Declarative UI built with **Jetpack Compose** and Material 3 components.

## Technical Specifications
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle 8.x with Kotlin DSL (`.gradle.kts`)
- **State Management**: `StateFlow` + `ViewModel`
- **Dependency Injection**: Manual injection (no external frameworks like Hilt/Koin are currently used).
- **Persistence**: Android Jetpack DataStore (Preferences).

## Key Game Mechanics (v2.0)
- **10 Government Types**: Democracy, Monarchy, Republic, Dictatorship, Communism, Theocracy, Federation, Confederacy, Technocracy, Socialism.
- **10 Core Stats**: Population, Economy, Military, Happiness, Stability, Technology, Education, Healthcare, Environment, Crime.
- **Resource Management**: Food, Energy, and Materials.
- **Event System**: Categorized events (Economic, Military, Political, etc.) with varying severity (Minor to Catastrophic).
- **Game Over Conditions**: Bankruptcy, Revolution, Invasion, Tech Failure, Famine, Environmental Collapse, Nuclear Winter, Civil War.

## Building and Running
### Prerequisites
- JDK 17
- Android SDK 24+

### Common Commands
- **Assemble Debug APK**: `./gradlew assembleDebug`
- **Install on Device**: `./gradlew installDebug`
- **Run Unit Tests**: `./gradlew test`
- **Clean Project**: `./gradlew clean`
- **Lint Check**: `./gradlew lint` (if configured)

## Development Conventions
- **Immutability**: Game state is managed using immutable data classes; updates are performed via `.copy()`.
- **State Management**: The UI observes a single `StateFlow<GameState>` from the ViewModel.
- **Logic Placement**: Business rules and stat calculations belong in `GameLogic.kt` or domain models, not in the UI or ViewModel.
- **Compose**: Use Material 3 components and follow standard Compose performance practices (e.g., hoisting state, using `remember`).
- **Naming**: 
    - ViewModels: `[Feature]ViewModel`
    - Composables: `[Feature]Screen` or `[Component]Item`
    - Domain Models: PascalCase nouns.
