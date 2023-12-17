package pl.bronieskrakow.sweetiebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import pl.bronieskrakow.sweetiebot.extensions.*

private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        extensions {
            add(::BoopExtension)
            add(::PurgeExtension)
            add(::ResponseExtension)
            add(::EventExtension)
            add(::SelectExtension)
        }

        presence {
            streaming("My Little Pony", "https://www.youtube.com/watch?v=SpdEDqsMx4E")
        }
    }

    bot.start()
}
