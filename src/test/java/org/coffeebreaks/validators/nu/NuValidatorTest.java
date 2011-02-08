package org.coffeebreaks.validators.nu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
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
  public void uploadValidHTML4_01TransitionalFileWithParser() throws IOException {
    InputStream inputStream = getContent("/valid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream, "html4tr");
    assertEquals("no errors", 0, result.getErrorCount());
  }

  @Test
  public void uploadInvalidHTML4_01TransitionalFileWithParser() throws IOException {
    InputStream inputStream = getContent("/invalid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream, "html4tr");
    System.out.println(result.getJSonOutput());
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
    InputStream inputStream = getContent("/invalid4.01Transitional.html");
    ValidationResult result = validator.validateContent(inputStream, null);
    System.out.println(result.getJSonOutput());
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
