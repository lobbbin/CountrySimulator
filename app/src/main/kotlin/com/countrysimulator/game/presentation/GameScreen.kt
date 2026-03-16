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

    val darkColorScheme = darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF1a237e),
        secondary = androidx.compose.ui.graphics.Color(0xFFffd700),
        tertiary = androidx.compose.ui.graphics.Color(0xFF7c4dff),
        background = androidx.compose.ui.graphics.Color(0xFF121212),
        surface = androidx.compose.ui.graphics.Color(0xFF1e1e1e),
        onPrimary = androidx.compose.ui.graphics.Color.White,
        onSecondary = androidx.compose.ui.graphics.Color.Black,
        onBackground = androidx.compose.ui.graphics.Color.White,
        onSurface = androidx.compose.ui.graphics.Color.White
    )

    MaterialTheme(colorScheme = darkColorScheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkColorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = darkColorScheme.secondary
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
                            onNextTurn = { viewModel.nextTurn() },
                            onInvestEconomy = { viewModel.investInEconomy() },
                            onRecruitMilitary = { viewModel.recruitMilitary() },
                            onImproveInfrastructure = { viewModel.improveInfrastructure() },
                            onInvestTechnology = { viewModel.investInTechnology() },
                            onImproveHappiness = { viewModel.improveHappiness() }
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
    var showGovInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Country Simulator",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Build Your Nation",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))

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
            GovernmentType.entries.forEach { govType ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGovType == govType,
                        onClick = { selectedGovType = govType }
                    )
                    Text(
                        text = govType.displayName,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedGovType.description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

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
fun GameScreen(
    gameState: com.countrysimulator.game.domain.GameState,
    incomeThisTurn: Int,
    onNextTurn: () -> Unit,
    onInvestEconomy: () -> Unit,
    onRecruitMilitary: () -> Unit,
    onImproveInfrastructure: () -> Unit,
    onInvestTechnology: () -> Unit,
    onImproveHappiness: () -> Unit
) {
    val scrollState = rememberScrollState()
    val country = gameState.country

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton("Invest in Economy ($1000)", onInvestEconomy, country.treasury >= 1000)
        ActionButton("Recruit Military ($800)", onRecruitMilitary, country.treasury >= 800)
        ActionButton("Improve Infrastructure ($1200)", onImproveInfrastructure, country.treasury >= 1200)
        ActionButton("Invest in Technology ($1500)", onInvestTechnology, country.treasury >= 1500)
        ActionButton("Improve Happiness ($800)", onImproveHappiness, country.treasury >= 800)

        Spacer(modifier = Modifier.height(24.dp))

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
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Last Event",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = gameState.lastEvent.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = gameState.lastEvent.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatBar(label: String, value: Int, max: Int, color: Int) {
    val progress = value.toFloat() / max.toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            modifier = Modifier.width(80.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(16.dp),
            color = androidx.compose.ui.graphics.Color(color),
            trackColor = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "$value",
            fontSize = 12.sp,
            modifier = Modifier.width(40.dp),
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
        Text(text)
    }
}

@Composable
fun EventDialog(
    event: com.countrysimulator.game.domain.GameEvent,
    onOptionSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
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
                        Text(option.label)
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
            text = reason.name,
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