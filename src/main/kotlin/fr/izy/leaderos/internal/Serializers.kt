package fr.izy.leaderos.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Date format used by LeaderOS, e.g. `2023-09-03 12:30:45`.
 */
internal val API_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

private fun Decoder.decodePrimitiveContent(): String? {
    val element = (this as? JsonDecoder)?.decodeJsonElement()
        ?: throw SerializationException("LeaderOS models can only be decoded from JSON")
    return (element as? JsonPrimitive)?.contentOrNull?.trim()
}

/**
 * LeaderOS sends booleans as `"1"` / `"0"`, sometimes as real JSON booleans.
 */
internal object ApiBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("fr.izy.leaderos.Boolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        val content = decoder.decodePrimitiveContent() ?: return false
        return when (content.lowercase()) {
            "1", "true" -> true
            "0", "false", "" -> false
            else -> content.toLongOrNull()?.let { it != 0L }
                ?: throw SerializationException("Invalid boolean: $content")
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}

/**
 * LeaderOS sends amounts as strings (`"10.00"`) or numbers (`10.25`).
 */
internal object ApiDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("fr.izy.leaderos.Decimal", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal {
        val content = decoder.decodePrimitiveContent()
        if (content.isNullOrEmpty()) return BigDecimal.ZERO
        return content.toBigDecimalOrNull() ?: throw SerializationException("Invalid decimal: $content")
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toPlainString())
}

/**
 * Parses LeaderOS dates. `1000-01-01 00:00:00` (LeaderOS "never expires" placeholder),
 * `0000-00-00 00:00:00` and empty values become `null`.
 */
@OptIn(ExperimentalSerializationApi::class)
internal object ApiDateTimeSerializer : KSerializer<LocalDateTime?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("fr.izy.leaderos.DateTime", PrimitiveKind.STRING).nullable

    override fun deserialize(decoder: Decoder): LocalDateTime? {
        val content = decoder.decodePrimitiveContent()
        if (content.isNullOrEmpty() || content.startsWith("1000-01-01") || content.startsWith("0000-00-00")) return null

        return try {
            LocalDateTime.parse(content, API_DATE_TIME)
        } catch (_: DateTimeParseException) {
            try {
                if (content.length == 10) LocalDate.parse(content).atStartOfDay() else LocalDateTime.parse(content)
            } catch (_: DateTimeParseException) {
                throw SerializationException("Invalid date: $content")
            }
        }
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.format(API_DATE_TIME))
    }
}

/**
 * LeaderOS sends `""` for unset IDs: decode them as `null`.
 */
@OptIn(ExperimentalSerializationApi::class)
internal object BlankAsNullSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("fr.izy.leaderos.OptionalString", PrimitiveKind.STRING).nullable

    override fun deserialize(decoder: Decoder): String? = decoder.decodePrimitiveContent()?.takeIf { it.isNotEmpty() }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value)
    }
}

/**
 * LeaderOS sends `[""]` for an unset list of IDs: drop blank entries.
 */
internal object NonBlankStringListSerializer : KSerializer<List<String>> {
    private val delegate = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): List<String> = delegate.deserialize(decoder).filter { it.isNotBlank() }

    override fun serialize(encoder: Encoder, value: List<String>) = delegate.serialize(encoder, value)
}

/**
 * PHP encodes an empty associative array as `[]` instead of `{}`: accept both.
 */
internal object LenientStringMapSerializer : KSerializer<Map<String, String>> {
    private val delegate = MapSerializer(String.serializer(), String.serializer())

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): Map<String, String> {
        val element = (decoder as? JsonDecoder)?.decodeJsonElement()
            ?: throw SerializationException("LeaderOS models can only be decoded from JSON")
        return when (element) {
            is JsonObject -> element.toStringMap()
            is JsonArray -> element.filterIsInstance<JsonObject>().fold(emptyMap()) { acc, obj -> acc + obj.toStringMap() }
            else -> emptyMap()
        }
    }

    override fun serialize(encoder: Encoder, value: Map<String, String>) = delegate.serialize(encoder, value)

    private fun JsonObject.toStringMap(): Map<String, String> =
        entries.mapNotNull { (key, value: JsonElement) -> (value as? JsonPrimitive)?.contentOrNull?.let { key to it } }.toMap()
}
