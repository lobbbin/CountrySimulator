# Country Simulator - Project Context

## Project Overview

**Country Simulator** is a text-based strategy/simulation Android game where players manage their own country through turn-based gameplay. Players make decisions on economy, military, diplomacy, and domestic policy while facing random events that test their leadership.

### Key Features
- **5 Government Types**: Democracy, Monarchy, Republic, Dictatorship, Communism (each with unique bonuses)
- **6 Core Stats**: Population, Economy, Military, Happiness, Stability, Technology
- **Turn-Based Gameplay**: Progress through years making strategic decisions
- **Random Events System**: Economic booms, natural disasters, wars, pandemics, and more
- **Game Over Conditions**: Bankruptcy, Revolution, Invasion, Tech Failure

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin 1.9.22 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Build System | Gradle 8.4 (Kotlin DSL) |
| Architecture | MVVM |
| UI Framework | Jetpack Compose + Material 3 |
| State Management | StateFlow + ViewModel |
| Data Persistence | DataStore Preferences |
| Dependency Injection | Manual (no Hilt/Koin) |

## Project Structure

```
app/src/main/kotlin/com/countrysimulator/game/
├── MainActivity.kt              # App entry point
├── domain/
│   ├── Models.kt               # Data classes (Country, Stats, Events, GameState)
│   └── GameLogic.kt            # Turn processing, events, bonuses, game over checks
├── data/
│   └── GameRepository.kt       # DataStore persistence layer
└── presentation/
    ├── GameViewModel.kt        # UI state management, user actions
    └── GameScreen.kt           # Jetpack Compose UI
```

## Building and Running

### Prerequisites
- Android Studio (Arctic Fox or newer recommended)
- JDK 17
- Android SDK 24+

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run on connected device
./gradlew installDebug

# Run tests (if any)
./gradlew test

# Clean build
./gradlew clean
```

### Output Locations
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

## Architecture Details

### MVVM Pattern
- **Model**: `domain/` package contains pure Kotlin data classes and business logic
- **ViewModel**: `GameViewModel` manages UI state and coordinates between UI and data
- **View**: `GameScreen.kt` and `CountrySimulatorApp` compose the UI

### Data Flow
1. UI events → `GameViewModel` methods
2. ViewModel updates state via `MutableStateFlow`
3. UI observes `StateFlow` and recomposes
4. Game state persisted to DataStore via `GameRepository`

### Key Domain Models
- `GovernmentType`: Enum with 5 government types and descriptions
- `CountryStats`: 6 core stats (population, economy, military, happiness, stability, technology)
- `Country`: Player's country with name, government, stats, year, treasury
- `GameEvent`: Random event with title, description, effect function, and options
- `GameState`: Complete game state including country, game over status, event history

## Development Conventions

### Code Style
- Kotlin idiomatic code with data classes for models
- `object` for singleton utility objects (e.g., `GameLogic`)
- Functional approach for event effects using lambda functions
- State copied immutably using `copy()` method

### Naming Conventions
- Packages: `com.countrysimulator.game.[layer]`
- ViewModels: `*ViewModel` suffix
- UI composables: `*Screen` or `*App` suffix
- Domain objects: PascalCase nouns

### State Management
- All UI state wrapped in `GameUiState` data class
- Loading states handled explicitly
- Dialog visibility controlled via boolean flags

## Game Mechanics Reference

### Income Calculation
```
baseIncome = population / 100,000
economyMultiplier = economy / 50.0
happinessFactor = happiness / 100.0
income = baseIncome * economyMultiplier * happinessFactor
```

### Government Bonuses
| Government | Bonus |
|------------|-------|
| Democracy | +5 Happiness, +5 Stability |
| Monarchy | +10 Economy, -10 Happiness |
| Republic | +15 Economy, -5 Military |
| Dictatorship | +15 Military, -15 Happiness |
| Communism | +5 Economy, +10 Stability |

### Game Over Conditions
- **Bankruptcy**: Treasury < -5000
- **Revolution**: Happiness < 10
- **Invasion**: Military < 5 with 10% random chance
- **Tech Failure**: Technology < 5

### Investment Costs
| Action | Cost | Effect |
|--------|------|--------|
| Economy | 1000 | +8 Economy |
| Military | 800 | +10 Military |
| Infrastructure | 1200 | +8 Stability |
| Technology | 1500 | +12 Technology |
| Happiness | 800 | +10 Happiness |

## Configuration Files

- `build.gradle.kts` (root): Plugin versions (Android 8.2.2, Kotlin 1.9.22)
- `app/build.gradle.kts`: App configuration, dependencies, Compose setup
- `settings.gradle.kts`: Repository configuration (Google, Maven Central)
- `gradle/wrapper/gradle-wrapper.properties`: Gradle 8.4 distribution

## Dependencies

### Core
- `androidx.core:core-ktx:1.12.0`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.7.0`
- `androidx.activity:activity-compose:1.8.2`

### Compose (BOM 2024.01.00)
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-graphics`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.material3:material3`

### Additional
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0`
- `androidx.datastore:datastore-preferences:1.0.0`

## Git Repository

- Initial commit: `f360135` - "Initial commit: CountrySimulator Android project"
- Author: lobbbin <lobinpek2@gmail.com>
- Branch: master
