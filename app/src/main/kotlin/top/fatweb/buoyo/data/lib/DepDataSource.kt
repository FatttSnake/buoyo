package top.fatweb.buoyo.data.lib

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import top.fatweb.buoyo.R
import top.fatweb.buoyo.di.AppDispatchers
import top.fatweb.buoyo.di.Dispatcher
import top.fatweb.buoyo.model.lib.Dependencies
import javax.inject.Inject

class DepDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val json = Json { ignoreUnknownKeys = true }

    val dependencies = flow {
        val inputStream = context.resources.openRawResource(R.raw.dependencies)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val dependencies = json.decodeFromString<Dependencies>(jsonString)
        emit(dependencies)
    }.flowOn(ioDispatcher)
}
