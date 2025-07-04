package top.fatweb.buoyo.ui.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

object ResourcesHelper {
    fun getConfiguration(context: Context): Configuration = context.resources.configuration

    fun getAppLocale(context: Context): Locale = getConfiguration(context).locales.get(0)

    fun getSystemLocale(): LocaleListCompat =
        ConfigurationCompat.getLocales(Resources.getSystem().configuration)

    fun getAppVersionName(context: Context): String =
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: "Unknown"
        } catch (_: PackageManager.NameNotFoundException) {
            "Unknown"
        }

    @Suppress("DEPRECATION")
    fun getAppVersionCode(context: Context): Long =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                context.packageManager.getPackageInfo(context.packageName, 0)?.longVersionCode ?: -1
            else context.packageManager.getPackageInfo(
                context.packageName,
                0
            )?.versionCode?.toLong() ?: -1
        } catch (_: PackageManager.NameNotFoundException) {
            -1
        }

    fun getString(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String =
        context.resources.getString(resId, *formatArgs)
}

fun Activity.safeRecreate() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
    val intent = intent
    finish()
    startActivity(intent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
