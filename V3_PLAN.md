# Country Simulator 3.0: The World Stage Update

**Goal:** Transform the game from a solitary internal management sim into a global geopolitical strategy game.

## New Features

### 1. Interactive Global Diplomacy
*   **Rival Nations:** 8 unique AI nations with distinct personalities (Aggressive, Peaceful, Trade-focused).
*   **Diplomatic Actions:**
    *   **Trade Deal:** Boost economy for both, requires good relations.
    *   **Non-Aggression Pact:** Prevents war for X turns.
    *   **Alliance:** Call into wars, defensive pacts.
    *   **Insult/Denounce:** Lowers relations, might provoke war (but boosts internal nationalism).
    *   **Declare War:** Enter the new War State.

### 2. Warfare System Overhaul
*   **War State:** Wars are no longer instant "Game Over" or simple text events. They are multi-turn conflicts.
*   **War Exhaustion:** Long wars drain stability and happiness.
*   **Battle Mechanics:** Each turn at war involves a battle calculation based on Military Tech + Troop Count + Strategy.

### 3. Global Economy
*   **Global Market:** Resource prices (Food, Energy, Materials) fluctuate based on global events (e.g., if a major energy producer goes to war, energy prices spike).

### 4. UI Overhaul
*   **Navigation:** Bottom navigation bar to switch between:
    *   **Dashboard:** (Classic view)
    *   **World:** (List of nations & diplomacy)
    *   **Military:** (War room & recruitment)
    *   **Tech/Policy:** (Research tree)

## Technical Changes
*   **AI Turn Processor:** `GameLogic` will now simulate the turn for all 8 AI nations, calculating their growth, wars between each other, and interactions with the player.
*   **Event System Expansion:** New events triggered by global state (e.g., "Ally calls for aid", "Global Market Crash").
