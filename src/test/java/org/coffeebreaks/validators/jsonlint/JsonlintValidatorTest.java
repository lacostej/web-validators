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

package org.coffeebreaks.validators.jsonlint;

import org.coffeebreaks.validators.ValidationRequest;
import org.coffeebreaks.validators.ValidationResult;
import org.coffeebreaks.validators.util.IfOfflineCondition;
import org.coffeebreaks.validators.util.RuntimeIgnore;
import org.coffeebreaks.validators.util.RuntimeIgnoreAwareRunner;
import org.coffeebreaks.validators.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:10 PM
 */
@RunWith(RuntimeIgnoreAwareRunner.class)
public class JsonlintValidatorTest {
  private JsonlintValidator validator;
  private ValidationRequest request;
  @Before
  public void setUp() {
    validator = new JsonlintValidator();
    request = mock(ValidationRequest.class);
  }

  @After
  public void tearDown() {

  }

  @Test
  @RuntimeIgnore(ifTrue = IfOfflineCondition.class)
  public void validateRedditJsonUri() throws Exception {
    ValidationResult result = validator.validateUri(new URL("http://www.reddit.com/r/programming/comments/9szpc/jsonlint_a_handy_json_validator_and_reformatter.json"), request);
    System.out.println(result.getResponseContent());
    assertEquals(0, result.getErrorCount());
  }

  @Test
  @RuntimeIgnore(ifTrue = IfOfflineCondition.class)
  public void uploadValidJson() throws IOException {
    InputStream inputStream = getContent("/jsonlint/valid.json");
    ValidationResult result = validator.validateContent(inputStream, request);
    assertEquals("no errors", 0, result.getErrorCount());
  }

  @Test
  @RuntimeIgnore(ifTrue = IfOfflineCondition.class)
  public void uploadInvalidJson() throws IOException {
    InputStream inputStream = getContent("/jsonlint/invalid.json");
    ValidationResult result = validator.validateContent(inputStream, request);
    assertEquals("errors expected", 1, result.getErrorCount());
  }

  @Test
  public void testParseJSonOK() throws IOException {
    String jsonString = loadUTF8Resource("/jsonlint/response_valid.json");

    JsonlintValidator.JsonlintValidatorJSonOutput json = JsonlintValidator.parseJSonObject(jsonString);
    assertFalse("result determinate", json.isResultIndeterminate());
    assertEquals(0, json.getErrorCount());
    assertEquals(0, json.getWarningCount());
  }

  @Test
  public void testParseJSonWithError() throws IOException {
    String jsonString = loadUTF8Resource("/jsonlint/response_error.json");

    JsonlintValidator.JsonlintValidatorJSonOutput json = JsonlintValidator.parseJSonObject(jsonString);
    assertFalse("result determinate", json.isResultIndeterminate());
    assertEquals(1, json.getErrorCount());
    assertEquals(0, json.getWarningCount());
  }

  private String loadUTF8Resource(String resource) throws IOException {
    return StringUtil.readIntoString(getContent(resource), "UTF-8");
  }

  private InputStream getContent(String resource) throws IOException {
    return JsonlintValidatorTest.class.getResourceAsStream(resource);
  }
}
