package top.fatweb.buoyo.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Developer(
    val name: String? = null,

    val organisationUrl: String? = null
)
