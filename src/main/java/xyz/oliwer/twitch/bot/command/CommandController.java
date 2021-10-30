package xyz.oliwer.twitch.bot.command;

import org.jetbrains.annotations.NotNull;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;
import xyz.oliwer.twitch.bot.util.ChildContainer;
import xyz.oliwer.twitch.bot.util.Controller;

import java.util.*;

import static xyz.oliwer.twitch.bot.command.Command.Requirement;

/**
 * This class represents the controller for all commands.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class CommandController implements Controller<Command>, ChildContainer<Command> {
  /**
   * {@link Perform} this property represents the performer for commands.
   */
  public static final Perform PERFORMER = (parent, commandAlias, arguments, user, client) -> {
    if (parent == null || commandAlias == null || arguments == null || user == null || client == null) {
      throw new NullPointerException("all of parent, commandAlias, arguments, user and client must NOT be null");
    }

    final String transformed = commandAlias.toLowerCase(Locale.ROOT);
    final Set<Command> searchThrough = (Set<Command>) parent.children();
    boolean failedToMatchAll = true;

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
      failedToMatchAll = false;

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

    if (failedToMatchAll && parent instanceof Command) {
      ((Command) parent).onInvalidChild(user, client, transformed);
    }
  };

  /**
   * {@link Set<Command>} this property represents all commands registered to this controller.
   */
  private final Set<Command> commands = new HashSet<>();

  /**
   * @see CommandController#PERFORMER
   */
  public void tryPerform(
    ChildContainer<Command> parent,
    String commandAlias,
    String[] arguments,
    ExtractedUser user,
    BotClient client
  ) {
    PERFORMER.commence(parent == null ? this : parent, commandAlias, arguments, user, client);
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
   * @return {@link Set<Command>} copy of current children.
   */
  public Set<Command> commands() {
    return new HashSet<>(commands);
  }

  /**
   * @see ChildContainer#children()
   * @return {@link Set<Command>}
   */
  @Override
  public @NotNull Collection<Command> children() {
    return new HashSet<>(commands);
  }

  /**
   * This functional interface represents a single method
   * used to commence the execution of a command.
   *
   * @author Oliwer - https://www.github.com/ImOliwer
   */
  @FunctionalInterface
  public interface Perform {
    /**
     * Commence the execution.
     */
    void commence(
      @NotNull ChildContainer<Command> parent,
      @NotNull String commandAlias,
      @NotNull String[] arguments,
      @NotNull ExtractedUser user,
      @NotNull BotClient client
    );
  }
}