# Country Simulator - Specification Document

## 1. Project Overview
- **Project Name**: Country Simulator
- **Type**: Text-based strategy/simulation Android game
- **Core Functionality**: Players manage their own country making decisions on economy, military, diplomacy, and domestic policy through turn-based gameplay

## 2. Technology Stack & Choices
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target/Compile SDK**: 34 (Android 14)
- **Build System**: Gradle 8.4 with Kotlin DSL
- **Architecture**: MVVM with Clean Architecture layers
- **UI**: Jetpack Compose with Material 3
- **State Management**: StateFlow + ViewModel
- **Data Persistence**: SharedPreferences (DataStore)
- **Dependency Injection**: Manual (simple app, no Hilt/Koin needed)

## 3. Feature List

### Core Gameplay
- Country creation with custom name and government type
- Turn-based gameplay with year progression
- Random events system affecting country stats
- Game over conditions (bankruptcy, revolution, invasion)

### Government Types (5 types)
- Democracy (balanced bonuses)
- Monarchy (economic bonus, low happiness)
- Republic (trade bonus, military weakness)
- Dictatorship (military bonus, happiness penalty)
- Communism (production bonus, limited trade)

### Stats System (6 core stats)
- Population: Affects tax income and military power
- Economy: Money generation rate
- Military: Defense and attack capability
- Happiness: Revolution risk
- Stability: Event probability modifier
- Technology: Unlocks advanced options

### Player Actions (per turn)
- Change laws/policies
- Trade with other nations
- Military recruitment/training
- Build infrastructure
- Diplomatic relations
- Handle random events

### Game Events
- Natural disasters
- Economic crises
- Wars and conflicts
- Scientific breakthroughs
- Political movements
- International agreements

## 4. UI/UX Design Direction
- **Visual Style**: Material Design 3 with dark theme optimized for text-heavy gameplay
- **Color Scheme**: Deep blue/purple primary with gold accents (regal feel)
- **Layout**: Single-screen with scrollable panels
  - Top: Country stats display
  - Middle: Current events and news ticker
  - Bottom: Action buttons and menu
- **Typography**: Clean, readable monospace-style for stats, regular for narrative
- **Navigation**: Bottom sheet for action menus, dialogs for decisions