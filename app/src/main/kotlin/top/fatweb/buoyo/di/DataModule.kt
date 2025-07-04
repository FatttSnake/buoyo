package top.fatweb.buoyo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import top.fatweb.buoyo.monitor.TimeZoneBroadcastMonitor
import top.fatweb.buoyo.monitor.TimeZoneMonitor
import top.fatweb.buoyo.repository.lib.DepRepository
import top.fatweb.buoyo.repository.lib.impl.LocalDepRepository
import top.fatweb.buoyo.repository.userdata.UserDataRepository
import top.fatweb.buoyo.repository.userdata.impl.LocalUserDataRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsTimeZoneMonitor(timeZoneMonitor: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    internal abstract fun bindsUserDataRepository(userDataRepository: LocalUserDataRepository): UserDataRepository

    @Binds
    internal abstract fun bindsDepRepository(depRepository: LocalDepRepository): DepRepository
}
