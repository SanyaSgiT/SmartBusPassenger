package com.example.smartbuspassenger.serializers

import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.PatternDateFormat
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.parse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object NskgtDateSerializer : KSerializer<DateTimeTz> {
    override val descriptor = PrimitiveSerialDescriptor("DateTimeTz", PrimitiveKind.STRING)

    private val formatter = PatternDateFormat("dd.MM.yyyy HH:mm:ss")

    override fun serialize(encoder: Encoder, value: DateTimeTz) = encoder.encodeString(value.format(formatter))
    override fun deserialize(decoder: Decoder): DateTimeTz =
        formatter.parse(StringBuilder(decoder.decodeString()).insert(6, "20").toString())
            .toOffsetUnadjusted(TimeSpan(7.0 * 60 * 60 * 1000))
}