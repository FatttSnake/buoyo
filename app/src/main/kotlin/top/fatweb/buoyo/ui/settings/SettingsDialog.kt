package top.fatweb.buoyo.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.buoyo.R
import top.fatweb.buoyo.icon.BuoyoIcons
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.model.userdata.UserData
import top.fatweb.buoyo.ui.component.DialogChooserRow
import top.fatweb.buoyo.ui.component.DialogClickerRow
import top.fatweb.buoyo.ui.component.DialogSectionGroup
import top.fatweb.buoyo.ui.component.DialogSectionTitle
import top.fatweb.buoyo.ui.component.Indicator
import top.fatweb.buoyo.ui.theme.supportsDynamicTheming

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit,
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsDialog(
        modifier = modifier,
        settingsUiState = settingsUiState,
        onNavigateToLibraries = onNavigateToLibraries,
        onNavigateToAbout = onNavigateToAbout,
        onDismiss = onDismiss,
        onChangeLanguageConfig = viewModel::updateLanguageConfig,
        onChangeThemeBrandConfig = viewModel::updateThemeBrandConfig,
        onChangeUseDynamicColor = viewModel::updateUseDynamicColor,
        onChangeThemeModeConfig = viewModel::updateThemeModeConfig
    )
}

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit,
    onChangeLanguageConfig: (LanguageConfig) -> Unit,
    onChangeThemeBrandConfig: (ThemeBrandConfig) -> Unit,
    onChangeUseDynamicColor: (Boolean) -> Unit,
    onChangeThemeModeConfig: (ThemeModeConfig) -> Unit
) {
    val windowInfo = LocalWindowInfo.current

    AlertDialog(
        modifier = modifier
            .widthIn(max = windowInfo.containerSize.width.dp - 80.dp)
            .heightIn(max = windowInfo.containerSize.height.dp - 40.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.feature_settings_title)
            )
        },
        text = {
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                when (settingsUiState) {
                    SettingsUiState.Loading -> {
                        Indicator()
                    }

                    is SettingsUiState.Success -> {
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
                            onNavigateToLibraries = onNavigateToLibraries,
                            onNavigateToAbout = onNavigateToAbout,
                            onDismiss = onDismiss,
                            onChangeLanguageConfig = onChangeLanguageConfig,
                            onChangeThemeBrandConfig = onChangeThemeBrandConfig,
                            onChangeUseDynamicColor = onChangeUseDynamicColor,
                            onChangeThemeModeConfig = onChangeThemeModeConfig
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(R.string.core_ok)
                )
            }
        },
    )
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserData,
    supportDynamicColor: Boolean,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit,
    onChangeLanguageConfig: (LanguageConfig) -> Unit,
    onChangeThemeBrandConfig: (ThemeBrandConfig) -> Unit,
    onChangeUseDynamicColor: (Boolean) -> Unit,
    onChangeThemeModeConfig: (ThemeModeConfig) -> Unit
) {
    DialogSectionTitle(text = stringResource(R.string.feature_settings_language))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_system_default),
            selected = settings.languageConfig == LanguageConfig.FollowSystem,
            onClick = {
                onChangeLanguageConfig(LanguageConfig.FollowSystem)
            }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_chinese),
            selected = settings.languageConfig == LanguageConfig.Chinese,
            onClick = {
                onChangeLanguageConfig(LanguageConfig.Chinese)
            }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_english),
            selected = settings.languageConfig == LanguageConfig.English,
            onClick = {
                onChangeLanguageConfig(LanguageConfig.English)
            }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_theme_brand))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_default),
            selected = settings.themeBrandConfig == ThemeBrandConfig.Default,
            onClick = {
                onChangeThemeBrandConfig(ThemeBrandConfig.Default)
            }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_android),
            selected = settings.themeBrandConfig == ThemeBrandConfig.Android,
            onClick = {
                onChangeThemeBrandConfig(ThemeBrandConfig.Android)
            }
        )
    }
    AnimatedVisibility(
        visible = settings.themeBrandConfig == ThemeBrandConfig.Default && supportDynamicColor
    ) {
        DialogSectionGroup {
            DialogSectionTitle(text = stringResource(R.string.feature_settings_dynamic_color))
            DialogChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_enable),
                selected = settings.useDynamicColor,
                onClick = {
                    onChangeUseDynamicColor(true)
                }
            )
            DialogChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_disable),
                selected = !settings.useDynamicColor,
                onClick = {
                    onChangeUseDynamicColor(false)
                }
            )
        }
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_theme_mode))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_mode_system_default),
            selected = settings.themeModeConfig == ThemeModeConfig.FollowSystem,
            onClick = {
                onChangeThemeModeConfig(ThemeModeConfig.FollowSystem)
            }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_mode_light),
            selected = settings.themeModeConfig == ThemeModeConfig.Light,
            onClick = {
                onChangeThemeModeConfig(ThemeModeConfig.Light)
            }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_mode_dark),
            selected = settings.themeModeConfig == ThemeModeConfig.Dark,
            onClick = {
                onChangeThemeModeConfig(ThemeModeConfig.Dark)
            }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_more))
    DialogSectionGroup {
        DialogClickerRow(
            icon = BuoyoIcons.Code,
            text = stringResource(R.string.feature_settings_more_open_source_license),
            onClick = {
                onNavigateToLibraries()
                onDismiss()
            }
        )
        DialogClickerRow(
            icon = BuoyoIcons.Info,
            text = stringResource(R.string.feature_settings_more_about),
            onClick = {
                onNavigateToAbout()
                onDismiss()
            }
        )
    }
}
