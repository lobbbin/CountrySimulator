package com.countrysimulator.game.domain

object GameLogic {

    private val nationNamesPrefixes = listOf("United", "Republic of", "Kingdom of", "Empire of", "Federation of", "People's Union of", "Grand Duchy of")
    private val nationNamesBases = listOf("Arstotzka", "Borginia", "Calisota", "Dinotopia", "Equestria", "Florin", "Genosha", "Hyrule", "Ishval", "Jalabad", "Krakozhia", "Latveria", "Moldavia", "Narnia", "Osterlich", "Panem", "Qumar", "Ruritania", "Sokovia", "Wakanda")

    fun generateAiNations(count: Int = 8): List<AiNation> {
        return (1..count).map {
            val name = "${nationNamesPrefixes.random()} ${nationNamesBases.random()}"
            val personality = AiPersonality.values().random()
            val govType = GovernmentType.values().random()
            
            // Base stats modified by personality
            val baseMilitary = if (personality == AiPersonality.AGGRESSIVE) 60 else 30
            val baseEconomy = if (personality == AiPersonality.TRADER) 60 else 40
            val baseTech = if (personality == AiPersonality.SCIENTIFIC) 50 else 20

            AiNation(
                id = "ai_$it",
                name = name,
                governmentType = govType,
                personality = personality,
                stats = CountryStats(
                    military = baseMilitary + (-10..10).random(),
                    economy = baseEconomy + (-10..10).random(),
                    technology = baseTech + (-5..5).random(),
                    population = (500000..5000000).random()
                ),
                treasury = (2000..8000).random()
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
                    Triple(stats.copy(happiness = stats.happiness + 5), treasury - 1500, resources)
                },
                EventOption("Focus on Military", "Secure the nation") { stats, treasury, resources ->
                    Triple(stats.copy(military = stats.military + 10), treasury - 500, resources)
                },
                EventOption("Request International Aid", "Ask for help") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 5), treasury + 500, resources)
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
                    Triple(stats.copy(economy = stats.economy + 10), treasury + 3000, resources)
                },
                EventOption("Share with World", "Build relations") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 10), treasury, resources)
                },
                EventOption("Military Application", "Weaponize it") { stats, treasury, resources ->
                    Triple(stats.copy(military = stats.military + 15), treasury - 1000, resources)
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
                    if (won) Triple(stats.copy(military = stats.military + 20, economy = stats.economy + 10), treasury - 2000, resources)
                    else Triple(stats.copy(military = stats.military - 10, stability = stats.stability - 20), treasury - 3000, resources)
                },
                EventOption("Negotiate Peace", "Seek diplomacy") { stats, treasury, resources ->
                    Triple(stats.copy(economy = stats.economy - 10, stability = stats.stability + 5), treasury - 1000, resources)
                },
                EventOption("Surrender", "Accept defeat") { stats, treasury, resources ->
                    Triple(stats.copy(military = 10, economy = stats.economy - 20), treasury - 5000, resources)
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
                    Triple(stats.copy(happiness = stats.happiness + 20, economy = stats.economy - 10), treasury - 1500, resources)
                },
                EventOption("Suppress Protests", "Use force") { stats, treasury, resources ->
                    Triple(stats.copy(military = stats.military + 10, happiness = stats.happiness - 10), treasury - 500, resources)
                },
                EventOption("Hold Elections", "Democratic solution") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 15, happiness = stats.happiness + 5), treasury - 800, resources)
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
                    Triple(stats.copy(economy = stats.economy + 15), treasury + 2500, resources.copy(materials = (resources.materials + 20).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Decline", "Stay isolated") { stats, treasury, resources ->
                    Triple(stats, treasury, resources)
                },
                EventOption("Negotiate Better Terms", "Push for more") { stats, treasury, resources ->
                    Triple(stats.copy(economy = stats.economy + 10), treasury + 1500, resources)
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
                    Triple(stats.copy(stability = stats.stability + 10, economy = stats.economy - 10), treasury - 3000, resources)
                },
                EventOption("Keep Economy Open", "Prioritize business") { stats, treasury, resources ->
                    Triple(stats.copy(population = (stats.population - 150000).coerceAtLeast(1), economy = stats.economy + 10), treasury - 1000, resources)
                },
                EventOption("Medical Response", "Focus on healthcare") { stats, treasury, resources ->
                    Triple(stats.copy(healthcare = stats.healthcare + 10, population = (stats.population - 50000).coerceAtLeast(1)), treasury - 2500, resources.copy(energy = (resources.energy - 20).coerceAtLeast(0)))
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
                    Triple(stats.copy(happiness = stats.happiness + 15), treasury - 2000, resources)
                },
                EventOption("Modest Event", "Keep it simple") { stats, treasury, resources ->
                    Triple(stats.copy(happiness = stats.happiness + 5), treasury - 500, resources)
                },
                EventOption("International Festival", "Invite foreigners") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 10, economy = stats.economy + 5), treasury - 1500, resources)
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
                    Triple(stats.copy(military = stats.military + 15), treasury, resources)
                },
                EventOption("Exchange for Prisoners", "Diplomatic solution") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 10), treasury + 1000, resources)
                },
                EventOption("Turn Them", "Use as double agents") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 10, technology = stats.technology + 5), treasury - 500, resources)
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
                    Triple(stats.copy(technology = stats.technology - 5, economy = stats.economy + 20, environment = stats.environment - 10), treasury + 5000, resources.copy(materials = (resources.materials + 50).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Sustainable Mining", "Long-term planning") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5, economy = stats.economy + 10), treasury + 2000, resources.copy(materials = (resources.materials + 30).coerceAtMost(resources.maxMaterials)))
                },
                EventOption("Research First", "Study the deposits") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 10), treasury - 500, resources.copy(materials = (resources.materials + 15).coerceAtMost(resources.maxMaterials)))
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
                    Triple(stats.copy(technology = stats.technology + 15, economy = stats.economy - 5), treasury - 3000, resources)
                },
                EventOption("Modest Improvements", "Basic reforms") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
                },
                EventOption("Focus on Vocational", "Trade schools") { stats, treasury, resources ->
                    Triple(stats.copy(economy = stats.economy + 10, technology = stats.technology + 3), treasury - 1500, resources)
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
                    Triple(stats.copy(healthcare = stats.healthcare + 20, happiness = stats.happiness + 10), treasury - 2500, resources.copy(energy = (resources.energy - 15).coerceAtLeast(0)))
                },
                EventOption("Private Sector", "Let markets handle it") { stats, treasury, resources ->
                    Triple(stats.copy(healthcare = stats.healthcare + 5, economy = stats.economy + 5), treasury - 500, resources)
                },
                EventOption("Research Cure", "Find the source") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 10, healthcare = stats.healthcare + 10), treasury - 2000, resources)
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
                    Triple(stats.copy(environment = stats.environment + 20, economy = stats.economy - 10), treasury - 3500, resources)
                },
                EventOption("Continue Industrialization", "Progress over environment") { stats, treasury, resources ->
                    Triple(stats.copy(environment = stats.environment - 10, economy = stats.economy + 15), treasury + 1000, resources)
                },
                EventOption("Compromise", "Balanced approach") { stats, treasury, resources ->
                    Triple(stats.copy(environment = stats.environment + 5, economy = stats.economy + 5), treasury - 1500, resources)
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
                    Triple(stats.copy(crime = (stats.crime - 20).coerceAtLeast(0), military = stats.military + 5), treasury - 2000, resources)
                },
                EventOption("Rehabilitation", "Focus on prevention") { stats, treasury, resources ->
                    Triple(stats.copy(crime = (stats.crime - 10).coerceAtLeast(0), happiness = stats.happiness + 5), treasury - 1500, resources)
                },
                EventOption("Legalize Some Crimes", "Regulate vice") { stats, treasury, resources ->
                    Triple(stats.copy(crime = (stats.crime - 5).coerceAtLeast(0), economy = stats.economy + 10), treasury + 2000, resources)
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
                    Triple(stats.copy(stability = stats.stability + 15, happiness = stats.happiness + 10, technology = (stats.technology - 5).coerceAtLeast(0)), treasury - 500, resources)
                },
                EventOption("Separate Church and State", "Secular approach") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 5), treasury, resources)
                },
                EventOption("Suppress It", "Ban the movement") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability - 10, happiness = stats.happiness - 10), treasury - 500, resources)
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
                    Triple(stats.copy(technology = stats.technology + 15, economy = stats.economy + 10, environment = (stats.environment - 5).coerceAtLeast(0)), treasury - 2000, resources.copy(energy = (resources.energy - 20).coerceAtLeast(0)))
                },
                EventOption("Strict Regulations", "Protect citizens") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5, stability = stats.stability + 5), treasury + 500, resources)
                },
                EventOption("Reject Offer", "Keep independence") { stats, treasury, resources ->
                    Triple(stats, treasury, resources)
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
                    Triple(stats.copy(population = (stats.population + 100000).coerceAtMost(100000000), economy = stats.economy + 10, happiness = (stats.happiness - 5).coerceAtLeast(0)), treasury - 1500, resources.copy(food = (resources.food - 30).coerceAtLeast(0)))
                },
                EventOption("Selective Immigration", "Skilled workers only") { stats, treasury, resources ->
                    Triple(stats.copy(population = (stats.population + 50000).coerceAtMost(100000000), technology = stats.technology + 5, economy = stats.economy + 5), treasury - 1000, resources.copy(food = (resources.food - 15).coerceAtLeast(0)))
                },
                EventOption("Close Borders", "No entry") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 5), treasury + 500, resources)
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
                    Triple(stats.copy(happiness = stats.happiness + 5, population = (stats.population + 20000).coerceAtMost(100000000)), treasury - 4000, resources.copy(food = resources.maxFood))
                },
                EventOption("Rationing", "Fair distribution") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 10, happiness = stats.happiness - 5), treasury - 1000, resources.copy(food = (resources.food / 2).coerceAtLeast(10)))
                },
                EventOption("Let Market Decide", "Survival of fittest") { stats, treasury, resources ->
                    Triple(stats.copy(population = (stats.population - 50000).coerceAtLeast(1), economy = stats.economy + 5), treasury + 1000, resources.copy(food = (resources.food / 3).coerceAtLeast(10)))
                }
            )
        ),
        GameEvent(
            id = "energy_crisis",
            title = "Energy Crisis",
            description = "Your power grids are failing due to resource depletion.",
            category = EventCategory.ENVIRONMENTAL,
            severity = EventSeverity.MAJOR,
            effect = { stats -> stats.copy(economy = (stats.economy - 10).coerceAtLeast(0), stability = (stats.stability - 5).coerceAtLeast(0)) },
            options = listOf(
                EventOption("Build Nuclear Plant", "Long-term solution") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 10), treasury - 5000, resources.copy(energy = resources.maxEnergy, materials = (resources.materials - 30).coerceAtLeast(0)))
                },
                EventOption("Invest in Renewables", "Green energy") { stats, treasury, resources ->
                    Triple(stats.copy(environment = stats.environment + 10, technology = stats.technology + 5), treasury - 3500, resources.copy(energy = (resources.energy + 50).coerceAtMost(resources.maxEnergy)))
                },
                EventOption("Drill for More", "Fossil fuels") { stats, treasury, resources ->
                    Triple(stats.copy(environment = (stats.environment - 10).coerceAtLeast(0)), treasury - 1500, resources.copy(energy = resources.maxEnergy))
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
                    if (success) Triple(stats.copy(stability = stats.stability + 20, military = stats.military + 10), treasury - 2000, resources)
                    else Triple(stats.copy(stability = 10, military = 20), treasury - 5000, resources)
                },
                EventOption("Negotiate", "Share power") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 5, happiness = stats.happiness + 5), treasury - 3000, resources)
                },
                EventOption("Flee Country", "Escape to exile") { stats, treasury, resources ->
                    Triple(stats.copy(stability = 5, happiness = 20), treasury - 10000, resources)
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
                    Triple(stats.copy(technology = stats.technology + 20, stability = stats.stability + 10), treasury - 8000, resources.copy(materials = (resources.materials - 30).coerceAtLeast(0)))
                },
                EventOption("Satellite Network", "Practical benefits") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 10, economy = stats.economy + 10), treasury - 3000, resources)
                },
                EventOption("Research Only", "Stay grounded") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5), treasury - 1000, resources)
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
                    Triple(stats.copy(stability = 30, happiness = 70), treasury, resources)
                },
                EventOption("Crush Rebellion", "Brutal suppression") { stats, treasury, resources ->
                    Triple(stats.copy(military = stats.military + 20, stability = 80, happiness = 10), treasury - 3000, resources)
                },
                EventOption("Reform Government", "Meet in middle") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 20, happiness = stats.happiness + 15, economy = stats.economy - 5), treasury - 2000, resources)
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
                    Triple(stats.copy(education = stats.education + 10, healthcare = stats.healthcare + 10), treasury + 3000, resources.copy(food = (resources.food + 30).coerceAtMost(resources.maxFood)))
                },
                EventOption("Reject Aid", "Stay independent") { stats, treasury, resources ->
                    Triple(stats.copy(stability = stats.stability + 5), treasury, resources)
                },
                EventOption("Negotiate Better", "More conditions") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5), treasury + 1500, resources)
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
                    Triple(stats.copy(technology = stats.technology + 20, economy = stats.economy + 15, happiness = (stats.happiness - 5).coerceAtLeast(0)), treasury - 2500, resources)
                },
                EventOption("Regulate Heavily", "Protect jobs") { stats, treasury, resources ->
                    Triple(stats.copy(technology = stats.technology + 5, happiness = stats.happiness + 10), treasury - 1000, resources)
                },
                EventOption("Ban AI", "Traditional approach") { stats, treasury, resources ->
                    Triple(stats.copy(happiness = stats.happiness + 5, stability = stats.stability + 5), treasury, resources)
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
                    Triple(stats.copy(stability = 50, military = stats.military + 20, economy = stats.economy - 10), treasury - 8000, resources)
                },
                EventOption("Lose Power", "New government") { stats, treasury, resources ->
                    Triple(stats.copy(stability = 30, happiness = 40), treasury - 3000, resources)
                },
                EventOption("Fragment", "Break into states") { stats, treasury, resources ->
                    Triple(stats.copy(stability = 20, economy = stats.economy - 20, population = (stats.population / 2)), treasury - 5000, resources)
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
        val baseIncome = country.stats.population / 10000
        val economyMultiplier = country.stats.economy / 50.0
        val happinessFactor = country.stats.happiness / 100.0
        val techBonus = country.stats.technology / 200.0
        return (baseIncome * economyMultiplier * happinessFactor * (1 + techBonus)).toInt().coerceAtLeast(500)
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

    fun processAiTurn(ai: AiNation, globalMarket: GlobalMarket): AiNation {
        // AI logic: Invest based on personality
        var newTreasury = ai.treasury + (ai.stats.economy * 10) // Simplified income
        var newStats = ai.stats.copy()

        // Random events for AI
        if ((1..10).random() > 8) {
            newStats = newStats.copy(
                economy = (newStats.economy + (-5..5).random()).coerceIn(0, 100),
                stability = (newStats.stability + (-5..5).random()).coerceIn(0, 100)
            )
        }

        // Spending logic
        if (newTreasury > 1000) {
            when (ai.personality) {
                AiPersonality.AGGRESSIVE -> {
                    if (newStats.military < 80) {
                         newStats = newStats.copy(military = newStats.military + 5)
                         newTreasury -= 800
                    }
                }
                AiPersonality.TRADER -> {
                    if (newStats.economy < 80) {
                        newStats = newStats.copy(economy = newStats.economy + 5)
                        newTreasury -= 1000
                    }
                }
                AiPersonality.SCIENTIFIC -> {
                     if (newStats.technology < 80) {
                        newStats = newStats.copy(technology = newStats.technology + 5)
                        newTreasury -= 1500
                    }
                }
                else -> {
                    // Balanced
                    if ((1..2).random() == 1) {
                         newStats = newStats.copy(economy = newStats.economy + 2)
                         newTreasury -= 500
                    }
                }
            }
        }

        return ai.copy(stats = newStats, treasury = newTreasury)
    }

    fun processTurn(currentState: GameState): GameState {
        var country = currentState.country
        
        // 1. Process Election (if any)
        country = processElection(country)

        // 2. Process Player Turn
        val income = calculateTurnIncome(country)
        var newTreasury = country.treasury + income
        var newStats = country.stats.copy()
        
        // Minister Bonuses
        country.ministers.forEach { minister ->
            when (minister.role) {
                MinisterRole.ECONOMY -> newStats = newStats.copy(economy = (newStats.economy + minister.skill / 20).coerceAtMost(100))
                MinisterRole.DEFENSE -> newStats = newStats.copy(military = (newStats.military + minister.skill / 20).coerceAtMost(100))
                MinisterRole.FOREIGN_AFFAIRS -> newStats = newStats.copy(softPower = (newStats.softPower + minister.skill / 20).coerceAtMost(100))
                else -> {}
            }
        }

        // Active Laws effects
        country.activeLaws.filter { it.isActive }.forEach { law ->
            newStats = newStats.copy(
                stability = (newStats.stability + law.stabilityEffect).coerceIn(0, 100),
                economy = (newStats.economy + law.economyEffect).coerceIn(0, 100),
                happiness = (newStats.happiness + law.happinessEffect).coerceIn(0, 100),
                corruption = (newStats.corruption + law.corruptionEffect).coerceIn(0, 100)
            )
            newTreasury -= law.cost / 10 // Cost per turn (maintenance)
        }

        var newResources = calculateResourceProduction(country.resources, newStats)
        newStats = getGovernmentBonus(country.governmentType)(newStats)

        // Corruption effect
        if (newStats.corruption > 50) {
            newTreasury -= (newTreasury * (newStats.corruption / 200.0)).toInt()
            newStats = newStats.copy(stability = (newStats.stability - 2).coerceAtLeast(0))
        }

        // --- New V6.0 Logic ---
        
        // Military & Warfare Processing
        var newMilitary = country.military
        
        // 1. Calculate Military Power from Branches
        val calculatedPower = calculateMilitaryPower(newMilitary)
        newStats = newStats.copy(military = calculatedPower.coerceIn(0, 100))
        
        // 2. Process Nuclear Program
        val nukeResult = processNuclearProgram(newMilitary.nuclearProgram)
        newMilitary = newMilitary.copy(nuclearProgram = nukeResult.first)
        if (nukeResult.second) { // New warhead built
             newStats = newStats.copy(softPower = (newStats.softPower - 5).coerceAtLeast(0)) // International concern
        }
        
        // 3. Process Mercenaries
        val activeMercs = newMilitary.mercenaries.map { it.copy(contractTurnsRemaining = it.contractTurnsRemaining - 1) }.filter { it.contractTurnsRemaining > 0 }
        val mercCost = activeMercs.sumOf { it.costPerTurn }
        newTreasury -= mercCost
        newMilitary = newMilitary.copy(mercenaries = activeMercs)
        
        // 4. Process War Theaters
        val theaterResult = processWarTheaters(newMilitary.warTheaters, newStats.military, currentState.aiNations)
        newMilitary = newMilitary.copy(warTheaters = theaterResult.first)
        val theaterEvents = theaterResult.second
        
        // Sanctions Effect
        val sanctionsPenalty = country.diplomaticRelations.sumOf { rel -> rel.sanctions.size * 5 } // 5% economy penalty per sanction type per nation
        if (sanctionsPenalty > 0) {
            newStats = newStats.copy(economy = (newStats.economy - sanctionsPenalty).coerceAtLeast(0))
        }

        // Process Espionage
        var spyMissions = country.activeSpyMissions.map { it.copy(turnsRemaining = it.turnsRemaining - 1) }
        val completedMissions = spyMissions.filter { it.turnsRemaining <= 0 }
        spyMissions = spyMissions.filter { it.turnsRemaining > 0 }
        
        val spyEvents = mutableListOf<String>()
        completedMissions.forEach { mission ->
            val success = (1..100).random() <= mission.successChance
            if (success) {
                spyEvents.add("Mission Success: ${mission.type.displayName} in ${mission.targetNationName}")
                when (mission.type) {
                    SpyMissionType.GATHER_INTEL -> newStats = newStats.copy(technology = (newStats.technology + 5).coerceAtMost(100))
                    SpyMissionType.STEAL_TECH -> newStats = newStats.copy(technology = (newStats.technology + 10).coerceAtMost(100))
                    SpyMissionType.SABOTAGE_ECONOMY -> { /* Effect on AI is handled abstractly or relation drop */ }
                    SpyMissionType.INCITE_UNREST -> { /* handled abstractly */ }
                    SpyMissionType.STAGE_COUP -> { /* handled abstractly */ }
                }
            } else {
                spyEvents.add("Mission Failed: Agent caught in ${mission.targetNationName}")
                newStats = newStats.copy(softPower = (newStats.softPower - 10).coerceAtLeast(0))
                // Relation penalty logic would go here
            }
        }
        
        // UN Process (Simplified: Random resolution every 4 turns)
        var un = country.unitedNations
        if (country.turnCount % 4 == 0) {
            val newResolution = generateRandomResolution(country.year, currentState.aiNations)
            un = un.copy(activeResolutions = un.activeResolutions + newResolution)
        }
        
        // Auto-vote on active resolutions (Simplified: auto-pass/fail for now based on randomness, player vote handled in UI later)
        val processedResolutions = un.activeResolutions.map { res ->
            val passed = (1..100).random() > 40 // 60% pass rate
            res.copy(
                status = if (passed) ResolutionStatus.PASSED else ResolutionStatus.FAILED,
                votesFor = if (passed) (un.memberCount / 2 + 1..un.memberCount).random() else (0..un.memberCount / 2).random(),
                votesAgainst = (0..un.memberCount / 2).random() // visual only
            )
        }
        
        un = un.copy(
            activeResolutions = emptyList(),
            passedResolutions = un.passedResolutions + processedResolutions.filter { it.status == ResolutionStatus.PASSED }
        )

        // Soft Power Calculation
        newStats = newStats.copy(softPower = ((newStats.economy + newStats.happiness + newStats.technology) / 3).coerceIn(0, 100))

        // 3. Process AI Turns
        val newAiNations = currentState.aiNations.map { processAiTurn(it, currentState.globalMarket) }

        // 4. Global Market Fluctuation
        val globalStability = newAiNations.sumOf { it.stats.stability } / newAiNations.size
        val newGlobalMarket = currentState.globalMarket.copy(
            globalInstability = 100 - globalStability,
            foodPrice = (currentState.globalMarket.foodPrice + (-1..1).random()).coerceIn(5, 50),
            energyPrice = (currentState.globalMarket.energyPrice + (-1..1).random()).coerceIn(5, 50)
        )

        // 5. Random Events (including new Political & Military ones)
        val allEvents = events + politicalEvents + diplomaticEvents + militaryEvents
        val eventRoll = (1..100).random()
        val eventThreshold = 100 - newStats.stability + 20
        val event = if (eventRoll <= eventThreshold && allEvents.isNotEmpty()) {
            allEvents.random()
        } else null

        event?.let {
            newStats = it.effect(newStats)
        }

        // 6. Factions & Parties update
        val newFactions = country.factions.map { faction ->
            val loyaltyChange = if (newStats.happiness > 60) 2 else if (newStats.happiness < 40) -2 else 0
            faction.copy(loyalty = (faction.loyalty + loyaltyChange + (-2..2).random()).coerceIn(0, 100))
        }

        val newParties = country.politicalParties.map { party ->
            val popChange = if (newStats.economy > 60) 2 else if (newStats.economy < 40) -2 else 0
            party.copy(popularity = (party.popularity + popChange + (-3..3).random()).coerceIn(0, 100))
        }

        // 7. Population & Stat Decay/Growth
        val populationGrowth = 0.01 // Simplified
        newStats = newStats.copy(
            population = (newStats.population * (1 + populationGrowth)).toInt().coerceAtMost(100000000),
            corruption = (newStats.corruption + (1..3).random()).coerceAtMost(100) // Natural corruption growth
        )

        // Check for Assassination, Coup, or Nuclear Winter
        val checkCountry = country.copy(stats = newStats, factions = newFactions, politicalParties = newParties)
        var gameOverReason = checkGameOver(checkCountry)
        
        if (gameOverReason == null) {
            if (newStats.stability < 20 && (1..100).random() < 5) {
                gameOverReason = GameOverReason.ASSASSINATION
            } else if (newFactions.any { it.loyalty < 10 && it.power > 40 } && (1..100).random() < 10) {
                gameOverReason = GameOverReason.COUP
            }
        }

        val newCountry = country.copy(
            stats = newStats,
            resources = newResources,
            treasury = newTreasury,
            year = country.year + 1,
            turnCount = country.turnCount + 1,
            factions = newFactions,
            politicalParties = newParties,
            currentTermYear = country.currentTermYear + 1,
            unitedNations = un,
            activeSpyMissions = spyMissions,
            military = newMilitary
        )

        // Auto-trigger election every 4 years in Democracy
        var finalCountry = newCountry
        if (country.governmentType == GovernmentType.DEMOCRACY && newCountry.currentTermYear >= 4) {
            finalCountry = newCountry.copy(
                election = Election(year = newCountry.year, isActive = true, turnsRemaining = 1),
                currentTermYear = 0
            )
        }

        val newEventHistory = mutableListOf<String>()
        event?.let {
            newEventHistory.add("Year ${finalCountry.year}: ${it.title}")
        }
        spyEvents.forEach { newEventHistory.add("Year ${finalCountry.year}: $it") }
        theaterEvents.forEach { newEventHistory.add("Year ${finalCountry.year}: $it") }
        processedResolutions.forEach { 
            if (it.status == ResolutionStatus.PASSED) newEventHistory.add("UN: ${it.description} PASSED")
            else newEventHistory.add("UN: ${it.description} FAILED")
        }
        newEventHistory.addAll(country.turnCount.coerceAtMost(9).let { country.eventHistory.take(it) })

        return currentState.copy(
            country = finalCountry.copy(eventHistory = newEventHistory),
            aiNations = newAiNations,
            globalMarket = newGlobalMarket,
            isGameOver = gameOverReason != null,
            gameOverReason = gameOverReason,
            lastEvent = event,
            eventHistory = newEventHistory,
            newsHeadline = event?.title ?: theaterEvents.firstOrNull() ?: spyEvents.firstOrNull() // Simplified headline
        )
    }

    private fun calculateMilitaryPower(military: Military): Int {
        val branchPower = (military.army.manpower + military.navy.manpower + military.airForce.manpower) / 300 // Scale down
        val equipBonus = (military.army.equipmentLevel + military.navy.equipmentLevel + military.airForce.equipmentLevel) * 2
        val expBonus = (military.army.experience + military.navy.experience + military.airForce.experience) / 10
        val mercPower = military.mercenaries.sumOf { it.power }
        val nukePower = military.nuclearProgram.warheads * 10
        
        return (branchPower + equipBonus + expBonus + mercPower + nukePower).coerceIn(0, 100)
    }

    private fun processNuclearProgram(program: NuclearProgram): Pair<NuclearProgram, Boolean> {
        if (!program.hasProgram) return Pair(program, false)
        
        var newProgress = program.researchProgress + 5 // Base growth
        var builtNew = false
        var newWarheads = program.warheads
        
        if (newProgress >= 100) {
            newProgress = 0
            newWarheads += 1
            builtNew = true
        }
        
        return Pair(program.copy(researchProgress = newProgress, warheads = newWarheads), builtNew)
    }

    private fun processWarTheaters(theaters: List<WarTheater>, playerPower: Int, aiNations: List<AiNation>): Pair<List<WarTheater>, List<String>> {
        val events = mutableListOf<String>()
        val newTheaters = theaters.map { theater ->
            if (!theater.isActive) return@map theater
            
            val enemy = aiNations.find { it.id == theater.enemyNationId }
            val enemyPower = enemy?.stats?.military ?: 50
            
            // Random flux
            val battleRoll = (1..100).random() + (playerPower - enemyPower)
            var territoryChange = 0
            
            if (battleRoll > 60) territoryChange = 5
            else if (battleRoll < 40) territoryChange = -5
            
            val newTerritory = (theater.territoryControlled + territoryChange).coerceIn(0, 100)
            
            if (territoryChange > 0) events.add("War in ${theater.name}: You gained ground!")
            else if (territoryChange < 0) events.add("War in ${theater.name}: Enemy pushed back!")
            
            theater.copy(territoryControlled = newTerritory, playerStrength = playerPower, enemyStrength = enemyPower)
        }
        
        return Pair(newTheaters, events)
    }

    private fun generateRandomResolution(year: Int, aiNations: List<AiNation>): UNResolution {
        val types = UNResolutionType.values()
        val type = types.random()
        val targetNation = if (type != UNResolutionType.GLOBAL_INITIATIVE) aiNations.random() else null
        return UNResolution(
            id = "un_res_${year}_${(1..1000).random()}",
            type = type,
            targetNationId = targetNation?.id,
            description = "UN Resolution on ${type.displayName}",
            yearProposed = year
        )
    }

    private val politicalEvents = listOf<GameEvent>()

    private val diplomaticEvents = listOf<GameEvent>()

    private val militaryEvents = listOf(
        GameEvent(
            id = "arms_race",
            title = "Arms Race",
            description = "Tensions are rising. Neighbors are arming themselves.",
            category = EventCategory.MILITARY,
            severity = EventSeverity.MODERATE,
            effect = { stats -> stats.copy(military = (stats.military - 5).coerceAtLeast(0), stability = (stats.stability - 5).coerceAtLeast(0)) }, // Relative power drops
            options = listOf(
                EventOption("Increase Spending", "Match them") { stats, treasury, resources ->
                    Triple(stats.copy(military = stats.military + 10), treasury - 2000, resources)
                },
                EventOption("Diplomatic Solution", "De-escalate") { stats, treasury, resources ->
                    Triple(stats.copy(softPower = stats.softPower + 5, stability = stats.stability + 5), treasury - 500, resources)
                }
            )
        )
    )

    fun generateInitialCountry(name: String, governmentType: GovernmentType): Pair<Country, List<AiNation>> {
        val aiNations = generateAiNations()
        val relations = generateInitialRelations(aiNations)
        
        val parties = listOf(
            PoliticalParty("Liberal Alliance", Ideology.LIBERAL, 30, 20),
            PoliticalParty("Conservative Union", Ideology.CONSERVATIVE, 30, 20),
            PoliticalParty("Socialist Front", Ideology.SOCIALIST, 20, 10),
            PoliticalParty("Nationalist Party", Ideology.NATIONALIST, 10, 5),
            PoliticalParty("Green Party", Ideology.ECOLOGIST, 10, 5)
        )

        val factions = listOf(
            PoliticalFaction("Military Elite", 70, 30),
            PoliticalFaction("Labor Unions", 60, 20),
            PoliticalFaction("Business Oligarchs", 50, 25),
            PoliticalFaction("Religious Groups", 80, 15),
            PoliticalFaction("Student Activists", 40, 10)
        )

        val ministers = listOf(
            Minister("m1", "John Smith", MinisterRole.ECONOMY, 60, 5, 80),
            Minister("m2", "Elena Vance", MinisterRole.DEFENSE, 75, 10, 90),
            Minister("m3", "Marcus Cole", MinisterRole.INTERIOR, 55, 20, 70)
        )

        val laws = listOf(
            Law("l1", "Progressive Taxation", "Increases revenue but lowers happiness of the wealthy.", false, 0, 0, 10, -5, -5),
            Law("l2", "Military Draft", "Increases military power but lowers happiness.", false, 0, -5, -10, -15, 0),
            Law("l3", "Universal Healthcare", "Increases healthcare and happiness but costs treasury.", false, 2000, 5, 0, 15, -2)
        )
        
        val country = Country(
            name = name,
            governmentType = governmentType,
            stats = CountryStats(),
            resources = Resources(),
            diplomaticRelations = relations,
            year = 2024,
            treasury = 10000,
            politicalParties = parties,
            factions = factions,
            ministers = ministers,
            activeLaws = laws
        )
        return Pair(country, aiNations)
    }

    fun processElection(country: Country): Country {
        val election = country.election ?: return country
        if (!election.isActive) return country

        val newTurnsRemaining = election.turnsRemaining - 1
        if (newTurnsRemaining > 0) {
            return country.copy(election = election.copy(turnsRemaining = newTurnsRemaining))
        }

        // Election concludes
        val results = country.politicalParties.associate { it.name to (it.popularity + (-10..10).random()).coerceIn(0, 100) }
        val winner = results.maxBy { it.value }.key
        
        val newStats = if (country.governmentType == GovernmentType.DEMOCRACY) {
            country.stats.copy(stability = (country.stats.stability + 10).coerceAtMost(100))
        } else {
            country.stats.copy(stability = (country.stats.stability - 15).coerceAtLeast(0))
        }

        return country.copy(
            election = election.copy(isActive = false, turnsRemaining = 0, results = results),
            stats = newStats,
            eventHistory = listOf("Election Year: $winner won the election!") + country.eventHistory
        )
    }
}
