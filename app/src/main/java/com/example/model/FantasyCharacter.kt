package com.example.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.DruidAmber
import com.example.ui.theme.MageViolet
import com.example.ui.theme.NecromancerTeal
import com.example.ui.theme.PaladinGold
import com.example.ui.theme.RangerMoss
import com.example.ui.theme.RogueEmerald
import com.example.ui.theme.WarriorCrimson

enum class CharacterClass(
    @StringRes val labelRes: Int,
    val displayName: String,
    val archetypeTitle: String,
    val accentColor: Color,
    val baseMight: Int,
    val baseArcana: Int,
    val baseAgility: Int,
    val relics: List<String>,
) {
    MAGE(
        labelRes = R.string.class_mage,
        displayName = "Mage",
        archetypeTitle = "Weaver of Astral Flame",
        accentColor = MageViolet,
        baseMight = 3,
        baseArcana = 10,
        baseAgility = 5,
        relics = listOf(
            "Starlight Spellstaff",
            "Codex of Infinite Embers",
            "Orb of the Astral Spire",
        ),
    ),
    ROGUE(
        labelRes = R.string.class_rogue,
        displayName = "Rogue",
        archetypeTitle = "Phantom of the Velvet Alley",
        accentColor = RogueEmerald,
        baseMight = 5,
        baseArcana = 4,
        baseAgility = 10,
        relics = listOf(
            "Twin Whisperblades",
            "Cloak of Eclipse Silk",
            "Skeleton Key of Glass",
        ),
    ),
    WARRIOR(
        labelRes = R.string.class_warrior,
        displayName = "Warrior",
        archetypeTitle = "Breaker of Siege Lines",
        accentColor = WarriorCrimson,
        baseMight = 10,
        baseArcana = 2,
        baseAgility = 6,
        relics = listOf(
            "Sunforged Greatsword",
            "Bulwark of Ironpeak",
            "Warhorn of the Vanguard",
        ),
    ),
    PALADIN(
        labelRes = R.string.class_paladin,
        displayName = "Paladin",
        archetypeTitle = "Beacon of the Dawn Oath",
        accentColor = PaladinGold,
        baseMight = 8,
        baseArcana = 7,
        baseAgility = 4,
        relics = listOf(
            "Aegis of the First Sun",
            "Oathkeeper Warhammer",
            "Chalice of Sacred Radiance",
        ),
    ),
    RANGER(
        labelRes = R.string.class_ranger,
        displayName = "Ranger",
        archetypeTitle = "Tracker of the Whispering Wilds",
        accentColor = RangerMoss,
        baseMight = 6,
        baseArcana = 5,
        baseAgility = 9,
        relics = listOf(
            "Silverwood Longbow",
            "Quiver of Gale Arrows",
            "Compass of Starlight Trails",
        ),
    ),
    NECROMANCER(
        labelRes = R.string.class_necromancer,
        displayName = "Necromancer",
        archetypeTitle = "Keeper of Twilight Echoes",
        accentColor = NecromancerTeal,
        baseMight = 3,
        baseArcana = 9,
        baseAgility = 5,
        relics = listOf(
            "Lantern of Lost Souls",
            "Bonebound Grimoire",
            "Scythe of Pale Frost",
        ),
    ),
    DRUID(
        labelRes = R.string.class_druid,
        displayName = "Druid",
        archetypeTitle = "Voice of the Ancient Canopy",
        accentColor = DruidAmber,
        baseMight = 5,
        baseArcana = 8,
        baseAgility = 7,
        relics = listOf(
            "Heartwood Totem Staff",
            "Crown of Autumn Antlers",
            "Vial of Moonwell Dew",
        ),
    ),
}

data class CharacterAttributes(
    val might: Int,
    val arcana: Int,
    val agility: Int,
)

data class FantasyCharacter(
    val id: Long,
    val rollNumber: Int,
    val name: String,
    val characterClass: CharacterClass,
    val signatureRelic: String,
    val attributes: CharacterAttributes,
)
