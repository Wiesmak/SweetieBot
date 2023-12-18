package pl.bronieskrakow.sweetiebot.models

import dev.kord.core.entity.Role
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class Event(
    val name: String,
    val date: String?,
    val startDate: Instant,
    val endDate: Instant,
    val place: String?,
    val hour: String?,
    val type: String?,
    val tickets: String?,
    val hosts: String?,
    val link: String?,
) {
    companion object {
        fun dateFormString(date: String, time: String): Pair<Instant, Instant> {
            if (date[2] == '-') {
                val startIsoString = buildString {
                    append(date.substring(9, 13))
                    append('-')
                    append(date.substring(6, 8))
                    append('-')
                    append(date.substring(0, 2))
                    append('T')
                    append(time)
                    append(":00.000Z")
                }

                val endIsoString = buildString {
                    append(date.substring(9, 13))
                    append('-')
                    append(date.substring(6, 8))
                    append('-')
                    append(date.substring(3, 5))
                    append('T')
                    append(time)
                    append(":00.000Z")
                }

                return Pair(
                    Instant.parse(startIsoString),
                    Instant.parse(endIsoString)
                )
            } else {
                val startEndIsoString = buildString {
                    append(date.substring(6, 10))
                    append('-')
                    append(date.substring(3, 5))
                    append('-')
                    append(date.substring(0, 2))
                    append('T')
                    append(time)
                    append(":00.000Z")
                }
                return Pair(
                    Instant.parse(startEndIsoString),
                    Instant.parse(startEndIsoString).plus(Duration.parse("PT1H")),
                )
            }
        }
    }

    fun generateMessage(role: Role): String {
        return """
            :blue_heart: Wydarzenie: **${name}** <@&${role.id.value}>
            
            :calendar_spiral: Data: **${date ?: "brak danych"}**
            
            :pushpin: Miejsce: ${place ?: "brak danych"}
            
            :clock2: Godzina: ${hour ?: "brak danych"}
            
            :gear: Typ: ${type ?: "brak danych"}
            
            :tickets: Bilety: ${tickets ?: "brak danych"}
            
            :construction: Organizatorzy: ${hosts ?: "brak danych"}
            
            ${if (link != null) ":link: Link do wydarzenia: $link" else ""}
        """.trimIndent()
    }

    fun generateAssignMessage(): String {
        return """
            Wybierasz się na **$name**?
            
            Naciśnij przycisk poniżej, aby otrzymać dostęp do sekcji związanej z tym wydarzeniem!
            
            Aby utrzymać rolę wyślij w trakcie meeta albo po nim swój identyfikator na kanale pokaż swoje id :)
            
            Do zobaczenia na meecie!
        """.trimIndent()
    }

    fun generateRoleName(): String {
        return name.replace(Regex("([0-9]| )+$"), "") + " '" + date?.split(".")?.get(2)?.substring(2)
    }
}
