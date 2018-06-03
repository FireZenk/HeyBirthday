import data.db.JsondbDataSource
import data.net.DiscordDataSource
import domain.models.Event
import domain.repositories.DiscordRepository
import domain.usecases.ListenMessages
import domain.usecases.SendMessage
import org.slf4j.LoggerFactory

/**
 * Main bot file
 */
object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)

    private val repository: DiscordRepository by lazy {
        DiscordRepository(DiscordDataSource(token), JsondbDataSource())
    }

    private val listenMessages: ListenMessages by lazy { ListenMessages(repository) }
    private val sendMessage: SendMessage by lazy { SendMessage(repository) }

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
            sendMessage.execute(it.channel, "Pong!")
                    .subscribe({}, { logger.debug("Discord api error", it) })
        } else if (it.message.equals("!pong", ignoreCase = true)) {
            sendMessage.execute(it.channel, "Ping and Pong!")
                    .subscribe({}, { logger.debug("Discord api error", it) })
        } else if (it.message.equals("nayeon", ignoreCase = true)) {
            sendMessage.execute(it.channel, "Cute!")
                    .subscribe({}, { logger.debug("Discord api error", it) })
        }
    }
}