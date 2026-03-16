package com.countrysimulator.game.domain

object GameLogic {

    private val events = listOf(
        GameEvent(
            title = "Economic Boom",
            description = "Trade routes are flourishing! Your economy gets a boost.",
            effect = { stats -> stats.copy(economy = (stats.economy + 10).coerceAtMost(100)) },
            options = listOf(
                EventOption("Invest in Industry") { stats, treasury ->
                    Pair(stats.copy(technology = stats.technology + 5), treasury - 1000)
                },
                EventOption("Save Treasury") { stats, treasury ->
                    Pair(stats, treasury + 2000)
                }
            )
        ),
        GameEvent(
            title = "Natural Disaster",
            description = "A powerful earthquake has struck! Infrastructure damaged.",
            effect = { stats -> stats.copy(stability = (stats.stability - 15).coerceAtLeast(0), population = (stats.population - 50000).coerceAtLeast(1)) },
            options = listOf(
                EventOption("Emergency Aid") { stats, treasury ->
                    Pair(stats.copy(happiness = stats.happiness + 5), treasury - 1500)
                },
                EventOption("Focus on Military") { stats, treasury ->
                    Pair(stats.copy(military = stats.military + 10), treasury - 500)
                }
            )
        ),
        GameEvent(
            title = "Scientific Breakthrough",
            description = "Your scientists have made a major discovery!",
            effect = { stats -> stats.copy(technology = (stats.technology + 15).coerceAtMost(100)) },
            options = listOf(
                EventOption("Patent Technology") { stats, treasury ->
                    Pair(stats.copy(economy = stats.economy + 10), treasury + 3000)
                },
                EventOption("Share with World") { stats, treasury ->
                    Pair(stats.copy(stability = stats.stability + 10), treasury)
                }
            )
        ),
        GameEvent(
            title = "War Declaration",
            description = "A neighboring nation has declared war!",
            effect = { stats -> stats.copy(military = (stats.military - 10).coerceAtLeast(0), happiness = (stats.happiness - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Fight Back") { stats, treasury ->
                    val won = stats.military > 40
                    if (won) Pair(stats.copy(military = stats.military + 20, economy = stats.economy + 10), treasury - 2000)
                    else Pair(stats.copy(military = stats.military - 10, stability = stats.stability - 20), treasury - 3000)
                },
                EventOption("Negotiate Peace") { stats, treasury ->
                    Pair(stats.copy(economy = stats.economy - 10, stability = stats.stability + 5), treasury - 1000)
                }
            )
        ),
        GameEvent(
            title = "Political Unrest",
            description = "Citizens are protesting in the streets demanding change.",
            effect = { stats -> stats.copy(happiness = (stats.happiness - 15).coerceAtLeast(0), stability = (stats.stability - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Grant Reforms") { stats, treasury ->
                    Pair(stats.copy(happiness = stats.happiness + 20, economy = stats.economy - 10), treasury - 1500)
                },
                EventOption("Suppress Protests") { stats, treasury ->
                    Pair(stats.copy(military = stats.military + 10, happiness = stats.happiness - 10), treasury - 500)
                }
            )
        ),
        GameEvent(
            title = "Trade Agreement",
            description = "Foreign powers want to establish trade routes.",
            effect = { stats -> stats.copy(economy = (stats.economy + 8).coerceAtMost(100)) },
            options = listOf(
                EventOption("Accept Deal") { stats, treasury ->
                    Pair(stats.copy(economy = stats.economy + 15), treasury + 2500)
                },
                EventOption("Decline") { stats, treasury ->
                    Pair(stats, treasury)
                }
            )
        ),
        GameEvent(
            title = "Pandemic Outbreak",
            description = "A deadly disease is spreading across your nation.",
            effect = { stats -> stats.copy(population = (stats.population - 100000).coerceAtLeast(1), happiness = (stats.happiness - 15).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Lockdown Measures") { stats, treasury ->
                    Pair(stats.copy(stability = stats.stability + 10), treasury - 3000)
                },
                EventOption("Keep Economy Open") { stats, treasury ->
                    Pair(stats.copy(population = (stats.population - 150000).coerceAtLeast(1), economy = stats.economy + 10), treasury - 1000)
                }
            )
        ),
        GameEvent(
            title = "Cultural Festival",
            description = "A national festival boosts citizen morale!",
            effect = { stats -> stats.copy(happiness = (stats.happiness + 10).coerceAtMost(100), stability = stats.stability + 5) },
            options = listOf(
                EventOption("Grand Celebration") { stats, treasury ->
                    Pair(stats.copy(happiness = stats.happiness + 15), treasury - 2000)
                },
                EventOption("Modest Event") { stats, treasury ->
                    Pair(stats.copy(happiness = stats.happiness + 5), treasury - 500)
                }
            )
        ),
        GameEvent(
            title = "Espionage Discovery",
            description = "Foreign spies have been caught in your country!",
            effect = { stats -> stats.copy(technology = (stats.technology - 5).coerceAtLeast(0), stability = stats.stability - 5) },
            options = listOf(
                EventOption("Execute Spies") { stats, treasury ->
                    Pair(stats.copy(military = stats.military + 15), treasury)
                },
                EventOption("Exchange for Prisoners") { stats, treasury ->
                    Pair(stats.copy(technology = stats.technology + 10), treasury + 1000)
                }
            )
        ),
        GameEvent(
            title = "Resource Discovery",
            description = "Valuable resources have been found in your territory!",
            effect = { stats -> stats.copy(economy = (stats.economy + 12).coerceAtMost(100)) },
            options = listOf(
                EventOption("Extract Quickly") { stats, treasury ->
                    Pair(stats.copy(technology = stats.technology - 5, economy = stats.economy + 20), treasury + 5000)
                },
                EventOption("Sustainable Mining") { stats, treasury ->
                    Pair(stats.copy(technology = stats.technology + 5, economy = stats.economy + 10), treasury + 2000)
                }
            )
        )
    )

    fun getGovernmentBonus(type: GovernmentType): (CountryStats) -> CountryStats {
        return when (type) {
            GovernmentType.DEMOCRACY -> { stats -> stats.copy(happiness = stats.happiness + 5, stability = stats.stability + 5) }
            GovernmentType.MONARCHY -> { stats -> stats.copy(economy = stats.economy + 10, happiness = stats.happiness - 10) }
            GovernmentType.REPUBLIC -> { stats -> stats.copy(economy = stats.economy + 15, military = stats.military - 5) }
            GovernmentType.DICTATORSHIP -> { stats -> stats.copy(military = stats.military + 15, happiness = stats.happiness - 15) }
            GovernmentType.COMMUNISM -> { stats -> stats.copy(economy = stats.economy + 5, stability = stats.stability + 10) }
        }
    }

    fun calculateTurnIncome(country: Country): Int {
        val baseIncome = country.stats.population / 100000
        val economyMultiplier = country.stats.economy / 50.0
        val happinessFactor = country.stats.happiness / 100.0
        return (baseIncome * economyMultiplier * happinessFactor).toInt().coerceAtLeast(0)
    }

    fun checkGameOver(country: Country): GameOverReason? {
        return when {
            country.treasury < -5000 -> GameOverReason.BANKRUPTCY
            country.stats.happiness < 10 -> GameOverReason.REVOLUTION
            country.stats.military < 5 && (1..10).random() == 1 -> GameOverReason.INVASION
            country.stats.technology < 5 -> GameOverReason.TECH_FAILURE
            else -> null
        }
    }

    fun processTurn(country: Country): GameState {
        val income = calculateTurnIncome(country)
        var newTreasury = country.treasury + income
        var newStats = country.stats.copy()

        newStats = getGovernmentBonus(country.governmentType)(newStats)

        val eventRoll = (1..100).random()
        val eventThreshold = 100 - newStats.stability
        val event = if (eventRoll <= eventThreshold && events.isNotEmpty()) {
            events.random()
        } else null

        event?.let {
            newStats = it.effect(newStats)
        }

        newStats = newStats.copy(
            population = (newStats.population + (newStats.population * 0.01).toInt()).coerceAtMost(100000000),
            economy = (newStats.economy + (1..3).random()).coerceAtMost(100),
            military = (newStats.military + (0..2).random()).coerceAtMost(100),
            happiness = (newStats.happiness + (-3..3).random()).coerceAtMost(100).coerceAtLeast(0),
            stability = (newStats.stability + (-2..2).random()).coerceAtMost(100).coerceAtLeast(0),
            technology = (newStats.technology + (0..1).random()).coerceAtMost(100)
        )

        val gameOverReason = checkGameOver(country.copy(stats = newStats, treasury = newTreasury))

        val newEventHistory = mutableListOf<String>()
        event?.let {
            newEventHistory.add("Year ${country.year + 1}: ${it.title}")
        }
        newEventHistory.addAll(country.turnCount.coerceAtMost(9).let { country.eventHistory.take(it) })

        return GameState(
            country = country.copy(
                stats = newStats,
                treasury = newTreasury,
                year = country.year + 1,
                turnCount = country.turnCount + 1,
                eventHistory = newEventHistory
            ),
            isGameOver = gameOverReason != null,
            gameOverReason = gameOverReason,
            lastEvent = event,
            eventHistory = newEventHistory
        )
    }

    fun getRandomEvent(): GameEvent = events.random()
}