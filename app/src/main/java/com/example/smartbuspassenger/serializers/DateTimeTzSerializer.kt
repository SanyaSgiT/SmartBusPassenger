package com.example.smartbuspassenger.serializers

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.parse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateTimeTzSerializer : KSerializer<DateTimeTz> {
    override val descriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

    val format = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    override fun serialize(encoder: Encoder, value: DateTimeTz) = encoder.encodeString(value.format(format))
    override fun deserialize(decoder: Decoder): DateTimeTz = format.parse(decoder.decodeString())
}