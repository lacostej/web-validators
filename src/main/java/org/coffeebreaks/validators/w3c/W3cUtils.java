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

/**
 * Created by IntelliJ IDEA.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/8/11 7:03 PM
 */
class W3cUtils {
  static class W3cSoapValidatorSoapOutput {
    private boolean resultIndeterminate;
    private int errorCount;
    private int warningCount;

    public boolean isResultIndeterminate() {
      return resultIndeterminate;
    }
    public int getErrorCount() {
      return errorCount;
    }
    public int getWarningCount() {
      return warningCount;
    }
  }

  static W3cSoapValidatorSoapOutput parseSoapObject(String soap) {
    // ugly hack for now, we don't bother with XML - where's the WSDL anyway ? Works for both CSS and Markup soap results...
    int errorCount = findNodeValueInXml(soap, "errorcount");
    int warningCount = findNodeValueInXml(soap, "warningcount");

    W3cSoapValidatorSoapOutput w3cSoapValidatorSoapOutput = new W3cSoapValidatorSoapOutput();
    w3cSoapValidatorSoapOutput.errorCount = errorCount;
    w3cSoapValidatorSoapOutput.warningCount = warningCount;
    w3cSoapValidatorSoapOutput.resultIndeterminate = false;
    return w3cSoapValidatorSoapOutput;
  }

  private static int findNodeValueInXml(String xml, final String nodeName) {
    String str = "<m:"+ nodeName + ">";
    String str1 = "</m:" + nodeName + ">";
    int start = xml.indexOf(str);
    if (start >= 0) {
      int stop = xml.indexOf(str1);
      if (stop >= 0 && stop > start + str.length())
        return Integer.parseInt(xml.substring(start + str.length(), stop));
    }
    return -1;
  }

}
