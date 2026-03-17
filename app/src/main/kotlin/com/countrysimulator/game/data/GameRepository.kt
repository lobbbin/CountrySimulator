package com.countrysimulator.game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.countrysimulator.game.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_data_v6")

class GameRepository(private val context: Context) {

    private object PreferencesKeys {
        val COUNTRY_NAME = stringPreferencesKey("country_name")
        val GOVERNMENT_TYPE = stringPreferencesKey("government_type")
        val POPULATION = intPreferencesKey("population")
        val ECONOMY = intPreferencesKey("economy")
        val MILITARY = intPreferencesKey("military")
        val HAPPINESS = intPreferencesKey("happiness")
        val STABILITY = intPreferencesKey("stability")
        val TECHNOLOGY = intPreferencesKey("technology")
        val EDUCATION = intPreferencesKey("education")
        val HEALTHCARE = intPreferencesKey("healthcare")
        val ENVIRONMENT = intPreferencesKey("environment")
        val CRIME = intPreferencesKey("crime")
        val CORRUPTION = intPreferencesKey("corruption")
        val PROPAGANDA = intPreferencesKey("propaganda")
        val SOFT_POWER = intPreferencesKey("soft_power")
        val FOOD = intPreferencesKey("food")
        val ENERGY = intPreferencesKey("energy")
        val MATERIALS = intPreferencesKey("materials")
        val YEAR = intPreferencesKey("year")
        val TREASURY = intPreferencesKey("treasury")
        val TURN_COUNT = intPreferencesKey("turn_count")
        
        val AI_NATIONS = stringPreferencesKey("ai_nations")
        val GLOBAL_MARKET = stringPreferencesKey("global_market")
        val DIPLOMATIC_RELATIONS = stringPreferencesKey("diplomatic_relations")
        val POLITICAL_PARTIES = stringPreferencesKey("political_parties")
        val ACTIVE_LAWS = stringPreferencesKey("active_laws")
        val POLITICAL_FACTIONS = stringPreferencesKey("political_factions")
        val MINISTERS = stringPreferencesKey("ministers")
        val ELECTION = stringPreferencesKey("election")
        val UN_RESOLUTIONS = stringPreferencesKey("un_resolutions")
        val SPY_MISSIONS = stringPreferencesKey("spy_missions")
    }

    suspend fun saveGame(gameState: GameState) {
        context.dataStore.edit { preferences ->
            val country = gameState.country
            preferences[PreferencesKeys.COUNTRY_NAME] = country.name
            preferences[PreferencesKeys.GOVERNMENT_TYPE] = country.governmentType.name
            preferences[PreferencesKeys.POPULATION] = country.stats.population
            preferences[PreferencesKeys.ECONOMY] = country.stats.economy
            preferences[PreferencesKeys.MILITARY] = country.stats.military
            preferences[PreferencesKeys.HAPPINESS] = country.stats.happiness
            preferences[PreferencesKeys.STABILITY] = country.stats.stability
            preferences[PreferencesKeys.TECHNOLOGY] = country.stats.technology
            preferences[PreferencesKeys.EDUCATION] = country.stats.education
            preferences[PreferencesKeys.HEALTHCARE] = country.stats.healthcare
            preferences[PreferencesKeys.ENVIRONMENT] = country.stats.environment
            preferences[PreferencesKeys.CRIME] = country.stats.crime
            preferences[PreferencesKeys.CORRUPTION] = country.stats.corruption
            preferences[PreferencesKeys.PROPAGANDA] = country.stats.propaganda
            preferences[PreferencesKeys.SOFT_POWER] = country.stats.softPower
            preferences[PreferencesKeys.FOOD] = country.resources.food
            preferences[PreferencesKeys.ENERGY] = country.resources.energy
            preferences[PreferencesKeys.MATERIALS] = country.resources.materials
            preferences[PreferencesKeys.YEAR] = country.year
            preferences[PreferencesKeys.TREASURY] = country.treasury
            preferences[PreferencesKeys.TURN_COUNT] = country.turnCount

            preferences[PreferencesKeys.AI_NATIONS] = serializeAiNations(gameState.aiNations)
            preferences[PreferencesKeys.GLOBAL_MARKET] = serializeGlobalMarket(gameState.globalMarket)
            preferences[PreferencesKeys.DIPLOMATIC_RELATIONS] = serializeRelations(country.diplomaticRelations)
            preferences[PreferencesKeys.POLITICAL_PARTIES] = serializeParties(country.politicalParties)
            preferences[PreferencesKeys.ACTIVE_LAWS] = serializeLaws(country.activeLaws)
            preferences[PreferencesKeys.POLITICAL_FACTIONS] = serializeFactions(country.factions)
            preferences[PreferencesKeys.MINISTERS] = serializeMinisters(country.ministers)
            preferences[PreferencesKeys.ELECTION] = country.election?.let { serializeElection(it) } ?: ""
            preferences[PreferencesKeys.UN_RESOLUTIONS] = serializeUnResolutions(country.unitedNations.passedResolutions)
            preferences[PreferencesKeys.SPY_MISSIONS] = serializeSpyMissions(country.activeSpyMissions)
        }
    }

    fun loadGame(): Flow<GameState?> {
        return context.dataStore.data.map { preferences ->
            val name = preferences[PreferencesKeys.COUNTRY_NAME] ?: return@map null
            val govType = preferences[PreferencesKeys.GOVERNMENT_TYPE]?.let {
                try {
                    GovernmentType.valueOf(it)
                } catch (e: Exception) {
                    GovernmentType.DEMOCRACY
                }
            } ?: return@map null

            val aiNationsString = preferences[PreferencesKeys.AI_NATIONS]
            val globalMarketString = preferences[PreferencesKeys.GLOBAL_MARKET]
            val relationsString = preferences[PreferencesKeys.DIPLOMATIC_RELATIONS]
            
            val partiesString = preferences[PreferencesKeys.POLITICAL_PARTIES]
            val lawsString = preferences[PreferencesKeys.ACTIVE_LAWS]
            val factionsString = preferences[PreferencesKeys.POLITICAL_FACTIONS]
            val ministersString = preferences[PreferencesKeys.MINISTERS]
            val electionString = preferences[PreferencesKeys.ELECTION]
            val unResolutionsString = preferences[PreferencesKeys.UN_RESOLUTIONS]
            val spyMissionsString = preferences[PreferencesKeys.SPY_MISSIONS]

            val aiNations = if (aiNationsString != null) deserializeAiNations(aiNationsString) else GameLogic.generateAiNations()
            val globalMarket = if (globalMarketString != null) deserializeGlobalMarket(globalMarketString) else GlobalMarket()
            val relations = if (relationsString != null) deserializeRelations(relationsString) else GameLogic.generateInitialRelations(aiNations)
            
            val parties = if (partiesString != null) deserializeParties(partiesString) else emptyList()
            val laws = if (lawsString != null) deserializeLaws(lawsString) else emptyList()
            val factions = if (factionsString != null) deserializeFactions(factionsString) else emptyList()
            val ministers = if (ministersString != null) deserializeMinisters(ministersString) else emptyList()
            val election = if (electionString != null && electionString.isNotBlank()) deserializeElection(electionString) else null
            val passedResolutions = if (unResolutionsString != null) deserializeUnResolutions(unResolutionsString) else emptyList()
            val spyMissions = if (spyMissionsString != null) deserializeSpyMissions(spyMissionsString) else emptyList()

            val country = Country(
                name = name,
                governmentType = govType,
                stats = CountryStats(
                    population = preferences[PreferencesKeys.POPULATION] ?: 1000000,
                    economy = preferences[PreferencesKeys.ECONOMY] ?: 50,
                    military = preferences[PreferencesKeys.MILITARY] ?: 30,
                    happiness = preferences[PreferencesKeys.HAPPINESS] ?: 60,
                    stability = preferences[PreferencesKeys.STABILITY] ?: 50,
                    technology = preferences[PreferencesKeys.TECHNOLOGY] ?: 20,
                    education = preferences[PreferencesKeys.EDUCATION] ?: 30,
                    healthcare = preferences[PreferencesKeys.HEALTHCARE] ?: 30,
                    environment = preferences[PreferencesKeys.ENVIRONMENT] ?: 50,
                    crime = preferences[PreferencesKeys.CRIME] ?: 20,
                    corruption = preferences[PreferencesKeys.CORRUPTION] ?: 10,
                    propaganda = preferences[PreferencesKeys.PROPAGANDA] ?: 0,
                    softPower = preferences[PreferencesKeys.SOFT_POWER] ?: 0
                ),
                resources = Resources(
                    food = preferences[PreferencesKeys.FOOD] ?: 100,
                    energy = preferences[PreferencesKeys.ENERGY] ?: 100,
                    materials = preferences[PreferencesKeys.MATERIALS] ?: 50
                ),
                diplomaticRelations = relations,
                year = preferences[PreferencesKeys.YEAR] ?: 2024,
                treasury = preferences[PreferencesKeys.TREASURY] ?: 10000,
                turnCount = preferences[PreferencesKeys.TURN_COUNT] ?: 0,
                politicalParties = parties,
                activeLaws = laws,
                factions = factions,
                ministers = ministers,
                election = election,
                unitedNations = UnitedNations(memberCount = aiNations.size + 1, passedResolutions = passedResolutions),
                activeSpyMissions = spyMissions
            )

            GameState(
                country = country,
                aiNations = aiNations,
                globalMarket = globalMarket
            )
        }
    }

    suspend fun clearGame() {
        context.dataStore.edit { it.clear() }
    }

    private fun serializeAiNations(nations: List<AiNation>): String {
        return nations.joinToString(";") { ai ->
            "${ai.id}|${ai.name}|${ai.governmentType.name}|${ai.personality.name}|${ai.stats.military}|${ai.stats.economy}|${ai.stats.technology}|${ai.stats.population}|${ai.treasury}|${ai.isAlive}|${ai.stats.stability}"
        }
    }

    private fun deserializeAiNations(data: String): List<AiNation> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                AiNation(
                    id = parts[0],
                    name = parts[1],
                    governmentType = GovernmentType.valueOf(parts[2]),
                    personality = AiPersonality.valueOf(parts[3]),
                    stats = CountryStats(
                        military = parts[4].toInt(),
                        economy = parts[5].toInt(),
                        technology = parts[6].toInt(),
                        population = parts[7].toInt(),
                        stability = parts.getOrElse(10) { "50" }.toInt()
                    ),
                    treasury = parts[8].toInt(),
                    isAlive = parts[9].toBoolean()
                )
            } catch (e: Exception) { null }
        }
    }

    private fun serializeGlobalMarket(market: GlobalMarket): String {
        return "${market.foodPrice},${market.energyPrice},${market.materialsPrice},${market.globalInstability}"
    }

    private fun deserializeGlobalMarket(data: String): GlobalMarket {
        try {
            val parts = data.split(",")
            return GlobalMarket(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        } catch (e: Exception) { return GlobalMarket() }
    }

    private fun serializeRelations(relations: List<DiplomaticRelation>): String {
        return relations.joinToString(";") { rel ->
            val sanctionsStr = rel.sanctions.joinToString(",") { it.name }
            "${rel.nationName}|${rel.nationId}|${rel.relationScore}|${rel.status.name}|${rel.isAtWar}|${rel.hasTradeAgreement}|${rel.hasNonAggressionPact}|${rel.hasAlliance}|$sanctionsStr"
        }
    }

    private fun deserializeRelations(data: String): List<DiplomaticRelation> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                val sanctions = if (parts.size > 8 && parts[8].isNotBlank()) {
                    parts[8].split(",").map { SanctionType.valueOf(it) }
                } else emptyList()
                DiplomaticRelation(
                    nationName = parts[0],
                    nationId = parts[1],
                    relationScore = parts[2].toInt(),
                    status = RelationStatus.valueOf(parts[3]),
                    isAtWar = parts[4].toBoolean(),
                    hasTradeAgreement = parts[5].toBoolean(),
                    hasNonAggressionPact = parts[6].toBoolean(),
                    hasAlliance = parts[7].toBoolean(),
                    sanctions = sanctions
                )
            } catch (e: Exception) { null }
        }
    }

    private fun serializeParties(parties: List<PoliticalParty>): String {
        return parties.joinToString(";") { "${it.name}|${it.ideology.name}|${it.popularity}|${it.influence}" }
    }

    private fun deserializeParties(data: String): List<PoliticalParty> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                PoliticalParty(parts[0], Ideology.valueOf(parts[1]), parts[2].toInt(), parts[3].toInt())
            } catch (e: Exception) { null }
        }
    }

    private fun serializeLaws(laws: List<Law>): String {
        return laws.joinToString(";") { "${it.id}|${it.name}|${it.description}|${it.isActive}|${it.cost}|${it.stabilityEffect}|${it.economyEffect}|${it.happinessEffect}|${it.corruptionEffect}" }
    }

    private fun deserializeLaws(data: String): List<Law> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                Law(parts[0], parts[1], parts[2], parts[3].toBoolean(), parts[4].toInt(), parts[5].toInt(), parts[6].toInt(), parts[7].toInt(), parts[8].toInt())
            } catch (e: Exception) { null }
        }
    }

    private fun serializeFactions(factions: List<PoliticalFaction>): String {
        return factions.joinToString(";") { "${it.name}|${it.loyalty}|${it.power}" }
    }

    private fun deserializeFactions(data: String): List<PoliticalFaction> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                PoliticalFaction(parts[0], parts[1].toInt(), parts[2].toInt())
            } catch (e: Exception) { null }
        }
    }

    private fun serializeMinisters(ministers: List<Minister>): String {
        return ministers.joinToString(";") { "${it.id}|${it.name}|${it.role.name}|${it.skill}|${it.corruption}|${it.loyalty}" }
    }

    private fun deserializeMinisters(data: String): List<Minister> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                Minister(parts[0], parts[1], MinisterRole.valueOf(parts[2]), parts[3].toInt(), parts[4].toInt(), parts[5].toInt())
            } catch (e: Exception) { null }
        }
    }

    private fun serializeElection(election: Election): String {
        return "${election.year},${election.isActive},${election.turnsRemaining}"
    }

    private fun deserializeElection(data: String): Election {
        val parts = data.split(",")
        return Election(parts[0].toInt(), parts[1].toBoolean(), parts[2].toInt())
    }

    private fun serializeUnResolutions(resolutions: List<UNResolution>): String {
        return resolutions.joinToString(";") { "${it.id}|${it.type.name}|${it.description}|${it.yearProposed}|${it.status.name}" }
    }

    private fun deserializeUnResolutions(data: String): List<UNResolution> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                UNResolution(parts[0], UNResolutionType.valueOf(parts[1]), null, parts[2], parts[3].toInt(), 0, 0, ResolutionStatus.valueOf(parts[4]))
            } catch (e: Exception) { null }
        }
    }

    private fun serializeSpyMissions(missions: List<SpyMission>): String {
        return missions.joinToString(";") { "${it.id}|${it.targetNationId}|${it.targetNationName}|${it.type.name}|${it.successChance}|${it.turnsRemaining}|${it.costPerTurn}" }
    }

    private fun deserializeSpyMissions(data: String): List<SpyMission> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { entry ->
            try {
                val parts = entry.split("|")
                SpyMission(parts[0], parts[1], parts[2], SpyMissionType.valueOf(parts[3]), parts[4].toInt(), parts[5].toInt(), parts[6].toInt())
            } catch (e: Exception) { null }
        }
    }
}
