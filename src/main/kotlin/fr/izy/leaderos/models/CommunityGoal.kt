@file:UseSerializers(ApiBooleanSerializer::class, ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import fr.izy.leaderos.internal.ApiDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class CommunityGoal(
    @SerialName("is_active") val isActive: Boolean = false,
    val currency: String = "",
    @SerialName("total_raised") val totalRaised: BigDecimal = BigDecimal.ZERO,
    @SerialName("goal_amount") val goalAmount: BigDecimal = BigDecimal.ZERO,
    @SerialName("percentage_achieved") val percentageAchieved: Double = 0.0
)
