package xyz.oliwer.twitch.bot.structure;

/**
 * This interface represents a snowflake - the mark of a uniquely identified object.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public interface Snowflake<Type> {
  /**
   * Get the identifier of this snowflake.
   *
   * @return {@link Type}
   */
  Type id();
}