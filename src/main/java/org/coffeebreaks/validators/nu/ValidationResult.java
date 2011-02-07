package org.coffeebreaks.validators.nu;

/**
 * Created by IntelliJ IDEA.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:16 PM
 */
public interface ValidationResult {
  boolean isResultIndeterminate();
  int getErrorCount();
  String getJSonOutput();
}
