# Country Simulator v4.0 - The World Stage Update

A text-based country management strategy game for Android built with Kotlin and Jetpack Compose. Manage your nation's economy, military, diplomacy, and resources in a dynamic world with AI-controlled nations and global events.

![Version](https://img.shields.io/badge/version-4.0.0-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Min SDK](https://img.shields.io/badge/min%20SDK-24-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 🎮 Features

### Government & Politics
- **10 Government Types**: Democracy, Monarchy, Republic, Dictatorship, Communism, Theocracy, Federation, Confederacy, Technocracy, Socialism
- Each government type has unique bonuses affecting economy, military, happiness, stability, and technology
- Dynamic political events and policy decisions

### Expanded Stats System
- **10 Core Stats**: Population, Economy, Military, Happiness, Stability, Technology, Education, Healthcare, Environment, Crime
- **Resource Management**: Food, Energy, Materials with production and consumption
- **Global Market**: Dynamic prices affected by world events

### Diplomacy & World Stage
- **AI Nations**: 8+ AI-controlled nations with unique personalities (Aggressive, Peaceful, Trader, Scientific, Isolationist)
- **Diplomatic Relations**: Relation scores, trade agreements, non-aggression pacts, military alliances
- **War System**: War declarations, war scores, war exhaustion
- **Relation Status**: Enemy, Rival, Neutral, Friendly, Ally

### Events & Challenges
- **50+ Random Events**: Economic booms, natural disasters, scientific breakthroughs, wars, pandemics, energy crises, coups, civil wars
- **Event Categories**: Economic, Military, Political, Disaster, Scientific, Cultural, Diplomatic, Environmental, Social
- **Event Severity**: Minor, Moderate, Major, Catastrophic
- **Multiple Choice Options**: Each event offers 3 strategic choices with different consequences

### Game Over Conditions
- Bankruptcy (Treasury < -5000)
- Revolution (Happiness < 10)
- Invasion (Military < 5 with random chance)
- Tech Failure (Technology < 5)
- Famine (Food = 0)
- Environmental Collapse (Environment = 0)
- Nuclear Winter
- Civil War

## 🎯 Gameplay

1. **Create Your Country**: Choose a name and government type
2. **Manage Your Nation**: Balance 10 stats and 3 resources
3. **Make Decisions**: Each turn presents strategic choices
4. **Face Events**: Random events test your leadership
5. **Engage in Diplomacy**: Build relations with AI nations
6. **Trade on Global Market**: Buy/sell resources at dynamic prices
7. **Survive**: Keep your people happy and nation stable

## 📋 Requirements

- **Android SDK**: 24+ (Android 7.0+)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.25
- **Android Gradle Plugin**: 8.5.1
- **Gradle**: 8.7

## 🛠️ Building

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 17

### Command Line Build
```bash
# Clone repository
git clone https://github.com/lobbbin/CountrySimulator.git
cd CountrySimulator

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The APK will be at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### GitHub Actions CI/CD
Every push to master automatically builds the APK. Download from:
1. Go to **Actions** tab
2. Select latest successful build
3. Download `app-debug` artifact

## 🏗️ Architecture

### MVVM Pattern
```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   UI Layer      │────▶│  ViewModel       │────▶│   Domain        │
│  (GameScreen)   │◀────│  (GameViewModel) │◀────│   (GameLogic)   │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                               │
                               ▼
                        ┌──────────────────┐
                        │   Data Layer     │
                        │  (Repository)    │
                        └──────────────────┘
```

### Technologies
- **Jetpack Compose**: Modern declarative UI
- **Material 3**: Material Design components
- **StateFlow**: Reactive state management
- **DataStore Preferences**: Persistent game state
- **Coroutines**: Async operations

### Project Structure
```
app/src/main/kotlin/com/countrysimulator/game/
├── MainActivity.kt              # App entry point
├── domain/
│   ├── Models.kt               # Data classes
│   │   ├── GovernmentType      # 10 government types
│   │   ├── CountryStats        # 10 core stats
│   │   ├── Resources           # Food, Energy, Materials
│   │   ├── DiplomaticRelation  # AI relations
│   │   ├── AiNation            # AI-controlled nations
│   │   ├── AiPersonality       # AI behavior types
│   │   ├── GlobalMarket        # Resource prices
│   │   ├── GameEvent           # Random events
│   │   ├── EventOption         # Event choices
│   │   ├── GameOverReason      # End conditions
│   │   └── GameState           # Complete game state
│   └── GameLogic.kt            # Turn processing, events, bonuses
├── data/
│   └── GameRepository.kt       # DataStore persistence
└── presentation/
    ├── GameViewModel.kt        # UI state management
    └── GameScreen.kt           # Jetpack Compose UI
```

## 📊 Version History

### v4.0 - The World Stage Update (2026)
**Major Features:**
- Added 5 new government types (Theocracy, Federation, Confederacy, Technocracy, Socialism)
- Expanded from 6 to 10 core stats (Education, Healthcare, Environment, Crime)
- Resource system (Food, Energy, Materials)
- AI Nations with unique personalities
- Diplomatic relations system
- Global market with dynamic prices
- 50+ random events with multiple choices
- Event categories and severity levels
- War and peace mechanics

### v3.0 - Enhanced Gameplay
- Improved event system
- Better UI/UX
- Performance optimizations

### v2.0 - Government & Events
- Added government types
- Random events system
- Turn-based income

### v1.0 - Initial Release
- Basic country management
- Core stats system
- Simple investment mechanics

## 🎮 Game Mechanics

### Income Calculation
```kotlin
baseIncome = population / 100,000
economyMultiplier = economy / 50.0
happinessFactor = happiness / 100.0
income = baseIncome * economyMultiplier * happinessFactor
```

### Government Bonuses
| Government | Economy | Military | Happiness | Stability | Tech |
|------------|---------|----------|-----------|-----------|------|
| Democracy | +5 | 0 | +5 | +5 | +5 |
| Monarchy | +10 | +5 | -10 | +5 | 0 |
| Republic | +15 | -5 | +5 | +5 | +5 |
| Dictatorship | 0 | +15 | -15 | +10 | 0 |
| Communism | +5 | +5 | -5 | +10 | +10 |
| Theocracy | 0 | 0 | +10 | +20 | -5 |
| Federation | +15 | +10 | +5 | 0 | +10 |
| Confederacy | +5 | -5 | +10 | -10 | 0 |
| Technocracy | +10 | 0 | +5 | +5 | +20 |
| Socialism | -5 | 0 | +20 | +10 | +5 |

### Investment Costs
| Action | Cost | Effect |
|--------|------|--------|
| Economy | 1000 | +8 Economy |
| Military | 800 | +10 Military |
| Infrastructure | 1200 | +8 Stability |
| Technology | 1500 | +12 Technology |
| Happiness | 800 | +10 Happiness |

## 📄 License

MIT License - See [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/lobbbin/CountrySimulator/issues)
- **Discussions**: [GitHub Discussions](https://github.com/lobbbin/CountrySimulator/discussions)

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Icons and assets from open-source resources
- Inspired by classic country management games

---

**Repository**: [https://github.com/lobbbin/CountrySimulator](https://github.com/lobbbin/CountrySimulator)

**Latest Build**: Check the [Actions](https://github.com/lobbbin/CountrySimulator/actions) tab for the latest APK.
