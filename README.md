# Country Simulator v6.0 - The Global Superpower Update

A deep, text-based country management strategy game for Android built with Kotlin and Jetpack Compose. Rise from a small nation to a global superpower by mastering politics, diplomacy, intelligence, and warfare.

![Version](https://img.shields.io/badge/version-6.0.0-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Min SDK](https://img.shields.io/badge/min%20SDK-24-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 🎮 New in v6.0

### 🏛️ Deep Government & Politics
- **Political Parties**: Multiple ideologies (Liberal, Conservative, Socialist, Nationalist, etc.) competing for popularity.
- **Election System**: Dynamic elections in democracies; hold emergency elections in other regimes.
- **Cabinet Ministers**: Hire specialized ministers (Economy, Defense, Foreign Affairs) to boost national growth.
- **Corruption & Propaganda**: Manage internal efficiency and influence public opinion.
- **Legislative Power**: Enact and toggle specific laws with varying socio-economic effects.

### 🌍 Diplomacy & International Stage
- **United Nations**: Participate in an international body, vote on resolutions, and face global consequences.
- **Intelligence Agency**: Launch covert operations, gather intel, steal technology, or stage coups.
- **Sanctions & Embargoes**: Use economic pressure to cripple rivals.
- **Foreign Aid**: Send aid to build soft power and improve relations.
- **Soft Power**: A new statistic representing your global influence and cultural appeal.

### ⚔️ Advanced Military & Warfare
- **Military Branches**: Independently manage Army, Navy, and Air Force manpower and equipment.
- **Nuclear Program**: Develop WMDs as a ultimate deterrent, but face international backlash.
- **War Theaters**: Active multi-front conflicts with territory control and strength calculations.
- **Military Doctrines**: Choose between Offensive, Defensive, Balanced, or Guerrilla strategies.
- **Mercenaries**: Hire private military companies for quick power boosts.

## 🎯 Gameplay Features

- **10 Government Types**: From Democracy to Technocracy, each with unique mechanics.
- **12+ Core Stats**: Economy, Military, Stability, Happiness, Soft Power, Corruption, and more.
- **Resource Management**: Food, Energy, Materials production and global market trading.
- **8+ AI Nations**: Dynamic rivals with unique personalities and diplomatic agendas.
- **100+ Random Events**: Complex multi-choice events testing every aspect of your leadership.

## 🛠️ Building

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17

### Command Line Build
```bash
# Clone repository
git clone https://github.com/lobbbin/CountrySimulator.git
cd CountrySimulator

# Build debug APK
./gradlew assembleDebug
```

## 🏗️ Architecture

The project follows a clean **MVVM (Model-View-ViewModel)** architectural pattern:
- **Kotlin & Coroutines**: For robust business logic and async operations.
- **Jetpack Compose**: For a modern, reactive, and beautiful UI.
- **DataStore Preferences**: For high-performance game state persistence.

---

## 🚀 Features Roadmap (Status)

### ✅ Government & Politics (v5.0) - COMPLETED
- [x] Political Parties & Elections
- [x] Cabinet Ministers & Corruption
- [x] Constitution & Laws
- [x] Propaganda & Factions

### ✅ Diplomacy & International (v6.0) - COMPLETED
- [x] United Nations & Resolutions
- [x] Espionage & Intelligence Agency
- [x] Sanctions & Foreign Aid
- [x] Soft Power & International Court

### ✅ Military & Warfare (v6.0) - COMPLETED
- [x] Army, Navy, Air Force Branches
- [x] Nuclear Weapons Program
- [x] War Theater & Territory Control
- [x] Mercenaries & Doctrines

### 💰 Economy & Trade (v7.0) - UPCOMING
- [ ] Stock Market & National Debt
- [ ] Manufacturing & Export Sector
- [ ] Tourism Industry
- [ ] Black Market & Illegal Trade

### 🔬 Technology & Society (v8.0) - PLANNED
- [ ] Deep Tech Tree
- [ ] Religion & Cultural Heritage
- [ ] Sports & Entertainment Events
- [ ] Space Colonization

---

**Repository**: [https://github.com/lobbbin/CountrySimulator](https://github.com/lobbbin/CountrySimulator)
