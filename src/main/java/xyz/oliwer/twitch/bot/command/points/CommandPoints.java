package xyz.oliwer.twitch.bot.command.points;

import xyz.oliwer.twitch.bot.command.Command;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;

import static xyz.oliwer.twitch.bot.command.CommandController.PERFORMER;

/**
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class CommandPoints extends Command {
  private static final class Check extends Command {
    @Override
    public void perform(ExtractedUser user, BotClient client, String[] arguments) {
      System.out.printf("%s has checked their points\n", user);
    }

    @Override
    public String[] getAliases() {
      return new String[] { "check" };
    }
  }

  public CommandPoints() {
    meta()
      .and(new Check())
      .apply(this);
  }

  @Override
  public void perform(ExtractedUser user, BotClient client, String[] arguments) {
    if (arguments.length == 0) {
      System.out.println("no sub command specified");
      return;
    }

    final String alias = arguments[0];
    PERFORMER.commence(this, alias, arguments, user, client);
  }

  @Override
  public void onInvalidChild(ExtractedUser user, BotClient client, String usedAlias) {
    System.out.printf("could not find child command '%s' of 'points'\n", usedAlias);
  }

  @Override
  public String[] getAliases() {
    return new String[] { "points" };
  }
}