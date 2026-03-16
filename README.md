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

## 🚀 Future Features Roadmap

A curated list of 55 potential features for future versions. Contributions welcome!

### 🏛️ Government & Politics (v5.0)
- [ ] **Political Parties System** - Multiple parties with different ideologies competing for power
- [ ] **Election Mechanics** - Campaign, debate, and win/lose elections based on performance
- [ ] **Corruption System** - High corruption reduces efficiency and triggers scandals
- [ ] **Constitution & Laws** - Enact specific laws (tax rates, military draft, welfare)
- [ ] **Political Factions** - Internal groups that support or oppose your policies
- [ ] **Assassination Attempts** - Random events targeting leaders in unstable regimes
- [ ] **Succession Crisis** - Handle leadership transitions in monarchies/dictatorships
- [ ] **Referendum System** - Let citizens vote on major decisions
- [ ] **Cabinet Ministers** - Appoint ministers affecting different stat growth rates
- [ ] **Propaganda System** - Influence happiness and stability through media control

### 🌍 Diplomacy & International (v5.0-v6.0)
- [ ] **United Nations Organization** - Join international body with voting and resolutions
- [ ] **Embargoes & Sanctions** - Economic pressure tool against hostile nations
- [ ] **Foreign Aid System** - Give/receive aid affecting relations and soft power
- [ ] **Intelligence Agencies** - Spy on other nations, steal tech, sabotage
- [ ] **Covert Operations** - Fund rebels, stage coups in other countries
- [ ] **Peacekeeping Missions** - Deploy troops internationally for stability
- [ ] **International Courts** - Face war crimes trials or sue other nations
- [ ] **Cultural Exchange Programs** - Boost relations through soft power
- [ ] **Refugee Crises** - Handle incoming/outgoing refugee flows
- [ ] **Territory Disputes** - Border conflicts that can escalate to war

### ⚔️ Military & Warfare (v6.0)
- [ ] **Military Branches** - Army, Navy, Air Force with separate stats
- [ ] **Nuclear Weapons Program** - Develop WMDs with international consequences
- [ ] **Military Doctrine** - Choose defensive, offensive, or balanced strategies
- [ ] **War Theater System** - Multiple fronts with separate battle calculations
- [ ] **Mercenary Hiring** - Temporary military boost at high cost
- [ ] **Veteran System** - Units gain experience and become more effective
- [ ] **Military Bases Abroad** - Project power globally
- [ ] **Arms Race** - Compete with rivals in military spending
- [ ] **Guerrilla Warfare** - Occupation mechanics in conquered territories
- [ ] **Ceasefire & Peace Treaties** - Negotiated war endings with terms

### 💰 Economy & Trade (v5.0)
- [ ] **Multiple Currencies** - Exchange rates, currency manipulation
- [ ] **Stock Market** - Invest treasury for high-risk returns
- [ ] **National Debt System** - Borrow money with interest payments
- [ ] **Trade Routes** - Establish permanent trade connections
- [ ] **Resource Extraction** - Mines, oil wells, farms as buildings
- [ ] **Manufacturing Sector** - Convert resources to goods for export
- [ ] **Tourism Industry** - Generate income from foreign visitors
- [ ] **Black Market** - Illegal trade with risks and rewards
- [ ] **Economic Unions** - Join trade blocs for bonuses
- [ ] **Inflation/Deflation** - Economic conditions affecting prices

### 🏗️ Infrastructure & Development (v6.0)
- [ ] **Building System** - Construct buildings with unique effects
- [ ] **City Development** - Multiple cities with individual stats
- [ ] **Transportation Networks** - Roads, railways, airports boosting economy
- [ ] **Power Plants** - Different energy sources with pros/cons
- [ ] **Housing Projects** - Affect population growth and happiness
- [ ] **Research Labs** - Boost technology gain rate
- [ ] **Hospitals** - Improve healthcare and population growth
- [ ] **Schools & Universities** - Education affects tech and economy
- [ ] **Monuments & Wonders** - Unique buildings with special effects
- [ ] **Disaster Preparedness** - Reduce natural disaster impact

### 🔬 Technology & Research (v7.0)
- [ ] **Tech Tree System** - Unlock advanced technologies progressively
- [ ] **Research Projects** - Choose specific areas to focus on
- [ ] **Space Program** - Prestige project with high cost/reward
- [ ] **AI Development** - Future tech with unknown consequences
- [ ] **Gene Editing** - Healthcare boost with ethical concerns
- [ ] **Renewable Energy** - Clean tech reducing environmental impact
- [ ] **Cyber Warfare** - Digital attack/defense capabilities
- [ ] **Surveillance State** - Security vs. privacy tradeoff
- [ ] **Clone Technology** - Population boost with moral issues
- [ ] **Time Research** - Endgame technology with game-changing effects

### 👥 Population & Society (v5.0-v6.0)
- [ ] **Age Demographics** - Young, working-age, elderly populations
- [ ] **Immigration Policy** - Open, restricted, or closed borders
- [ ] **Social Classes** - Upper, middle, lower class with different needs
- [ ] **Religion System** - State religion, religious freedom, or atheism
- [ ] **Language Policy** - Official languages affecting unity
- [ ] **Cultural Heritage** - Preserve traditions vs. modernization
- [ ] **Sports & Entertainment** - Boost happiness through events
- [ ] **Media Freedom** - Press freedom affecting transparency
- [ ] **Crime Organizations** - Mafia, cartels affecting crime stats
- [ ] **Social Movements** - Environmental, labor, civil rights movements

### 🎯 Game Modes & Challenges (v7.0)
- [ ] **Scenario Mode** - Historical or fictional starting situations
- [ ] **Endless Mode** - No game over, just survive as long as possible
- [ ] **Speed Run Mode** - Achieve goals in minimum turns
- [ ] **Ironman Mode** - Single save, permadeath
- [ ] **Multiplayer** - Play against friends online
- [ ] **Leaderboards** - Global rankings by score, turns survived
- [ ] **Achievements System** - Unlock badges for accomplishments
- [ ] **Daily Challenges** - Unique scenarios each day
- [ ] **Custom Scenarios** - Create and share your own scenarios
- [ ] **Tutorial Mode** - Interactive learning for new players

### 🎨 UI/UX & Quality of Life
- [ ] **Dark/Light Theme Toggle** - User preference support
- [ ] **Stat Graphs** - Visual history of your nation's performance
- [ ] **Event Log** - Full history of all events and decisions
- [ ] **Comparison View** - Compare your stats with AI nations
- [ ] **Prediction Tool** - See projected outcomes of decisions
- [ ] **Auto-Save** - Multiple save slots with timestamps
- [ ] **Speed Control** - Adjust turn animation speed
- [ ] **Notifications** - Alerts for critical events
- [ ] **Accessibility Options** - Font size, colorblind modes
- [ ] **Statistics Dashboard** - End-game summary with scores

---

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
