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

package org.coffeebreaks.validators;

import java.util.HashMap;
import java.util.Map;

/**
 * This class isn't part of the public API yet.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/11/11 1:43 PM
 */
public class SimpleValidationRequest implements ValidationRequest {

  private Map<String, Object> map = new HashMap<String, Object>();

  public <T> T getValue(String name, T defaultValue) {
    Object object = map.get(name);
    if(object == null){
      return defaultValue;
    } else {
      try {
        return (T) object;
      } catch(Exception e) {
        throw new IllegalStateException("Request parameter named " + name + " not stored as type " + defaultValue.getClass(), e);
      }
    }
  }

}
