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

import org.coffeebreaks.validators.ValidationRequest;
import org.coffeebreaks.validators.ValidationResult;
import org.coffeebreaks.validators.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;

/**
 * This CSS validator doesn't depend on external resources. It is a bundling of the validator found
 * on <a href="http://jigsaw.w3.org/css-validator/">W3c's site</a>.
 *
 * This validator only support uri addressing so far. Direct content validation isn't yet supported.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/8/11 6:14 PM
 */
public class BundledCssValidator implements Validator {

  /**
   * Not supported.
   */
  public ValidationResult validateContent(String content, ValidationRequest request) throws IOException {
    throw new UnsupportedOperationException("validateContent not yet supported");
  }
  /**
   * Not supported.
   */
  public ValidationResult validateContent(InputStream inputStream, ValidationRequest request) throws IOException {
    throw new UnsupportedOperationException("validateContent not yet supported");
  }
  /**
   * equals to validateUri(uri.toString(), request);
   * @param uri
   * @param request
   * @return
   * @throws IOException
   */
  public ValidationResult validateUri(URI uri, ValidationRequest request) throws IOException {
    return validateUri(uri.toString(), request);
  }
  /**
   * @param uri file: or http[s]:// url are supported
   * @param request
         profile css1, css2, css21 (default), css3, svg, svgbasic, svgtiny, atsc-tv, mobile, tv
   * @return
   * @throws IOException
   */
  public ValidationResult validateUri(String uri, ValidationRequest request) throws IOException {
    String profile = request.getValue("profile", "css21");

    // ugly hack. Note that passing the wrong options will call System.out! I haven't patched it yet.
    /*
    OPTIONS
	-p, --printCSS
		Prints the validated CSS (only with text output, the CSS is printed with other outputs)
	-profile PROFILE, --profile=PROFILE
		Checks the Stylesheet against PROFILE
		Possible values for PROFILE are css1, css2, css21 (default), css3, svg, svgbasic, svgtiny, atsc-tv, mobile, tv
	-medium MEDIUM, --medium=MEDIUM
		Checks the Stylesheet using the medium MEDIUM
		Possible values for MEDIUM are all (default), aural, braille, embossed, handheld, print, projection, screen, tty, tv, presentation
	-output OUTPUT, --output=OUTPUT
		Prints the result in the selected format
		Possible values for OUTPUT are text (default), xhtml, html (same result as xhtml), soap12
	-lang LANG, --lang=LANG
		Prints the result in the specified language
		Possible values for LANG are de, en (default), es, fr, ja, ko, nl, zh-cn, pl, it
	-warning WARN, --warning=WARN
		Warnings verbosity level
		Possible values for WARN are -1 (no warning), 0, 1, 2 (default, all the warnings
URL
	URL can either represent a distant web resource (http://) or a local file (file:/)
      */
    String[] args = {};
    args[0] = "-output";
    args[1] = "soap12";
    args[2] = uri;
    args[3] = "-profile";
    args[4] = profile;
    PrintStream oldSysout = System.out;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    System.setOut(ps);
    try{
      org.w3c.css.css.CssValidator.main(args);
    } finally{
      System.setOut(oldSysout);
    }
    final String soap = baos.toString("UTF-8");
    final W3cUtils.W3cSoapValidatorSoapOutput soapObject = W3cUtils.parseSoapObject(soap);


    return new ValidationResult() {
      public boolean isResultIndeterminate() {
        return false; // FIXME
      }
      public int getErrorCount() {
        return soapObject.getErrorCount();
      }
      public int getWarningCount() {
        return soapObject.getWarningCount();
      }
      public String getResponseContent() {
        return soap;
      }
    };
  }


}

