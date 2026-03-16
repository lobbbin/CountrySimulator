package com.countrysimulator.game.domain

enum class GovernmentType(val displayName: String, val description: String) {
    DEMOCRACY("Democracy", "Balanced bonuses. Free elections, moderate taxes."),
    MONARCHY("Monarchy", "Economic bonus. Low citizen happiness."),
    REPUBLIC("Republic", "Trade bonus. Limited military power."),
    DICTATORSHIP("Dictatorship", "Military bonus. Very low happiness."),
    COMMUNISM("Communism", "Production bonus. Limited foreign trade.")
}

data class CountryStats(
    val population: Int = 1000000,
    val economy: Int = 50,
    val military: Int = 30,
    val happiness: Int = 60,
    val stability: Int = 50,
    val technology: Int = 20
)

data class Country(
    val name: String,
    val governmentType: GovernmentType,
    val stats: CountryStats,
    val year: Int = 2024,
    val treasury: Int = 10000,
    val turnCount: Int = 0,
    val eventHistory: List<String> = emptyList()
)

data class GameEvent(
    val title: String,
    val description: String,
    val effect: (CountryStats) -> CountryStats,
    val options: List<EventOption>
)

data class EventOption(
    val label: String,
    val effect: (CountryStats, Int) -> Pair<CountryStats, Int>
)

enum class GameOverReason {
    BANKRUPTCY,
    REVOLUTION,
    INVASION,
    TECH_FAILURE
}

data class GameState(
    val country: Country,
    val isGameOver: Boolean = false,
    val gameOverReason: GameOverReason? = null,
    val lastEvent: GameEvent? = null,
    val eventHistory: List<String> = emptyList()
)