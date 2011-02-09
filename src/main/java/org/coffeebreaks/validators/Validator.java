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

package org.coffeebreaks.validators;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * NOTE THAT THE API IS STILL BEING DESIGNED AND NO GUARANTEE THAT IT WILL REMAIN STABLE IN BETWEEN EARLY RELEASES (< 1.0).
 * <p>
 * Interface to a Validator.
 * <p>
 * Validator validate files given a {@link ValidationRequest request} and return {@link ValidationResult results}. The files can be addressed using a URI (most often an URL, but not always), or a content (String, InputStream) assumed to be in UTF-8, excepted otherwise noted.
 * Not all validators support all features, in which case they should throw {@link UnsupportedOperationException}.
 * Check the various implementations for details.
 * <p>
 * The Validator can depend on a remote service (e.g. like the W3c Validator service), so make sure to check the various service conditions.
 * <p>
 * For all methods, {@link ValidationRequest} cannot be null, otherwise a {@link NullPointerException} will be thrown.
 * Nor the returned {@link ValidationResult} will never be null.
 * @author jerome@coffeebreaks.org
 * @since 2/9/11 12:40 AM
 */
public interface Validator {
  ValidationResult validateContent(String content, ValidationRequest request) throws IOException;
  ValidationResult validateContent(InputStream inputStream, ValidationRequest request) throws IOException;
  ValidationResult validateUri(URI uri, ValidationRequest request) throws IOException;
  ValidationResult validateUri(String uri, ValidationRequest request) throws IOException;
}
