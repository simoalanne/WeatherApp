package com.example.weatherapp

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Singleton object providing a Gson instance for serializing and deserializing data. It has two
 * custom type adapters for serializing and deserializing LocalDate and LocalDateTime objects. This
 * code was mostly written by ChatGPT, because had no time (and motivation) to start learning about
 * type adapters.
 */
object GsonProvider {

    private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
    }

    class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        override fun serialize(
            src: LocalDate?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            // Converts the LocalDate to a formatted string the parser expects
            return JsonPrimitive(src?.format(localDateFormatter))
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            // Converts the string back to a LocalDate
            return LocalDate.parse(json?.asString, localDateFormatter)
        }
    }

    class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        override fun serialize(
            src: LocalDateTime?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            // Converts the LocalDateTime to a formatted string the parser expects
            return JsonPrimitive(src?.format(localDateTimeFormatter))
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDateTime {
            // Converts the string back to a LocalDateTime
            return LocalDateTime.parse(json?.asString, localDateTimeFormatter)
        }
    }
}
