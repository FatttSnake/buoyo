package top.fatweb.buoyo.data.userdata

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import top.fatweb.buoyo.data.LanguageConfigProto
import top.fatweb.buoyo.data.ThemeBrandConfigProto
import top.fatweb.buoyo.data.ThemeModeConfigProto
import top.fatweb.buoyo.data.UserPreferences
import top.fatweb.buoyo.data.copy
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.model.userdata.UserData
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                languageConfig = when (it.languageConfig) {
                    null,
                    LanguageConfigProto.UNRECOGNIZED,
                    LanguageConfigProto.LANGUAGE_CONFIG_UNSPECIFIED,
                    LanguageConfigProto.LANGUAGE_CONFIG_FOLLOW_SYSTEM
                        -> LanguageConfig.FollowSystem

                    LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH -> LanguageConfig.English
                    LanguageConfigProto.LANGUAGE_CONFIG_CHINESE -> LanguageConfig.Chinese
                },
                themeBrandConfig = when (it.themeBrandConfig) {
                    null,
                    ThemeBrandConfigProto.UNRECOGNIZED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_UNSPECIFIED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                        ->
                        ThemeBrandConfig.Default

                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                        -> ThemeBrandConfig.Android

                },
                useDynamicColor = it.useDynamicColor,
                themeModeConfig = when (it.themeModeConfig) {
                    null,
                    ThemeModeConfigProto.UNRECOGNIZED,
                    ThemeModeConfigProto.THEME_MODE_CONFIG_UNSPECIFIED,
                    ThemeModeConfigProto.THEME_MODE_CONFIG_FOLLOW_SYSTEM
                        ->
                        ThemeModeConfig.FollowSystem

                    ThemeModeConfigProto.THEME_MODE_CONFIG_LIGHT
                        -> ThemeModeConfig.Light

                    ThemeModeConfigProto.THEME_MODE_CONFIG_DARK
                        -> ThemeModeConfig.Dark
                }
            )
        }

    suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        userPreferences.updateData {
            it.copy {
                this.languageConfig = when (languageConfig) {
                    LanguageConfig.FollowSystem -> LanguageConfigProto.LANGUAGE_CONFIG_FOLLOW_SYSTEM
                    LanguageConfig.Chinese -> LanguageConfigProto.LANGUAGE_CONFIG_CHINESE
                    LanguageConfig.English -> LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH
                }
            }
        }
    }

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        userPreferences.updateData {
            it.copy {
                this.themeBrandConfig = when (themeBrandConfig) {
                    ThemeBrandConfig.Default -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                    ThemeBrandConfig.Android -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                }
            }
        }
    }

    suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDynamicColor = useDynamicColor
            }
        }
    }

    suspend fun setThemeModeConfig(themeModeConfig: ThemeModeConfig) {
        userPreferences.updateData {
            it.copy {
                this.themeModeConfig = when (themeModeConfig) {
                    ThemeModeConfig.FollowSystem -> ThemeModeConfigProto.THEME_MODE_CONFIG_FOLLOW_SYSTEM
                    ThemeModeConfig.Dark -> ThemeModeConfigProto.THEME_MODE_CONFIG_DARK
                    ThemeModeConfig.Light -> ThemeModeConfigProto.THEME_MODE_CONFIG_LIGHT
                }
            }
        }
    }
}
