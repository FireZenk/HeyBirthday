import data.DiscordDataSource
import domain.models.Event
import domain.usecases.ListenMessages
import org.slf4j.LoggerFactory

/**
 * Main bot file
 */
object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)

    private val listenMessages: ListenMessages by lazy {
        ListenMessages(DiscordDataSource(token))
    }

    private lateinit var token: String

    @JvmStatic fun main(args: Array<String>) {
        token = args[0]

        listenMessages.execute()
                .subscribe({
                    println(it)
                    processEvent(it)
                }, {
                    logger.debug("Discord api error", it)
                })
    }

    private fun processEvent(it: Event) {
        if (it.message.equals("!ping", ignoreCase = true)) {
            it.response("Pong!")
        } else if (it.message.equals("!pong", ignoreCase = true)) {
            it.response("Ping and Pong!")
        }
    }
}