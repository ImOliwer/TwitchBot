package xyz.oliwer.twitch.bot.command;

import org.jetbrains.annotations.NotNull;
import xyz.oliwer.twitch.bot.structure.BotClient;
import xyz.oliwer.twitch.bot.structure.ExtractedUser;
import xyz.oliwer.twitch.bot.util.ChildContainer;

import java.util.*;
import java.util.function.Consumer;

/**
 * This abstraction layer represents a chat (primary and sub) command.
 *
 * TODO: Implement a way to define the owner of this command - i.e "system" or "user" for global and private commands.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public abstract class Command implements ChildContainer<Command> {
  /**
   * {@link Byte} this constant represents the command prefix (33 = '!').
   */
  public static final byte PREFIX = 33;

  /**
   * {@link Set<Command>} this property represents a collection of this command's children.
   */
  private final Set<Command> children = new HashSet<>();

  /**
   * {@link Set<Requirement>} this property represents all requirements needed to perform corresponding command.
   */
  private final Set<Requirement> requirements = new LinkedHashSet<>();

  /**
   * Perform the command for said user alongside arguments.
   *
   * Note: This method is only invoked after all requirements are met.
   *
   * @param user {@link ExtractedUser} whom executed this command.
   * @param client {@link BotClient} the client from where this command was executed.
   * @param channel {@link String} name of the channel this command was executed in.
   * @param arguments {@link String} array of arguments executed with the command.
   */
  public abstract void perform(ExtractedUser user, BotClient client, String channel, String[] arguments);

  /**
   * Get the aliases of this command.
   *
   * @return {@link String}
   */
  public abstract String[] getAliases();

  /**
   * This method is invoked only when an invalid alias of a child is executed.
   *
   * @param user {@link ExtractedUser} the user who executed the command.
   * @param client {@link BotClient} the client from where this command was executed.
   * @param usedAlias {@link String} alias of unknown child which was used during execution.
   */
  public void onInvalidChild(ExtractedUser user, BotClient client, String usedAlias) {}

  /**
   * The private owner of this command (null if global).
   *
   * @return {@link String}
   */
  public String representative() {
    return null;
  }

  /**
   * Generate a new meta builder.
   *
   * @return {@link MetaBuilder}
   */
  public MetaBuilder meta() {
    return new MetaBuilder();
  }

  /**
   * @see ChildContainer#children()
   * @return {@link Set<Command>} copy of current children.
   */
  @Override
  public @NotNull Set<Command> children() {
    return new HashSet<>(children);
  }

  /**
   * Get a copy of the requirements for this command.
   *
   * @return {@link Set<Requirement>}
   */
  public Set<Requirement> requirements() {
    return new LinkedHashSet<>(requirements);
  }

  /**
   * This functional interface represents a single method
   * to check if a user meets said requirement(s).
   *
   * @author Oliwer - https://www.github.com/ImOliwer
   */
  @FunctionalInterface
  public interface Requirement {
    /**
     * Check if a user meets the requirements needed to pass.
     *
     * @param user {@link ExtractedUser} the user to attempt said check on.
     * @return {@link Boolean} whether the user meets said requirements.
     */
    boolean attempt(ExtractedUser user);
  }

  /**
   * This class represents the builder for command options (children & requirements).
   *
   * @author Oliwer - https://www.github.com/ImOliwer
   */
  public static final class MetaBuilder {
    /**
     * {@link List<Consumer>} this property represents all changes made in this builder.
     */
    private final List<Consumer<Command>> changes;

    /**
     * Primary constructor.
     */
    private MetaBuilder() {
      this.changes = new LinkedList<>();
    }

    /**
     * Include a requirement in the builder.
     *
     * @param requirement {@link Requirement} the requirement to add.
     * @return {@link MetaBuilder} current instance.
     */
    public MetaBuilder and(Requirement requirement) {
      return also(command -> command.requirements.add(requirement));
    }

    /**
     * Include a command child in the builder.
     *
     * @param child {@link Command} the child to add.
     * @return {@link MetaBuilder} current instance.
     */
    public MetaBuilder and(Command child) {
      return also(command -> command.children.add(child));
    }

    /**
     * Apply a change to the corresponding command of which will be applied to.
     *
     * @param metaChange {@link Consumer<Command>} the change to insert.
     * @return {@link MetaBuilder} current instance.
     */
    private MetaBuilder also(Consumer<Command> metaChange) {
      changes.add(metaChange);
      return this;
    }

    /**
     * Apply the changes to a primary command.
     *
     * @param primary {@link Command} the command to apply changes to.
     */
    public void apply(Command primary) {
      for (Consumer<Command> change : this.changes)
        change.accept(primary);
    }
  }
}