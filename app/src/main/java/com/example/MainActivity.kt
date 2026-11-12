package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.configureAppCheck
import com.example.ui.CharacterCreatorScreen
import com.example.ui.CharacterViewModel
import com.example.ui.theme.FantasyCreatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            FantasyCreatorTheme {
                val characterViewModel: CharacterViewModel = viewModel(
                    factory = CharacterViewModel.provideFactory(),
                )
                val uiState by characterViewModel.uiState.collectAsStateWithLifecycle()

                CharacterCreatorScreen(
                    uiState = uiState,
                    onGenerateClick = characterViewModel::generateCharacter,
                    onGeneratePortraitClick = characterViewModel::generatePortrait,
                    onRegeneratePortraitClick = characterViewModel::regeneratePortrait,
                    onSelectRecent = characterViewModel::selectRecentCharacter,
                )
            }
        }
    }
}
