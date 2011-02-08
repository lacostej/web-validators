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

package org.coffeebreaks.validators.w3c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:10 PM
 */
public class BundledCSSValidatorTest {
  private BundledCssValidator validator;

  @Before
  public void setUp() {
    // validator = new W3cMarkupValidator("http://localhost/w3c-markup-validator/");
    validator = new BundledCssValidator();
  }

  @After
  public void tearDown() {}

  @Test
  public void checkValidLocalCss() throws IOException {
    URL url = getContentUrl("/valid2.css");
    ValidationResult result = validator.validateUri(url.toString(), null);
    System.out.println(result.getResponseContent());
    assertEquals("errors", 0, result.getErrorCount());
    assertEquals("warning", 0, result.getWarningCount());
  }

  @Test
  public void checkInvalidLocalCss() throws IOException {
    URL url = getContentUrl("/parse-error2.css");
    ValidationResult result = validator.validateUri(url.toString(), null);
    System.out.println(result.getResponseContent());
    assertEquals("errors", 3, result.getErrorCount());
    assertEquals("warning", 0, result.getWarningCount());
  }

  private URL getContentUrl(String resource) throws IOException {
    return W3cMarkupValidatorTest.class.getResource(resource);
  }
}
