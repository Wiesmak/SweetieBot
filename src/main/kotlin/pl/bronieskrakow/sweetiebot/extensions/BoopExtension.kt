package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicUserCommand

class BoopExtension : Extension() {
    override val name = "boop"

    override suspend fun setup() {
        publicSlashCommand(::BoopArgs) {
            name = "boop"
            description = "Boop someone!"

            action {
                respond {
                    content = "_${member?.mention} boops ${arguments.target.mention} straight on the nose!_"
                }
            }
        }

        publicUserCommand {
            name = "Boop!"

            action {
                respond {
                    content = "_${member?.mention} boops ${targetUsers.first().mention} straight on the nose!_"
                }
            }

        }
    }

    inner class BoopArgs : Arguments() {
        val target by user {
            name = "pony"
            description = "Pony to boop"
        }
    }
}