package xyz.oliwer.twitch.bot.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface represents a controller.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public interface Controller<Of> {
  /**
   * Register a new value to this controller.
   *
   * @param value {@link Of} instance to register.
   * @return {@link Boolean} whether the registration was successful.
   */
  boolean register(@NotNull Of value);

  /**
   * Unregister a value by lookup.
   *
   * @param lookup {@link Lookup} type of lookup to commence.
   * @param data {@link Data} the data to match lookup with.
   * @return {@link Boolean} whether the operation was successful.
   */
  <Data> boolean unregister(@NotNull Lookup<Of, Controller<Of>, Data> lookup, @NotNull Data data);

  /**
   * This functional interface represents a single method
   * to commence a lookup search for a type by controller and data.
   *
   * @param <Type> the type to look for.
   * @param <Data> the data required to perform search.
   *
   * @author Oliwer - https://www.github.com/ImOliwer
   */
  @FunctionalInterface
  interface Lookup<Type, C extends Controller<Type>, Data> {
    /**
     * Commence a search for type by controller and data.
     *
     * @param controller {@link C} the controller to search through.
     * @param data {@link Data} the data needed to execute the search.
     * @return {@link Type}
     */
    @Nullable
    Type commence(@NotNull C controller, @NotNull Data data);
  }
}