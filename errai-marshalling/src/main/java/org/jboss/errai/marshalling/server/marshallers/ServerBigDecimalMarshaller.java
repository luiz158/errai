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

package org.jboss.errai.marshalling.server.marshallers;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.marshallers.AbstractBigDecimalMarshaller;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Mike Brock
 */
@ServerMarshaller(multiReferenceable = true)
public class ServerBigDecimalMarshaller extends AbstractBigDecimalMarshaller<Map> {
  @Override
  public BigDecimal demarshall(Map o, MarshallingSession ctx) {
    return new BigDecimal(String.valueOf(o.get(SerializationParts.VALUE)));
  }

  @Override
  public boolean handles(Map o) {
    return MarshallUtil.handles(o, getTypeHandled());
  }
}
