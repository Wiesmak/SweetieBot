package pl.bronieskrakow.sweetiebot.repositories.event

import com.kotlindiscord.kord.extensions.adapters.mongodb.kordExCodecRegistry
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dev.kord.core.entity.Role
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.codecs.configuration.CodecRegistries
import pl.bronieskrakow.sweetiebot.repositories.event.models.Event

class MongoEventRepository(private val database: MongoDatabase) : EventRepository {
    private val registry = CodecRegistries.fromRegistries(
        kordExCodecRegistry,
        MongoClientSettings.getDefaultCodecRegistry(),
    )

    private val eventCollection = database.getCollection<Event>("events").withCodecRegistry(registry)

    override suspend fun listEvents(): List<Event> {
        return eventCollection.find<Event>().toList()
    }

    override suspend fun getEvent(role: Role): Event? {
        return eventCollection.find<Event>(Filters.eq("roleId", role.id)).limit(1).firstOrNull()
    }

    override suspend fun addEvent(event: Event) {
        eventCollection.insertOne(event)
    }

    override suspend fun removeEvent(role: Role) {
        eventCollection.deleteOne(Filters.eq("roleId", role))
    }

    override suspend fun archiveEvent(role: Role) {
        TODO()
    }
}