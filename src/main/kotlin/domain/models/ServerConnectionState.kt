package domain.models

data class ServerConnectionState(val serverId: Long, val state: ConnectionSate)

sealed class ConnectionSate {
    class Available : ConnectionSate()
    class Unavailable : ConnectionSate()
}