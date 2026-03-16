package com.countrysimulator.game.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.countrysimulator.game.domain.GameOverReason
import com.countrysimulator.game.domain.GovernmentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySimulatorApp(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                uiState.showNewGameDialog -> {
                    NewGameDialog(
                        onStartGame = { name, govType ->
                            viewModel.startNewGame(name, govType)
                        }
                    )
                }
                uiState.showEventDialog && uiState.currentEvent != null -> {
                    EventDialog(
                        event = uiState.currentEvent!!,
                        onOptionSelected = { index ->
                            viewModel.handleEventOption(index)
                        }
                    )
                }
                uiState.gameState != null -> {
                    val gameState = uiState.gameState!!
                    if (gameState.isGameOver && gameState.gameOverReason != null) {
                        GameOverScreen(
                            reason = gameState.gameOverReason!!,
                            message = viewModel.getGameOverMessage(gameState.gameOverReason!!),
                            onRestart = { viewModel.restartGame() }
                        )
                    } else {
                        GameScreen(
                            gameState = gameState,
                            incomeThisTurn = uiState.incomeThisTurn,
                            newsHeadline = uiState.newsHeadline,
                            onNextTurn = { viewModel.nextTurn() },
                            onInvestEconomy = { viewModel.investInEconomy() },
                            onRecruitMilitary = { viewModel.recruitMilitary() },
                            onImproveInfrastructure = { viewModel.improveInfrastructure() },
                            onInvestTechnology = { viewModel.investInTechnology() },
                            onImproveHappiness = { viewModel.improveHappiness() },
                            onInvestEducation = { viewModel.investInEducation() },
                            onInvestHealthcare = { viewModel.investInHealthcare() },
                            onImproveEnvironment = { viewModel.improveEnvironment() },
                            onFightCrime = { viewModel.fightCrime() },
                            onBuyFood = { viewModel.buyFood() },
                            onBuyEnergy = { viewModel.buyEnergy() },
                            onBuyMaterials = { viewModel.buyMaterials() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewGameDialog(onStartGame: (String, GovernmentType) -> Unit) {
    var countryName by remember { mutableStateOf("") }
    var selectedGovType by remember { mutableStateOf(GovernmentType.DEMOCRACY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Country Simulator",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Version 2.0",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Build Your Nation",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = countryName,
            onValueChange = { countryName = it },
            label = { Text("Country Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Government Type",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            GovernmentType.entries.chunked(2).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { govType ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGovType == govType,
                                onClick = { selectedGovType = govType }
                            )
                            Text(
                                text = govType.displayName,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = selectedGovType.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedGovType.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (selectedGovType.economyBonus != 0) {
                        StatChip("Econ", selectedGovType.economyBonus)
                    }
                    if (selectedGovType.militaryBonus != 0) {
                        StatChip("Mil", selectedGovType.militaryBonus)
                    }
                    if (selectedGovType.happinessBonus != 0) {
                        StatChip("Happy", selectedGovType.happinessBonus)
                    }
                    if (selectedGovType.stabilityBonus != 0) {
                        StatChip("Stab", selectedGovType.stabilityBonus)
                    }
                    if (selectedGovType.techBonus != 0) {
                        StatChip("Tech", selectedGovType.techBonus)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (countryName.isNotBlank()) {
                    onStartGame(countryName, selectedGovType)
                }
            },
            enabled = countryName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Start Game", fontSize = 16.sp)
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int) {
    val color = if (value > 0) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336)
    Text(
        text = "$label: ${if (value > 0) "+" else ""}$value",
        fontSize = 10.sp,
        color = color
    )
}

@Composable
fun GameScreen(
    gameState: com.countrysimulator.game.domain.GameState,
    incomeThisTurn: Int,
    newsHeadline: String?,
    onNextTurn: () -> Unit,
    onInvestEconomy: () -> Unit,
    onRecruitMilitary: () -> Unit,
    onImproveInfrastructure: () -> Unit,
    onInvestTechnology: () -> Unit,
    onImproveHappiness: () -> Unit,
    onInvestEducation: () -> Unit,
    onInvestHealthcare: () -> Unit,
    onImproveEnvironment: () -> Unit,
    onFightCrime: () -> Unit,
    onBuyFood: () -> Unit,
    onBuyEnergy: () -> Unit,
    onBuyMaterials: () -> Unit
) {
    val scrollState = rememberScrollState()
    val country = gameState.country

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        if (newsHeadline != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = newsHeadline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = country.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = country.governmentType.displayName,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("Year", "${country.year}")
                    StatItem("Turn", "${country.turnCount}")
                    StatItem("Treasury", "$${formatNumber(country.treasury)}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+$${formatNumber(incomeThisTurn)}/turn",
                    fontSize = 12.sp,
                    color = if (incomeThisTurn > 0) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Resources",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResourceItem("Food", country.resources.food, country.resources.maxFood, 0xFF4CAF50.toInt())
                    ResourceItem("Energy", country.resources.energy, country.resources.maxEnergy, 0xFFFFD700.toInt())
                    ResourceItem("Materials", country.resources.materials, country.resources.maxMaterials, 0xFF9C27B0.toInt())
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nation Statistics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        StatBar("Population", country.stats.population, 100000000, 0xFF4CAF50.toInt())
        StatBar("Economy", country.stats.economy, 100, 0xFFFFD700.toInt())
        StatBar("Military", country.stats.military, 100, 0xFFF44336.toInt())
        StatBar("Happiness", country.stats.happiness, 100, 0xFF2196F3.toInt())
        StatBar("Stability", country.stats.stability, 100, 0xFF9C27B0.toInt())
        StatBar("Technology", country.stats.technology, 100, 0xFF00BCD4.toInt())
        StatBar("Education", country.stats.education, 100, 0xFFE91E63.toInt())
        StatBar("Healthcare", country.stats.healthcare, 100, 0xFF8BC34A.toInt())
        StatBar("Environment", country.stats.environment, 100, 0xFF009688.toInt())
        StatBar("Crime", 100 - country.stats.crime, 100, 0xFF607D8B.toInt())

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Economy Actions",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ActionButtonSmall("Economy ($1000)", onInvestEconomy, country.treasury >= 1000, Modifier.weight(1f))
            ActionButtonSmall("Military ($800)", onRecruitMilitary, country.treasury >= 800, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ActionButtonSmall("Tech ($1500)", onInvestTechnology, country.treasury >= 1500, Modifier.weight(1f))
            ActionButtonSmall("Infra ($1200)", onImproveInfrastructure, country.treasury >= 1200, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Social Actions",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ActionButtonSmall("Happy ($800)", onImproveHappiness, country.treasury >= 800, Modifier.weight(1f))
            ActionButtonSmall("Education ($1200)", onInvestEducation, country.treasury >= 1200, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ActionButtonSmall("Health ($1000)", onInvestHealthcare, country.treasury >= 1000, Modifier.weight(1f))
            ActionButtonSmall("Crime ($800)", onFightCrime, country.treasury >= 800, Modifier.weight(1f))
        }
        ActionButtonSmall("Environment ($1500)", onImproveEnvironment, country.treasury >= 1500, Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Buy Resources",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ActionButtonSmall("Food ($500)", onBuyFood, country.treasury >= 500, Modifier.weight(1f))
            ActionButtonSmall("Energy ($600)", onBuyEnergy, country.treasury >= 600, Modifier.weight(1f))
            ActionButtonSmall("Matls ($800)", onBuyMaterials, country.treasury >= 800, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNextTurn,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Next Turn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (gameState.lastEvent != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Last Event",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = gameState.lastEvent.category.name,
                            fontSize = 11.sp,
                            color = when (gameState.lastEvent.severity) {
                                com.countrysimulator.game.domain.EventSeverity.CATASTROPHIC -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                com.countrysimulator.game.domain.EventSeverity.MAJOR -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            }
                        )
                    }
                    Text(
                        text = gameState.lastEvent.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = gameState.lastEvent.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ResourceItem(label: String, value: Int, max: Int, color: Int) {
    val progress = value.toFloat() / max.toFloat()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(60.dp)
                .height(8.dp),
            color = androidx.compose.ui.graphics.Color(color),
            trackColor = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "$value/$max",
            fontSize = 9.sp
        )
    }
}

@Composable
fun StatBar(label: String, value: Int, max: Int, color: Int) {
    val progress = value.toFloat() / max.toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            modifier = Modifier.width(70.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(12.dp),
            color = androidx.compose.ui.graphics.Color(color),
            trackColor = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "$value",
            fontSize = 10.sp,
            modifier = Modifier.width(35.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun ActionButtonSmall(text: String, onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp)
    }
}

@Composable
fun EventDialog(
    event: com.countrysimulator.game.domain.GameEvent,
    onOptionSelected: (Int) -> Unit
) {
    val severityColor = when (event.severity) {
        com.countrysimulator.game.domain.EventSeverity.CATASTROPHIC -> androidx.compose.ui.graphics.Color(0xFFF44336)
        com.countrysimulator.game.domain.EventSeverity.MAJOR -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        com.countrysimulator.game.domain.EventSeverity.MODERATE -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        com.countrysimulator.game.domain.EventSeverity.MINOR -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
    }

    AlertDialog(
        onDismissRequest = { },
        title = {
            Column {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                )
                Text(
                    text = "${event.category.name} - ${event.severity.name}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        text = {
            Column {
                Text(
                    text = event.description,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                event.options.forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = { onOptionSelected(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(option.label, fontSize = 13.sp)
                            Text(
                                option.description,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
fun GameOverScreen(
    reason: GameOverReason,
    message: String,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Over",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color(0xFFF44336)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = reason.name.replace("_", " "),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Play Again", fontSize = 18.sp)
        }
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}
