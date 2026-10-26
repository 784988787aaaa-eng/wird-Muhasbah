package com.example.feature.main

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.navigation.WirdBottomNavigationBar
import com.example.core.navigation.WirdRoutes
import com.example.core.navigation.WirdSideNavigationRail
import com.example.core.navigation.WirdTopLevelTab
import com.example.domain.model.PrayerDayLog
import com.example.domain.model.PrayerType
import com.example.feature.history.HistoryScreen
import com.example.feature.home.HomeScreen
import com.example.feature.messages.GoodPrayersScreen
import com.example.feature.onboarding.WelcomeOnboardingScreen
import com.example.feature.prayers.PrayerDetailScreen
import com.example.feature.reminders.RemindersScreen
import com.example.feature.settings.SettingsScreen
import com.example.feature.worship.WorshipScreen
import com.example.feature.worship.WorshipSubTab

@Composable
fun WirdAppRoot(
    uiState: WirdAppUiState,
    viewModel: WirdMainViewModel,
    modifier: Modifier = Modifier
) {
    // Show first-launch Welcome Onboarding screen until completed or skipped
    if (!uiState.preferences.isOnboardingCompleted) {
        WelcomeOnboardingScreen(
            initialName = uiState.preferences.displayName,
            onStartNow = { name -> viewModel.completeOnboarding(name) },
            modifier = modifier
        )
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: WirdTopLevelTab.HOME.route

    val currentTopLevelTab = remember(currentRoute) {
        WirdTopLevelTab.entries.firstOrNull { it.route == currentRoute } ?: WirdTopLevelTab.HOME
    }

    val isTopLevelDestination = remember(currentRoute) {
        WirdTopLevelTab.entries.any { it.route == currentRoute }
    }

    var preferredWorshipSubTab by remember { mutableStateOf(WorshipSubTab.TASBIH_AND_ADHKAR) }

    val navigateToTopLevelTab: (WirdTopLevelTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isExpandedScreen && isTopLevelDestination) {
                    WirdBottomNavigationBar(
                        currentTab = currentTopLevelTab,
                        onSelectTab = navigateToTopLevelTab
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen && isTopLevelDestination) {
                    WirdSideNavigationRail(
                        currentTab = currentTopLevelTab,
                        onSelectTab = navigateToTopLevelTab
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = WirdTopLevelTab.HOME.route,
                    modifier = Modifier.weight(1f)
                ) {
                    // 1. Home Tab («الرئيسية»)
                    composable(WirdTopLevelTab.HOME.route) {
                        val goodnessSuggestions = remember(uiState.todayActivities) {
                            val uncompleted = uiState.todayActivities.filter { !it.isCompleted }
                            (uncompleted + uiState.todayActivities.filter { it.isCompleted }).take(3)
                        }
                        HomeScreen(
                            gregorianDateText = uiState.gregorianDateText,
                            hijriDateText = uiState.hijriDateText,
                            greetingText = uiState.greetingText,
                            preferences = uiState.preferences,
                            prayersSummary = uiState.todayPrayersSummary,
                            activeHeartMessage = uiState.activeHeartMessage,
                            goodnessSuggestions = goodnessSuggestions,
                            allActivitiesCompletedCount = uiState.todayActivities.count { it.isCompleted },
                            allActivitiesTotalCount = uiState.todayActivities.size,
                            weeklyGlimpse = uiState.weeklyGlimpse,
                            showReturnAfterPauseBanner = uiState.showReturnAfterPauseBanner,
                            statusFeedbackMessage = uiState.calmFeedbackBanner,
                            onOpenPrayerDetail = { prayerType ->
                                navController.navigate(
                                    WirdRoutes.prayerDetailRoute(uiState.todayDateKey, prayerType.code)
                                )
                            },
                            onQuickTogglePrayer = { prayerType ->
                                viewModel.togglePrayerQuick(uiState.todayDateKey, prayerType)
                            },
                            onNextHeartMessage = viewModel::cycleNextHeartMessage,
                            onToggleActivity = { code ->
                                viewModel.toggleActivity(uiState.todayDateKey, code)
                            },
                            onOpenAllGoodDeeds = {
                                preferredWorshipSubTab = WorshipSubTab.GOOD_DEEDS_AND_QURAN
                                navigateToTopLevelTab(WirdTopLevelTab.WORSHIP)
                            },
                            onOpenHistory = {
                                navigateToTopLevelTab(WirdTopLevelTab.HISTORY)
                            },
                            onOpenSettings = {
                                navigateToTopLevelTab(WirdTopLevelTab.SETTINGS)
                            },
                            onAcknowledgeReturnBanner = viewModel::acknowledgeReturnBanner
                        )
                    }

                    // 2. Worship Tab («عباداتي»: Tasbih & Adhkar + Good Deeds & Quran)
                    composable(WirdTopLevelTab.WORSHIP.route) {
                        WorshipScreen(
                            initialSubTab = preferredWorshipSubTab,
                            tasbihState = uiState.tasbihState,
                            authenticAdhkar = uiState.authenticAdhkar,
                            activityRecords = uiState.todayActivities,
                            useEasternDigits = uiState.preferences.useEasternArabicDigits,
                            onIncrementTasbih = viewModel::incrementTasbih,
                            onResetTasbih = viewModel::resetTasbih,
                            onCycleTasbihDhikr = viewModel::cycleTasbihDhikr,
                            onSelectDhikrForTasbih = viewModel::selectDhikrForTasbih,
                            onToggleActivity = { code ->
                                viewModel.toggleActivity(uiState.todayDateKey, code)
                            },
                            onAddCustomActivity = viewModel::addCustomActivity,
                            onUpdateCustomActivity = viewModel::updateCustomActivity,
                            onDeleteCustomActivity = viewModel::deleteCustomActivity,
                            onReorderActivity = viewModel::reorderActivity
                        )
                    }

                    // 3. History Tab («سجلّي»)
                    composable(WirdTopLevelTab.HISTORY.route) {
                        HistoryScreen(
                            todayDate = uiState.todayDate,
                            selectedDateKey = uiState.selectedHistoryDateKey,
                            summariesByDate = uiState.allHistoryByDate,
                            useEasternDigits = uiState.preferences.useEasternArabicDigits,
                            onSelectDateKey = viewModel::selectHistoryDateKey,
                            onTogglePrayerOnDate = viewModel::togglePrayerQuick,
                            onOpenPrayerDetailOnDate = { dateKey, prayerType ->
                                navController.navigate(
                                    WirdRoutes.prayerDetailRoute(dateKey, prayerType.code)
                                )
                            },
                            onToggleActivityOnDate = viewModel::toggleActivity
                        )
                    }

                    // 4. Good Prayers & Messages Tab («دعوات طيبة»)
                    composable(WirdTopLevelTab.MESSAGES.route) {
                        GoodPrayersScreen(
                            messages = uiState.allHeartMessages
                        )
                    }

                    // 5. Settings / My Account Tab («حسابي / الإعدادات»)
                    composable(WirdTopLevelTab.SETTINGS.route) {
                        SettingsScreen(
                            preferences = uiState.preferences,
                            onUpdateDisplayName = viewModel::updateDisplayName,
                            onUpdateThemeMode = viewModel::updateThemeMode,
                            onUpdateMessageTone = viewModel::updateMessageTone,
                            onUpdateCardVisibility = viewModel::updateCardVisibility,
                            onUpdateHijriAndDigits = viewModel::updateHijriAndDigits,
                            onOpenReminders = {
                                navController.navigate(WirdRoutes.REMINDERS)
                            },
                            onClearAllLocalData = viewModel::clearAllLocalData
                        )
                    }

                    // 6. Sub-screen: Prayer Detail («تفاصيل الصلاة»)
                    composable(
                        route = "${WirdRoutes.PRAYER_DETAIL_PREFIX}/{dateKey}/{prayerCode}",
                        arguments = listOf(
                            navArgument("dateKey") { type = NavType.StringType },
                            navArgument("prayerCode") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val dateKey = entry.arguments?.getString("dateKey") ?: uiState.todayDateKey
                        val prayerCode = entry.arguments?.getString("prayerCode") ?: PrayerType.FAJR.code
                        val prayerType = PrayerType.fromCode(prayerCode)
                        val daySummary = uiState.allHistoryByDate[dateKey]
                        val currentLog = daySummary?.prayerLogs?.firstOrNull { it.prayerType == prayerType }
                            ?: uiState.todayPrayersSummary.logsByPrayer[prayerType]
                            ?: PrayerDayLog(dateKey = dateKey, prayerType = prayerType)

                        PrayerDetailScreen(
                            prayerLog = currentLog,
                            postPrayerAdhkarList = uiState.authenticAdhkar.filter { it.isPostPrayer },
                            onSavePrayer = { isCompleted, mode, postAdhkarDone, note ->
                                viewModel.savePrayerDetails(
                                    dateKey = dateKey,
                                    prayerType = prayerType,
                                    isCompleted = isCompleted,
                                    mode = mode,
                                    postPrayerAdhkarDone = postAdhkarDone,
                                    note = note
                                )
                                navController.popBackStack()
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // 7. Sub-screen: Reminders («التنبيهات»)
                    composable(WirdRoutes.REMINDERS) {
                        RemindersScreen(
                            preferences = uiState.preferences,
                            onUpdateQuietMode = viewModel::updateQuietMode,
                            onUpdateReminder = viewModel::updateReminder,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
