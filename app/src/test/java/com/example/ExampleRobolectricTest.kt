package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.core.app.ApplicationProvider
import com.example.ai.PortraitGenerator
import com.example.data.FantasyCharacterGenerator
import com.example.model.FantasyCharacter
import com.example.ui.CharacterCreatorScreen
import com.example.ui.CharacterViewModel
import com.example.ui.PortraitUiState
import com.example.ui.theme.FantasyCreatorTheme
import kotlin.random.Random
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Fantasy Forge", appName)
    }

    @Test
    fun `viewModel generate and regenerate portrait keep same character and increment version`() = runTest {
        val fakeGenerator = object : PortraitGenerator {
            val capturedCalls = mutableListOf<Pair<FantasyCharacter, Int>>()
            override suspend fun generatePortrait(
                character: FantasyCharacter,
                variationNumber: Int,
            ): Bitmap {
                capturedCalls += character to variationNumber
                return Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
            }
        }
        val viewModel = CharacterViewModel(
            generator = FantasyCharacterGenerator(Random(7)),
            portraitGenerator = fakeGenerator,
        )
        val initialHero = viewModel.uiState.value.currentCharacter

        viewModel.generatePortrait()
        composeTestRule.waitForIdle()

        val firstSuccess = viewModel.uiState.value.portraitState as PortraitUiState.Success
        assertEquals(initialHero.id, firstSuccess.characterId)
        assertEquals(1, firstSuccess.version)

        viewModel.regeneratePortrait()
        composeTestRule.waitForIdle()

        val secondSuccess = viewModel.uiState.value.portraitState as PortraitUiState.Success
        assertEquals(initialHero, viewModel.uiState.value.currentCharacter)
        assertEquals(initialHero.id, secondSuccess.characterId)
        assertEquals(2, secondSuccess.version)
        assertEquals(listOf(initialHero to 1, initialHero to 2), fakeGenerator.capturedCalls)
    }

    @Test
    fun `generate character and generate or regenerate portrait work end to end`() {
        val portraitRequests = mutableListOf<Pair<String, Int>>()
        val fakePortraitGenerator = object : PortraitGenerator {
            override suspend fun generatePortrait(
                character: FantasyCharacter,
                variationNumber: Int,
            ): Bitmap {
                portraitRequests += "${character.name}:${character.characterClass.displayName}" to variationNumber
                return Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
            }
        }
        val viewModel = CharacterViewModel(
            generator = FantasyCharacterGenerator(Random(123)),
            portraitGenerator = fakePortraitGenerator,
        )

        composeTestRule.setContent {
            FantasyCreatorTheme {
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                CharacterCreatorScreen(
                    uiState = state,
                    onGenerateClick = viewModel::generateCharacter,
                    onGeneratePortraitClick = viewModel::generatePortrait,
                    onRegeneratePortraitClick = viewModel::regeneratePortrait,
                    onSelectRecent = viewModel::selectRecentCharacter,
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Fantasy Character Creator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate Character").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate Portrait").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Regenerate Portrait").performScrollTo().assertIsDisplayed()

        val initialHero = viewModel.uiState.value.currentCharacter
        composeTestRule.onNodeWithTag("character_name").performScrollTo().assertTextEquals(initialHero.name)

        // Generate Portrait for the current hero
        composeTestRule.onNodeWithTag("generate_portrait_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("character_portrait_image").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Portrait v1").assertIsDisplayed()

        // Regenerate Portrait for the SAME hero
        composeTestRule.onNodeWithTag("regenerate_portrait_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("character_portrait_image").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Portrait v2").assertIsDisplayed()
        assertEquals(initialHero.name, viewModel.uiState.value.currentCharacter.name)
        assertEquals(2, portraitRequests.size)
        assertEquals(1, portraitRequests[0].second)
        assertEquals(2, portraitRequests[1].second)

        // Generate a new Character and verify immediate update
        composeTestRule.onNodeWithTag("generate_character_button").performClick()
        composeTestRule.waitForIdle()

        val nextHero = viewModel.uiState.value.currentCharacter
        assertNotEquals(initialHero.name, nextHero.name)
        composeTestRule.onNodeWithTag("character_name").performScrollTo().assertTextEquals(nextHero.name)
        composeTestRule.onNodeWithTag("character_class").performScrollTo().assertIsDisplayed()
    }
}
