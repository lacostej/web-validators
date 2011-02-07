package org.coffeebreaks.validators.nu;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:13 PM
 */
public interface NuValidator {
  /**
   *
   * @param inputStream
   * @param parser optional
   * @return
   * @throws IOException
   * @see <a href="http://wiki.whatwg.org/wiki/Validator.nu_Common_Input_Parameters"></a>
   */
  ValidationResult validateContent(InputStream inputStream, String parser) throws IOException;
}
