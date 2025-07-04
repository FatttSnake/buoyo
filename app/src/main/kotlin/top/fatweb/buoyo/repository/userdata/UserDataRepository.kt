package top.fatweb.buoyo.repository.userdata

import kotlinx.coroutines.flow.Flow
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.model.userdata.UserData

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setLanguageConfig(languageConfig: LanguageConfig)

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig)

    suspend fun setUseDynamicColor(useDynamicColor: Boolean)

    suspend fun setThemeModeConfig(themeModeConfig: ThemeModeConfig)
}
