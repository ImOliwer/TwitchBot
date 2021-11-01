package xyz.oliwer.twitch.bot.subscribers;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.jetbrains.annotations.NotNull;
import xyz.oliwer.twitch.bot.command.CommandController;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.EventSubscriber;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;
import xyz.oliwer.twitch.bot.util.annotation.SilentUse;

import static xyz.oliwer.twitch.bot.command.Command.PREFIX;

/**
 * This record represents the command event subscriber.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
@SilentUse(by = {BotClient.class})
public record CommandSubscriber(
  CommandController controller,
  BotClient client
) implements EventSubscriber {
  /**
   * Primary constructor.
   */
  public CommandSubscriber(@NotNull CommandController controller, @NotNull BotClient client) {
    if (controller == null || client == null)
      throw new NullPointerException("controller and client must not be null");
    this.controller = controller;
    this.client = client;
  }

  /**
   * Handle the regular incoming messages in channels.
   *
   * @param event {@link ChannelMessageEvent}
   */
  public void onMessage(ChannelMessageEvent event) {
    // enforce prefix
    final String message = event.getMessage();
    if (message.charAt(0) != PREFIX) {
      return;
    }

    // extract necessities
    final String[] arguments = message.split(" ");
    final String command = arguments[0].substring(1);

    // attempt to perform command by alias
    controller.tryPerform(
      null,
      event.getChannel().getName(),
      command,
      arguments,
      new ExtractedUser(event),
      this.client
    );
  }
}