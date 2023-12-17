package pl.bronieskrakow.sweetiebot.extensions

import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.components.*
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand

class SelectExtension : Extension() {
    override val name = "select"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "select"
            description = "Epic select menus"

            ephemeralSubCommand {
                name = "princess"
                description = "Select a better princess"

                action {
                    respond {
                        components {
                            ephemeralStringSelectMenu {
                                id = "better-princess"
                                placeholder = "Select a princess"
                                option("Celestia", "celestia")
                                option("Luna", "luna")
                                maximumChoices = 1

                                action {
                                    respond {
                                        content = if (selected.joinToString("") == "celestia") "Better princess!" else "Luna eats oats!"
                                    }
                                }
                            }
                        }
                    }
                }
            }

            publicSubCommand {
                name = "race"
                description = "Select a pony race"

                action {
                    respond {
                        components {
                            publicStringSelectMenu {
                                option("Earth pony", "earth-pony")
                                option("Pegasus", "pegasus")
                                option("Unicorn", "unicorn")
                                maximumChoices = 3

                                action {
                                    respond {
                                        content = "You selected: " + if (selected.contains("unicorn") && selected.contains("pegasus")) "an Alicorn!" else selected.joinToString(", ")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = "city"
                description = "Select a city"

                action {
                    respond {
                        components {
                            ephemeralStringSelectMenu {
                                option("Canterlot", "canterlot")
                                option("Ponyville", "ponyville")
                                option("Cloudsdale", "cloudsdale")
                                maximumChoices = null

                                action {
                                    respond {
                                        content = "You live in: " + selected.joinToString(", ")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = "role"
                description = "Select a role"

                action {
                    respond {
                        components {
                            ephemeralRoleSelectMenu {
                                maximumChoices = 1

                                action {
                                    respond {
                                        content = "You selected: " + selected.first().mention
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = "channel"
                description = "Select a channel"

                action {
                    respond {
                        components {
                            ephemeralChannelSelectMenu {
                                maximumChoices = 1

                                action {
                                    respond {
                                        content = "You selected: " + selected.first().mention
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}