package xyz.oliwer.twitch.bot.util.annotation;

import xyz.oliwer.twitch.bot.subscribers.CommandSubscriber;

/**
 * Whatever type is annotated with this is marked as one
 * that is used "silently" by types provided in the `by` method.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 * @see CommandSubscriber
 */
public @interface SilentUse {
  /**
   * Get the array of types silently used by.
   *
   * @return {@link Class}
   */
  Class<?>[] by();
}