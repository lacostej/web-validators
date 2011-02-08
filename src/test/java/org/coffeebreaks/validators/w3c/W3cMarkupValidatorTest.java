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

import org.coffeebreaks.validators.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:10 PM
 */
public class W3cMarkupValidatorTest {
  private W3cMarkupValidator validator;
  @Before
  public void setUp() {
    // validator = new W3cMarkupValidator("http://localhost/w3c-markup-validator/");
    validator = new W3cMarkupValidator("http://validator.w3.org/") {
      @Override
      public ValidationResult validateContent(InputStream inputStream) throws IOException {
        dontOverloadW3cServer();
        return super.validateContent(inputStream);
      }
      @Override
      public ValidationResult validateUri(URL url) throws IOException, URISyntaxException {
        dontOverloadW3cServer();
        return super.validateUri(url);
      }
    };
  }
  private void dontOverloadW3cServer() {
    try{
      Thread.sleep(1000);
    } catch(InterruptedException e){
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  @After
  public void tearDown() {

  }

  @Test
  public void uploadValidHTML4_01TransitionalFile() throws IOException {
    InputStream inputStream = getContent("/valid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream);
    System.out.println(result.getResponseContent());
    assertEquals("no errors", 0, result.getErrorCount());
    assertEquals("warning", 1, result.getWarningCount());
  }

  @Test
  public void validateCoffeebreaksOrgUri() throws Exception {
    ValidationResult result = validator.validateUri(new URL("http://coffeebreaks.org/"));
    System.out.println(result.getResponseContent());
    assertEquals(0, result.getErrorCount());
    assertEquals(0, result.getWarningCount());
  }

  @Test
  public void uploadInvalidHTML4_01TransitionalFile_1() throws IOException {
    InputStream inputStream = getContent("/invalid4.01Transitional_1.html");
    ValidationResult result = validator.validateContent(inputStream);
    System.out.println(result.getResponseContent());
    assertEquals("no error", 0, result.getErrorCount());
    assertEquals("2 warnings", 2, result.getWarningCount());
  }

  @Test
  public void uploadInvalidHTML4_01TransitionalFile_2() throws IOException {
    InputStream inputStream = getContent("/invalid4.01Transitional_2.html");
    ValidationResult result = validator.validateContent(inputStream);
    System.out.println(result.getResponseContent());
    assertEquals("errors", 2, result.getErrorCount());
    assertEquals("warning", 1, result.getWarningCount());
  }

  @Test
  public void testParseSoapOK() throws IOException {
    String soapString = getContentAsString("/valid4.01Transitional_soap_response.xml", "UTF-8");
    W3cMarkupValidator.W3cSoapValidatorSoapOutput soap = W3cMarkupValidator.parseSoapObject(soapString);
    assertFalse("result determinate", soap.isResultIndeterminate());
    assertEquals(0, soap.getErrorCount());
    assertEquals(1, soap.getWarningCount());
  }

  @Test
  public void testParseSoapWithError_1() throws IOException {
    String soapString = getContentAsString("/invalid4.01Transitional_1_soap_response.xml", "UTF-8");
    W3cMarkupValidator.W3cSoapValidatorSoapOutput soap = W3cMarkupValidator.parseSoapObject(soapString);
    assertFalse("result determinate", soap.isResultIndeterminate());
    assertEquals(0, soap.getErrorCount());
    assertEquals(2, soap.getWarningCount());
  }

  @Test
  public void testParseSoapWithError_2() throws IOException {
    String soapString = getContentAsString("/invalid4.01Transitional_2_soap_response.xml", "UTF-8");
    W3cMarkupValidator.W3cSoapValidatorSoapOutput soap = W3cMarkupValidator.parseSoapObject(soapString);
    assertFalse("result determinate", soap.isResultIndeterminate());
    assertEquals(2, soap.getErrorCount());
    assertEquals(1, soap.getWarningCount());
  }

  private InputStream getContent(String resource) throws IOException {
    return W3cMarkupValidatorTest.class.getResourceAsStream(resource);
  }
  private String getContentAsString(String resource, String charset) throws IOException {
    return StringUtil.readIntoString(W3cMarkupValidatorTest.class.getResourceAsStream(resource), charset);
  }
}
