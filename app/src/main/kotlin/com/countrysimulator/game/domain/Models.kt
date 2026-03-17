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
    val crime: Int = 20,
    val corruption: Int = 10, // 0-100
    val propaganda: Int = 0    // 0-100
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

enum class Ideology(val displayName: String) {
    LIBERAL("Liberal"),
    CONSERVATIVE("Conservative"),
    SOCIALIST("Socialist"),
    NATIONALIST("Nationalist"),
    AUTHORITARIAN("Authoritarian"),
    ECOLOGIST("Ecologist")
}

data class PoliticalParty(
    val name: String,
    val ideology: Ideology,
    val popularity: Int = 0, // 0-100
    val influence: Int = 0   // 0-100
)

data class Law(
    val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean = false,
    val cost: Int = 0,
    val stabilityEffect: Int = 0,
    val economyEffect: Int = 0,
    val happinessEffect: Int = 0,
    val corruptionEffect: Int = 0
)

data class PoliticalFaction(
    val name: String,
    val loyalty: Int = 50, // 0-100
    val power: Int = 20    // 0-100
)

data class Minister(
    val id: String,
    val name: String,
    val role: MinisterRole,
    val skill: Int = 50, // 0-100
    val corruption: Int = 10, // 0-100
    val loyalty: Int = 70 // 0-100
)

enum class MinisterRole(val displayName: String) {
    ECONOMY("Minister of Economy"),
    DEFENSE("Minister of Defense"),
    INTERIOR("Minister of Interior"),
    EDUCATION("Minister of Education"),
    HEALTH("Minister of Health"),
    FOREIGN_AFFAIRS("Minister of Foreign Affairs")
}

data class Election(
    val year: Int,
    val isActive: Boolean = false,
    val turnsRemaining: Int = 0,
    val results: Map<String, Int> = emptyMap()
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
    val policies: List<String> = emptyList(),
    val politicalParties: List<PoliticalParty> = emptyList(),
    val activeLaws: List<Law> = emptyList(),
    val factions: List<PoliticalFaction> = emptyList(),
    val ministers: List<Minister> = emptyList(),
    val election: Election? = null,
    val currentTermYear: Int = 0
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

data class GameEvent(
    val id: String,
    val title: String,
    val description: String,
    val category: EventCategory,
    val severity: EventSeverity,
    val effect: (CountryStats) -> CountryStats,
    val options: List<EventOption>,
    val prerequisites: ((Country) -> Boolean)? = null
)

enum class EventCategory {
    ECONOMIC,
    MILITARY,
    POLITICAL,
    DISASTER,
    SCIENTIFIC,
    CULTURAL,
    DIPLOMATIC,
    ENVIRONMENTAL,
    SOCIAL
}

enum class EventSeverity {
    MINOR,
    MODERATE,
    MAJOR,
    CATASTROPHIC
}

data class EventOption(
    val label: String,
    val description: String,
    val effect: (CountryStats, Int, Resources) -> Triple<CountryStats, Int, Resources>
)

enum class GameOverReason {
    BANKRUPTCY,
    REVOLUTION,
    INVASION,
    TECH_FAILURE,
    FAMINE,
    ENVIRONMENTAL_COLLAPSE,
    NUCLEAR_WINTER,
    CIVIL_WAR,
    ASSASSINATION,
    COUP
}
