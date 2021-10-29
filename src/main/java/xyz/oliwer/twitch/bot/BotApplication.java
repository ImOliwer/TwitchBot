package xyz.oliwer.twitch.bot;

import xyz.oliwer.twitch.bot.command.CommandController;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.subscribers.CommandSubscriber;

import java.util.Properties;

/**
 * This class represents the main class for our Twitch application.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class BotApplication {
  // called when the application starts
  public static void main(String[] args) {
    // set up our application properties
    final Properties properties = new Properties();
    properties.setProperty("Client-Id", System.getenv("clientId"));
    properties.setProperty("Chat-Bot-Access-Token", System.getenv("chatBotAccessToken"));

    // controller
    final CommandController controller = new CommandController();

    // client
    final BotClient client = new BotClient(properties);
    client.connect("ImOliwer");
    client.subscribe(new CommandSubscriber(controller, client));
  }
}