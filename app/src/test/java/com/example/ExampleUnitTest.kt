package com.example

import android.graphics.Bitmap
import com.example.ai.FirebasePortraitGenerator
import com.example.ai.PortraitGenerator
import com.example.data.FantasyCharacterGenerator
import com.example.model.CharacterClass
import com.example.model.FantasyCharacter
import com.example.ui.CharacterViewModel
import com.example.ui.PortraitUiState
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun generator_coversAllSevenRequiredClasses() {
        val generator = FantasyCharacterGenerator(Random(42))
        val classesSeen = mutableSetOf<String>()
        repeat(200) { index ->
            classesSeen += generator.generate(rollNumber = index + 1).characterClass.displayName
        }

        val expectedClasses = setOf(
            "Mage",
            "Rogue",
            "Warrior",
            "Paladin",
            "Ranger",
            "Necromancer",
            "Druid",
        )
        assertEquals(expectedClasses, classesSeen)
        assertEquals(7, CharacterClass.entries.size)
    }

    @Test
    fun generator_avoidsRepeatingPreviousNameAndProducesTwoWordFantasyName() {
        val generator = FantasyCharacterGenerator(Random(99))
        var previousName: String? = null
        repeat(50) { index ->
            val character = generator.generate(
                rollNumber = index + 1,
                previousName = previousName,
            )
            assertTrue("Expected non-blank name", character.name.isNotBlank())
            assertTrue("Expected first and surname", character.name.contains(" "))
            assertNotEquals(previousName, character.name)
            previousName = character.name
        }
    }

    @Test
    fun portraitPrompt_includesCharacterNameClassAndVariation() {
        val character = FantasyCharacterGenerator(Random(11)).generate(rollNumber = 1)
        val prompt1 = FirebasePortraitGenerator.buildPortraitPrompt(character, variationNumber = 1)
        val prompt2 = FirebasePortraitGenerator.buildPortraitPrompt(character, variationNumber = 2)

        assertTrue(prompt1.contains(character.name))
        assertTrue(prompt1.contains(character.characterClass.displayName))
        assertTrue(prompt1.contains("cartoon", ignoreCase = true))
        assertNotEquals(prompt1, prompt2)
    }

    @Test
    fun viewModel_updatesCurrentCharacterImmediatelyOnGenerate() {
        val fakeGenerator = object : PortraitGenerator {
            override suspend fun generatePortrait(
                character: FantasyCharacter,
                variationNumber: Int,
            ): Bitmap = throw UnsupportedOperationException("Not called during character roll")
        }
        val viewModel = CharacterViewModel(
            generator = FantasyCharacterGenerator(Random(7)),
            portraitGenerator = fakeGenerator,
        )
        val initial = viewModel.uiState.value.currentCharacter

        viewModel.generateCharacter()
        val second = viewModel.uiState.value.currentCharacter

        assertNotEquals(initial.name, second.name)
        assertEquals(2, viewModel.uiState.value.totalGenerated)
        assertEquals(2, viewModel.uiState.value.recentCharacters.size)
        assertEquals(PortraitUiState.Idle, viewModel.uiState.value.portraitState)
    }
}
