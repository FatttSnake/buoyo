package top.fatweb.buoyo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.monitor.TimeZoneMonitor
import top.fatweb.buoyo.repository.userdata.UserDataRepository
import top.fatweb.buoyo.ui.BuoyoApp
import top.fatweb.buoyo.ui.rememberBuoyoAppState
import top.fatweb.buoyo.ui.theme.BuoyoTheme
import top.fatweb.buoyo.ui.util.LocalTimeZone
import top.fatweb.buoyo.ui.util.LocaleHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)
        val showSettingsDialogState = mutableStateOf(false)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        setContent {
            val locale = whatLocale(uiState)
            if (uiState is MainActivityUiState.Success) {
                LaunchedEffect(locale) {
                    LocaleHelper.switchLocale(
                        activity = this@MainActivity,
                        languageConfig = locale,
                        beforeSwitch = {
                            showSettingsDialogState.value = false
                        }
                    )
                }
            }
            val isDarkTheme = shouldUseDarkTheme(uiState)
            val appState = rememberBuoyoAppState(
                windowSizeClass = calculateWindowSizeClass(this),
                timeZoneMonitor = timeZoneMonitor
            )
            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            LaunchedEffect(isDarkTheme) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                }
            }

            CompositionLocalProvider(LocalTimeZone provides currentTimeZone) {
                BuoyoTheme(
                    darkTheme = isDarkTheme,
                    androidTheme = shouldUseAndroidTheme(uiState),
                    dynamicColor = shouldUseDynamicColor(uiState)
                ) {
                    BuoyoApp(
                        appState = appState,
                        showSettingsDialogState = showSettingsDialogState
                    )
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserDataRepositoryEntryPoint {
        val userDataRepository: UserDataRepository
    }

    override fun attachBaseContext(newBase: Context) {
        val userDataRepository =
            EntryPointAccessors.fromApplication<UserDataRepositoryEntryPoint>(newBase).userDataRepository
        val languageConfig = runBlocking {
            userDataRepository.userData.first().languageConfig
        }

        super.attachBaseContext(
            LocaleHelper.attachBaseContext(
                context = newBase,
                languageConfig = languageConfig
            )
        )
    }
}

@Composable
private fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success ->
            when (uiState.userData.themeModeConfig) {
                ThemeModeConfig.FollowSystem -> isSystemInDarkTheme()
                ThemeModeConfig.Light -> false
                ThemeModeConfig.Dark -> true
            }
    }

@Composable
private fun shouldUseAndroidTheme(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success ->
            when (uiState.userData.themeBrandConfig) {
                ThemeBrandConfig.Default -> false
                ThemeBrandConfig.Android -> true
            }
    }

@Composable
private fun shouldUseDynamicColor(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> true
        is MainActivityUiState.Success -> uiState.userData.useDynamicColor
    }

@Composable
private fun whatLocale(uiState: MainActivityUiState): LanguageConfig =
    when (uiState) {
        MainActivityUiState.Loading -> LanguageConfig.FollowSystem
        is MainActivityUiState.Success -> uiState.userData.languageConfig
    }
