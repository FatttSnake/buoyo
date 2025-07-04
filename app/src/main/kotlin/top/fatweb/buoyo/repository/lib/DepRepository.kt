package top.fatweb.buoyo.repository.lib

import kotlinx.coroutines.flow.Flow
import top.fatweb.buoyo.model.lib.Dependencies

interface DepRepository {
    fun searchName(name: String): Flow<Dependencies>

    fun getSearchNameCount(): Flow<Int>
}
