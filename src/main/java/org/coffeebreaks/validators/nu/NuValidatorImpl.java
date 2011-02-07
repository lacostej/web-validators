package org.coffeebreaks.validators.nu;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:13 PM
 */
public class NuValidatorImpl implements NuValidator {
  private String baseUrl;

  public NuValidatorImpl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
  public ValidationResult validateContent(String content) {
    return new ValidationResult();
  }
}
