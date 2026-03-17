package com.countrysimulator.game.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.countrysimulator.game.data.GameRepository
import com.countrysimulator.game.domain.Country
import com.countrysimulator.game.domain.CountryStats
import com.countrysimulator.game.domain.GameEvent
import com.countrysimulator.game.domain.GameLogic
import com.countrysimulator.game.domain.GameOverReason
import com.countrysimulator.game.domain.GameState
import com.countrysimulator.game.domain.GovernmentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val isLoading: Boolean = true,
    val hasActiveGame: Boolean = false,
    val gameState: GameState? = null,
    val showNewGameDialog: Boolean = false,
    val showEventDialog: Boolean = false,
    val currentEvent: GameEvent? = null,
    val incomeThisTurn: Int = 0,
    val newsHeadline: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSavedGame()
    }

    private fun loadSavedGame() {
        viewModelScope.launch {
            repository.loadGame().collect { savedGameState ->
                if (savedGameState != null) {
                    val income = GameLogic.calculateTurnIncome(savedGameState.country)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasActiveGame = true,
                        gameState = savedGameState,
                        incomeThisTurn = income
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showNewGameDialog = true
                    )
                }
            }
        }
    }

    fun startNewGame(name: String, governmentType: GovernmentType) {
        val (newCountry, aiNations) = GameLogic.generateInitialCountry(name, governmentType)
        val newGameState = GameState(
            country = newCountry,
            aiNations = aiNations
        )

        viewModelScope.launch {
            repository.saveGame(newGameState)
            _uiState.value = _uiState.value.copy(
                hasActiveGame = true,
                showNewGameDialog = false,
                gameState = newGameState,
                incomeThisTurn = GameLogic.calculateTurnIncome(newCountry)
            )
        }
    }

    fun nextTurn() {
        val currentState = _uiState.value.gameState ?: return
        val newState = GameLogic.processTurn(currentState)

        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(
                gameState = newState,
                showEventDialog = newState.lastEvent != null,
                currentEvent = newState.lastEvent,
                incomeThisTurn = GameLogic.calculateTurnIncome(newState.country),
                newsHeadline = newState.newsHeadline
            )
        }
    }

    fun handleEventOption(optionIndex: Int) {
        val event = _uiState.value.currentEvent ?: return
        val currentState = _uiState.value.gameState ?: return

        val option = event.options.getOrNull(optionIndex) ?: return
        val (newStats, newTreasury, newResources) = option.effect(
            currentState.country.stats,
            currentState.country.treasury,
            currentState.country.resources
        )

        val updatedCountry = currentState.country.copy(
            stats = newStats,
            treasury = newTreasury,
            resources = newResources
        )
        val newState = currentState.copy(country = updatedCountry)

        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(
                gameState = newState,
                showEventDialog = false,
                currentEvent = null,
                incomeThisTurn = GameLogic.calculateTurnIncome(updatedCountry)
            )
        }
    }

    private fun updateCountry(update: (Country) -> Country) {
        val currentState = _uiState.value.gameState ?: return
        val newCountry = update(currentState.country)
        val newState = currentState.copy(country = newCountry)

        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(
                gameState = newState,
                incomeThisTurn = GameLogic.calculateTurnIncome(newCountry) // Recalculate income in case stats changed
            )
        }
    }

    fun investInEconomy() {
        updateCountry { country ->
            if (country.treasury < 1000) country
            else country.copy(
                stats = country.stats.copy(economy = (country.stats.economy + 8).coerceAtMost(100)),
                treasury = country.treasury - 1000
            )
        }
    }

    fun recruitMilitary() {
        updateCountry { country ->
            if (country.treasury < 800) country
            else country.copy(
                stats = country.stats.copy(military = (country.stats.military + 10).coerceAtMost(100)),
                treasury = country.treasury - 800
            )
        }
    }

    fun improveInfrastructure() {
        updateCountry { country ->
            if (country.treasury < 1200) country
            else country.copy(
                stats = country.stats.copy(stability = (country.stats.stability + 8).coerceAtMost(100)),
                treasury = country.treasury - 1200
            )
        }
    }

    fun investInTechnology() {
        updateCountry { country ->
            if (country.treasury < 1500) country
            else country.copy(
                stats = country.stats.copy(technology = (country.stats.technology + 12).coerceAtMost(100)),
                treasury = country.treasury - 1500
            )
        }
    }

    fun improveHappiness() {
        updateCountry { country ->
            if (country.treasury < 800) country
            else country.copy(
                stats = country.stats.copy(happiness = (country.stats.happiness + 10).coerceAtMost(100)),
                treasury = country.treasury - 800
            )
        }
    }

    fun investInEducation() {
        updateCountry { country ->
            if (country.treasury < 1200) country
            else country.copy(
                stats = country.stats.copy(education = (country.stats.education + 10).coerceAtMost(100)),
                treasury = country.treasury - 1200
            )
        }
    }

    fun investInHealthcare() {
        updateCountry { country ->
            if (country.treasury < 1000) country
            else country.copy(
                stats = country.stats.copy(healthcare = (country.stats.healthcare + 10).coerceAtMost(100)),
                treasury = country.treasury - 1000
            )
        }
    }

    fun improveEnvironment() {
        updateCountry { country ->
            if (country.treasury < 1500) country
            else country.copy(
                stats = country.stats.copy(environment = (country.stats.environment + 10).coerceAtMost(100)),
                treasury = country.treasury - 1500
            )
        }
    }

    fun fightCrime() {
        updateCountry { country ->
            if (country.treasury < 800) country
            else country.copy(
                stats = country.stats.copy(crime = (country.stats.crime - 10).coerceAtLeast(0)),
                treasury = country.treasury - 800
            )
        }
    }

    fun buyFood() {
        updateCountry { country ->
            if (country.treasury < 500) country
            else country.copy(
                resources = country.resources.copy(
                    food = (country.resources.food + 50).coerceAtMost(country.resources.maxFood)
                ),
                treasury = country.treasury - 500
            )
        }
    }

    fun buyEnergy() {
        updateCountry { country ->
            if (country.treasury < 600) country
            else country.copy(
                resources = country.resources.copy(
                    energy = (country.resources.energy + 50).coerceAtMost(country.resources.maxEnergy)
                ),
                treasury = country.treasury - 600
            )
        }
    }

    fun buyMaterials() {
        updateCountry { country ->
            if (country.treasury < 800) country
            else country.copy(
                resources = country.resources.copy(
                    materials = (country.resources.materials + 30).coerceAtMost(country.resources.maxMaterials)
                ),
                treasury = country.treasury - 800
            )
        }
    }

    fun restartGame() {
        viewModelScope.launch {
            repository.clearGame()
            _uiState.value = _uiState.value.copy(
                hasActiveGame = false,
                gameState = null,
                showNewGameDialog = true,
                showEventDialog = false,
                currentEvent = null,
                newsHeadline = null
            )
        }
    }

    fun improveRelations(nationId: String) {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 500) return
        
        val newRelations = currentState.country.diplomaticRelations.map { rel ->
            if (rel.nationId == nationId) {
                rel.copy(relationScore = (rel.relationScore + 10).coerceAtMost(100))
            } else rel
        }
        
        val newCountry = currentState.country.copy(
            diplomaticRelations = newRelations,
            treasury = currentState.country.treasury - 500
        )
        val newState = currentState.copy(country = newCountry)
        
        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(gameState = newState)
        }
    }

    fun declareWar(nationId: String) {
        val currentState = _uiState.value.gameState ?: return
        
        val newRelations = currentState.country.diplomaticRelations.map { rel ->
            if (rel.nationId == nationId) {
                rel.copy(
                    status = com.countrysimulator.game.domain.RelationStatus.ENEMY,
                    isAtWar = true,
                    relationScore = 0,
                    hasTradeAgreement = false,
                    hasAlliance = false,
                    hasNonAggressionPact = false
                )
            } else rel
        }
        
        val newCountry = currentState.country.copy(
            diplomaticRelations = newRelations,
            stats = currentState.country.stats.copy(stability = (currentState.country.stats.stability - 10).coerceAtLeast(0))
        )
        val newState = currentState.copy(country = newCountry)
        
        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(gameState = newState)
        }
    }

    fun offerTrade(nationId: String) {
        val currentState = _uiState.value.gameState ?: return
        // Check if relations are high enough (>40)
        val relation = currentState.country.diplomaticRelations.find { it.nationId == nationId } ?: return
        if (relation.relationScore < 40) return
        
        val newRelations = currentState.country.diplomaticRelations.map { rel ->
            if (rel.nationId == nationId) {
                rel.copy(hasTradeAgreement = true)
            } else rel
        }
        
        val newCountry = currentState.country.copy(diplomaticRelations = newRelations)
        val newState = currentState.copy(country = newCountry)
        
        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(gameState = newState)
        }
    }

    fun formAlliance(nationId: String) {
        val currentState = _uiState.value.gameState ?: return
        // Check if relations are high enough (>80)
        val relation = currentState.country.diplomaticRelations.find { it.nationId == nationId } ?: return
        if (relation.relationScore < 80) return
        
        val newRelations = currentState.country.diplomaticRelations.map { rel ->
            if (rel.nationId == nationId) {
                rel.copy(
                    hasAlliance = true,
                    status = com.countrysimulator.game.domain.RelationStatus.ALLY
                )
            } else rel
        }
        
        val newCountry = currentState.country.copy(diplomaticRelations = newRelations)
        val newState = currentState.copy(country = newCountry)
        
        viewModelScope.launch {
            repository.saveGame(newState)
            _uiState.value = _uiState.value.copy(gameState = newState)
        }
    }

    fun toggleLaw(lawId: String) {
        updateCountry { country ->
            val newLaws = country.activeLaws.map { law ->
                if (law.id == lawId) {
                    if (!law.isActive && country.treasury < law.cost) law
                    else law.copy(isActive = !law.isActive)
                } else law
            }
            val cost = country.activeLaws.find { it.id == lawId && !it.isActive }?.cost ?: 0
            country.copy(activeLaws = newLaws, treasury = country.treasury - cost)
        }
    }

    fun runPropaganda() {
        updateCountry { country ->
            if (country.treasury < 1000) country
            else country.copy(
                stats = country.stats.copy(
                    propaganda = (country.stats.propaganda + 15).coerceAtMost(100),
                    happiness = (country.stats.happiness + 5).coerceAtMost(100),
                    stability = (country.stats.stability + 10).coerceAtMost(100)
                ),
                treasury = country.treasury - 1000
            )
        }
    }

    fun bribeFaction(factionName: String) {
        updateCountry { country ->
            if (country.treasury < 2000) country
            else {
                val newFactions = country.factions.map { faction ->
                    if (faction.name == factionName) faction.copy(loyalty = (faction.loyalty + 20).coerceAtMost(100))
                    else faction
                }
                country.copy(
                    factions = newFactions,
                    stats = country.stats.copy(corruption = (country.stats.corruption + 5).coerceAtMost(100)),
                    treasury = country.treasury - 2000
                )
            }
        }
    }

    fun hireMinister(name: String, role: MinisterRole) {
        updateCountry { country ->
            if (country.treasury < 3000) country
            else {
                val newMinister = Minister("m_${System.currentTimeMillis()}", name, role, (40..80).random(), (0..20).random(), 100)
                val newMinisters = country.ministers.filter { it.role != role } + newMinister
                country.copy(ministers = newMinisters, treasury = country.treasury - 3000)
            }
        }
    }

    fun triggerElection() {
        updateCountry { country ->
            if (country.election?.isActive == true) country
            else country.copy(election = Election(year = country.year, isActive = true, turnsRemaining = 1))
        }
    }

    fun getGameOverMessage(reason: GameOverReason): String {
        return when (reason) {
            GameOverReason.BANKRUPTCY -> "Your country has gone bankrupt! The government has collapsed due to unsustainable debt."
            GameOverReason.REVOLUTION -> "The people have revolted! Your regime has been overthrown by angry citizens."
            GameOverReason.INVASION -> "Your military was too weak to defend the nation. Invaders have conquered your country."
            GameOverReason.TECH_FAILURE -> "Your nation fell behind technologically and became obsolete in the modern world."
            GameOverReason.FAMINE -> "Widespread famine has devastated your population. The nation can no longer sustain itself."
            GameOverReason.ENVIRONMENTAL_COLLAPSE -> "Environmental collapse has made your nation uninhabitable. Mass exodus ensues."
            GameOverReason.NUCLEAR_WINTER -> "Nuclear war has brought on a devastating winter. Civilization has collapsed."
            GameOverReason.CIVIL_WAR -> "The nation has split into warring factions. Civil war has destroyed everything."
            GameOverReason.ASSASSINATION -> "You have been assassinated by political rivals! The nation descends into chaos."
            GameOverReason.COUP -> "The military has seized power in a violent coup! Your leadership has ended."
        }
    }
}
