package xyz.oliwer.twitch.bot.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;
import xyz.oliwer.twitch.bot.util.Controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static xyz.oliwer.twitch.bot.command.Command.Requirement;

/**
 * This class represents the controller for all commands.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class CommandController implements Controller<Command> {
  /**
   * {@link Set<Command>} this property represents all commands registered to this controller.
   */
  private final Set<Command> commands = new HashSet<>();

  /**
   * Try to match command and perform by passed string & event.
   *
   * @param parent {@link Command} the parent to fetch children from - null if none.
   * @param commandAlias {@link String} alias to attempt execution of.
   * @param arguments {@link String} array of arguments from performed command.
   * @param user {@link ExtractedUser} the user who executed this command.
   * @param client {@link BotClient} the bot client - where said command was called from originally.
   */
  public void tryPerform(@Nullable Command parent, String commandAlias, String[] arguments, ExtractedUser user, BotClient client) {
    final String transformed = commandAlias.toLowerCase(Locale.ROOT);
    final Set<Command> searchThrough = parent != null ? parent.children() : this.commands;

    for (Command command : searchThrough) {
      boolean matches = false;

      final String[] aliases = command.getAliases();
      for (String alias : aliases) {
        if (!alias.equals(transformed)) {
          continue;
        }
        matches = true;
        break;
      }

      if (!matches) {
        continue;
      }

      final Set<Requirement> requirements = command.requirements();
      boolean passedRequirements = true;

      for (Requirement requirement : requirements) {
        if (requirement.attempt(user)) {
          continue;
        }
        passedRequirements = false;
        break;
      }

      if (!passedRequirements) {
        break;
      }

      final String[] exactArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
      command.perform(user, client, exactArguments);
      break;
    }
  }

  /**
   * @see Controller#register(Object)
   */
  @Override
  public boolean register(@NotNull Command value) {
    if (value == null)
      throw new NullPointerException("Command must not be null");
    return commands.add(value);
  }

  /**
   * @see Controller#unregister(Lookup, Object)
   */
  @Override
  public <Data> boolean unregister(@NotNull Lookup<Command, Controller<Command>, Data> lookup, @NotNull Data data) {
    if (lookup == null || data == null)
      throw new NullPointerException("lookup and data must not be null");

    final Command found = lookup.commence(this, data);
    if (found == null) {
      return false;
    }

    return commands.remove(found);
  }

  /**
   * Get a copy of all commands registered to this controller.
   *
   * @return {@link Set<Command>}
   */
  public Set<Command> commands() {
    return new HashSet<>(commands);
  }
}