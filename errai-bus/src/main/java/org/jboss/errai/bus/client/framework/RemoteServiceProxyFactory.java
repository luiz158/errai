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
package org.jboss.errai.bus.client.framework;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.framework.Assert;

/**
 * {@link ProxyFactory} storing {@link ProxyProvider}s for generated remote proxies.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class RemoteServiceProxyFactory implements ProxyFactory {
  private static Map<Class<?>, ProxyProvider> remoteProxyProviders = new HashMap<Class<?>, ProxyProvider>();

  @Override
  @SuppressWarnings({ "unchecked" })
  public <T> T getRemoteProxy(Class<T> proxyType) {
    Assert.notNull(proxyType);
    
    if (remoteProxyProviders.isEmpty()) {
      throw new RuntimeException("There are no proxy providers registered yet.");
    }
    
    ProxyProvider proxyProvider = remoteProxyProviders.get(proxyType);
    if (proxyProvider == null) {
      throw new RuntimeException("No proxy provider found for type:"+proxyType.getName());
    }
    
    Object proxy = proxyProvider.getProxy();
    if (proxy == null) {
      throw new RuntimeException("No proxy instance provided for: " + proxyType.getName());
    }
    
    return (T) proxy;
  }

  public static void addRemoteProxy(Class<?> proxyType, ProxyProvider proxyProvider) {
    Assert.notNull(proxyType);
    Assert.notNull(proxyProvider);
    
    remoteProxyProviders.put(proxyType, proxyProvider);
  }
}
