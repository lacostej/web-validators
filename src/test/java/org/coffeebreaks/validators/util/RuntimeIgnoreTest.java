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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import static org.junit.Assert.*;

/**
 * Test the junit extension for RuntimeIgnore
 */
@RunWith(RuntimeIgnoreAwareRunner.class)
public class RuntimeIgnoreTest {
  @Ignore
  @Test
  public void testStandardIgnore() {
  }

  static class NameAwareIgnoreCondition implements RuntimeCondition {
    public boolean isTrue(FrameworkMethod method) {
      return method.getName().endsWith("Ignore");
    }
  }

  static class FalseIgnoreCondition implements RuntimeCondition {
    public boolean isTrue(FrameworkMethod method) {
      return false;
    }
  }

  @RuntimeIgnore(ifTrue=NameAwareIgnoreCondition.class)
  @Test
  public void testRuntimeIgnore() {
    fail("was run");
  }

  @RuntimeIgnore(ifTrue=NameAwareIgnoreCondition.class)
  @Test
  public void testRuntimeAccept() {
    assertTrue("not ignored", true);
  }
}
