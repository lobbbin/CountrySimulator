package com.countrysimulator.game.domain

enum class GovernmentType(
    val displayName: String,
    val description: String,
    val economyBonus: Int,
    val militaryBonus: Int,
    val happinessBonus: Int,
    val stabilityBonus: Int,
    val techBonus: Int
) {
    DEMOCRACY(
        "Democracy",
        "Balanced bonuses. Free elections, moderate taxes.",
        5, 0, 5, 5, 5
    ),
    MONARCHY(
        "Monarchy",
        "Economic bonus. Low citizen happiness.",
        10, 5, -10, 5, 0
    ),
    REPUBLIC(
        "Republic",
        "Trade bonus. Limited military power.",
        15, -5, 5, 5, 5
    ),
    DICTATORSHIP(
        "Dictatorship",
        "Military bonus. Very low happiness.",
        0, 15, -15, 10, 0
    ),
    COMMUNISM(
        "Communism",
        "Production bonus. Limited foreign trade.",
        5, 5, -5, 10, 10
    ),
    THEOCRACY(
        "Theocracy",
        "Religious authority. High stability, limited progress.",
        0, 0, 10, 20, -5
    ),
    FEDERATION(
        "Federation",
        "United states. Strong economy, shared defense.",
        15, 10, 5, 0, 10
    ),
    CONFEDERACY(
        "Confederacy",
        "States rights. High autonomy, weak central power.",
        5, -5, 10, -10, 0
    ),
    TECHNOCRACY(
        "Technocracy",
        "Rule by experts. High tech, moderate happiness.",
        10, 0, 5, 5, 20
    ),
    SOCIALISM(
        "Socialism",
        "Workers paradise. High equality, lower efficiency.",
        -5, 0, 20, 10, 5
    )
}

data class CountryStats(
    val population: Int = 1000000,
    val economy: Int = 50,
    val military: Int = 30,
    val happiness: Int = 60,
    val stability: Int = 50,
    val technology: Int = 20,
    val education: Int = 30,
    val healthcare: Int = 30,
    val environment: Int = 50,
    val crime: Int = 20
)

data class Resources(
    val food: Int = 100,
    val energy: Int = 100,
    val materials: Int = 50,
    val maxFood: Int = 200,
    val maxEnergy: Int = 200,
    val maxMaterials: Int = 150
)

enum class RelationStatus {
    ENEMY, RIVAL, NEUTRAL, FRIENDLY, ALLY
}

data class DiplomaticRelation(
    val nationName: String,
    val nationId: String,
    val relationScore: Int = 50, // 0 to 100
    val status: RelationStatus = RelationStatus.NEUTRAL,
    val isAtWar: Boolean = false,
    val hasTradeAgreement: Boolean = false,
    val hasNonAggressionPact: Boolean = false,
    val hasAlliance: Boolean = false,
    val warScore: Int = 0, // Positive = Winning, Negative = Losing
    val warExhaustion: Int = 0
)

data class AiNation(
    val id: String,
    val name: String,
    val governmentType: GovernmentType,
    val personality: AiPersonality,
    val stats: CountryStats,
    val treasury: Int = 5000,
    val isAlive: Boolean = true
)

enum class AiPersonality {
    AGGRESSIVE, // Military focus, likely to war
    PEACEFUL,   // Stability focus, likes alliances
    TRADER,     // Economy focus, likes trade deals
    SCIENTIFIC, // Tech focus, neutral
    ISOLATIONIST // Hard to influence
}

data class GlobalMarket(
    val foodPrice: Int = 10,
    val energyPrice: Int = 15,
    val materialsPrice: Int = 20,
    val globalInstability: Int = 10 // 0-100, affects prices
)

data class Country(
    val name: String,
    val governmentType: GovernmentType,
    val stats: CountryStats,
    val resources: Resources = Resources(),
    val diplomaticRelations: List<DiplomaticRelation> = emptyList(),
    val year: Int = 2024,
    val treasury: Int = 10000,
    val turnCount: Int = 0,
    val eventHistory: List<String> = emptyList(),
    val policies: List<String> = emptyList()
)

data class GameState(
    val country: Country,
    val aiNations: List<AiNation> = emptyList(),
    val globalMarket: GlobalMarket = GlobalMarket(),
    val isGameOver: Boolean = false,
    val gameOverReason: GameOverReason? = null,
    val lastEvent: GameEvent? = null,
    val eventHistory: List<String> = emptyList(),
    val newsHeadline: String? = null
)
