package org.jboss.errai.marshalling.tests;

import org.jboss.errai.codegen.framework.meta.MetaClassFactory;
import org.jboss.errai.marshalling.rebind.MarshallerGeneratorFactory;
import org.jboss.errai.marshalling.tests.res.SType;
import org.junit.Test;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class BasicMarshallingTests {
  @Test
  public void test1() {
    for (int i = 0; i < 2; i++) {
      new MarshallerGeneratorFactory().generate("org.foo", "MarshallerBootstrapperImpl");
    }
 //   System.out.println("HAPPY!");
  }

  @Test
  public void test2() {
    System.out.println(Object[].class.getName());
  }
}