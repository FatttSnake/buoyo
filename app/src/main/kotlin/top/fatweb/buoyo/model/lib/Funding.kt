package top.fatweb.buoyo.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Funding(
    val platform: String,

    val url: String
)
