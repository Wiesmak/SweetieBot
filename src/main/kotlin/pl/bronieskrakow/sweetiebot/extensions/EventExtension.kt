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
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.createRole
import dev.kord.core.behavior.createScheduledEvent
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.actionRow
import kotlinx.datetime.Clock
import pl.bronieskrakow.sweetiebot.models.Event

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

                // get date and hour
                val dateHour = if ((modal.date.value ?: "brak danych").contains(" | ") && (modal.date.value ?: "brak danych").split(" | ").size >= 2)
                    modal.date.value
                    else modal.date.value + " | brak danych"



                // create event object
                val myEvent: Event = Event(
                    modal.name.value ?: "???",
                    dateHour?.split(" | ")?.get(0),
                    Event.dateFormString(dateHour?.split(" | ")?.get(0) ?: "11.09.2030", dateHour?.split(" | ")?.get(1) ?: "00:00").first,
                    Event.dateFormString(dateHour?.split(" | ")?.get(0) ?: "11.09.2030", dateHour?.split(" | ")?.get(1) ?: "00:00").second,
                    modal.place.value,
                    dateHour?.split(" | ")?.get(1),
                    arguments.type,
                    modal.tickets.value,
                    arguments.hosts.parsed,
                    modal.link.value,
                )

                // build messages
                val roleName = myEvent.generateRoleName()
                val assignMessage = myEvent.generateAssignMessage()

                // create role
                val role = guild?.createRole {
                    name = roleName
                    color = Color(0x000000)
                    mentionable = true
                }

                // build message
                val message = myEvent.generateMessage(role!!)

                // send message to events channel
                MessageChannelBehavior(arguments.eventsChannel.id, arguments.eventsChannel.kord).createMessage(message)

                // create discord event
                guild?.createScheduledEvent(
                    name = myEvent.name,
                    privacyLevel = GuildScheduledEventPrivacyLevel.GuildOnly,
                    scheduledStartTime = myEvent.startDate,
                    entityType = ScheduledEntityType.External,
                ) {
                    name = myEvent.name
                    privacyLevel = GuildScheduledEventPrivacyLevel.GuildOnly
                    scheduledStartTime = myEvent.startDate
                    scheduledEndTime = myEvent.endDate
                    description = myEvent.generateMessage(role)
                    entityType = ScheduledEntityType.External
                    entityMetadata = GuildScheduledEventEntityMetadata(Optional(myEvent.place ?: "brak danych"))
                }

                // send message with a button
                respond {
                    content = assignMessage
                    components {
                        actionRow {
                            interactionButton(
                                customId = "join-event-${role.id.value}",
                                style = ButtonStyle.Primary
                            ) {
                                label = "Jadę na ${myEvent.name}!"
                            }
                        }
                    }
                }
            }
        }

        event<ButtonInteractionCreateEvent> {
            action {
                // return if not button
                if (event.interaction.type != InteractionType.Component) return@action

                // get role id from button custom id
                val roleId = event.interaction.data.data.customId.value?.split("-")?.last()

                // add role to user
                event.interaction.user.asMember(event.interaction.data.guildId.value!!).addRole(Snowflake(roleId!!))

                event.interaction.respondEphemeral {
                    content = "Dodano rolę!"
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