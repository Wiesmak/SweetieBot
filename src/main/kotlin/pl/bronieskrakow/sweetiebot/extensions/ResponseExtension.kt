package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.core.event.message.MessageCreateEvent

class ResponseExtension : Extension() {
    override val name = "response"

    private val triggerResponse: Map<String, String> = mapOf(
        "luna" to "Luna eats oats! <:1185611903561633984:1185611903561633984>",
        "celestia" to "<:646373519357116416:646373519357116416> The Solar Empire, towards a brighter future! <:646373519357116416:646373519357116416>",
        "rarity" to "White pony best pony. <:569909529815285770:569909529815285770>",
    )

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                if (event.message.author?.isBot == true)
                    return@action

                val message = event.message.content.lowercase()

               val response = triggerResponse.filterKeys { message.contains(it) }.values.firstOrNull()
                if (response != null) {
                    event.message.channel.createMessage(response)
                }
            }
        }
    }
}