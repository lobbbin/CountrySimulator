package com.countrysimulator.game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.countrysimulator.game.domain.Country
import com.countrysimulator.game.domain.CountryStats
import com.countrysimulator.game.domain.GovernmentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_data")

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
        val YEAR = intPreferencesKey("year")
        val TREASURY = intPreferencesKey("treasury")
        val TURN_COUNT = intPreferencesKey("turn_count")
    }

    suspend fun saveGame(country: Country) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTRY_NAME] = country.name
            preferences[PreferencesKeys.GOVERNMENT_TYPE] = country.governmentType.name
            preferences[PreferencesKeys.POPULATION] = country.stats.population
            preferences[PreferencesKeys.ECONOMY] = country.stats.economy
            preferences[PreferencesKeys.MILITARY] = country.stats.military
            preferences[PreferencesKeys.HAPPINESS] = country.stats.happiness
            preferences[PreferencesKeys.STABILITY] = country.stats.stability
            preferences[PreferencesKeys.TECHNOLOGY] = country.stats.technology
            preferences[PreferencesKeys.YEAR] = country.year
            preferences[PreferencesKeys.TREASURY] = country.treasury
            preferences[PreferencesKeys.TURN_COUNT] = country.turnCount
        }
    }

    fun loadGame(): Flow<Country?> {
        return context.dataStore.data.map { preferences ->
            val name = preferences[PreferencesKeys.COUNTRY_NAME] ?: return@map null
            val govType = preferences[PreferencesKeys.GOVERNMENT_TYPE]?.let {
                GovernmentType.valueOf(it)
            } ?: return@map null

            Country(
                name = name,
                governmentType = govType,
                stats = CountryStats(
                    population = preferences[PreferencesKeys.POPULATION] ?: 1000000,
                    economy = preferences[PreferencesKeys.ECONOMY] ?: 50,
                    military = preferences[PreferencesKeys.MILITARY] ?: 30,
                    happiness = preferences[PreferencesKeys.HAPPINESS] ?: 60,
                    stability = preferences[PreferencesKeys.STABILITY] ?: 50,
                    technology = preferences[PreferencesKeys.TECHNOLOGY] ?: 20
                ),
                year = preferences[PreferencesKeys.YEAR] ?: 2024,
                treasury = preferences[PreferencesKeys.TREASURY] ?: 10000,
                turnCount = preferences[PreferencesKeys.TURN_COUNT] ?: 0
            )
        }
    }

    suspend fun clearGame() {
        context.dataStore.edit { it.clear() }
    }
}