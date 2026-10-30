package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AudioBottomPlayer
import com.example.ui.components.TasbihDialog
import com.example.ui.screens.AiMoodScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.ExploreScreen

@Composable
fun MainAppScreen(
    viewModel: DoaViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val displayedDoas by viewModel.displayedDoas.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories

    val playbackState by viewModel.playbackState.collectAsState()

    val selectedMood by viewModel.selectedMood.collectAsState()
    val userMoodNotes by viewModel.userMoodNotes.collectAsState()
    val aiRecommendationState by viewModel.aiRecommendationState.collectAsState()

    val activeTasbihDoa by viewModel.activeTasbihDoa.collectAsState()
    val tasbihCount by viewModel.tasbihCount.collectAsState()

    // Handle system back navigation if not on home Explore tab
    if (currentTab != DoaViewModel.AppTab.EXPLORE) {
        BackHandler {
            viewModel.selectTab(DoaViewModel.AppTab.EXPLORE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                // Tab 1: Jelajah Doa
                NavigationBarItem(
                    selected = currentTab == DoaViewModel.AppTab.EXPLORE,
                    onClick = { viewModel.selectTab(DoaViewModel.AppTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == DoaViewModel.AppTab.EXPLORE) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Jelajah Doa"
                        )
                    },
                    label = {
                        Text(
                            text = "Jelajah",
                            fontWeight = if (currentTab == DoaViewModel.AppTab.EXPLORE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_explore")
                )

                // Tab 2: Fire AI Mood
                NavigationBarItem(
                    selected = currentTab == DoaViewModel.AppTab.AI_MOOD,
                    onClick = { viewModel.selectTab(DoaViewModel.AppTab.AI_MOOD) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == DoaViewModel.AppTab.AI_MOOD) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Fire AI Mood"
                        )
                    },
                    label = {
                        Text(
                            text = "Fire AI Mood",
                            fontWeight = if (currentTab == DoaViewModel.AppTab.AI_MOOD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        indicatorColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.testTag("nav_ai_mood")
                )

                // Tab 3: Bookmark
                NavigationBarItem(
                    selected = currentTab == DoaViewModel.AppTab.BOOKMARKS,
                    onClick = { viewModel.selectTab(DoaViewModel.AppTab.BOOKMARKS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == DoaViewModel.AppTab.BOOKMARKS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Doa Tersimpan"
                        )
                    },
                    label = {
                        Text(
                            text = "Tersimpan",
                            fontWeight = if (currentTab == DoaViewModel.AppTab.BOOKMARKS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_bookmarks")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                DoaViewModel.AppTab.EXPLORE -> {
                    ExploreScreen(
                        doas = displayedDoas,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onSelectCategory = viewModel::selectCategory,
                        playbackState = playbackState,
                        onPlayAudio = viewModel::playAudio,
                        onStopAudio = viewModel::stopAudio,
                        onToggleBookmark = viewModel::toggleBookmark,
                        onOpenTasbih = viewModel::openTasbihCounter,
                        onNavigateToAiMood = { viewModel.selectTab(DoaViewModel.AppTab.AI_MOOD) }
                    )
                }

                DoaViewModel.AppTab.AI_MOOD -> {
                    AiMoodScreen(
                        selectedMood = selectedMood,
                        onSelectMood = viewModel::selectMood,
                        userNotes = userMoodNotes,
                        onUserNotesChange = viewModel::updateUserMoodNotes,
                        aiRecommendationState = aiRecommendationState,
                        onConsultAi = viewModel::consultFireAi,
                        playbackState = playbackState,
                        onPlayAudio = viewModel::playAudio,
                        onStopAudio = viewModel::stopAudio,
                        onToggleBookmark = viewModel::saveAiDoa,
                        onOpenTasbih = viewModel::openTasbihCounter
                    )
                }

                DoaViewModel.AppTab.BOOKMARKS -> {
                    BookmarksScreen(
                        bookmarkedDoas = displayedDoas,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        playbackState = playbackState,
                        onPlayAudio = viewModel::playAudio,
                        onStopAudio = viewModel::stopAudio,
                        onToggleBookmark = viewModel::toggleBookmark,
                        onOpenTasbih = viewModel::openTasbihCounter,
                        onNavigateToExplore = { viewModel.selectTab(DoaViewModel.AppTab.EXPLORE) }
                    )
                }
            }

            // Sticky Bottom Audio Player Bar
            AudioBottomPlayer(
                playbackState = playbackState,
                onStopAudio = viewModel::stopAudio,
                onSelectPlayMode = viewModel::setAudioPlayMode,
                onSelectSpeedRate = viewModel::setSpeechRate,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
        }
    }

    // Digital Tasbih Dialog
    val currentTasbihDoa = activeTasbihDoa
    if (currentTasbihDoa != null) {
        TasbihDialog(
            doa = currentTasbihDoa,
            count = tasbihCount,
            onIncrement = viewModel::incrementTasbih,
            onReset = viewModel::resetTasbih,
            onDismiss = viewModel::closeTasbihCounter
        )
    }
}
