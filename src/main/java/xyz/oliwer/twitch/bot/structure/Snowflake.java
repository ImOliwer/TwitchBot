package xyz.oliwer.twitch.bot.structure;

/**
 * This interface represents a snowflake - the mark of a uniquely identified object.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public interface Snowflake {
  /**
   * Get the identifier of this snowflake.
   *
   * @return {@link Long}
   */
  long id();
}