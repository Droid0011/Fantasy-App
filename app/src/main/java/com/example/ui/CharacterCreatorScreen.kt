package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CharacterAttributes
import com.example.model.FantasyCharacter
import com.example.ui.components.ClassSigil

@Composable
fun CharacterCreatorScreen(
    uiState: CharacterCreatorUiState,
    onGenerateClick: () -> Unit,
    onGeneratePortraitClick: () -> Unit,
    onRegeneratePortraitClick: () -> Unit,
    onSelectRecent: (FantasyCharacter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val activeAccent = uiState.currentCharacter.characterClass.accentColor

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeAccent.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                )
                .padding(innerPadding),
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
            ) {
                val isWideLayout = maxWidth >= 680.dp

                if (isWideLayout) {
                    WideCharacterCreatorLayout(
                        uiState = uiState,
                        onGenerateClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onGenerateClick()
                        },
                        onGeneratePortraitClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onGeneratePortraitClick()
                        },
                        onRegeneratePortraitClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onRegeneratePortraitClick()
                        },
                        onSelectRecent = onSelectRecent,
                    )
                } else {
                    CompactCharacterCreatorLayout(
                        uiState = uiState,
                        onGenerateClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onGenerateClick()
                        },
                        onGeneratePortraitClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onGeneratePortraitClick()
                        },
                        onRegeneratePortraitClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onRegeneratePortraitClick()
                        },
                        onSelectRecent = onSelectRecent,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactCharacterCreatorLayout(
    uiState: CharacterCreatorUiState,
    onGenerateClick: () -> Unit,
    onGeneratePortraitClick: () -> Unit,
    onRegeneratePortraitClick: () -> Unit,
    onSelectRecent: (FantasyCharacter) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            RealmHeaderBanner(
                bannerHeight = 96.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
            )

            CharacterGrimoireCard(
                character = uiState.currentCharacter,
                portraitState = uiState.portraitState,
                onGeneratePortraitClick = onGeneratePortraitClick,
                onRegeneratePortraitClick = onRegeneratePortraitClick,
                isCompact = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
            )

            if (uiState.recentCharacters.size > 1) {
                RecentSummonsStrip(
                    recentCharacters = uiState.recentCharacters,
                    selectedCharacterId = uiState.currentCharacter.id,
                    onSelectRecent = onSelectRecent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            GenerateCharacterButton(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
            )
        }
    }
}

@Composable
private fun WideCharacterCreatorLayout(
    uiState: CharacterCreatorUiState,
    onGenerateClick: () -> Unit,
    onGeneratePortraitClick: () -> Unit,
    onRegeneratePortraitClick: () -> Unit,
    onSelectRecent: (FantasyCharacter) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1040.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                RealmHeaderBanner(modifier = Modifier.fillMaxWidth())

                GenerateCharacterButton(
                    onClick = onGenerateClick,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (uiState.recentCharacters.size > 1) {
                    RecentSummonsStrip(
                        recentCharacters = uiState.recentCharacters,
                        selectedCharacterId = uiState.currentCharacter.id,
                        onSelectRecent = onSelectRecent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1.05f)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                CharacterGrimoireCard(
                    character = uiState.currentCharacter,
                    portraitState = uiState.portraitState,
                    onGeneratePortraitClick = onGeneratePortraitClick,
                    onRegeneratePortraitClick = onRegeneratePortraitClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RealmHeaderBanner(
    modifier: Modifier = Modifier,
    bannerHeight: Dp = 144.dp,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_fantasy_realm_banner),
                contentDescription = stringResource(id = R.string.cd_realm_banner),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x550D0F17),
                                Color(0xCC0D0F17),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.heading_fantasy_character_creator),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFF6EFE0),
                    modifier = Modifier.testTag("app_heading"),
                )
                Text(
                    text = stringResource(id = R.string.subtitle_realm_forge),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD5C8B0),
                )
            }
        }
    }
}

@Composable
private fun CharacterGrimoireCard(
    character: FantasyCharacter,
    portraitState: PortraitUiState,
    onGeneratePortraitClick: () -> Unit,
    onRegeneratePortraitClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
) {
    val accentColor = character.characterClass.accentColor
    val isPortraitLoading = portraitState is PortraitUiState.Loading

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.65f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isCompact) 18.dp else 22.dp,
                    vertical = if (isCompact) 16.dp else 22.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            AnimatedContent(
                targetState = character,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) +
                        scaleIn(
                            initialScale = 0.96f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        )) togetherWith fadeOut(animationSpec = tween(140))
                },
                label = "character_card_reveal",
            ) { hero ->
                val heroAccent = hero.characterClass.accentColor
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = heroAccent.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, heroAccent.copy(alpha = 0.45f)),
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_character_number, hero.rollNumber),
                                style = MaterialTheme.typography.labelSmall,
                                color = heroAccent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = stringResource(id = R.string.label_character_class).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    PortraitShowcase(
                        character = hero,
                        portraitState = portraitState,
                        isCompact = isCompact,
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_character_name).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = hero.name,
                            style = if (isCompact) {
                                MaterialTheme.typography.headlineMedium
                            } else {
                                MaterialTheme.typography.displaySmall
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("character_name"),
                        )
                    }

                    // Class Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = heroAccent.copy(alpha = 0.18f),
                        border = BorderStroke(1.2.dp, heroAccent),
                        modifier = Modifier.testTag("character_class"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(heroAccent),
                            )
                            Text(
                                text = stringResource(id = hero.characterClass.labelRes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Text(
                        text = hero.characterClass.archetypeTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            PortraitActionButtons(
                isLoading = isPortraitLoading,
                accentColor = accentColor,
                onGeneratePortraitClick = onGeneratePortraitClick,
                onRegeneratePortraitClick = onRegeneratePortraitClick,
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            )

            AttributeMetersSection(
                attributes = character.attributes,
                accentColor = accentColor,
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(id = R.string.label_signature_gear),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = character.signatureRelic,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortraitShowcase(
    character: FantasyCharacter,
    portraitState: PortraitUiState,
    isCompact: Boolean,
) {
    val accent = character.characterClass.accentColor
    val classLabel = stringResource(id = character.characterClass.labelRes)
    val matchingSuccess = (portraitState as? PortraitUiState.Success)
        ?.takeIf { it.characterId == character.id }
    val matchingLoading = (portraitState as? PortraitUiState.Loading)
        ?.takeIf { it.characterId == character.id }
    val matchingError = (portraitState as? PortraitUiState.Error)
        ?.takeIf { it.characterId == character.id }

    when {
        matchingSuccess != null -> {
            val frameWidth = if (isCompact) 208.dp else 248.dp
            Box(
                modifier = Modifier
                    .width(frameWidth)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                accent,
                            ),
                        ),
                        shape = MaterialTheme.shapes.large,
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Image(
                    bitmap = matchingSuccess.bitmap.asImageBitmap(),
                    contentDescription = stringResource(
                        id = R.string.cd_character_portrait,
                        character.name,
                        classLabel,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("character_portrait_image"),
                    contentScale = ContentScale.Crop,
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xCC0D0F17),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.label_portrait_version,
                            matchingSuccess.version,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF6EFE0),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }

        matchingLoading != null -> {
            val frameWidth = if (isCompact) 196.dp else 232.dp
            Surface(
                modifier = Modifier
                    .width(frameWidth)
                    .aspectRatio(1f)
                    .testTag("portrait_loading_indicator"),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, accent.copy(alpha = 0.65f)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        color = accent,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(
                            id = if (matchingLoading.isRegenerate) {
                                R.string.label_portrait_regenerating
                            } else {
                                R.string.label_portrait_loading
                            },
                            character.name,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        matchingError != null -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ClassSigil(
                    characterClass = character.characterClass,
                    size = if (isCompact) 72.dp else 96.dp,
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("portrait_error_message"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = matchingError.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }

        else -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ClassSigil(
                    characterClass = character.characterClass,
                    size = if (isCompact) 76.dp else 104.dp,
                )
                Text(
                    text = stringResource(id = R.string.label_portrait_idle_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PortraitActionButtons(
    isLoading: Boolean,
    accentColor: Color,
    onGeneratePortraitClick: () -> Unit,
    onRegeneratePortraitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalButton(
            onClick = onGeneratePortraitClick,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .minimumInteractiveComponentSize()
                .testTag("generate_portrait_button"),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = accentColor.copy(alpha = 0.22f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Brush,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = R.string.action_generate_portrait),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }

        OutlinedButton(
            onClick = onRegeneratePortraitClick,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .minimumInteractiveComponentSize()
                .testTag("regenerate_portrait_button"),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = R.string.action_regenerate_portrait),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun AttributeMetersSection(
    attributes: CharacterAttributes,
    accentColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.label_archetype_traits),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AttributeBarRow(
            label = stringResource(id = R.string.stat_might),
            value = attributes.might,
            accentColor = accentColor,
        )
        AttributeBarRow(
            label = stringResource(id = R.string.stat_arcana),
            value = attributes.arcana,
            accentColor = accentColor,
        )
        AttributeBarRow(
            label = stringResource(id = R.string.stat_agility),
            value = attributes.agility,
            accentColor = accentColor,
        )
    }
}

@Composable
private fun AttributeBarRow(
    label: String,
    value: Int,
    accentColor: Color,
) {
    val animatedFraction by animateFloatAsState(
        targetValue = (value / 10f).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "attribute_bar",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(64.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.72f),
                                accentColor,
                            ),
                        ),
                    ),
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(22.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RecentSummonsStrip(
    recentCharacters: List<FantasyCharacter>,
    selectedCharacterId: Long,
    onSelectRecent: (FantasyCharacter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.label_recent_summons),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
        ) {
            items(
                items = recentCharacters,
                key = { it.id },
            ) { hero ->
                val isSelected = hero.id == selectedCharacterId
                val chipAccent = hero.characterClass.accentColor
                Surface(
                    onClick = { onSelectRecent(hero) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) {
                        chipAccent.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
                    },
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) {
                            chipAccent
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                        },
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = hero.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                        )
                        Text(
                            text = stringResource(id = hero.characterClass.labelRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = chipAccent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateCharacterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(58.dp)
            .minimumInteractiveComponentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
                shape = MaterialTheme.shapes.medium,
            )
            .testTag("generate_character_button"),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.Casino,
            contentDescription = stringResource(id = R.string.cd_generate_dice),
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.action_generate_character),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
