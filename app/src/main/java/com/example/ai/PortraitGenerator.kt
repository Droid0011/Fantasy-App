package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.model.CharacterClass
import com.example.model.FantasyCharacter
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

interface PortraitGenerator {
    suspend fun generatePortrait(
        character: FantasyCharacter,
        variationNumber: Int,
    ): Bitmap
}

class FirebasePortraitGenerator : PortraitGenerator {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            },
        )
    }

    override suspend fun generatePortrait(
        character: FantasyCharacter,
        variationNumber: Int,
    ): Bitmap {
        val prompt = buildPortraitPrompt(character, variationNumber)
        return try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val imagePart = response.candidates
                .firstOrNull()
                ?.content
                ?.parts
                ?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            imagePart?.image
                ?: throw IllegalStateException("No portrait image was returned by the model.")
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Portrait generation timed out", e)
            throw IllegalStateException("Portrait generation timed out after 60 seconds. Please try again.")
        } catch (e: QuotaExceededException) {
            Log.e(TAG, "Portrait generation quota exceeded", e)
            throw IllegalStateException("Image generation quota exceeded. Please try again later.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Portrait generation failed", e)
            throw IllegalStateException(
                e.localizedMessage ?: "Failed to generate character portrait.",
            )
        }
    }

    companion object {
        private const val TAG = "FirebasePortraitGen"

        fun buildPortraitPrompt(
            character: FantasyCharacter,
            variationNumber: Int,
        ): String {
            val classVisualCue = when (character.characterClass) {
                CharacterClass.MAGE ->
                    "ornate arcane scholar robes, glowing violet rune sigils, crystalline magic energy around their hands, mystical staff silhouette"
                CharacterClass.ROGUE ->
                    "hooded leather cloak, agile stealth armor, emerald-tinted shadow mist, twin dagger hilts at the shoulder"
                CharacterClass.WARRIOR ->
                    "battle-worn crimson and steel plate armor, broad heroic shoulders, etched battle crest, fiery forge embers in the background"
                CharacterClass.PALADIN ->
                    "gleaming gilded knight armor, radiant golden halo light, sacred sun emblem on breastplate, noble resolute expression"
                CharacterClass.RANGER ->
                    "forest-green hooded mantle, weathered leather quiver and bowstrap, windswept leaves, misty woodland twilight"
                CharacterClass.NECROMANCER ->
                    "dark obsidian and teal bone-trimmed vestments, eerie spectral wisps, pale glowing eyes, ancient runic lantern glow"
                CharacterClass.DRUID ->
                    "antler-woven mantle and mossy bark-patterned cloak, glowing amber nature runes, drifting forest fireflies and autumn leaves"
            }

            val variationCue = when ((variationNumber - 1).coerceAtLeast(0) % 4) {
                0 -> "Heroic three-quarter bust angle, confident steady gaze, dramatic rim lighting."
                1 -> "Dynamic action-ready pose, intense focused expression, swirling magical atmosphere."
                2 -> "Front-facing regal character card portrait, warm lantern key light, intricate costume detail."
                else -> "Slight side profile turning toward the viewer, wind-swept hair and cloak, atmospheric sparks."
            }

            return buildString {
                append("Stylized cartoon video game character portrait of a fantasy ")
                append(character.characterClass.displayName)
                append(" named ")
                append(character.name)
                append(" (")
                append(character.characterClass.archetypeTitle)
                append("). ")
                append("Art style: vibrant modern fantasy RPG game concept art and stylized 2D cel-shaded illustration, crisp expressive linework, rich colors, clean bust framing centered on the character's face and upper armor. ")
                append("Visual details matching their class and identity: ")
                append(classVisualCue)
                append(", carrying or accented by ")
                append(character.signatureRelic)
                append(". ")
                append(variationCue)
                append(" No text, no watermarks, no UI borders.")
            }
        }
    }
}
