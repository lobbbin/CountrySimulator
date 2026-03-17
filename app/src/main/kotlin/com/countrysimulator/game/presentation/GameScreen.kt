package com.countrysimulator.game.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.countrysimulator.game.domain.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySimulatorApp(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF3F51B5),
            secondary = Color(0xFFFFC107),
            tertiary = Color(0xFF009688),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.showNewGameDialog) {
            NewGameDialog(onStartGame = { name, govType -> viewModel.startNewGame(name, govType) })
        } else if (uiState.gameState != null) {
            val gameState = uiState.gameState!!
            
            if (gameState.isGameOver && gameState.gameOverReason != null) {
                GameOverScreen(
                    reason = gameState.gameOverReason!!,
                    message = viewModel.getGameOverMessage(gameState.gameOverReason!!),
                    onRestart = { viewModel.restartGame() }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, "Dashboard") },
                                label = { Text("Nation") },
                                selected = currentScreen == Screen.DASHBOARD,
                                onClick = { currentScreen = Screen.DASHBOARD }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.AccountBox, "Politics") },
                                label = { Text("Politics") },
                                selected = currentScreen == Screen.POLITICS,
                                onClick = { currentScreen = Screen.POLITICS }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Search, "Intel") },
                                label = { Text("Intel") },
                                selected = currentScreen == Screen.INTEL,
                                onClick = { currentScreen = Screen.INTEL }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.List, "World") },
                                label = { Text("World") },
                                selected = currentScreen == Screen.WORLD,
                                onClick = { currentScreen = Screen.WORLD }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Info, "Market") },
                                label = { Text("UN") },
                                selected = currentScreen == Screen.UN,
                                onClick = { currentScreen = Screen.UN }
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            Screen.DASHBOARD -> DashboardScreen(
                                gameState = gameState,
                                incomeThisTurn = uiState.incomeThisTurn,
                                newsHeadline = uiState.newsHeadline,
                                onNextTurn = { viewModel.nextTurn() },
                                viewModel = viewModel
                            )
                            Screen.POLITICS -> PoliticsScreen(
                                gameState = gameState,
                                viewModel = viewModel
                            )
                            Screen.INTEL -> IntelScreen(
                                gameState = gameState,
                                viewModel = viewModel
                            )
                            Screen.WORLD -> WorldScreen(
                                gameState = gameState,
                                viewModel = viewModel
                            )
                            Screen.UN -> UnitedNationsScreen(
                                gameState = gameState,
                                viewModel = viewModel
                            )
                            Screen.MARKET -> MarketScreen(gameState = gameState)
                        }

                        if (uiState.showEventDialog && uiState.currentEvent != null) {
                            EventDialog(
                                event = uiState.currentEvent!!,
                                onOptionSelected = { index -> viewModel.handleEventOption(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen { DASHBOARD, POLITICS, INTEL, WORLD, UN, MARKET }
enum class DiplomacyAction { IMPROVE, TRADE, ALLIANCE, WAR, AID, SANCTION }

@Composable
fun DashboardScreen(
    gameState: GameState,
    incomeThisTurn: Int,
    newsHeadline: String?,
    onNextTurn: () -> Unit,
    viewModel: GameViewModel
) {
    val country = gameState.country
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = country.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Year: ${country.year} | Turn: ${country.turnCount}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Treasury: $${formatNumber(country.treasury)}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    Text("Income: +$${formatNumber(incomeThisTurn)}", color = Color(0xFF8BC34A))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (newsHeadline != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    text = newsHeadline,
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Stats Grid
        Text("Nation Statistics", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        StatBar("Population", country.stats.population, 100000000, 0xFF4CAF50.toInt())
        StatBar("Economy", country.stats.economy, 100, 0xFFFFD700.toInt())
        StatBar("Military", country.stats.military, 100, 0xFFF44336.toInt())
        StatBar("Stability", country.stats.stability, 100, 0xFF9C27B0.toInt())
        StatBar("Soft Power", country.stats.softPower, 100, 0xFF009688.toInt())
        StatBar("Corruption", country.stats.corruption, 100, Color.Red.hashCode())
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Resources
        Text("Resources", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ResourceItem("Food", country.resources.food, country.resources.maxFood, 0xFF4CAF50.toInt())
            ResourceItem("Energy", country.resources.energy, country.resources.maxEnergy, 0xFFFFD700.toInt())
            ResourceItem("Matls", country.resources.materials, country.resources.maxMaterials, 0xFF9C27B0.toInt())
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Text("Quick Actions", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButtonSmall("Invest Econ ($1K)", { viewModel.investInEconomy() }, country.treasury >= 1000, Modifier.weight(1f))
            ActionButtonSmall("Recruit Mil ($800)", { viewModel.recruitMilitary() }, country.treasury >= 800, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButtonSmall("Improve Infra ($1.2K)", { viewModel.improveInfrastructure() }, country.treasury >= 1200, Modifier.weight(1f))
            ActionButtonSmall("Propaganda ($1K)", { viewModel.runPropaganda() }, country.treasury >= 1000, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNextTurn,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("NEXT TURN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun IntelScreen(gameState: GameState, viewModel: GameViewModel) {
    val country = gameState.country
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Intelligence Agency", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Conduct covert operations and gather intel.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Active Missions", fontWeight = FontWeight.Bold)
            if (country.activeSpyMissions.isEmpty()) {
                Text("No active missions.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            }
            country.activeSpyMissions.forEach { mission ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(mission.type.displayName, fontWeight = FontWeight.Bold)
                            Text("Turns: ${mission.turnsRemaining}", fontSize = 12.sp)
                        }
                        Text("Target: ${mission.targetNationName}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(progress = { (mission.type.duration - mission.turnsRemaining) / mission.type.duration.toFloat() }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Launch New Mission", fontWeight = FontWeight.Bold)
            gameState.aiNations.filter { it.isAlive }.forEach { ai ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(ai.name, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpyMissionType.values().forEach { type ->
                                ActionButtonSmall(type.displayName, { viewModel.launchSpyMission(ai.id, type) }, country.treasury >= type.cost, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitedNationsScreen(gameState: GameState, viewModel: GameViewModel) {
    val un = gameState.country.unitedNations
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("United Nations", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("International body for global cooperation.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("UN Status", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Member States: ${un.memberCount}")
                Text("Your Status: Member", color = Color.Green)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Passed Resolutions", fontWeight = FontWeight.Bold)
            if (un.passedResolutions.isEmpty()) {
                Text("No passed resolutions.", fontSize = 12.sp, color = Color.Gray)
            }
            un.passedResolutions.forEach { res ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(res.type.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(res.description, fontSize = 12.sp)
                        Text("Passed in Year ${res.yearProposed}", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun WorldScreen(gameState: GameState, viewModel: GameViewModel) {
    val relations = gameState.country.diplomaticRelations
    val aiNations = gameState.aiNations

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("Global Powers", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(relations) { relation ->
            val aiNation = aiNations.find { it.id == relation.nationId }
            if (aiNation != null && aiNation.isAlive) {
                AiNationCard(aiNation, relation, { action, id ->
                    when (action) {
                        DiplomacyAction.IMPROVE -> viewModel.improveRelations(id)
                        DiplomacyAction.TRADE -> viewModel.offerTrade(id)
                        DiplomacyAction.ALLIANCE -> viewModel.formAlliance(id)
                        DiplomacyAction.WAR -> viewModel.declareWar(id)
                        DiplomacyAction.AID -> viewModel.sendForeignAid(id)
                        DiplomacyAction.SANCTION -> viewModel.imposeSanctions(id, SanctionType.TRADE_EMBARGO)
                    }
                })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AiNationCard(aiNation: AiNation, relation: DiplomaticRelation, onAction: (DiplomacyAction, String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        onClick = { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(aiNation.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(aiNation.governmentType.displayName, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Personality: ${aiNation.personality.name}", fontSize = 12.sp)
                Text(
                    text = "Relation: ${relation.relationScore} (${relation.status})",
                    color = if (relation.status == RelationStatus.ENEMY) Color.Red else if (relation.status == RelationStatus.ALLY) Color.Green else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            if (relation.sanctions.isNotEmpty()) {
                Text("Sanctions: ${relation.sanctions.size} active", color = Color.Yellow, fontSize = 10.sp)
            }
            if (relation.isAtWar) {
                Text("⚠ AT WAR ⚠", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Diplomacy: ${aiNation.name}") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Government: ${aiNation.governmentType.displayName}")
                    Text("Economy: ${aiNation.stats.economy} | Military: ${aiNation.stats.military}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!relation.isAtWar) {
                        Button(onClick = { onAction(DiplomacyAction.IMPROVE, aiNation.id); showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                            Text("Improve Relations ($500)")
                        }
                        Button(onClick = { onAction(DiplomacyAction.AID, aiNation.id); showDialog = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                            Text("Send Foreign Aid ($2000)")
                        }
                        Button(
                            onClick = { onAction(DiplomacyAction.TRADE, aiNation.id); showDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = relation.relationScore >= 40 && !relation.hasTradeAgreement
                        ) {
                            Text(if (relation.hasTradeAgreement) "Trade Active" else "Offer Trade Agreement (Req: 40)")
                        }
                        Button(
                            onClick = { onAction(DiplomacyAction.ALLIANCE, aiNation.id); showDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = relation.relationScore >= 80 && !relation.hasAlliance
                        ) {
                            Text(if (relation.hasAlliance) "Alliance Active" else "Form Alliance (Req: 80)")
                        }
                        Button(onClick = { onAction(DiplomacyAction.SANCTION, aiNation.id); showDialog = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow, contentColor = Color.Black)) {
                            Text("Impose Sanctions ($500)")
                        }
                        Button(
                            onClick = { onAction(DiplomacyAction.WAR, aiNation.id); showDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("DECLARE WAR")
                        }
                    } else {
                         Text("You are at war with this nation!", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
fun PoliticsScreen(gameState: GameState, viewModel: GameViewModel) {
    val country = gameState.country
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Government & Politics", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Government Type: ${country.governmentType.displayName}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Political Parties", fontWeight = FontWeight.Bold)
            country.politicalParties.forEach { party ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(party.name, fontWeight = FontWeight.Bold)
                            Text(party.ideology.displayName, fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Popularity: ${party.popularity}%", fontWeight = FontWeight.Bold)
                            LinearProgressIndicator(progress = { party.popularity / 100f }, modifier = Modifier.width(80.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Cabinet Ministers", fontWeight = FontWeight.Bold)
            country.ministers.forEach { minister ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(minister.name, fontWeight = FontWeight.Bold)
                            Text(minister.role.displayName, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("Skill: ${minister.skill}", fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (country.ministers.size < 6) {
                Button(onClick = { viewModel.hireMinister("New Candidate", MinisterRole.values().random()) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Hire New Minister ($3000)")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Political Factions", fontWeight = FontWeight.Bold)
            country.factions.forEach { faction ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(faction.name, fontWeight = FontWeight.Bold)
                            Text("Power: ${faction.power}%", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Loyalty: ${faction.loyalty}%", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                            LinearProgressIndicator(progress = { faction.loyalty / 100f }, modifier = Modifier.weight(1f), color = if (faction.loyalty < 30) Color.Red else Color.Green)
                            Button(onClick = { viewModel.bribeFaction(faction.name) }, modifier = Modifier.padding(start = 8.dp), contentPadding = PaddingValues(0.dp)) {
                                Text("Bribe ($2K)", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Active Laws", fontWeight = FontWeight.Bold)
            country.activeLaws.forEach { law ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(law.name, fontWeight = FontWeight.Bold)
                            Text(law.description, fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(checked = law.isActive, onCheckedChange = { viewModel.toggleLaw(law.id) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (country.governmentType != GovernmentType.DEMOCRACY) {
            item {
                Button(onClick = { viewModel.triggerElection() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Text("Hold Emergency Election")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MarketScreen(gameState: GameState) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Global Market", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        
        MarketCard("Food", gameState.globalMarket.foodPrice, "Essential for population growth.")
        Spacer(modifier = Modifier.height(8.dp))
        MarketCard("Energy", gameState.globalMarket.energyPrice, "Powers industry and military.")
        Spacer(modifier = Modifier.height(8.dp))
        MarketCard("Materials", gameState.globalMarket.materialsPrice, "Required for construction.")
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Global Instability: ${gameState.globalMarket.globalInstability}%", color = if (gameState.globalMarket.globalInstability > 50) Color.Red else Color.Green)
        Text("High instability increases price volatility.", fontSize = 12.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Event History", fontWeight = FontWeight.Bold)
        LazyColumn {
            items(gameState.eventHistory) { event ->
                Text("• $event", fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
fun MarketCard(name: String, price: Int, desc: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
            Text("$${price}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8BC34A))
        }
    }
}

@Composable
fun NewGameDialog(onStartGame: (String, GovernmentType) -> Unit) {
    var countryName by remember { mutableStateOf("") }
    var selectedGovType by remember { mutableStateOf(GovernmentType.DEMOCRACY) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Country Simulator 6.0", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Diplomacy & International Update", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = countryName, onValueChange = { countryName = it }, label = { Text("Country Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Government Type")
        GovernmentType.values().forEach { gov ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(selected = selectedGovType == gov, onClick = { selectedGovType = gov })
                Text(gov.displayName, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { if (countryName.isNotBlank()) onStartGame(countryName, selectedGovType) }, enabled = countryName.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text("Start Game")
        }
    }
}

@Composable
fun EventDialog(event: com.countrysimulator.game.domain.GameEvent, onOptionSelected: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(event.title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(event.description)
                Spacer(modifier = Modifier.height(16.dp))
                event.options.forEachIndexed { index, option ->
                    Button(onClick = { onOptionSelected(index) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(option.label)
                            Text(option.description, fontSize = 10.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun GameOverScreen(reason: GameOverReason, message: String, onRestart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("GAME OVER", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Text(reason.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart) { Text("Play Again") }
    }
}

@Composable fun StatBar(label: String, value: Int, max: Int, color: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, modifier = Modifier.width(80.dp), fontSize = 12.sp)
        LinearProgressIndicator(progress = { value / max.toFloat() }, modifier = Modifier.weight(1f).height(8.dp), color = Color(color), trackColor = Color.DarkGray)
        Text("$value", modifier = Modifier.width(40.dp), textAlign = TextAlign.End, fontSize = 12.sp)
    }
}

@Composable fun ResourceItem(label: String, value: Int, max: Int, color: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        LinearProgressIndicator(progress = { value / max.toFloat() }, modifier = Modifier.width(60.dp), color = Color(color))
        Text("$value", fontSize = 10.sp)
    }
}

@Composable fun ActionButtonSmall(text: String, onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier, contentPadding = PaddingValues(4.dp)) {
        Text(text, fontSize = 11.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}
