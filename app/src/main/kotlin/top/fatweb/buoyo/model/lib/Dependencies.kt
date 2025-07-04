package top.fatweb.buoyo.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Dependencies(
    val libraries: List<Library>,

    val licenses: Map<String, License>
)
