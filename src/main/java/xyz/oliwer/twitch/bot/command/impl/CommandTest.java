package xyz.oliwer.twitch.bot.command.impl;

import xyz.oliwer.twitch.bot.command.Command;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;

/**
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class CommandTest extends Command {
  @Override
  public void perform(ExtractedUser user, BotClient client, String[] arguments) {
    System.out.printf("%s executed the 'test' command\n", user);
  }

  @Override
  public String[] getAliases() {
    return new String[] { "test" };
  }
}