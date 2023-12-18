package pl.bronieskrakow.sweetiebot.repositories.event

import dev.kord.core.entity.Role
import pl.bronieskrakow.sweetiebot.repositories.event.models.Event

interface EventRepository {
    suspend fun listEvents(): List<Event>
    suspend fun getEvent(role: Role): Event?
    suspend fun addEvent(event: Event)
    suspend fun removeEvent(role: Role)
    suspend fun archiveEvent(role: Role)
}