package org.jboss.errai.bus.server.service;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.MessageListener;
import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.client.api.SubscribeListener;
import org.jboss.errai.bus.client.api.UnsubscribeListener;
import org.jboss.errai.bus.client.framework.BooleanRoutingRule;
import org.jboss.errai.bus.client.framework.BusMonitor;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.bus.client.framework.Subscription;
import org.jboss.errai.bus.server.api.MessageQueue;
import org.jboss.errai.bus.server.api.QueueClosedListener;
import org.jboss.errai.bus.server.api.ServerMessageBus;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Mike Brock
 */
public class MessageBusProxy implements ServerMessageBus {
  private List<Message> heldGlobalMessages = new ArrayList<Message>();
  private List<Message> heldMessages = new ArrayList<Message>();
  private Map<Message, Boolean> heldMessageFireListener = new LinkedHashMap<Message, Boolean>();
  private Multimap<String, MessageCallback> heldSubscribe = LinkedHashMultimap.create();
  private Multimap<String, MessageCallback> heldLocalSubscribe = LinkedHashMultimap.create();
  private List<MessageListener> heldGlobalListener = new ArrayList<MessageListener>();
  private List<SubscribeListener> heldSubscribeListener = new ArrayList<SubscribeListener>();
  private List<UnsubscribeListener> heldUnsubscribeListener = new ArrayList<UnsubscribeListener>();
  private BusMonitor heldBusMonitor;

  private MessageBus proxied;
  private boolean proxyClosed;

  @Override
  public void sendGlobal(Message message) {
    if (proxyClosed) {
      proxied.sendGlobal(message);
    }
    else {
      heldGlobalMessages.add(message);
    }
  }

  @Override
  public void send(Message message) {
    if (proxyClosed) {
      proxied.send(message);
    }
    else {
      heldMessages.add(message);
    }
  }

  @Override
  public void send(Message message, boolean fireListeners) {
    if (proxyClosed) {
      proxied.send(message, fireListeners);
    }
    else {
      heldMessageFireListener.put(message, fireListeners);
    }
  }

  @Override
  public Subscription subscribe(String subject, MessageCallback receiver) {
    if (proxyClosed) {
      return proxied.subscribe(subject, receiver);
    }
    else {
      heldSubscribe.put(subject, receiver);
      return new Subscription() {
        @Override
        public void remove() {
          throw new IllegalStateException("cannot unsubscribe from a proxied MessageBus");
        }
      };
    }
  }

  @Override
  public Subscription subscribeLocal(String subject, MessageCallback receiver) {
    if (proxyClosed) {
      return proxied.subscribe(subject, receiver);
    }
    else {
      heldLocalSubscribe.put(subject, receiver);
      return new Subscription() {
        @Override
        public void remove() {
          throw new IllegalStateException("cannot unsubscribe from a proxied MessageBus");
        }
      };
    }
  }

  @Override
  public void unsubscribeAll(String subject) {
  }

  @Override
  public boolean isSubscribed(String subject) {
    return false;
  }

  @Override
  public void addGlobalListener(MessageListener listener) {
    if (proxyClosed) {
      proxied.addGlobalListener(listener);
    }
    else {
      heldGlobalListener.add(listener);
    }
  }

  @Override
  public void addSubscribeListener(SubscribeListener listener) {
    if (proxyClosed) {
      proxied.addSubscribeListener(listener);
    }
    else {
      heldSubscribeListener.add(listener);
    }
  }

  @Override
  public void addUnsubscribeListener(UnsubscribeListener listener) {
    if (proxyClosed) {
      proxied.addUnsubscribeListener(listener);
    }
    else {
      heldUnsubscribeListener.add(listener);
    }
  }

  @Override
  public MessageQueue getQueue(QueueSession session) {
    return null;
  }

  @Override
  public void closeQueue(String sessionId) {
  }

  @Override
  public void closeQueue(MessageQueue queue) {
  }

  @Override
  public void addRule(String subject, BooleanRoutingRule rule) {
  }

  @Override
  public ExecutorService getScheduler() {
    return null;
  }

  @Override
  public void addQueueClosedListener(QueueClosedListener listener) {
  }

  @Override
  public void configure(ErraiServiceConfigurator service) {
  }

  @Override
  public List<MessageCallback> getReceivers(String subject) {
    return null;
  }

  @Override
  public boolean hasRemoteSubscriptions(String subject) {
    return false;
  }

  @Override
  public boolean hasRemoteSubscription(String sessionId, String subject) {
    return false;
  }

  @Override
  public Map<QueueSession, MessageQueue> getMessageQueues() {
    return null;
  }

  @Override
  public MessageQueue getQueueBySession(String id) {
    return null;
  }

  @Override
  public QueueSession getSessionBySessionId(String id) {
    return null;
  }

  @Override
  public void associateNewQueue(QueueSession oldSession, QueueSession newSession) {
  }

  @Override
  public void stop() {
  }

  @Override
  public void attachMonitor(BusMonitor monitor) {
    this.heldBusMonitor = monitor;
  }

  public void closeProxy(MessageBus bus) {
    if (proxied != null) {
      throw new IllegalStateException("proxy already closed");
    }

    this.proxied = bus;
    this.proxyClosed = true;

    if (heldBusMonitor != null) {
      bus.attachMonitor(heldBusMonitor);
    }

    for (Map.Entry<String, MessageCallback> entry : heldSubscribe.entries()) {
      bus.subscribe(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, MessageCallback> entry : heldLocalSubscribe.entries()) {
      bus.subscribe(entry.getKey(), entry.getValue());
    }

    for (SubscribeListener subscribeListener : heldSubscribeListener) {
      bus.addSubscribeListener(subscribeListener);
    }

    for (UnsubscribeListener unsubscribeListener : heldUnsubscribeListener) {
      bus.addUnsubscribeListener(unsubscribeListener);
    }

    for (MessageListener listener : heldGlobalListener) {
      bus.addGlobalListener(listener);
    }

    for (Message message : heldMessages) {
      bus.send(message);
    }

    for (Message message : heldGlobalMessages) {
      bus.sendGlobal(message);
    }

    for (Map.Entry<Message, Boolean> entry : heldMessageFireListener.entrySet()) {
      bus.send(entry.getKey(), entry.getValue());
    }

    this.heldBusMonitor = null;
    this.heldSubscribe = null;
    this.heldLocalSubscribe = null;
    this.heldSubscribeListener = null;
    this.heldUnsubscribeListener = null;
    this.heldGlobalListener = null;
    this.heldMessages = null;
    this.heldGlobalMessages = null;
    this.heldMessageFireListener = null;
  }
}
