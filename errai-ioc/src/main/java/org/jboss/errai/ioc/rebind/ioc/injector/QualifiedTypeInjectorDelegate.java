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

package org.jboss.errai.ioc.rebind.ioc.injector;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableInstance;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.RegistrationHook;
import org.jboss.errai.ioc.rebind.ioc.metadata.QualifyingMetadata;

/**
 * This injector wraps another injector to create qualifying references based on type parameters and qualifiers
 * to the underlying bean. For instance if two beans implement a common interface, with two different type
 * parameters, each bean will be wrapped in this injector and added as common injectors to the interface.
 *
 * @author Mike Brock
 */
public class QualifiedTypeInjectorDelegate extends AbstractInjector {
  private MetaClass type;
  private Injector delegate;

  public QualifiedTypeInjectorDelegate(MetaClass type, Injector delegate, MetaParameterizedType parameterizedType) {
    this.type = type;
    this.delegate = delegate;
    this.qualifyingMetadata = delegate.getQualifyingMetadata();
    this.qualifyingTypeInformation = parameterizedType;

    delegate.addRegistrationHook(
           new RegistrationHook() {
             @Override
             public void onRegister(InjectionContext context, Statement beanValue) {
               registerWithBeanManager(context, beanValue);
             }
           }
    );
  }

  @Override
  public boolean isRendered() {
    return delegate.isRendered();
  }

  @Override
  public boolean isTestmock() {
    return delegate.isTestmock();
  }

  @Override
  public boolean isSingleton() {
    return delegate.isSingleton();
  }

  @Override
  public boolean isPseudo() {
    return delegate.isPseudo();
  }

  @Override
  public String getVarName() {
    return delegate.getVarName();
  }

  @Override
  public MetaClass getInjectedType() {
    return delegate.getInjectedType();
  }

  @Override
  public Statement getBeanInstance(InjectableInstance injectableInstance) {
    return delegate.getBeanInstance(injectableInstance);
  }

  @Override
  public boolean isDependent() {
    return delegate.isDependent();
  }

  @Override
  public boolean isProvider() {
    return delegate.isProvider();
  }

  @Override
  public MetaClass getEnclosingType() {
    return delegate.getEnclosingType();
  }

  @Override
  public String getPostInitCallbackVar() {
    return delegate.getPostInitCallbackVar();
  }

  @Override
  public String getPreDestroyCallbackVar() {
    return delegate.getPreDestroyCallbackVar();
  }

  @Override
  public void setPostInitCallbackVar(String var) {
    delegate.setPostInitCallbackVar(var);
  }

  @Override
  public void setPreDestroyCallbackVar(String preDestroyCallbackVar) {
    delegate.setPreDestroyCallbackVar(preDestroyCallbackVar);
  }

  @Override
  public String getCreationalCallbackVarName() {
    return delegate.getCreationalCallbackVarName();
  }

  public void registerWithBeanManager(InjectionContext context, Statement valueRef) {
    if (InjectUtil.checkIfTypeNeedsAddingToBeanStore(context, this)) {
      QualifyingMetadata md = delegate.getQualifyingMetadata();
      if (md == null) {
        md = context.getProcessingContext().getQualifyingMetadataFactory().createDefaultMetadata();
      }

      context.getProcessingContext().appendToEnd(
              Stmt.loadVariable(context.getProcessingContext().getContextVariableReference())
                      .invoke("addBean", type, Refs.get(delegate.getCreationalCallbackVarName()),
                              isSingleton() ? valueRef : null , md.render()));
    }
  }

  @Override
  public boolean isAlternative() {
    return delegate.isAlternative();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
