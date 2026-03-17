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
    val propaganda: Int = 0,    // 0-100
    val softPower: Int = 0      // 0-100 (New)
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
    val warScore: Int = 0,
    val warExhaustion: Int = 0,
    val sanctions: List<SanctionType> = emptyList(), // Sanctions imposed BY us ON them
    val isSpying: Boolean = false
)

enum class SanctionType {
    TRADE_EMBARGO,
    ARMS_EMBARGO,
    TRAVEL_BAN
}

data class AiNation(
    val id: String,
    val name: String,
    val governmentType: GovernmentType,
    val personality: AiPersonality,
    val stats: CountryStats,
    val treasury: Int = 5000,
    val isAlive: Boolean = true,
    val isUNMember: Boolean = true,
    val military: Military = Military()
)

enum class AiPersonality {
    AGGRESSIVE, // Military focus, likely to war
    PEACEFUL,   // Stability focus, likes alliances
    TRADER,     // Economy focus, likes trade deals
    SCIENTIFIC, // Tech focus, neutral
    ISOLATIONIST // Hard to influence
}

// --- United Nations Models ---
data class UnitedNations(
    val memberCount: Int = 0,
    val activeResolutions: List<UNResolution> = emptyList(),
    val passedResolutions: List<UNResolution> = emptyList()
)

data class UNResolution(
    val id: String,
    val type: UNResolutionType,
    val targetNationId: String?, // Null if general
    val description: String,
    val yearProposed: Int,
    val votesFor: Int = 0,
    val votesAgainst: Int = 0,
    val status: ResolutionStatus = ResolutionStatus.PROPOSED
)

enum class UNResolutionType(val displayName: String) {
    CONDEMNATION("Condemnation"),
    SANCTIONS("Sanctions"),
    PEACEKEEPING_MISSION("Peacekeeping Mission"),
    HUMANITARIAN_AID("Humanitarian Aid"),
    GLOBAL_INITIATIVE("Global Initiative")
}

enum class ResolutionStatus {
    PROPOSED, PASSED, FAILED
}

// --- Espionage Models ---
data class SpyMission(
    val id: String,
    val targetNationId: String,
    val targetNationName: String,
    val type: SpyMissionType,
    val successChance: Int,
    val turnsRemaining: Int,
    val costPerTurn: Int
)

enum class SpyMissionType(val displayName: String, val cost: Int, val duration: Int) {
    GATHER_INTEL("Gather Intel", 200, 2),
    STEAL_TECH("Steal Technology", 500, 4),
    SABOTAGE_ECONOMY("Sabotage Economy", 800, 3),
    INCITE_UNREST("Incite Unrest", 600, 5),
    STAGE_COUP("Stage Coup", 2000, 8)
}

// --- Military & Warfare Models ---
enum class MilitaryDoctrine(val displayName: String, val description: String) {
    BALANCED("Balanced", "No specific focus."),
    OFFENSIVE("Offensive", "Bonus to attack, penalty to defense."),
    DEFENSIVE("Defensive", "Bonus to defense, penalty to attack."),
    GUERRILLA("Guerrilla", "Bonus to resistance, penalty to open field battles.")
}

data class MilitaryBranch(
    val name: String,
    val manpower: Int = 1000,
    val equipmentLevel: Int = 1, // 1-10
    val experience: Int = 0 // 0-100
)

data class NuclearProgram(
    val hasProgram: Boolean = false,
    val researchProgress: Int = 0, // 0-100
    val warheads: Int = 0
)

data class MercenaryGroup(
    val name: String,
    val power: Int,
    val costPerTurn: Int,
    val contractTurnsRemaining: Int
)

data class WarTheater(
    val id: String,
    val name: String,
    val enemyNationId: String,
    val playerStrength: Int,
    val enemyStrength: Int,
    val territoryControlled: Int = 50, // 0-100 (50 is neutral/border, 100 is total victory, 0 is total defeat)
    val isActive: Boolean = true
)

data class Military(
    val army: MilitaryBranch = MilitaryBranch("Army"),
    val navy: MilitaryBranch = MilitaryBranch("Navy"),
    val airForce: MilitaryBranch = MilitaryBranch("Air Force"),
    val doctrine: MilitaryDoctrine = MilitaryDoctrine.BALANCED,
    val nuclearProgram: NuclearProgram = NuclearProgram(),
    val mercenaries: List<MercenaryGroup> = emptyList(),
    val warTheaters: List<WarTheater> = emptyList(),
    val basesAbroad: Int = 0
)

data class GlobalMarket(
    val foodPrice: Int = 10,
    val energyPrice: Int = 15,
    val materialsPrice: Int = 20,
    val globalInstability: Int = 10 // 0-100, affects prices
)

// ... Ideology, PoliticalParty, Law, PoliticalFaction, Minister, Election ... (Keep as is)
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
    val currentTermYear: Int = 0,
    // V6.0 fields
    val unitedNations: UnitedNations = UnitedNations(),
    val activeSpyMissions: List<SpyMission> = emptyList(),
    val military: Military = Military()
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
