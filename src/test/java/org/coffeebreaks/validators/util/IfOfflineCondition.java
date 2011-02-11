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

package org.coffeebreaks.validators.util;

import org.junit.runners.model.FrameworkMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * A JUnit RuntimeCondition to exclude tests that depend on network.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/11/11 8:15 AM
 */
public class IfOfflineCondition implements RuntimeCondition {
  // test your DNS, router and the inter-net :)
  private static final boolean IS_OFFLINE = ! isSiteReachable("http://www.google.com");

  public boolean isTrue(FrameworkMethod ignored) {
    return IS_OFFLINE;
  }

  /**
   * Checks for connection to the internet through request to the specified
   * @param sourceUrl the source to look for
   * @return true if the site reachable
   */
  public static boolean isSiteReachable(String sourceUrl) {
    try {
      URL url = new URL(sourceUrl);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      // getContent() fails when there's no connection
      urlConnection.getContent();
    } catch(Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
  }
}