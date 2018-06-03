import org.javacord.api.DiscordApiBuilder
import org.javacord.api.util.logging.ExceptionLogger

/**
 * Main bot file
 */
object Bot {

    @JvmStatic fun main(args: Array<String>) {
        val token = args[0]

        DiscordApiBuilder().setToken(token).login().thenAccept({ api ->

            // Add a listener which answers with "Pong!" if someone writes "!ping"
            api.addMessageCreateListener({ event ->
                println(event)
                if (event.message.content.equals("!ping", ignoreCase = true)) {
                    event.channel.sendMessage("Pong!")
                }
            })

            // Print the invite url of your bot
            System.out.println("You can invite the bot by using the following url: " + api.createBotInvite())

        }).exceptionally(ExceptionLogger.get())
    }
}