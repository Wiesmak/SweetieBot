package pl.bronieskrakow.sweetiebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env

import pl.bronieskrakow.sweetiebot.extensions.BoopExtension

private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        extensions {
            add(::BoopExtension)
        }
    }

    bot.start()
}