package xyz.oliwer.twitch.bot.structure;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IDisposable;
import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import xyz.oliwer.twitch.bot.util.ChatMessage;
import xyz.oliwer.twitch.bot.util.Connector;
import xyz.oliwer.twitch.bot.util.Forwarder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This class represents the client for our bot.
 *
 * @author Oliwer - https://www.github.com/ImOliwer
 */
public final class BotClient implements Connector<String>, Forwarder<String, ChatMessage> {
  /** {@link TwitchClient} the main client for this application. **/
  private final TwitchClient twitch;

  /**
   * {@link Map} a map holding all subscribers.
   */
  private final Map<EventSubscriber, Set<IEventSubscription>> subscribers = new ConcurrentHashMap<>();

  /**
   * Primary constructor.
   *
   * @param properties {@link Properties} the properties of this bot client.
   */
  public BotClient(Properties properties) {
    if (properties == null) {
      throw new NullPointerException("Bot client properties must not be null");
    }

    final String clientId = properties.getProperty("Client-Id");
    final String chatAccessToken = properties.getProperty("Chat-Bot-Access-Token");

    if (clientId == null || chatAccessToken == null) {
      throw new NullPointerException("Property 'Client-Id' and/or 'Chat-Bot-Access-Token' are missing");
    }

    this.twitch = TwitchClientBuilder
      .builder()
      .withTimeout(15)
      .withClientId(clientId)
      .withEnableChat(true)
      .withChatAccount(new OAuth2Credential("twitch", chatAccessToken))
      .withEnablePubSub(true)
      .withEnableHelix(true)
      .withEnableKraken(true)
      .build();
  }

  /**
   * Subscribe to a mass of events via an implementation of {@link EventSubscriber}.
   *
   * @param provider {@link Class} class of the provider to subscribe to.
   * @return {@link Boolean} whether the subscription was successful.
   */
  public <Subscriber extends EventSubscriber> boolean subscribe(Subscriber provider) {
    if (subscribers.containsKey(provider)) {
      return false;
    }

    final EventManager eventManager = twitch.getEventManager();
    final Set<IEventSubscription> subscriptions = new HashSet<>();
    final Method[] methods = provider.getClass().getMethods();

    if (methods.length == 0) {
      return false;
    }

    for (final Method method : methods) {
      final Class<?>[] parameters = method.getParameterTypes();
      if ((method.getModifiers() & Modifier.PUBLIC) == 0 || parameters.length != 1)
        continue;

      final Class<?> eventType = parameters[0];
      if (eventType == ChannelMessageEvent.class) {
        subscriptions.add(
          eventManager.onEvent(eventType, it -> {
            try {
              method.invoke(provider, it);
            } catch (Exception ignored) {}
          })
        );
      }
    }

    subscribers.put(provider, subscriptions);
    return true;
  }

  /**
   * Dispose of all event subscriptions from passed provider.
   *
   * @param provider {@link Class} class of provider to dispose all subscriptions from.
   * @return {@link Boolean} whether all was disposed of successfully.
   */
  public <Subscriber extends EventSubscriber> boolean unsubscribe(Subscriber provider) {
    return with(subscribers.remove(provider), subscriptions -> {
      final boolean wasRemoved = subscriptions != null;
      if (wasRemoved)
        subscriptions.forEach(IDisposable::dispose);
      return wasRemoved;
    });
  }

  /**
   * @see Forwarder#forward(Object, Object, Object...)
   */
  @Override
  public boolean forward(String receiver, ChatMessage data, Object... extra) {
    return with(twitch.getChat(), chat -> {
      if (receiver == null || data == null || extra.length == 0) {
        return false;
      }

      final String message = extra[0].toString();
      switch (data) {
        case REGULAR -> {
          return chat.sendMessage(receiver, message);
        }
        case ACTION  -> {
          return chat.sendActionMessage(receiver, message);
        }
        case WHISPER -> chat.sendPrivateMessage(receiver, message);
      }
      return true;
    });
  }

  /**
   * @see Connector#connect(Object)
   */
  @Override
  public boolean connect(String channel) {
    return handleConnection(true, channel);
  }

  /**
   * @see Connector#disconnect(Object)
   */
  @Override
  public boolean disconnect(String channel) {
    return handleConnection(false, channel);
  }

  /**
   * Handle the connection.
   *
   * @param isConnect {@link Boolean} whether this operation is a "connect" (if not it's "disconnect").
   * @param channel {@link String} the channel to connect to / disconnect from.
   * @return {@link Boolean} state of operation - successful or not.
   */
  private boolean handleConnection(boolean isConnect, String channel) {
    return with(twitch.getChat(), chat -> {
      final boolean shouldContinue = channel != null && isConnect != chat.isChannelJoined(channel);
      if (shouldContinue) {
        if (isConnect) chat.joinChannel(channel);
        else chat.leaveChannel(channel);
      }
      return shouldContinue;
    });
  }

  /**
   * Perform an operation with passed instance in a cleaner way.
   */
  private static <Type, Return> Return with(Type instance, Function<Type, Return> application) {
    return application.apply(instance);
  }
}