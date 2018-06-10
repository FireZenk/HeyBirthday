package domain.models

import org.javacord.api.entity.channel.TextChannel

data class Event(val id: Long, val message: String, val channel: TextChannel)