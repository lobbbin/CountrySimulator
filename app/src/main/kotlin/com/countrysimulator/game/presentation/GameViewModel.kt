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
            repository.loadGame().collect { savedCountry ->
                if (savedCountry != null) {
                    val income = GameLogic.calculateTurnIncome(savedCountry)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasActiveGame = true,
                        gameState = GameState(country = savedCountry),
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
        val newCountry = GameLogic.generateInitialCountry(name, governmentType)

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                hasActiveGame = true,
                showNewGameDialog = false,
                gameState = GameState(country = newCountry),
                incomeThisTurn = GameLogic.calculateTurnIncome(newCountry)
            )
        }
    }

    fun nextTurn() {
        val currentState = _uiState.value.gameState ?: return
        val newState = GameLogic.processTurn(currentState.country)

        viewModelScope.launch {
            repository.saveGame(newState.country)
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

        viewModelScope.launch {
            repository.saveGame(updatedCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = updatedCountry),
                showEventDialog = false,
                currentEvent = null,
                incomeThisTurn = GameLogic.calculateTurnIncome(updatedCountry)
            )
        }
    }

    fun investInEconomy() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1000) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(economy = (currentState.country.stats.economy + 8).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1000
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry),
                incomeThisTurn = GameLogic.calculateTurnIncome(newCountry)
            )
        }
    }

    fun recruitMilitary() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 800) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(military = (currentState.country.stats.military + 10).coerceAtMost(100)),
            treasury = currentState.country.treasury - 800
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun improveInfrastructure() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1200) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(stability = (currentState.country.stats.stability + 8).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1200
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun investInTechnology() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1500) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(technology = (currentState.country.stats.technology + 12).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1500
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun improveHappiness() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 800) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(happiness = (currentState.country.stats.happiness + 10).coerceAtMost(100)),
            treasury = currentState.country.treasury - 800
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry),
                incomeThisTurn = GameLogic.calculateTurnIncome(newCountry)
            )
        }
    }

    fun investInEducation() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1200) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(education = (currentState.country.stats.education + 10).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1200
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun investInHealthcare() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1000) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(healthcare = (currentState.country.stats.healthcare + 10).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1000
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun improveEnvironment() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 1500) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(environment = (currentState.country.stats.environment + 10).coerceAtMost(100)),
            treasury = currentState.country.treasury - 1500
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun fightCrime() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 800) return

        val newCountry = currentState.country.copy(
            stats = currentState.country.stats.copy(crime = (currentState.country.stats.crime - 10).coerceAtLeast(0)),
            treasury = currentState.country.treasury - 800
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun buyFood() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 500) return

        val newCountry = currentState.country.copy(
            resources = currentState.country.resources.copy(
                food = (currentState.country.resources.food + 50).coerceAtMost(currentState.country.resources.maxFood)
            ),
            treasury = currentState.country.treasury - 500
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun buyEnergy() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 600) return

        val newCountry = currentState.country.copy(
            resources = currentState.country.resources.copy(
                energy = (currentState.country.resources.energy + 50).coerceAtMost(currentState.country.resources.maxEnergy)
            ),
            treasury = currentState.country.treasury - 600
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
            )
        }
    }

    fun buyMaterials() {
        val currentState = _uiState.value.gameState ?: return
        if (currentState.country.treasury < 800) return

        val newCountry = currentState.country.copy(
            resources = currentState.country.resources.copy(
                materials = (currentState.country.resources.materials + 30).coerceAtMost(currentState.country.resources.maxMaterials)
            ),
            treasury = currentState.country.treasury - 800
        )

        viewModelScope.launch {
            repository.saveGame(newCountry)
            _uiState.value = _uiState.value.copy(
                gameState = currentState.copy(country = newCountry)
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
        }
    }
}
