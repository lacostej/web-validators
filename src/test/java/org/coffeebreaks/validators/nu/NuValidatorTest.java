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

package org.coffeebreaks.validators.nu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:10 PM
 */
public class NuValidatorTest {
  private NuValidator validator;
  @Before
  public void setUp() {
    validator = new NuValidator("http://validator.nu/");
  }

  @After
  public void tearDown() {

  }

  @Test
  public void validateCoffeebreaksOrgUri() throws Exception {
    ValidationResult result = validator.validateUri(new URL("http://coffeebreaks.org/"), "html4tr");
    System.out.println(result.getResponseContent());
    assertEquals(1, result.getErrorCount());
  }

  @Test
  public void uploadValidHTML4_01TransitionalFileWithParser() throws IOException {
    InputStream inputStream = getContent("/valid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream, "html4tr");
    assertEquals("no errors", 0, result.getErrorCount());
  }

  @Test
  public void uploadInvalidHTML4_01TransitionalFileWithParser() throws IOException {
    InputStream inputStream = getContent("/invalid4.01Transitional_1.html");
    ValidationResult result = validator.validateContent(inputStream, "html4tr");
    System.out.println(result.getResponseContent());
    assertTrue("at least one error", result.getErrorCount() > 0);
  }

  @Test
  public void uploadValidHTML4_01TransitionalFile() throws IOException {
    InputStream inputStream = getContent("/valid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream, null);
    assertEquals("no errors", 0, result.getErrorCount());
  }

  @Test
  public void uploadInvalidHTML4_01TransitionalFile() throws IOException {
    InputStream inputStream = getContent("/invalid4.01Transitional_1.html");
    ValidationResult result = validator.validateContent(inputStream, null);
    System.out.println(result.getResponseContent());
    assertTrue("at least one error", result.getErrorCount() > 0);
  }

  @Test
  public void testParseJSonOK() {
    String jsonString = "{\"messages\":[{\"type\":\"info\",\"message\":\"HTML4-specific tokenization errors are enabled.\"}]}";
    NuValidator.NuValidatorJSonOutput json = NuValidator.parseJSonObject(jsonString);
    assertFalse("result determinate", json.isResultIndeterminate());
    assertEquals(1, json.getMessages().size());
    assertEquals("info", json.getMessages().get(0).getType());
    assertEquals("HTML4-specific tokenization errors are enabled.", json.getMessages().get(0).getMessage());
  }

  @Test
  public void testParseJSonWithError() {
    String jsonString = "{\"messages\":[{\"type\":\"info\",\"message\":\"HTML4-specific tokenization errors are enabled.\"},{\"type\":\"error\",\"lastLine\":7,\"lastColumn\":7,\"message\":\"The “/>” syntax on void elements is not allowed.  (This is an HTML4-only error.)\",\"extract\":\"\\n</head>\\n<body/>\\n</ht\",\"hiliteStart\":15,\"hiliteLength\":1},{\"type\":\"error\",\"lastLine\":7,\"lastColumn\":7,\"firstColumn\":1,\"message\":\"Self-closing syntax (“/>”) used on a non-void HTML element. Ignoring the slash and treating as a start tag.\",\"extract\":\">\\n</head>\\n<body/>\\n</htm\",\"hiliteStart\":10,\"hiliteLength\":7}]}";
    NuValidator.NuValidatorJSonOutput json = NuValidator.parseJSonObject(jsonString);
    assertFalse("result determinate", json.isResultIndeterminate());
    assertEquals(3, json.getMessages().size());
    assertEquals("info", json.getMessages().get(0).getType());
    assertEquals("error", json.getMessages().get(1).getType());
    assertEquals("error", json.getMessages().get(2).getType());
  }

  @Test
  public void testParseJSonWithNonDocumentError() {
    String jsonString = "{\"messages\":[{\"type\":\"non-document-error\",\"message\":\"I'm dying...........\"}]}";
    NuValidator.NuValidatorJSonOutput json = NuValidator.parseJSonObject(jsonString);
    assertTrue("result determinate", json.isResultIndeterminate());
    assertEquals(1, json.getMessages().size());
  }

  private InputStream getContent(String resource) throws IOException {
    return NuValidatorTest.class.getResourceAsStream(resource);
  }
}
