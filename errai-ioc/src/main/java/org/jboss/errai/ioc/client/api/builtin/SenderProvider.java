/*
 * Copyright 2011 JBoss, by Red Hat, Inc
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

package org.jboss.errai.ioc.client.api.builtin;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.api.ProviderException;
import org.jboss.errai.ioc.client.api.ReplyTo;
import org.jboss.errai.ioc.client.api.Sender;
import org.jboss.errai.ioc.client.api.ToSubject;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * @author Mike Brock .
 */
@Singleton
@IOCProvider
public class SenderProvider implements ContextualTypeProvider<Sender<?>> {
  @Override
  public Sender provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    String toSubject = null, replyTo = null;
    typeargs = typeargs == null ? new Class<?>[0] : typeargs;

    for (Annotation a : qualifiers) {
      if (a instanceof ToSubject) {
        toSubject = ((ToSubject) a).value();
      }
      else if (a instanceof ReplyTo) {
        replyTo = ((ReplyTo) a).value();
      }
    }

    if (typeargs.length != 1) {
      throw new ProviderException(PROVIDER_EXCEPTION_ERROR_MSG_BASE + ": Type at injection point must have exactly" +
              " one type parameter. (found: " + typeargs.length + ")");
    }

    if (toSubject == null) {
      throw new ProviderException(PROVIDER_EXCEPTION_ERROR_MSG_BASE + ": Required "
              + ToSubject.class.getName() + " qualifier missing at injection point.");
    }

    if (typeargs.length != 1) {
      throw new ProviderException(PROVIDER_EXCEPTION_ERROR_MSG_BASE + ": Type at injection point must have exactly" +
              " one type parameter. (found: " + typeargs.length + ")");
    }

    return ErraiMessageSender.of(toSubject, replyTo, ErraiBus.get());
  }

  private static final String PROVIDER_EXCEPTION_ERROR_MSG_BASE = "Injection of " + Sender.class.getName()
          + " implicit bean failed. ";
}
