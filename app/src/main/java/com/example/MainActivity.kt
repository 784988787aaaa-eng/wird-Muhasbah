package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DarkModePreference
import com.example.data.WirdDatabase
import com.example.data.WirdRepository
import com.example.ui.WirdHomeScreen
import com.example.ui.WirdViewModel
import com.example.ui.theme.WirdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = WirdDatabase.getInstance(applicationContext)
        val repository = WirdRepository(database.wirdDao())

        setContent {
            val viewModel: WirdViewModel = viewModel(
                factory = WirdViewModel.Factory(repository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.refreshToday()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (uiState.darkModeEnum) {
                DarkModePreference.SYSTEM -> systemDark
                DarkModePreference.DARK -> true
                DarkModePreference.LIGHT -> false
            }

            WirdTheme(
                darkTheme = useDarkTheme,
                genderGroup = uiState.genderGroupEnum,
                ageGroup = uiState.ageGroupEnum
            ) {
                WirdHomeScreen(
                    uiState = uiState,
                    onToggleDailyItem = viewModel::onToggleDailyItem,
                    onToggleFivePrayersExpanded = viewModel::onToggleFivePrayersExpanded,
                    onToggleSinglePrayer = viewModel::onToggleSinglePrayer,
                    onSetAllFivePrayers = viewModel::onSetAllFivePrayers,
                    onCycleMotivation = viewModel::cycleMotivation,
                    onFlashNotificationSent = viewModel::onFlashNotificationSent,
                    onOpenCounter = viewModel::openCounter,
                    onCloseCounter = viewModel::closeCounter,
                    onIncrementCounter = viewModel::incrementCounter,
                    onCycleCounterDhikr = viewModel::cycleCounterDhikr,
                    onRequestResetCounter = viewModel::requestResetCounter,
                    onConfirmResetCounter = viewModel::confirmResetCounter,
                    onCancelResetCounter = viewModel::cancelResetCounter,
                    onCycleAdhkar = viewModel::cycleSuggestedAdhkar,
                    onSelectDhikr = viewModel::openDhikrReader,
                    onCloseDhikrReader = viewModel::closeDhikrReader,
                    onOpenCustomization = viewModel::openCustomization,
                    onCloseCustomization = viewModel::closeCustomization,
                    onOpenHistory = viewModel::openHistory,
                    onCloseHistory = viewModel::closeHistory,
                    onApplyPreset = viewModel::applyProfilePreset,
                    onDismissOnboarding = viewModel::dismissInitialOnboarding,
                    onToggleTemplateItem = viewModel::toggleTemplateItem,
                    onMoveTemplateItem = viewModel::moveTemplateItem,
                    onReorderTemplateItems = viewModel::reorderTemplateItems,
                    onAddCustomDeed = viewModel::addCustomDeed,
                    onDeleteCustomDeed = viewModel::deleteCustomDeed,
                    onCycleDarkMode = viewModel::cycleDarkMode,
                    onSetDarkMode = viewModel::setDarkModePreference
                )
            }
        }
    }
}
