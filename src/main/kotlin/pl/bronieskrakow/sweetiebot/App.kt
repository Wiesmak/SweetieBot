package pl.bronieskrakow.sweetiebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env

import pl.bronieskrakow.sweetiebot.extensions.BoopExtension
import pl.bronieskrakow.sweetiebot.extensions.PurgeExtension
import pl.bronieskrakow.sweetiebot.extensions.ResponseExtension

private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        extensions {
            add(::BoopExtension)
            add(::ResponseExtension)
        }

        presence {
            streaming("My Little Pony", "https://www.youtube.com/watch?v=SpdEDqsMx4E")
        }
    }

    bot.start()
}