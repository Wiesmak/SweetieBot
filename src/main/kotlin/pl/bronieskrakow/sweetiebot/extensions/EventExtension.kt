package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.forms.ModalForm
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.createRole
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.message.actionRow

class EventExtension : Extension() {
    override val name = "event"

    override suspend fun setup() {
        publicSlashCommand(modal = ::EventModal, arguments = ::EventArgs) {
            name = "event"
            description = "Stwórz wydarzenie"

            action { modal ->
                // return if modal not filled
                if (modal == null) {
                    respond {
                        content = "Podczas tworzenia wydarzenia wystąpił błąd! Spróbuj ponownie."
                        ephemeral
                    }
                    return@action
                }

                val dateHour = if ((modal.date.value ?: "brak danych").contains(" | ") && (modal.date.value ?: "brak danych").split(" | ").size >= 2)
                    modal.date.value
                    else modal.date.value + " | brak danych"

                // get event data
                val event: Map<String, String?> = mapOf(
                    "name" to modal.name.value,
                    "date" to dateHour?.split(" | ")?.get(0),
                    "place" to modal.place.value,
                    "hour" to dateHour?.split(" | ")?.get(1),
                    "type" to arguments.type,
                    "tickets" to modal.tickets.value,
                    "hosts" to arguments.hosts.parsed,
                    "link" to modal.link.value,
                )

                // Build role name
                val roleName = event["name"]?.replace(Regex("([0-9]| )+$"), "") + " '" + event["date"]?.split(".")?.get(2)?.substring(2)

                // create role
                val role = guild?.createRole {
                    name = roleName
                    color = Color(0x000000)
                    mentionable = true
                }

                // build message
                val message = """
                    :blue_heart: Wydarzenie: **${event["name"] ?: "???"}** <@&${role?.id?.value.toString()}>
                    
                    :calendar_spiral: Data: **${event["date"] ?: "brak danych"}**
                    
                    :pushpin: Miejsce: ${event["place"] ?: "brak danych"}
                    
                    :clock2: Godzina: ${event["hour"] ?: "brak danych"}
                    
                    :gear: Typ: ${event["type"] ?: "brak danych"}
                    
                    :tickets: Bilety: ${event["tickets"] ?: "brak danych"}
                    
                    :construction: Organizatorzy: ${event["hosts"] ?: "brak danych"}
                    
                    ${if (event["link"] != null) ":link: Link do wydarzenia: ${event["link"]}" else ""}
                    
                """.trimIndent()

                // send message to events channel
                MessageChannelBehavior(arguments.eventsChannel.id, arguments.eventsChannel.kord).createMessage(message)

                // build message for event assign channel
                val assignMessage = """
                    Wybierasz się na **${event["name"] ?: "???"}**?
                    
                    Naciśnij przycisk poniżej, aby otrzymać dostęp do sekcji związanej z tym wydarzeniem!
                    
                    Aby utrzymać rolę wyślij w trakcie meeta albo po nim swój identyfikator na kanale pokaż swoje id :)
                    
                    Do zobaczenia na meecie!
                """.trimIndent()

                // send message with a button
                respond {
                    content = assignMessage
                    components {
                        actionRow {
                            interactionButton(
                                customId = "join-event-${role?.id?.value.toString()}",
                                style = ButtonStyle.Primary
                            ) {
                                label = "Jadę na ${event["name"]}!"

                                // add role to user on button click

                            }
                        }
                    }
                }
            }
        }

        event<InteractionCreateEvent> {
            action {
                // return if not button
                if (event.interaction.type != InteractionType.Component) return@action

                // get role id from button custom id
                val roleId = event.interaction.data.data.customId.value?.split("-")?.last()

                // add role to user
                event.interaction.user.asMember(event.interaction.data.guildId.value!!).addRole(Snowflake(roleId!!))
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