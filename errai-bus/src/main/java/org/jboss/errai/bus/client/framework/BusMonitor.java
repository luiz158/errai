/*
 * Copyright 2009 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.bus.client.framework;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.bus.server.MessageQueue;

public interface BusMonitor {
    public void attach(MessageBus bus);

    public void notifyNewSubscriptionEvent(SubscriptionEvent event);
    public void notifyUnSubcriptionEvent(SubscriptionEvent event);

    public void notifyQueueAttached(Object queueId, Object queueInstance);

    public void notifyIncomingMessageFromRemote(Object queue, Message message);
    public void notifyOutgoingMessageToRemote(Object queue, Message message);
    public void notifyInBusMessage(Message message);

    public void notifyMessageDeliveryFailure(Object queue, Message mesage);
}