package pl.bronieskrakow.sweetiebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.adapters.mongodb.mongoDB
import com.kotlindiscord.kord.extensions.adapters.mongodb.kordExCodecRegistry
import com.kotlindiscord.kord.extensions.utils.env
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.codecs.configuration.CodecRegistries
import pl.bronieskrakow.sweetiebot.extensions.*
import java.util.Objects

private val TOKEN = env("TOKEN")
private val MONGODB_URI = env("ADAPTER_MONGODB_URI")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        mongoDB()

        val registry = CodecRegistries.fromRegistries(
            kordExCodecRegistry,
            MongoClientSettings.getDefaultCodecRegistry(),
        )

        val client = MongoClient.create(MONGODB_URI)
        val database = client.getDatabase("sweetiebot")

        val eventCollection = database.getCollection<Objects>("events").withCodecRegistry(registry)

        extensions {
            add(::BoopExtension)
            add(::PurgeExtension)
            add(::ResponseExtension)
            add(::EventExtension)
            add(::SelectExtension)
        }

        presence {
            streaming("My Little Pony", "https://www.youtube.com/watch?v=SpdEDqsMx4E")
            //watching("My Little Pony")
        }
    }

    bot.start()
}
