package xyz.oliwer.twitch.bot.structure;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * This data class represents an extracted user.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class ExtractedUser implements Snowflake<String> {
  /**
   * {@link String} the identifier of this user.
   */
  private final String id;

  /**
   * {@link String} the name of this user.
   */
  private final String name;

  /**
   * {@link Set<CommandPermission>} set of permissions.
   */
  private final Set<CommandPermission> permissions;

  /**
   * Primary constructor.
   */
  public ExtractedUser(String id, String name, Set<CommandPermission> permissions) {
    // enforce non-nullable values
    if (id == null || name == null)
      throw new NullPointerException("id and name must not be null");

    // initialize our values
    this.id = id;
    this.name = name;
    this.permissions = EnumSet.copyOf(permissions == null ? new HashSet<>() : permissions);
  }

  /**
   * @param user {@link EventUser} event user object to fetch name and identifier from.
   * @see ExtractedUser#ExtractedUser(String, String, Set)
   */
  public ExtractedUser(EventUser user, Set<CommandPermission> permissions) {
    this(user.getId(), user.getName(), permissions);
  }

  /**
   * @param event {@link ChannelMessageEvent} message event to fetch user and permission from.
   * @see ExtractedUser#ExtractedUser(EventUser, Set)
   */
  public ExtractedUser(ChannelMessageEvent event) {
    this(event.getUser(), event.getPermissions());
  }

  /**
   * Get whether this user can access a permission.
   *
   * @param permission {@link CommandPermission} the permission to check.
   * @return {@link Boolean}
   */
  public boolean canAccess(CommandPermission permission) {
    return this.permissions.contains(permission);
  }

  /**
   * Get a copy of all permissions that this user have.
   *
   * @return {@link Set<CommandPermission>}
   */
  public Set<CommandPermission> permissions() {
    return new HashSet<>(this.permissions);
  }

  /**
   * @see Snowflake#id()
   */
  @Override
  public String id() {
    return this.id;
  }

  /**
   * Get the name of this user.
   *
   * @return {@link String}
   */
  public String getName() {
    return this.name;
  }
}