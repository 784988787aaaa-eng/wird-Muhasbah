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
import com.example.core.designsystem.theme.WirdTheme
import com.example.data.local.datastore.DataStorePreferencesDataSource
import com.example.data.local.db.WirdAppDatabase
import com.example.data.repository.WirdDomainRepository
import com.example.domain.model.ThemeModePreference
import com.example.feature.main.WirdAppRoot
import com.example.feature.main.WirdMainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = WirdAppDatabase.getInstance(applicationContext)
        val preferencesDataSource = DataStorePreferencesDataSource(applicationContext)
        val repository = WirdDomainRepository(
            dao = database.dao(),
            preferencesDataSource = preferencesDataSource
        )

        setContent {
            val viewModel: WirdMainViewModel = viewModel(
                factory = WirdMainViewModel.Factory(
                    repository = repository,
                    preferencesDataSource = preferencesDataSource
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.refreshTodaySession()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (uiState.preferences.themeMode) {
                ThemeModePreference.SYSTEM -> systemDark
                ThemeModePreference.DARK -> true
                ThemeModePreference.LIGHT -> false
            }

            WirdTheme(darkTheme = useDarkTheme) {
                WirdAppRoot(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}
