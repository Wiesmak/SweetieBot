package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.stringList
import com.kotlindiscord.kord.extensions.components.forms.ModalForm
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import dev.kord.core.behavior.channel.MessageChannelBehavior

class EventExtension : Extension() {
    override val name = "event"

    override suspend fun setup() {
        ephemeralSlashCommand(modal = ::EventModal, arguments = ::EventArgs) {
            name = "event"
            description = "Stwórz wydarzenie"

            action { modal ->
                if (modal == null) {
                    respond {
                        content = "Podczas tworzenia wydarzenia wystąpił błąd! Spróbuj ponownie."
                        ephemeral
                    }
                    return@action
                }

                val event: Map<String, String?> = mapOf(
                    "name" to modal.name.value,
                    "date" to modal.date.value?.split(" | ")?.get(0),
                    "place" to modal.place.value,
                    "hour" to modal.date.value?.split(" | ")?.get(1),
                    "type" to arguments.type,
                    "tickets" to modal.tickets.value,
                    "hosts" to arguments.hosts.parsed,
                    "link" to modal.link.value,
                )

                val message = """
                    :blue_heart: Wydarzenie: **${event["name"] ?: "???"}**
                    
                    :calendar_spiral: Data: **${event["date"] ?: "brak danych"}**
                    
                    :pushpin: Miejsce: ${event["place"] ?: "brak danych"}
                    
                    :clock2: Godzina: ${event["hour"] ?: "brak danych"}
                    
                    :gear: Typ: ${event["type"] ?: "brak danych"}
                    
                    :tickets: Bilety: ${event["tickets"] ?: "brak danych"}
                    
                    :construction: Organizatorzy: ${event["hosts"] ?: "brak danych"}
                    
                    ${if (event["link"] != null) ":link: Link do wydarzenia: ${event["link"]}" else ""}
                    
                """.trimIndent()

                // Waaaay too long, but I don't know how to make it shorter
                MessageChannelBehavior(arguments.eventsChannel.id, arguments.eventsChannel.kord).createMessage(message)

                respond {
                    content = "Wydarzenie ${event["name"]} zostało utworzone!"
                    ephemeral
                }
            }
        }
    }

    inner class EventArgs : Arguments() {
        val eventsChannel by channel {
            name = "events-channel"
            description = "Kanał do publikacji wydarzenia"
        }

        val type by stringChoice {
            name = "type"
            description = "Typ wydarzenia"
            choices = mutableMapOf(
                "meet" to "Meet",
                "konwent" to "Konwent",
                "delegacja" to "Delegacja",
                "urodziny" to "Urodziny",
                "inne" to "Inne",
            )
        }

        val hosts = string {
            name = "hosts"
            description = "Organizatorzy"
        }
    }

    inner class EventModal : ModalForm() {
        override var title: String = "Nowe wydarzenie"

        val name = lineText {
            label = "Nazwa wydarzenia"
            placeholder = "Nazwa wydarzenia"
            required = true
        }

        val date = lineText {
            label = "Data i godzina"
            placeholder = "Rozdziel datę i godzinę znakiem \" | \""
            required = false
        }

        val place = lineText {
            label = "Miejsce"
            placeholder = "Miejsce"
            required = false
        }

        val tickets = lineText {
            label = "Bilety"
            placeholder = "Ceny biletów"
            required = false
        }

        val link = lineText {
            label = "Link do wydarzenia"
            placeholder = "Link do wydarzenia"
            required = false
        }
    }
}