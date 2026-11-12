package com.example.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ai.FirebasePortraitGenerator
import com.example.ai.PortraitGenerator
import com.example.data.FantasyCharacterGenerator
import com.example.model.FantasyCharacter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PortraitUiState {
    data object Idle : PortraitUiState
    data class Loading(val characterId: Long, val isRegenerate: Boolean) : PortraitUiState
    data class Success(
        val characterId: Long,
        val bitmap: Bitmap,
        val version: Int,
    ) : PortraitUiState
    data class Error(val characterId: Long, val message: String) : PortraitUiState
}

data class CharacterCreatorUiState(
    val currentCharacter: FantasyCharacter,
    val recentCharacters: List<FantasyCharacter>,
    val totalGenerated: Int,
    val portraitState: PortraitUiState = PortraitUiState.Idle,
)

class CharacterViewModel(
    private val generator: FantasyCharacterGenerator = FantasyCharacterGenerator(),
    private val portraitGenerator: PortraitGenerator = FirebasePortraitGenerator(),
) : ViewModel() {

    private val _uiState: MutableStateFlow<CharacterCreatorUiState>
    private val cachedPortraits = mutableMapOf<Long, Pair<Bitmap, Int>>()
    private val variationCounters = mutableMapOf<Long, Int>()
    private var portraitJob: Job? = null

    init {
        val initial = generator.generate(rollNumber = 1)
        _uiState = MutableStateFlow(
            CharacterCreatorUiState(
                currentCharacter = initial,
                recentCharacters = listOf(initial),
                totalGenerated = 1,
                portraitState = PortraitUiState.Idle,
            ),
        )
    }

    val uiState: StateFlow<CharacterCreatorUiState> = _uiState.asStateFlow()

    fun generateCharacter() {
        portraitJob?.cancel()
        _uiState.update { state ->
            val nextRollNumber = state.totalGenerated + 1
            val nextCharacter = generator.generate(
                rollNumber = nextRollNumber,
                previousName = state.currentCharacter.name,
            )
            val updatedRecent = (listOf(nextCharacter) + state.recentCharacters).take(6)
            state.copy(
                currentCharacter = nextCharacter,
                recentCharacters = updatedRecent,
                totalGenerated = nextRollNumber,
                portraitState = PortraitUiState.Idle,
            )
        }
    }

    fun selectRecentCharacter(character: FantasyCharacter) {
        portraitJob?.cancel()
        val cached = cachedPortraits[character.id]
        val restoredState = if (cached != null) {
            PortraitUiState.Success(
                characterId = character.id,
                bitmap = cached.first,
                version = cached.second,
            )
        } else {
            PortraitUiState.Idle
        }
        _uiState.update { state ->
            state.copy(
                currentCharacter = character,
                portraitState = restoredState,
            )
        }
    }

    fun generatePortrait() {
        requestPortrait(isRegenerate = false)
    }

    fun regeneratePortrait() {
        requestPortrait(isRegenerate = true)
    }

    private fun requestPortrait(isRegenerate: Boolean) {
        val targetCharacter = _uiState.value.currentCharacter
        portraitJob?.cancel()

        val nextVersion = (variationCounters[targetCharacter.id] ?: 0) + 1
        variationCounters[targetCharacter.id] = nextVersion

        _uiState.update { state ->
            state.copy(
                portraitState = PortraitUiState.Loading(
                    characterId = targetCharacter.id,
                    isRegenerate = isRegenerate,
                ),
            )
        }

        portraitJob = viewModelScope.launch {
            try {
                val bitmap = portraitGenerator.generatePortrait(
                    character = targetCharacter,
                    variationNumber = nextVersion,
                )
                cachedPortraits[targetCharacter.id] = bitmap to nextVersion
                _uiState.update { state ->
                    if (state.currentCharacter.id == targetCharacter.id) {
                        state.copy(
                            portraitState = PortraitUiState.Success(
                                characterId = targetCharacter.id,
                                bitmap = bitmap,
                                version = nextVersion,
                            ),
                        )
                    } else {
                        state
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { state ->
                    if (state.currentCharacter.id == targetCharacter.id) {
                        state.copy(
                            portraitState = PortraitUiState.Error(
                                characterId = targetCharacter.id,
                                message = e.localizedMessage
                                    ?: "Unable to generate portrait. Please try again.",
                            ),
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            generator: FantasyCharacterGenerator = FantasyCharacterGenerator(),
            portraitGenerator: PortraitGenerator = FirebasePortraitGenerator(),
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CharacterViewModel(
                    generator = generator,
                    portraitGenerator = portraitGenerator,
                )
            }
        }
    }
}
