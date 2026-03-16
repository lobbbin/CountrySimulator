package com.countrysimulator.game.domain

object GameLogic {

    private val nations = listOf(
        "United Republic", "Eastern Empire", "Western Alliance", "Northern Federation",
        "Southern Kingdom", "Central Union", "Pacific Coalition", "Atlantic Dominion"
    )

    private fun generateDiplomaticRelations(): List<DiplomaticRelation> {
        return nations.take(4).map { nation ->
            DiplomaticRelation(
                nationName = nation,
                relation = (20..80).random(),
                tradeAgreement = (1..10).random() <= 2,
                militaryAlliance = (1..20).random() == 1,
                war = false
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
                    Pair(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
                },
                EventOption("Save Treasury", "Bank the profits") { stats, treasury, resources ->
                    Pair(stats, treasury + 2000, resources)
                },
                EventOption("Distribute Wealth", "Boost citizen morale") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 10), treasury, resources)
                }
            )
        ),
        GameEvent(
            id = "natural_disaster",
            title = "Natural Disaster",
            description = "A powerful earthquake has struck! Infrastructure damaged.",
            category = EventCategory.DISASTER,
            severity = EventSeverity.MAJOR,
            effect = { stats -> stats.copy(stability = (stats.stability - 15).coerceAtLeast(0), population = (stats.population - 50000).coerceAtLeast(1)) },
            options = listOf(
                EventOption("Emergency Aid", "Deploy resources") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 5), treasury - 1500, resources)
                },
                EventOption("Focus on Military", "Secure the nation") { stats, treasury, resources ->
                    Pair(stats.copy(military = stats.military + 10), treasury - 500, resources)
                },
                EventOption("Request International Aid", "Ask for help") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 5), treasury + 500, resources)
                }
            )
        ),
        GameEvent(
            id = "scientific_breakthrough",
            title = "Scientific Breakthrough",
            description = "Your scientists have made a major discovery!",
            category = EventCategory.SCIENTIFIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(technology = (stats.technology + 15).coerceAtMost(100)) },
            options = listOf(
                EventOption("Patent Technology", "Commercialize it") { stats, treasury, resources ->
                    Pair(stats.copy(economy = stats.economy + 10), treasury + 3000, resources)
                },
                EventOption("Share with World", "Build relations") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 10), treasury, resources)
                },
                EventOption("Military Application", "Weaponize it") { stats, treasury, resources ->
                    Pair(stats.copy(military = stats.military + 15), treasury - 1000, resources)
                }
            )
        ),
        GameEvent(
            id = "war_declaration",
            title = "War Declaration",
            description = "A neighboring nation has declared war!",
            category = EventCategory.MILITARY,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(military = (stats.military - 10).coerceAtLeast(0), happiness = (stats.happiness - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Fight Back", "Mobilize forces") { stats, treasury, resources ->
                    val won = stats.military > 40
                    if (won) Pair(stats.copy(military = stats.military + 20, economy = stats.economy + 10), treasury - 2000, resources)
                    else Pair(stats.copy(military = stats.military - 10, stability = stats.stability - 20), treasury - 3000, resources)
                },
                EventOption("Negotiate Peace", "Seek diplomacy") { stats, treasury, resources ->
                    Pair(stats.copy(economy = stats.economy - 10, stability = stats.stability + 5), treasury - 1000, resources)
                },
                EventOption("Surrender", "Accept defeat") { stats, treasury, resources ->
                    Pair(stats.copy(military = 10, economy = stats.economy - 20), treasury - 5000, resources)
                }
            )
        ),
        GameEvent(
            id = "political_unrest",
            title = "Political Unrest",
            description = "Citizens are protesting in the streets demanding change.",
            category = EventCategory.POLITICAL,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(happiness = (stats.happiness - 15).coerceAtLeast(0), stability = (stats.stability - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Grant Reforms", "Meet demands") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 20, economy = stats.economy - 10), treasury - 1500, resources)
                },
                EventOption("Suppress Protests", "Use force") { stats, treasury, resources ->
                    Pair(stats.copy(military = stats.military + 10, happiness = stats.happiness - 10), treasury - 500, resources)
                },
                EventOption("Hold Elections", "Democratic solution") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 15, happiness = stats.happiness + 5), treasury - 800, resources)
                }
            )
        ),
        GameEvent(
            id = "trade_agreement",
            title = "Trade Agreement",
            description = "Foreign powers want to establish trade routes.",
            category = EventCategory.ECONOMIC,
            severity = EventSeverity.MINOR,
            effect = { stats -> stats.copy(economy = (stats.economy + 8).coerceAtMost(100)) },
            options = listOf(
                EventOption("Accept Deal", "Open markets") { stats, treasury, resources ->
                    Pair(stats.copy(economy = stats.economy + 15), treasury + 2500, resources.copy(materials = (resources.materials + 20).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Decline", "Stay isolated") { stats, treasury, resources ->
                    Pair(stats, treasury, resources)
                },
                EventOption("Negotiate Better Terms", "Push for more") { stats, treasury, resources ->
                    Pair(stats.copy(economy = stats.economy + 10), treasury + 1500, resources)
                }
            )
        ),
        GameEvent(
            id = "pandemic_outbreak",
            title = "Pandemic Outbreak",
            description = "A deadly disease is spreading across your nation.",
            category = EventCategory.DISASTER,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(population = (stats.population - 100000).coerceAtLeast(1), happiness = (stats.happiness - 15).coerceAtLeast(0), healthcare = (stats.healthcare - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Lockdown Measures", "Close everything") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 10, economy = stats.economy - 10), treasury - 3000, resources)
                },
                EventOption("Keep Economy Open", "Prioritize business") { stats, treasury, resources ->
                    Pair(stats.copy(population = (stats.population - 150000).coerceAtLeast(1), economy = stats.economy + 10), treasury - 1000, resources)
                },
                EventOption("Medical Response", "Focus on healthcare") { stats, treasury, resources ->
                    Pair(stats.copy(healthcare = stats.healthcare + 10, population = (stats.population - 50000).coerceAtLeast(1)), treasury - 2500, resources.copy(energy = (resources.energy - 20).coerceAtLeast(0)))
                }
            )
        ),
        GameEvent(
            id = "cultural_festival",
            title = "Cultural Festival",
            description = "A national festival boosts citizen morale!",
            category = EventCategory.CULTURAL,
            severity = EventSeverity.MINOR,
            effect = { stats -> stats.copy(happiness = (stats.happiness + 10).coerceAtMost(100), stability = stats.stability + 5) },
            options = listOf(
                EventOption("Grand Celebration", "Spare no expense") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 15), treasury - 2000, resources)
                },
                EventOption("Modest Event", "Keep it simple") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 5), treasury - 500, resources)
                },
                EventOption("International Festival", "Invite foreigners") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 10, economy = stats.economy + 5), treasury - 1500, resources)
                }
            )
        ),
        GameEvent(
            id = "espionage_discovery",
            title = "Espionage Discovery",
            description = "Foreign spies have been caught in your country!",
            category = EventCategory.DIPLOMATIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(technology = (stats.technology - 5).coerceAtLeast(0), stability = stats.stability - 5) },
            options = listOf(
                EventOption("Execute Spies", "Show no mercy") { stats, treasury, resources ->
                    Pair(stats.copy(military = stats.military + 15), treasury, resources)
                },
                EventOption("Exchange for Prisoners", "Diplomatic solution") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 10), treasury + 1000, resources)
                },
                EventOption("Turn Them", "Use as double agents") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 10, technology = stats.technology + 5), treasury - 500, resources)
                }
            )
        ),
        GameEvent(
            id = "resource_discovery",
            title = "Resource Discovery",
            description = "Valuable resources have been found in your territory!",
            category = EventCategory.ECONOMIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(economy = (stats.economy + 12).coerceAtMost(100)) },
            options = listOf(
                EventOption("Extract Quickly", "Maximum short-term gain") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology - 5, economy = stats.economy + 20, environment = stats.environment - 10), treasury + 5000, resources.copy(materials = (resources.materials + 50).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Sustainable Mining", "Long-term planning") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5, economy = stats.economy + 10), treasury + 2000, resources.copy(materials = (resources.materials + 30).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Research First", "Study the deposits") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 10), treasury - 500, resources.copy(materials = (resources.materials + 15).coerceAtMost(resources.maxMaterials)))
                }
            )
        ),
        GameEvent(
            id = "education_reform",
            title = "Education Reform",
            description = "Your education system needs modernization.",
            category = EventCategory.SOCIAL,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(education = (stats.education + 10).coerceAtMost(100)) },
            options = listOf(
                EventOption("Invest Heavily", "Build universities") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 15, economy = stats.economy - 5), treasury - 3000, resources)
                },
                EventOption("Modest Improvements", "Basic reforms") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
                },
                EventOption("Focus on Vocational", "Trade schools") { stats, treasury, resources ->
                    Pair(stats.copy(economy = stats.economy + 10, technology = stats.technology + 3), treasury - 1500, resources)
                }
            )
        ),
        GameEvent(
            id = "healthcare_crisis",
            title = "Healthcare Crisis",
            description = "A new disease is affecting your population.",
            category = EventCategory.SOCIAL,
            severity = EventSeverity.MAJOR,
            effect = { stats -> stats.copy(healthcare = (stats.healthcare - 15).coerceAtLeast(0), population = (stats.population - 30000).coerceAtLeast(1), happiness = (stats.happiness - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Universal Healthcare", "Free for all") { stats, treasury, resources ->
                    Pair(stats.copy(healthcare = stats.healthcare + 20, happiness = stats.happiness + 10), treasury - 2500, resources.copy(energy = (resources.energy - 15).coerceAtLeast(0)))
                },
                EventOption("Private Sector", "Let markets handle it") { stats, treasury, resources ->
                    Pair(stats.copy(healthcare = stats.healthcare + 5, economy = stats.economy + 5), treasury - 500, resources)
                },
                EventOption("Research Cure", "Find the source") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 10, healthcare = stats.healthcare + 10), treasury - 2000, resources)
                }
            )
        ),
        GameEvent(
            id = "environmental_disaster",
            title = "Environmental Disaster",
            description = "Industrial pollution has caused a major environmental crisis.",
            category = EventCategory.ENVIRONMENTAL,
            severity = EventSeverity.MAJOR,
            effect = { stats -> stats.copy(environment = (stats.environment - 20).coerceAtLeast(0), population = (stats.population - 20000).coerceAtLeast(1), happiness = (stats.happiness - 10).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Green Initiative", "Clean up everything") { stats, treasury, resources ->
                    Pair(stats.copy(environment = stats.environment + 20, economy = stats.economy - 10), treasury - 3500, resources)
                },
                EventOption("Continue Industrialization", "Progress over environment") { stats, treasury, resources ->
                    Pair(stats.copy(environment = stats.environment - 10, economy = stats.economy + 15), treasury + 1000, resources)
                },
                EventOption("Compromise", "Balanced approach") { stats, treasury, resources ->
                    Pair(stats.copy(environment = stats.environment + 5, economy = stats.economy + 5), treasury - 1500, resources)
                }
            )
        ),
        GameEvent(
            id = "crime_wave",
            title = "Crime Wave",
            description = "Organized crime is threatening your nation.",
            category = EventCategory.SOCIAL,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(crime = (stats.crime + 15).coerceAtMost(100), economy = (stats.economy - 10).coerceAtLeast(0), happiness = (stats.happiness - 5).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Tough on Crime", "Increase police") { stats, treasury, resources ->
                    Pair(stats.copy(crime = (stats.crime - 20).coerceAtLeast(0), military = stats.military + 5), treasury - 2000, resources)
                },
                EventOption("Rehabilitation", "Focus on prevention") { stats, treasury, resources ->
                    Pair(stats.copy(crime = (stats.crime - 10).coerceAtLeast(0), happiness = stats.happiness + 5), treasury - 1500, resources)
                },
                EventOption("Legalize Some Crimes", "Regulate vice") { stats, treasury, resources ->
                    Pair(stats.copy(crime = (stats.crime - 5).coerceAtLeast(0), economy = stats.economy + 10), treasury + 2000, resources)
                }
            )
        ),
        GameEvent(
            id = "religious_movement",
            title = "Religious Movement",
            description = "A new religious movement is gaining followers.",
            category = EventCategory.CULTURAL,
            severity = EventSeverity.MINOR,
            effect = { stats -> stats.copy(stability = (stats.stability + 5).coerceAtMost(100), happiness = (stats.happiness + 5).coerceAtMost(100)) },
            options = listOf(
                EventOption("Embrace It", "State religion") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 15, happiness = stats.happiness + 10, technology = (stats.technology - 5).coerceAtLeast(0)), treasury - 500, resources)
                },
                EventOption("Separate Church and State", "Secular approach") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 5), treasury, resources)
                },
                EventOption("Suppress It", "Ban the movement") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability - 10, happiness = stats.happiness - 10), treasury - 500, resources)
                }
            )
        ),
        GameEvent(
            id = "tech_company_arrival",
            title = "Tech Giant Arrival",
            description = "A major technology company wants to build facilities.",
            category = EventCategory.ECONOMIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(technology = (stats.technology + 10).coerceAtMost(100), economy = (stats.economy + 5).coerceAtMost(100)) },
            options = listOf(
                EventOption("Welcome Them", "Tax breaks") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 15, economy = stats.economy + 10, environment = (stats.environment - 5).coerceAtLeast(0)), treasury - 2000, resources.copy(energy = (resources.energy - 20).coerceAtLeast(0)))
                },
                EventOption("Strict Regulations", "Protect citizens") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5, stability = stats.stability + 5), treasury + 500, resources)
                },
                EventOption("Reject Offer", "Keep independence") { stats, treasury, resources ->
                    Pair(stats, treasury, resources)
                }
            )
        ),
        GameEvent(
            id = "immigration_wave",
            title = "Immigration Wave",
            description = "People are fleeing a neighboring conflict zone.",
            category = EventCategory.DIPLOMATIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(population = (stats.population + 50000).coerceAtMost(100000000), economy = (stats.economy + 5).coerceAtMost(100), happiness = (stats.happiness - 5).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Open Borders", "Accept everyone") { stats, treasury, resources ->
                    Pair(stats.copy(population = (stats.population + 100000).coerceAtMost(100000000), economy = stats.economy + 10, happiness = (stats.happiness - 5).coerceAtLeast(0)), treasury - 1500, resources.copy(food = (resources.food - 30).coerceAtLeast(0)))
                },
                EventOption("Selective Immigration", "Skilled workers only") { stats, treasury, resources ->
                    Pair(stats.copy(population = (stats.population + 50000).coerceAtMost(100000000), technology = stats.technology + 5, economy = stats.economy + 5), treasury - 1000, resources.copy(food = (resources.food - 15).coerceAtLeast(0)))
                },
                EventOption("Close Borders", "No entry") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 5), treasury + 500, resources)
                }
            )
        ),
        GameEvent(
            id = "famine",
            title = "Famine",
            description = "Crop failures have caused widespread food shortages.",
            category = EventCategory.DISASTER,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(population = (stats.population - 80000).coerceAtLeast(1), happiness = (stats.happiness - 20).coerceAtLeast(0), stability = (stats.stability - 15).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Import Food", "Buy from abroad") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 5, population = (stats.population + 20000).coerceAtMost(100000000)), treasury - 4000, resources.copy(food = resources.maxFood))
                },
                EventOption("Rationing", "Fair distribution") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 10, happiness = stats.happiness - 5), treasury - 1000, resources.copy(food = (resources.food / 2).coerceAtLeast(10)))
                },
                EventOption("Let Market Decide", "Survival of fittest") { stats, treasury, resources ->
                    Pair(stats.copy(population = (stats.population - 50000).coerceAtLeast(1), economy = stats.economy + 5), treasury + 1000, resources.copy(food = (resources.food / 3).coerceAtLeast(10)))
                }
            )
        ),
        GameEvent(
            id = "energy_crisis",
            title = "Energy Crisis",
            description = "Your power grids are failing due to resource depletion.",
            category = EventCategory.ENVIRONMENTAL,
            severity = EventSeverity.MAJOR,
            effect = { stats -> stats.copy(energy = (stats.energy - 20).coerceAtLeast(0), economy = (stats.economy - 10).coerceAtLeast(0), stability = (stats.stability - 5).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Build Nuclear Plant", "Long-term solution") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 10), treasury - 5000, resources.copy(energy = resources.maxEnergy, materials = (resources.materials - 30).coerceAtLeast(0)))
                },
                EventOption("Invest in Renewables", "Green energy") { stats, treasury, resources ->
                    Pair(stats.copy(environment = stats.environment + 10, technology = stats.technology + 5), treasury - 3500, resources.copy(energy = (resources.energy + 50).coerceAtMost(resources.maxEnergy)))
                },
                EventOption("Drill for More", "化石 fuels") { stats, treasury, resources ->
                    Pair(stats.copy(environment = (stats.environment - 10).coerceAtLeast(0)), treasury - 1500, resources.copy(energy = resources.maxEnergy))
                }
            )
        ),
        GameEvent(
            id = "coup_attempt",
            title = "Coup Attempt",
            description = "Military officers are attempting a coup!",
            category = EventCategory.POLITICAL,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(stability = (stats.stability - 25).coerceAtLeast(0), military = (stats.military - 10).coerceAtLeast(0), happiness = (stats.happiness - 15).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Loyal Forces", "Fight back") { stats, treasury, resources ->
                    val success = stats.military > 50
                    if (success) Pair(stats.copy(stability = stats.stability + 20, military = stats.military + 10), treasury - 2000, resources)
                    else Pair(stats.copy(stability = 10, military = 20), treasury - 5000, resources)
                },
                EventOption("Negotiate", "Share power") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 5, happiness = stats.happiness + 5), treasury - 3000, resources)
                },
                EventOption("Flee Country", "Escape to exile") { stats, treasury, resources ->
                    Pair(stats.copy(stability = 5, happiness = 20), treasury - 10000, resources)
                }
            )
        ),
        GameEvent(
            id = "space_program",
            title = "Space Program",
            description = "Your nation is ready to reach for the stars.",
            category = EventCategory.SCIENTIFIC,
            severity = EventSeverity.MINOR,
            effect = { stats -> stats.copy(technology = (stats.technology + 8).coerceAtMost(100)) },
            options = listOf(
                EventOption("Moon Landing", "Bold initiative") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 20, stability = stats.stability + 10), treasury - 8000, resources.copy(materials = (resources.materials - 30).coerceAtLeast(0)))
                },
                EventOption("Satellite Network", "Practical benefits") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 10, economy = stats.economy + 10), treasury - 3000, resources)
                },
                EventOption("Research Only", "Stay grounded") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
                }
            )
        ),
        GameEvent(
            id = "revolution",
            title = "Revolution",
            description = "The people demand radical change!",
            category = EventCategory.POLITICAL,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(stability = (stats.stability - 30).coerceAtLeast(0), happiness = (stats.happiness - 20).coerceAtLeast(0), economy = (stats.economy - 15).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Step Down", "Allow revolution") { stats, treasury, resources ->
                    Pair(stats.copy(stability = 30, happiness = 70, governmentType = GovernmentType.DEMOCRACY), treasury, resources)
                },
                EventOption("Crush Rebellion", "Brutal suppression") { stats, treasury, resources ->
                    Pair(stats.copy(military = stats.military + 20, stability = 80, happiness = 10), treasury - 3000, resources)
                },
                EventOption("Reform Government", "Meet in middle") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 20, happiness = stats.happiness + 15, economy = stats.economy - 5), treasury - 2000, resources)
                }
            )
        ),
        GameEvent(
            id = "foreign_aid",
            title = "Foreign Aid Offer",
            description = "International organizations offer development assistance.",
            category = EventCategory.DIPLOMATIC,
            severity = EventSeverity.MINOR,
            effect = { stats -> stats.copy(economy = (stats.economy + 5).coerceAtMost(100), stability = (stats.stability + 5).coerceAtMost(100)) },
            options = listOf(
                EventOption("Accept Aid", "With conditions") { stats, treasury, resources ->
                    Pair(stats.copy(education = stats.education + 10, healthcare = stats.healthcare + 10), treasury + 3000, resources.copy(food = (resources.food + 30).coerceAtMost(resources.maxFood)))
                },
                EventOption("Reject Aid", "Stay independent") { stats, treasury, resources ->
                    Pair(stats.copy(stability = stats.stability + 5), treasury, resources)
                },
                EventOption("Negotiate Better", "More conditions") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5), treasury + 1500, resources)
                }
            )
        ),
        GameEvent(
            id = "artificial_intelligence",
            title = "AI Revolution",
            description = "Artificial intelligence is transforming your society.",
            category = EventCategory.SCIENTIFIC,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(technology = (stats.technology + 12).coerceAtMost(100), economy = (stats.economy + 5).coerceAtMost(100)) },
            options = listOf(
                EventOption("Embrace AI", "Lead the revolution") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 20, economy = stats.economy + 15, happiness = (stats.happiness - 5).coerceAtLeast(0)), treasury - 2500, resources)
                },
                EventOption("Regulate Heavily", "Protect jobs") { stats, treasury, resources ->
                    Pair(stats.copy(technology = stats.technology + 5, happiness = stats.happiness + 10), treasury - 1000, resources)
                },
                EventOption("Ban AI", "Traditional approach") { stats, treasury, resources ->
                    Pair(stats.copy(happiness = stats.happiness + 5, stability = stats.stability + 5), treasury, resources)
                }
            )
        ),
        GameEvent(
            id = "civil_war",
            title = "Civil War",
            description = "The nation has split into factions at war!",
            category = EventCategory.POLITICAL,
            severity = EventSeverity.CATASTROPHIC,
            effect = { stats -> stats.copy(population = (stats.population - 200000).coerceAtLeast(1), economy = (stats.economy - 30).coerceAtLeast(0), stability = 0) },
            options = listOf(
                EventOption("Win Civil War", "Unify the nation") { stats, treasury, resources ->
                    Pair(stats.copy(stability = 50, military = stats.military + 20, economy = stats.economy - 10), treasury - 8000, resources)
                },
                EventOption("Lose Power", "New government") { stats, treasury, resources ->
                    Pair(stats.copy(stability = 30, governmentType = GovernmentType.DEMOCRACY, happiness = 40), treasury - 3000, resources)
                },
                EventOption("Fragment", "Break into states") { stats, treasury, resources ->
                    Pair(stats.copy(stability = 20, economy = stats.economy - 20, population = (stats.population / 2)), treasury - 5000, resources)
                }
            )
        )
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
        val foodChange = when {
            stats.economy > 70 -> 15
            stats.economy > 40 -> 10
            stats.economy > 20 -> 5
            else -> 0
        } - (stats.population / 100000)

        val energyChange = when {
            stats.technology > 70 -> 20
            stats.technology > 40 -> 15
            stats.technology > 20 -> 10
            else -> 5
        } - (stats.population / 200000)

        val materialsChange = when {
            stats.economy > 60 -> 10
            stats.economy > 30 -> 5
            else -> 2
        }

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

    fun processTurn(country: Country): GameState {
        val income = calculateTurnIncome(country)
        var newTreasury = country.treasury + income
        var newStats = country.stats.copy()
        var newResources = calculateResourceProduction(country.resources, newStats)

        newStats = getGovernmentBonus(country.governmentType)(newStats)

        val eventRoll = (1..100).random()
        val eventThreshold = 100 - newStats.stability + 20
        val event = if (eventRoll <= eventThreshold && events.isNotEmpty()) {
            val availableEvents = events.filter { event ->
                event.prerequisites == null || event.prerequisites.invoke(country)
            }
            if (availableEvents.isNotEmpty()) availableEvents.random() else null
        } else null

        event?.let {
            newStats = it.effect(newStats)
        }

        val populationGrowth = when {
            newStats.healthcare > 70 -> 0.015
            newStats.healthcare > 40 -> 0.01
            newStats.healthcare > 20 -> 0.008
            else -> 0.005
        }
        val hungerFactor = if (newResources.food < 30) -0.02 else 0.0

        newStats = newStats.copy(
            population = (newStats.population * (1 + populationGrowth + hungerFactor)).toInt().coerceAtMost(100000000),
            economy = (newStats.economy + (1..3).random()).coerceAtMost(100),
            military = (newStats.military + (-2..3).random()).coerceAtMost(100).coerceAtLeast(0),
            happiness = (newStats.happiness + (-3..3).random()).coerceAtMost(100).coerceAtLeast(0),
            stability = (newStats.stability + (-2..2).random()).coerceAtMost(100).coerceAtLeast(0),
            technology = (newStats.technology + (0..2).random()).coerceAtMost(100),
            education = (newStats.education + (0..1).random()).coerceAtMost(100),
            healthcare = (newStats.healthcare + (-1..1).random()).coerceAtMost(100).coerceAtLeast(0),
            environment = (newStats.environment + (-2..1).random()).coerceAtMost(100).coerceAtLeast(0),
            crime = (newStats.crime + (-2..2).random()).coerceAtMost(100).coerceAtLeast(0)
        )

        val newCountry = country.copy(
            stats = newStats,
            resources = newResources,
            treasury = newTreasury,
            year = country.year + 1,
            turnCount = country.turnCount + 1
        )

        val gameOverReason = checkGameOver(newCountry)

        val newEventHistory = mutableListOf<String>()
        event?.let {
            newEventHistory.add("Year ${country.year + 1}: ${it.title}")
        }
        newEventHistory.addAll(country.turnCount.coerceAtMost(9).let { country.eventHistory.take(it) })

        val headline = when {
            event != null && event.severity == EventSeverity.CATASTROPHIC -> "BREAKING: ${event.title} rocks the nation!"
            event != null && event.severity == EventSeverity.MAJOR -> "MAJOR: ${event.title} impacts citizens"
            event != null -> "NEWS: ${event.title} develops"
            income > 5000 -> "ECONOMY: Record growth this quarter"
            newStats.happiness > 80 -> "MORALE: Citizens celebrating banner year"
            newStats.technology > 80 -> "TECH: Nation leads the world in innovation"
            newResources.food < 30 -> "ALERT: Food shortage warning issued"
            else -> null
        }

        return GameState(
            country = newCountry.copy(eventHistory = newEventHistory),
            isGameOver = gameOverReason != null,
            gameOverReason = gameOverReason,
            lastEvent = event,
            eventHistory = newEventHistory,
            newsHeadline = headline
        )
    }

    fun getRandomEvent(): GameEvent = events.random()

    fun generateInitialCountry(name: String, governmentType: GovernmentType): Country {
        return Country(
            name = name,
            governmentType = governmentType,
            stats = CountryStats(),
            resources = Resources(),
            diplomaticRelations = generateDiplomaticRelations(),
            year = 2024,
            treasury = 10000
        )
    }
}
