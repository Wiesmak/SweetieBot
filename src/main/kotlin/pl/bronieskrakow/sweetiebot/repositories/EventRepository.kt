package pl.bronieskrakow.sweetiebot.repositories

import dev.kord.core.entity.Role
import pl.bronieskrakow.sweetiebot.models.Event

interface EventRepository {
    suspend fun listEvents(): List<Event>
    suspend fun getEvent(role: Role): Event?
    suspend fun addEvent(event: Event)
    suspend fun removeEvent(role: Role)
    suspend fun archiveEvent(role: Role)
}