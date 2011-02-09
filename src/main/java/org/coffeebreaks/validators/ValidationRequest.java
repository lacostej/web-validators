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

/**
 * So far, no real implementation is made for this class.
 * <p>
 * IDEA: Particular validators will come with their own implementations, in order to ease setup, but the only method they
 * will use internally is this general call, allowing implementers to chose their own implementation. I am not yet sure this is a good idea.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/9/11 12:42 AM
 */
public interface ValidationRequest {
  <T>T  getValue(String name, T defaultValue);
}
