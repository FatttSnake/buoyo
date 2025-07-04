package top.fatweb.buoyo.monitor

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone

interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}
