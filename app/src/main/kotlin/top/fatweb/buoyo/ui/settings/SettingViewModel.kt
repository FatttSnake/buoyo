package top.fatweb.buoyo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.model.userdata.UserData
import top.fatweb.buoyo.repository.userdata.UserDataRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map {
                SettingsUiState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = SettingsUiState.Loading,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
            )

    fun updateLanguageConfig(languageConfig: LanguageConfig) {
        viewModelScope.launch {
            userDataRepository.setLanguageConfig(languageConfig)
        }
    }

    fun updateThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        viewModelScope.launch {
            userDataRepository.setThemeBrandConfig(themeBrandConfig)
        }
    }

    fun updateUseDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseDynamicColor(useDynamicColor)
        }
    }

    fun updateThemeModeConfig(themeModeConfig: ThemeModeConfig) {
        viewModelScope.launch {
            userDataRepository.setThemeModeConfig(themeModeConfig)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserData) : SettingsUiState
}
