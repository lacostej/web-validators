/*
 * Copyright (C) 2011 by Jerome Lacoste (jerome@coffeebreaks.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.coffeebreaks.validators.util;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * A JUnit4 runner aware of RuntimeIgnore. Can't we backport this back to Ignore/BlockJunit4ClassRunner
 *
 * @author jerome@coffeebreaks.org
 * @since 2/10/11 11:00 PM
 */
public class RuntimeIgnoreAwareRunner extends BlockJUnit4ClassRunner {

  public RuntimeIgnoreAwareRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    EachTestNotifier eachNotifier= makeNotifier(method, notifier);
    if (isRuntimeIgnored(method)) {
      eachNotifier.fireTestIgnored();
    } else {
      super.runChild(method, notifier);
    }
  }
  private boolean isRuntimeIgnored(FrameworkMethod method) {
    RuntimeIgnore annotation = method.getAnnotation(RuntimeIgnore.class);
    return annotation != null && annotation.ifTrue() != null && isTrue(annotation.ifTrue(), method);
  }

  private boolean isTrue(Class<? extends RuntimeCondition> cl, FrameworkMethod method) {
    try{
      return cl.newInstance().isTrue(method);
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  private EachTestNotifier makeNotifier(FrameworkMethod method,
      RunNotifier notifier) {
    Description description= describeChild(method);
    return new EachTestNotifier(notifier, description);
  }
}
