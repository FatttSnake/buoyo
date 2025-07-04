package top.fatweb.buoyo.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import top.fatweb.buoyo.di.ApplicationScope
import top.fatweb.buoyo.di.AppDispatchers
import top.fatweb.buoyo.di.Dispatcher
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class TimeZoneBroadcastMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @ApplicationScope applicationScope: CoroutineScope,
    @param:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : TimeZoneMonitor {
    override val currentTimeZone: Flow<TimeZone> = callbackFlow {
        trySend(TimeZone.currentSystemDefault())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                val zoneIdFromIntent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    null
                } else {
                    intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.run {
                        val zoneId = ZoneId.of(this, ZoneId.SHORT_IDS)
                        zoneId.toKotlinTimeZone()
                    }
                }

                trySend(zoneIdFromIntent ?: TimeZone.currentSystemDefault())
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(ioDispatcher)
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds),
            replay = 1
        )
}
