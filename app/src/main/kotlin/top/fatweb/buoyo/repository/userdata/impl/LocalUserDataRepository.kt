package top.fatweb.buoyo.repository.userdata.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.buoyo.data.userdata.PreferencesDataSource
import top.fatweb.buoyo.model.userdata.LanguageConfig
import top.fatweb.buoyo.model.userdata.ThemeBrandConfig
import top.fatweb.buoyo.model.userdata.ThemeModeConfig
import top.fatweb.buoyo.model.userdata.UserData
import top.fatweb.buoyo.repository.userdata.UserDataRepository
import javax.inject.Inject

class LocalUserDataRepository @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> =
        preferencesDataSource.userData

    override suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        preferencesDataSource.setLanguageConfig(languageConfig)
    }

    override suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        preferencesDataSource.setThemeBrandConfig(themeBrandConfig)
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        preferencesDataSource.setUseDynamicColor(useDynamicColor)
    }

    override suspend fun setThemeModeConfig(themeModeConfig: ThemeModeConfig) {
        preferencesDataSource.setThemeModeConfig(themeModeConfig)
    }
}
