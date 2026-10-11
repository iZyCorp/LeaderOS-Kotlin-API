package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

/**
 * Predefined staff answer for support tickets.
 */
@Serializable
data class QuickAnswer(
    val id: Int,
    val title: String,

    /**
     * HTML content.
     */
    val content: String
)
