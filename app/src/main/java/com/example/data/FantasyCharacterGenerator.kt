package com.example.data

import com.example.model.CharacterAttributes
import com.example.model.CharacterClass
import com.example.model.FantasyCharacter
import kotlin.random.Random

class FantasyCharacterGenerator(
    private val random: Random = Random.Default,
) {
    private val firstNamePrefixes = listOf(
        "Ael", "Ald", "Ar", "Bael", "Bran", "Cael", "Cal", "Cor",
        "Dael", "Drak", "Elow", "El", "Fael", "Fen", "Garr", "Gwen",
        "Hald", "Hel", "Isol", "Ivor", "Jor", "Kael", "Kor", "Lor",
        "Ly", "Mal", "Mor", "Nyx", "Or", "Py", "Quen", "Ryn",
        "Ser", "Syl", "Thal", "Tor", "Ulf", "Vael", "Val", "Wyn",
        "Xan", "Ysol", "Zeph", "Zor",
    )

    private val firstNameMiddles = listOf(
        "a", "ae", "e", "i", "o", "y", "an", "ar", "el", "en", "il", "or",
    )

    private val firstNameSuffixes = listOf(
        "dor", "en", "eth", "ia", "iel", "indor", "ion", "is",
        "mir", "orn", "ra", "rix", "ryn", "thor", "us", "vash", "wyn",
    )

    private val curatedFirstNames = listOf(
        "Aurelia", "Caspian", "Elowen", "Theron", "Seraphina", "Kaelen",
        "Vespera", "Dorian", "Isolde", "Rowan", "Malakor", "Sylphira",
        "Garrick", "Nymeria", "Orin", "Freya", "Alaric", "Morgana",
    )

    private val surnameRoots = listOf(
        "Ash", "Black", "Dawn", "Dusk", "Ember", "Frost", "Gale", "Grim",
        "Iron", "Mist", "Moon", "Night", "Oak", "Rune", "Shadow", "Silver",
        "Star", "Storm", "Sun", "Thorn", "Void", "Wild", "Winter", "Wyrm",
    )

    private val surnameEndings = listOf(
        "bane", "blade", "born", "brook", "crest", "fall", "forge", "gaze",
        "heart", "keeper", "shield", "song", "spire", "stride", "vale",
        "veil", "ward", "walker", "weave", "whisper", "wind", "wood",
    )

    fun generate(
        rollNumber: Int,
        previousName: String? = null,
    ): FantasyCharacter {
        val selectedClass = CharacterClass.entries[random.nextInt(CharacterClass.entries.size)]
        var generatedName = buildFantasyName()
        var attempts = 0
        while (generatedName == previousName && attempts < 10) {
            generatedName = buildFantasyName()
            attempts++
        }

        val relic = selectedClass.relics[random.nextInt(selectedClass.relics.size)]
        val attributes = CharacterAttributes(
            might = (selectedClass.baseMight + random.nextInt(-1, 2)).coerceIn(1, 10),
            arcana = (selectedClass.baseArcana + random.nextInt(-1, 2)).coerceIn(1, 10),
            agility = (selectedClass.baseAgility + random.nextInt(-1, 2)).coerceIn(1, 10),
        )

        return FantasyCharacter(
            id = System.nanoTime() + rollNumber,
            rollNumber = rollNumber,
            name = generatedName,
            characterClass = selectedClass,
            signatureRelic = relic,
            attributes = attributes,
        )
    }

    private fun buildFantasyName(): String {
        val firstName = if (random.nextFloat() < 0.28f) {
            curatedFirstNames[random.nextInt(curatedFirstNames.size)]
        } else {
            val prefix = firstNamePrefixes[random.nextInt(firstNamePrefixes.size)]
            val useMiddle = random.nextFloat() < 0.45f
            val middle = if (useMiddle) {
                firstNameMiddles[random.nextInt(firstNameMiddles.size)]
            } else {
                ""
            }
            val suffix = firstNameSuffixes[random.nextInt(firstNameSuffixes.size)]
            cleanDoubleVowels("$prefix$middle$suffix")
        }

        val surnameRoot = surnameRoots[random.nextInt(surnameRoots.size)]
        var surnameEnding = surnameEndings[random.nextInt(surnameEndings.size)]
        while (surnameEnding.equals(surnameRoot, ignoreCase = true)) {
            surnameEnding = surnameEndings[random.nextInt(surnameEndings.size)]
        }

        return "$firstName $surnameRoot$surnameEnding"
    }

    private fun cleanDoubleVowels(raw: String): String {
        val collapsed = raw.replace(Regex("([aeiouyAEIOUY])\\1{2,}"), "$1$1")
        return collapsed.replaceFirstChar { it.uppercaseChar() }
    }
}
