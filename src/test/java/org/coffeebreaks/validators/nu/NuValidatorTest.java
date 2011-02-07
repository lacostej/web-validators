package org.coffeebreaks.validators.nu;

import org.apache.commons.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import static org.junit.Assert.assertEquals;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:10 PM
 */
public class NuValidatorTest {
  private NuValidator validator;
  @Before
  public void setUp() {
    validator = new NuValidatorImpl();
  }

  @After
  public void tearDown() {

  }

  @Test
  public void uploadValidHTML4_01TransitionalFile() throws IOException {
    String content = getContent("/valid4.01Transitional.html");
    ValidationResult result = validator.validateContent(content);
    assertEquals("no errors", 0, result.getErrorCount());
    assertEquals("no warnings", 0, result.getWarningCount());
  }

  private String getContent(String resource) throws IOException {
    InputStream resourceAsStream = NuValidatorTest.class.getResourceAsStream(resource);
    StringWriter stringWriter = new StringWriter();
    try{
      IOUtils.copy(resourceAsStream, stringWriter);
    } finally{
      IOUtils.closeQuietly(resourceAsStream);
      IOUtils.closeQuietly(stringWriter);
    }
    return stringWriter.toString();
  }
}
