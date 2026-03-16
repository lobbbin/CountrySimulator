# Country Simulator

A text-based country management game for Android built with Kotlin and Jetpack Compose.

## Features

- **5 Government Types**: Democracy, Monarchy, Republic, Dictatorship, Communism - each with unique bonuses
- **6 Core Stats**: Population, Economy, Military, Happiness, Stability, Technology
- **Turn-Based Gameplay**: Progress through years making strategic decisions
- **Random Events**: Face economic booms, natural disasters, wars, and more
- **Government Bonuses**: Each government type affects your nation's performance differently
- **Game Over Conditions**: Bankruptcy, Revolution, Invasion, Tech Failure

## Gameplay

1. Create your country with a custom name and choose your government type
2. Each turn, your treasury gains income based on population, economy, and happiness
3. Invest in your nation: Economy, Military, Infrastructure, Technology, or Happiness
4. Face random events that challenge your leadership
5. Keep your people happy and your nation stable to survive!

## Requirements

- Android SDK 24+ (Android 7.0+)
- Android Studio (for building)
- Kotlin 1.9.x

## Building

1. Clone this repository
2. Open in Android Studio
3. Let Gradle download dependencies
4. Run on device/emulator

Or from command line:
```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## Architecture

- **MVVM Pattern**: Clean separation of UI and logic
- **Jetpack Compose**: Modern declarative UI
- **DataStore**: Persistent game state saving
- **StateFlow**: Reactive state management

## Project Structure

```
app/src/main/kotlin/com/countrysimulator/game/
├── MainActivity.kt          # Entry point
├── domain/                  # Game logic & models
│   ├── Models.kt           # Data classes
│   └── GameLogic.kt         # Turn processing
├── data/                    # Persistence
│   └── GameRepository.kt   # DataStore handling
└── presentation/            # UI layer
    ├── GameViewModel.kt     # State management
    └── GameScreen.kt       # Compose UI
```

## License

MIT License