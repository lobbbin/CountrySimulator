# Country Simulator - Specification Document

## 1. Project Overview
- **Project Name**: Country Simulator
- **Type**: Text-based strategy/simulation Android game
- **Core Functionality**: Players manage their own country making decisions on economy, military, diplomacy, and domestic policy through turn-based gameplay
- **Version**: 2.0 (Major Update)

## 2. Technology Stack & Choices
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target/Compile SDK**: 34 (Android 14)
- **Build System**: Gradle 8.7 with Kotlin DSL
- **Architecture**: MVVM with Clean Architecture layers
- **UI**: Jetpack Compose with Material 3
- **State Management**: StateFlow + ViewModel
- **Data Persistence**: SharedPreferences (DataStore)
- **Dependency Injection**: Manual (simple app, no Hilt/Koin needed)

## 3. Feature List (Version 2.0)

### Core Gameplay
- Country creation with custom name and government type
- Turn-based gameplay with year progression
- Random events system affecting country stats
- Game over conditions (bankruptcy, revolution, invasion, famine, environmental collapse, civil war)
- News headlines system

### Government Types (10 types)
- Democracy (balanced bonuses)
- Monarchy (economic bonus, low happiness)
- Republic (trade bonus, military weakness)
- Dictatorship (military bonus, happiness penalty)
- Communism (production bonus, limited trade)
- **NEW** Theocracy (religious authority, high stability, limited progress)
- **NEW** Federation (united states, strong economy, shared defense)
- **NEW** Confederacy (states rights, high autonomy, weak central power)
- **NEW** Technocracy (rule by experts, high tech, moderate happiness)
- **NEW** Socialism (workers paradise, high equality, lower efficiency)

### Stats System (10 stats)
- Population: Affects tax income and military power
- Economy: Money generation rate
- Military: Defense and attack capability
- Happiness: Revolution risk
- Stability: Event probability modifier
- Technology: Unlocks advanced options
- **NEW** Education: Affects technology growth
- **NEW** Healthcare: Affects population growth
- **NEW** Environment: New game over condition
- **NEW** Crime: Affects stability and economy

### Resource Management (NEW)
- Food: Required for population survival
- Energy: Powers industry and technology
- Materials: Needed for construction and military

### Player Actions (per turn)
- Invest in Economy
- Recruit Military
- Improve Infrastructure
- Invest in Technology
- Improve Happiness
- **NEW** Invest in Education
- **NEW** Invest in Healthcare
- **NEW** Improve Environment
- **NEW** Fight Crime
- **NEW** Buy Food/Energy/Materials
- Handle random events

### Game Events (25+ events)
- Natural disasters (earthquake, pandemic, famine)
- Economic events (boom, trade agreements, recession)
- Military conflicts (war declaration, invasion)
- Political events (coup, revolution, political unrest)
- Scientific breakthroughs (AI revolution, space program)
- Social issues (crime wave, healthcare crisis, education reform)
- Environmental events (environmental disaster, energy crisis)
- Diplomatic events (immigration, foreign aid, espionage)
- Cultural events (religious movement, cultural festival)
- **NEW** Resource events (resource discovery, energy crisis)

### Event Severity System (NEW)
- Minor: Small effects
- Moderate: Noticeable impact
- Major: Significant consequences
- Catastrophic: Game-changing events

### Event Categories (NEW)
- Economic, Military, Political, Disaster, Scientific, Cultural, Diplomatic, Environmental, Social

## 4. UI/UX Design Direction
- **Visual Style**: Material Design 3 with dark theme optimized for text-heavy gameplay
- **Color Scheme**: Deep blue/purple primary with gold accents (regal feel)
- **Layout**: Single-screen with scrollable panels
  - Top: News headline ticker
  - Stats overview with resources
  - Detailed stats bars (10 stats)
  - Action buttons organized by category
  - Event dialogs with severity indicators
- **Typography**: Clean, readable monospace-style for stats, regular for narrative
- **Navigation**: Dialog-based for events, scrollable main screen
- **Version 2.0 Updates**:
  - Government type selection shows bonus chips
  - Resource bars with visual indicators
  - Event severity color coding
  - News headline system
  - Organized action buttons by category (Economy, Social, Resources)
