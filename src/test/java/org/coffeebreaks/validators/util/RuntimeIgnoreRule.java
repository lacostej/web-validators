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

import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/14/11 6:18 PM
 */
public class RuntimeIgnoreRule implements MethodRule {

  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    Assume.assumeTrue(!isRuntimeIgnored(method));
    return base;
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
}
