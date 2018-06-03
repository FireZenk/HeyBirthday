package domain.models

data class Event(val id: Long, val message: String, val response: (String) -> Unit)