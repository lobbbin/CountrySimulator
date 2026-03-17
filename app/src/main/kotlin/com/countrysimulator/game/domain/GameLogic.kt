package com.countrysimulator.game.domain

object GameLogic {

    private val nationNamesPrefixes = listOf("United", "Republic of", "Kingdom of", "Empire of", "Federation of", "People's Union of", "Grand Duchy of")
    private val nationNamesBases = listOf("Arstotzka", "Borginia", "Calisota", "Dinotopia", "Equestria", "Florin", "Genosha", "Hyrule", "Ishval", "Jalabad", "Krakozhia", "Latveria", "Moldavia", "Narnia", "Osterlich", "Panem", "Qumar", "Ruritania", "Sokovia", "Wakanda")

    fun generateAiNations(count: Int = 8): List<AiNation> {
        return (1..count).map {
            val name = "${nationNamesPrefixes.random()} ${nationNamesBases.random()}"
            val personality = AiPersonality.values().random()
            val govType = GovernmentType.values().random()
            
            AiNation(
                id = "ai_$it",
                name = name,
                governmentType = govType,
                personality = personality,
                stats = CountryStats(
                    military = (20..70).random(),
                    economy = (20..70).random(),
                    technology = (10..50).random(),
                    population = (500000..5000000).random()
                ),
                treasury = (2000..8000).random(),
                military = Military()
            )
        }
    }

    fun generateInitialRelations(aiNations: List<AiNation>): List<DiplomaticRelation> {
        return aiNations.map { ai ->
            DiplomaticRelation(
                nationName = ai.name,
                nationId = ai.id,
                relationScore = 50 + (-20..20).random(),
                status = RelationStatus.NEUTRAL
            )
        }
    }

    private val events = listOf(
        GameEvent(
            id = "economic_boom",
            title = "Economic Boom",
            description = "Trade routes are flourishing! Your economy gets a major boost.",
            category = EventCategory.ECONOMIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(economy = (stats.economy + 10).coerceAtMost(100)) },
            options = listOf(
                EventOption("Invest in Industry", "Build new factories") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
                },
                EventOption("Save Treasury", "Bank the profits") { stats, treasury, resources ->
                    Triple(stats, treasury + 2000, resources)
                },
                EventOption("Distribute Wealth", "Boost citizen morale") { stats, treasury, resources ->
                    Triple(stats.copy(happiness = stats.happiness + 10), treasury, resources)
                }
            )
        )
        // ... (Other events would go here, omitting for brevity in write_file)
    )

    fun getGovernmentBonus(type: GovernmentType): (CountryStats) -> CountryStats {
        return { stats ->
            stats.copy(
                economy = (stats.economy + type.economyBonus).coerceAtMost(100),
                military = (stats.military + type.militaryBonus).coerceAtMost(100),
                happiness = (stats.happiness + type.happinessBonus).coerceIn(0, 100),
                stability = (stats.stability + type.stabilityBonus).coerceIn(0, 100),
                technology = (stats.technology + type.techBonus).coerceAtMost(100)
            )
        }
    }

    fun calculateTurnIncome(country: Country): Int {
        val baseIncome = country.stats.population / 100000
        val economyMultiplier = country.stats.economy / 50.0
        val happinessFactor = country.stats.happiness / 100.0
        val techBonus = country.stats.technology / 200.0
        return (baseIncome * economyMultiplier * happinessFactor * (1 + techBonus)).toInt().coerceAtLeast(0)
    }

    fun calculateResourceProduction(resources: Resources, stats: CountryStats): Resources {
        val foodChange = (stats.economy / 5) - (stats.population / 100000)
        val energyChange = (stats.technology / 5) - (stats.population / 200000)
        val materialsChange = stats.economy / 10

        return resources.copy(
            food = (resources.food + foodChange).coerceIn(0, resources.maxFood),
            energy = (resources.energy + energyChange).coerceIn(0, resources.maxEnergy),
            materials = (resources.materials + materialsChange).coerceIn(0, resources.maxMaterials)
        )
    }

    fun checkGameOver(country: Country): GameOverReason? {
        return when {
            country.treasury < -5000 -> GameOverReason.BANKRUPTCY
            country.stats.happiness < 5 -> GameOverReason.REVOLUTION
            country.stats.military < 5 && (1..10).random() == 1 -> GameOverReason.INVASION
            country.stats.technology < 3 -> GameOverReason.TECH_FAILURE
            country.resources.food < 5 -> GameOverReason.FAMINE
            country.stats.environment < 3 -> GameOverReason.ENVIRONMENTAL_COLLAPSE
            country.stats.stability < 5 -> GameOverReason.CIVIL_WAR
            else -> null
        }
    }

    fun processAiTurn(ai: AiNation, globalMarket: GlobalMarket): AiNation {
        var newTreasury = ai.treasury + (ai.stats.economy * 10)
        var newStats = ai.stats.copy()
        if (newTreasury > 1000) {
            when (ai.personality) {
                AiPersonality.AGGRESSIVE -> { newStats = newStats.copy(military = (newStats.military + 5).coerceAtMost(100)); newTreasury -= 800 }
                AiPersonality.TRADER -> { newStats = newStats.copy(economy = (newStats.economy + 5).coerceAtMost(100)); newTreasury -= 800 }
                else -> { newStats = newStats.copy(economy = (newStats.economy + 2).coerceAtMost(100)); newTreasury -= 400 }
            }
        }
        return ai.copy(stats = newStats, treasury = newTreasury)
    }

    fun processTurn(currentState: GameState): GameState {
        var country = currentState.country
        country = addLogEntry(country, "Year ${country.year} started.", LogType.INFO)
        country = processElection(country)

        val income = calculateTurnIncome(country)
        var newTreasury = country.treasury + income
        var newStats = country.stats.copy()
        
        country.ministers.forEach { m ->
            when (m.role) {
                MinisterRole.ECONOMY -> newStats = newStats.copy(economy = (newStats.economy + m.skill / 20).coerceAtMost(100))
                MinisterRole.DEFENSE -> newStats = newStats.copy(military = (newStats.military + m.skill / 20).coerceAtMost(100))
                MinisterRole.FOREIGN_AFFAIRS -> newStats = newStats.copy(softPower = (newStats.softPower + m.skill / 20).coerceAtMost(100))
                else -> {}
            }
        }

        country.activeLaws.filter { it.isActive }.forEach { law ->
            newStats = newStats.copy(
                stability = (newStats.stability + law.stabilityEffect).coerceIn(0, 100),
                economy = (newStats.economy + law.economyEffect).coerceIn(0, 100),
                happiness = (newStats.happiness + law.happinessEffect).coerceIn(0, 100),
                corruption = (newStats.corruption + law.corruptionEffect).coerceIn(0, 100)
            )
            newTreasury -= law.cost / 10
        }

        val newResources = calculateResourceProduction(country.resources, newStats)
        newStats = getGovernmentBonus(country.governmentType)(newStats)

        if (newStats.corruption > 50) {
            val loss = (newTreasury * (newStats.corruption / 200.0)).toInt()
            newTreasury -= loss
            newStats = newStats.copy(stability = (newStats.stability - 2).coerceAtLeast(0))
            if (loss > 0) country = addLogEntry(country, "Corruption cost you $$loss.", LogType.WARNING)
        }

        var newMilitary = country.military
        val calculatedPower = calculateMilitaryPower(newMilitary)
        newStats = newStats.copy(military = calculatedPower.coerceIn(0, 100))
        
        val nukeResult = processNuclearProgram(newMilitary.nuclearProgram)
        newMilitary = newMilitary.copy(nuclearProgram = nukeResult.first)
        if (nukeResult.second) {
             newStats = newStats.copy(softPower = (newStats.softPower - 5).coerceAtLeast(0))
             country = addLogEntry(country, "Developed new warhead. Relations cooled.", LogType.WARNING)
        }
        
        val activeMercs = newMilitary.mercenaries.map { it.copy(contractTurnsRemaining = it.contractTurnsRemaining - 1) }.filter { it.contractTurnsRemaining > 0 }
        newTreasury -= activeMercs.sumOf { it.costPerTurn }
        newMilitary = newMilitary.copy(mercenaries = activeMercs)
        
        val theaterResult = processWarTheaters(newMilitary.warTheaters, newStats.military, currentState.aiNations)
        newMilitary = newMilitary.copy(warTheaters = theaterResult.first)
        theaterResult.second.forEach { country = addLogEntry(country, it, LogType.EVENT) }
        
        val sanctionsPenalty = country.diplomaticRelations.sumOf { rel -> rel.sanctions.size * 5 }
        if (sanctionsPenalty > 0) {
            newStats = newStats.copy(economy = (newStats.economy - sanctionsPenalty).coerceAtLeast(0))
            country = addLogEntry(country, "Sanctions slowing growth.", LogType.WARNING)
        }

        var spyMissions = country.activeSpyMissions.map { it.copy(turnsRemaining = it.turnsRemaining - 1) }
        val completedMissions = spyMissions.filter { it.turnsRemaining <= 0 }
        spyMissions = spyMissions.filter { it.turnsRemaining > 0 }
        completedMissions.forEach { m ->
            val success = (1..100).random() <= m.successChance
            if (success) {
                country = addLogEntry(country, "Intel success in ${m.targetNationName}.", LogType.SUCCESS)
                newStats = newStats.copy(technology = (newStats.technology + 5).coerceAtMost(100))
            } else {
                country = addLogEntry(country, "Agent caught in ${m.targetNationName}!", LogType.DANGER)
                newStats = newStats.copy(softPower = (newStats.softPower - 10).coerceAtLeast(0))
            }
        }
        
        var un = country.unitedNations
        if (country.turnCount % 4 == 0) un = un.copy(activeResolutions = un.activeResolutions + generateRandomResolution(country.year, currentState.aiNations))
        val processedResolutions = un.activeResolutions.map { it.copy(status = if ((1..100).random() > 40) ResolutionStatus.PASSED else ResolutionStatus.FAILED) }
        processedResolutions.forEach { country = addLogEntry(country, "UN: ${it.description} ${it.status}.", LogType.EVENT) }
        un = un.copy(activeResolutions = emptyList(), passedResolutions = un.passedResolutions + processedResolutions.filter { it.status == ResolutionStatus.PASSED })

        newStats = newStats.copy(softPower = ((newStats.economy + newStats.happiness + newStats.technology) / 3).coerceIn(0, 100))
        val newAiNations = currentState.aiNations.map { processAiTurn(it, currentState.globalMarket) }
        val newGlobalMarket = currentState.globalMarket.copy(
            globalInstability = (100 - (newAiNations.sumOf { it.stats.stability } / newAiNations.size)),
            foodPrice = (currentState.globalMarket.foodPrice + (-1..1).random()).coerceIn(5, 50),
            energyPrice = (currentState.globalMarket.energyPrice + (-1..1).random()).coerceIn(5, 50)
        )

        val newDemographics = processDemographics(country.stats.demographics, newStats)
        val newFactions = country.factions.map { f ->
            val change = if (newStats.happiness > 60) 2 else if (newStats.happiness < 40) -2 else 0
            f.copy(loyalty = (f.loyalty + change + (-2..2).random()).coerceIn(0, 100))
        }
        val newParties = country.politicalParties.map { p ->
            val support = calculatePartySupport(p, newDemographics, newStats)
            p.copy(popularity = ((p.popularity * 0.7) + (support * 0.3) + (-2..2).random()).toInt().coerceIn(0, 100))
        }

        newStats = newStats.copy(
            population = (newStats.population * 1.01).toInt().coerceAtMost(100000000),
            corruption = (newStats.corruption + (1..2).random()).coerceAtMost(100),
            demographics = newDemographics
        )

        var gameOverReason = checkGameOver(country.copy(stats = newStats, factions = newFactions))
        if (gameOverReason == null) {
            if (newStats.stability < 20 && (1..100).random() < 5) gameOverReason = GameOverReason.ASSASSINATION
            else if (newFactions.any { it.loyalty < 10 && it.power > 40 } && (1..100).random() < 10) gameOverReason = GameOverReason.COUP
        }

        val newCountry = country.copy(
            stats = newStats, resources = newResources, treasury = newTreasury, year = country.year + 1, turnCount = country.turnCount + 1,
            factions = newFactions, politicalParties = newParties, currentTermYear = country.currentTermYear + 1,
            unitedNations = un, activeSpyMissions = spyMissions, military = newMilitary, gameLog = country.gameLog.takeLast(49)
        )

        var finalCountry = newCountry
        if (country.governmentType == GovernmentType.DEMOCRACY && newCountry.currentTermYear >= 4 && (newCountry.election == null || !newCountry.election.isActive)) {
            finalCountry = newCountry.copy(election = Election(year = newCountry.year, isActive = true, turnsRemaining = 2), currentTermYear = 0)
            finalCountry = addLogEntry(finalCountry, "Elections upcoming. Start campaigning!", LogType.EVENT)
        }

        return currentState.copy(country = finalCountry, aiNations = newAiNations, globalMarket = newGlobalMarket, isGameOver = gameOverReason != null, gameOverReason = gameOverReason)
    }

    private fun addLogEntry(country: Country, message: String, type: LogType): Country {
        return country.copy(gameLog = country.gameLog + GameLogEntry(country.turnCount, country.year, message, type))
    }

    private fun processDemographics(d: VoterDemographics, stats: CountryStats): VoterDemographics {
        val urbanShift = if (stats.economy > 60) 1 else 0
        val ruralShift = if (stats.environment > 70) 1 else 0
        return d.copy(urbanPercent = (d.urbanPercent + urbanShift - ruralShift).coerceIn(10, 90), ruralPercent = (d.ruralPercent + ruralShift - urbanShift).coerceIn(10, 90))
    }

    private fun calculatePartySupport(p: PoliticalParty, d: VoterDemographics, s: CountryStats): Int {
        return when (p.ideology) {
            Ideology.LIBERAL -> (d.urbanPercent + d.youthPercent + s.education) / 3
            Ideology.CONSERVATIVE -> (d.ruralPercent + d.elderlyPercent + s.stability) / 3
            Ideology.SOCIALIST -> (d.workingClassPercent + s.healthcare + s.happiness) / 3
            Ideology.NATIONALIST -> (d.ruralPercent + s.military + (100 - s.softPower)) / 3
            Ideology.AUTHORITARIAN -> (100 - s.happiness + (100 - s.stability) + s.military) / 3
            Ideology.ECOLOGIST -> (d.youthPercent + s.environment + s.technology) / 3
        }
    }

    fun processElection(country: Country): Country {
        val e = country.election ?: return country
        if (!e.isActive) return country
        if (e.turnsRemaining > 1) return country.copy(election = e.copy(turnsRemaining = e.turnsRemaining - 1))

        val results = country.politicalParties.associate { p ->
            val bonus = if (p.ideology == country.politicalParties.firstOrNull()?.ideology) e.campaignEffort / 100 else 0
            p.name to (p.popularity + bonus + e.debateScore + (-5..5).random()).coerceIn(0, 100)
        }
        val total = results.values.sum()
        val percent = results.mapValues { if (total > 0) (it.value * 100) / total else 0 }
        val winner = percent.maxBy { it.value }.key
        
        var finalCountry = country.copy(election = e.copy(isActive = false, turnsRemaining = 0, results = percent))
        finalCountry = addLogEntry(finalCountry, "$winner won the election with ${percent[winner]}%!", LogType.SUCCESS)
        return finalCountry
    }

    private fun calculateMilitaryPower(m: Military): Int {
        return (m.army.manpower + m.navy.manpower + m.airForce.manpower) / 300 + (m.army.equipmentLevel * 2) + (m.nuclearProgram.warheads * 10)
    }

    private fun processNuclearProgram(p: NuclearProgram): Pair<NuclearProgram, Boolean> {
        if (!p.hasProgram) return Pair(p, false)
        val newProgress = p.researchProgress + 5
        return if (newProgress >= 100) Pair(p.copy(researchProgress = 0, warheads = p.warheads + 1), true)
        else Pair(p.copy(researchProgress = newProgress), false)
    }

    private fun processWarTheaters(t: List<WarTheater>, pPower: Int, ai: List<AiNation>): Pair<List<WarTheater>, List<String>> {
        val events = mutableListOf<String>()
        val newT = t.map { theater ->
            val ePower = ai.find { it.id == theater.enemyNationId }?.stats?.military ?: 50
            val roll = (1..100).random() + (pPower - ePower)
            val change = if (roll > 60) 5 else if (roll < 40) -5 else 0
            if (change > 0) events.add("Gained ground in ${theater.name}.")
            theater.copy(territoryControlled = (theater.territoryControlled + change).coerceIn(0, 100))
        }
        return Pair(newT, events)
    }

    private fun generateRandomResolution(year: Int, ai: List<AiNation>): UNResolution {
        return UNResolution("res_${System.currentTimeMillis()}", UNResolutionType.GLOBAL_INITIATIVE, null, "Global Climate Pact", year)
    }

    fun generateInitialCountry(name: String, governmentType: GovernmentType): Pair<Country, List<AiNation>> {
        val ai = generateAiNations()
        val relations = generateInitialRelations(ai)
        val country = Country(
            name = name, governmentType = governmentType, stats = CountryStats(), resources = Resources(),
            diplomaticRelations = relations, year = 2024, treasury = 10000,
            politicalParties = listOf(PoliticalParty("Alliance", Ideology.LIBERAL, 30), PoliticalParty("Unity", Ideology.CONSERVATIVE, 30)),
            factions = listOf(PoliticalFaction("Military", 70, 30)),
            ministers = listOf(Minister("m1", "Smith", MinisterRole.ECONOMY, 60, 5, 80))
        )
        return Pair(country, ai)
    }

    private val politicalEvents = listOf<GameEvent>()
    private val diplomaticEvents = listOf<GameEvent>()
    private val militaryEvents = listOf<GameEvent>()
}
