package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.color
import com.kotlindiscord.kord.extensions.commands.converters.impl.role
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.forms.ModalForm
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.utils.hasRole
import dev.kord.common.Color
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.createRole
import dev.kord.core.behavior.createScheduledEvent
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import org.bson.types.ObjectId
import pl.bronieskrakow.sweetiebot.models.Event
import pl.bronieskrakow.sweetiebot.repositories.EventRepository

class EventExtension(private val eventRepository: EventRepository) : Extension() {
    override val name = "event"

    override suspend fun setup() {
        publicSlashCommand() {
            name = "event"
            description = "Zarządzaj wydarzeniami"

            // create event subcommand
            publicSubCommand(modal = ::CreateEventModal, arguments = ::CreateEventArgs) {
                name = "create"
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
                    val myEvent = Event(
                        id = ObjectId(),
                        name = modal.name.value ?: "???",
                        date = dateHour?.split(" | ")?.get(0),
                        startDate = Event.dateFormString(dateHour?.split(" | ")?.get(0) ?: "11.09.2030", dateHour?.split(" | ")?.get(1) ?: "00:00").first,
                        endDate = Event.dateFormString(dateHour?.split(" | ")?.get(0) ?: "11.09.2030", dateHour?.split(" | ")?.get(1) ?: "00:00").second,
                        place = modal.place.value,
                        hour = dateHour?.split(" | ")?.get(1),
                        type = arguments.type,
                        tickets = modal.tickets.value,
                        hosts = arguments.hosts.parsed,
                        link = modal.link.value,
                        null,
                    )

                    // build messages
                    val roleName = myEvent.generateRoleName()
                    val assignMessage = myEvent.generateAssignMessage()

                    // create role
                    val role = guild?.createRole {
                        name = roleName
                        color = arguments.color.parsed
                        mentionable = true
                    }

                    // add role to event object
                    myEvent.roleId= role?.id

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

                    // save event to database
                    eventRepository.addEvent(myEvent)

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

            publicSubCommand(::InfoEventArgs) {
                name = "info"
                description = "Informacje o wydarzeniu"

                action {
                    // get event from database
                    val myEvent = eventRepository.getEvent(arguments.event.asRole())

                    // return if event not found
                    if (myEvent == null) {
                        respond {
                            content = "Nie znaleziono wydarzenia!"
                        }
                        return@action
                    }

                    // send message with a button
                    respond {
                        embed {
                            title = myEvent.name
                            description = myEvent.generateMessage(arguments.event)
                            color = arguments.event.color
                        }
                        //content = myEvent.generateMessage(arguments.event)
                    }
                }
            }
        }

        event<ButtonInteractionCreateEvent> {
            action {
                // return if not button
                if (event.interaction.type != InteractionType.Component) return@action
                if (!event.interaction.data.data.customId.value?.startsWith("join-event")!!) return@action

                // get role id from button custom id
                val roleId = event.interaction.data.data.customId.value?.split("-")?.last()

                val guild = event.interaction.data.guildId.value?.let { event.kord.getGuild(it) }
                val role: RoleBehavior = guild?.getRole(Snowflake(roleId!!))!!

                // check if user has role
                if (event.interaction.user.asMember(guild.id).hasRole(role)) {
                    // remove role from user
                    event.interaction.user.asMember(guild.id).removeRole(role.id)

                    // respond to user
                    event.interaction.respondEphemeral {
                        content = "Usunięto rolę :("
                    }
                } else {
                    // add role to user
                    event.interaction.user.asMember(guild.id).addRole(role.id)

                    // respond to user
                    event.interaction.respondEphemeral {
                        content = "Dodano rolę!"

                        components {
                            actionRow {
                                interactionButton(
                                    customId = "leave-event-${role.id.value}",
                                    style = ButtonStyle.Danger
                                ) {
                                    label = "Cofnij"
                                }
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
                if (!event.interaction.data.data.customId.value?.startsWith("leave-event")!!) return@action

                // get role id from button custom id
                val roleId = event.interaction.data.data.customId.value?.split("-")?.last()

                val guild = event.interaction.data.guildId.value?.let { event.kord.getGuild(it) }
                val role: RoleBehavior = guild?.getRole(Snowflake(roleId!!))!!

                // remove role from user
                event.interaction.user.asMember(guild.id).removeRole(role.id)

                // respond to user
                event.interaction.respondEphemeral {
                    content = "Usunięto rolę :("
                }
            }
        }
    }

    inner class InfoEventArgs : Arguments() {
        val event by role {
            name = "event"
            description = "Wydarzenie"
        }
    }

    inner class CreateEventArgs : Arguments() {
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

        val color = color {
            name = "color"
            description = "Kolor roli"
        }
    }

    inner class CreateEventModal : ModalForm() {
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