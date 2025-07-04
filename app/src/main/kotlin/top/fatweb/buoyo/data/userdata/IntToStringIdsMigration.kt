package top.fatweb.buoyo.data.userdata

import androidx.datastore.core.DataMigration
import top.fatweb.buoyo.data.UserPreferences
import top.fatweb.buoyo.data.copy

internal object IntToStringIdsMigration : DataMigration<UserPreferences> {
    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy { }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean = false
}
