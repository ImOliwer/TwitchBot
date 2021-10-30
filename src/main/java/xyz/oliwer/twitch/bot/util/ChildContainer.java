package xyz.oliwer.twitch.bot.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/**
 * This functional interface represents a single method
 * to fetch all children of said container.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
@FunctionalInterface
public interface ChildContainer<Type> {
  /**
   * Get the children of this container.
   *
   * @return {@link Set<Type>}
   */
  @NotNull
  Collection<Type> children();
}