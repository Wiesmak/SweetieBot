package pl.bronieskrakow.sweetiebot.repositories.event.models

enum class EventState(val state: String) {
    UPCOMING("upcoming"),
    ONGOING("ongoing"),
    ARCHIVED("archived"),
    CANCELED("canceled"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(state: String): EventState {
            return when (state) {
                "upcoming" -> UPCOMING
                "ongoing" -> ONGOING
                "archived" -> ARCHIVED
                "canceled" -> CANCELED
                else -> UNKNOWN
            }
        }
    }
}