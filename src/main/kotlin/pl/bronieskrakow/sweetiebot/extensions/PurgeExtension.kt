package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

class PurgeExtension : Extension() {
    override val name = "purge"

    override suspend fun setup() {
        publicSlashCommand(::PurgeArgs) {
            name = "purge"
            description = "Masowo usuń wiadomości z kanału"

            action {
                val ammount = arguments.ammount
                if (ammount > 100) {
                    respond {
                        content = "Nie możesz usunąć więcej niż 100 wiadomości na raz!"
                        ephemeral
                    }
                    return@action
                }
                val messages = channel.getMessagesBefore(channel.messages.first().id, ammount)
                messages.collect { it.delete() }
                respond {
                    content = "<:1185625937010766016:1185625937010766016>"
                    ephemeral
                }
            }

        }
    }

    inner class PurgeArgs : Arguments() {
        val ammount by int {
            name = "ammount"
            description = "Liczba wiadomości do usunięcia"
        }
    }
}