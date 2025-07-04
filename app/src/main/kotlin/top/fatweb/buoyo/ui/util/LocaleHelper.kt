package top.fatweb.buoyo.ui.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import top.fatweb.buoyo.model.userdata.LanguageConfig
import java.util.Locale

object LocaleHelper {
    fun switchLocale(
        activity: Activity,
        languageConfig: LanguageConfig,
        beforeSwitch: () -> Unit = {}
    ) {
        val newLanguage = getLocaleFromLanguageConfig(languageConfig).language
        val currentLanguage = ResourcesHelper.getAppLocale(activity).language
        if (newLanguage != currentLanguage) {
            beforeSwitch()
            activity.safeRecreate()
        }
    }

    fun attachBaseContext(context: Context, languageConfig: LanguageConfig): Context {
        val locale: Locale =
            getLocaleFromLanguageConfig(languageConfig)

        return createConfigurationContext(
            context,
            locale
        )
    }

    private fun getLocaleFromLanguageConfig(languageConfig: LanguageConfig): Locale =
        when (languageConfig) {
            LanguageConfig.FollowSystem -> ResourcesHelper.getSystemLocale().get(0)!!
            LanguageConfig.Chinese -> Locale.CHINESE
            LanguageConfig.English -> Locale.ENGLISH
        }

    private fun createConfigurationContext(context: Context, locale: Locale): Context {
        val configuration = Configuration(ResourcesHelper.getConfiguration(context))
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }
}
