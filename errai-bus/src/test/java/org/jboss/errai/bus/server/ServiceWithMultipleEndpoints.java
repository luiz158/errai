package org.jboss.errai.bus.server;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.server.annotations.Command;
import org.jboss.errai.bus.server.annotations.Service;

/**
 * @author Mike Brock
 */
@Service("TestSvc")
public class ServiceWithMultipleEndpoints {

  @Command("foo")
  public void foo(Message message) {
    MessageBuilder.createConversation(message)
            .subjectProvided()
            .with("Msg", "Foo!")
            .done().reply();
  }

  @Command("bar")
  public void bar(Message message) {
    MessageBuilder.createConversation(message)
            .subjectProvided()
            .with("Msg", "Bar!")
            .done().reply();
  }

}
