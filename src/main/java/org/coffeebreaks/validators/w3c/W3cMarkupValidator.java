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

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.coffeebreaks.validators.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Support the W3c Markup service.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/8/11 9:57 AM
 * @see <a href="http://validator.w3.org/docs/users.html">
 */
public class W3cMarkupValidator {
  private String baseUrl;

  public W3cMarkupValidator(String baseUrl) {
    this.baseUrl = baseUrl;
  }

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

  public ValidationResult validateContent(InputStream inputStream) throws IOException {
    return validateW3cMarkup("fragment", StringUtil.readIntoString(inputStream, "UTF-8"), false);
  }

  private ValidationResult validateW3cMarkup(String type, String value, boolean get) throws IOException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpRequestBase method;
    if (get) {
      List<NameValuePair> qParams = new ArrayList<NameValuePair>();
      qParams.add(new BasicNameValuePair("output", "soap12"));
      qParams.add(new BasicNameValuePair(type, value));

      try{
        URI uri = new URI(baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "check");
        URI uri2 = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(qParams, "UTF-8"), null);
        method = new HttpGet(uri2);
      } catch(URISyntaxException e){
        throw new IllegalArgumentException("invalid uri. Check your baseUrl " + baseUrl, e);
      }
    } else {
      HttpPost httpPost = new HttpPost(baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "check");
      List<NameValuePair> formParams = new ArrayList<NameValuePair>();
      formParams.add(new BasicNameValuePair("output", "soap12"));
      formParams.add(new BasicNameValuePair(type, value));
      UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
      httpPost.setEntity(requestEntity);
      method = httpPost;
    }
    HttpResponse response = httpclient.execute(method);
    /*
    for(Header header : response.getAllHeaders()){
      System.out.println(header.toString());
    }
    */
    HttpEntity responseEntity = response.getEntity();
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
      throw new IllegalStateException("Unexpected HTTP status code: " + statusCode + ". Implementation error ?");
    }
    if (responseEntity == null) {
      throw new IllegalStateException("No entity but HTTP status code: " + statusCode + ". Server side error ?");
    }
    InputStream entityContentInputStream = responseEntity.getContent();
    StringWriter output = new StringWriter();
    IOUtils.copy(entityContentInputStream, output, "UTF-8");
    final String soap = output.toString();
    final W3cSoapValidatorSoapOutput soapObject = parseSoapObject(soap);

    return new ValidationResult() {
      public boolean isResultIndeterminate() {
        return soapObject.isResultIndeterminate();
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

  public ValidationResult validateUri(URL url) throws IOException, URISyntaxException {
    return validateW3cMarkup("uri", url.toString(), true);
  }

  static W3cSoapValidatorSoapOutput parseSoapObject(String soap) {
    // ugly hack for now, we don't bother with XML
    int errorCount = findErrorCount(soap, "errorcount");
    int warningCount = findErrorCount(soap, "warningcount");

    W3cSoapValidatorSoapOutput w3cSoapValidatorSoapOutput = new W3cSoapValidatorSoapOutput();
    w3cSoapValidatorSoapOutput.errorCount = errorCount;
    w3cSoapValidatorSoapOutput.warningCount = warningCount;
    w3cSoapValidatorSoapOutput.resultIndeterminate = false;
    return w3cSoapValidatorSoapOutput;
  }
  private static int findErrorCount(String soap, final String errorcount) {
    String str = "<m:"+ errorcount + ">";
    String str1 = "</m:" + errorcount + ">";
    int start = soap.indexOf(str);
    if (start >= 0) {
      int stop = soap.indexOf(str1);
      if (stop >= 0 && stop > start + str.length())
        return Integer.parseInt(soap.substring(start + str.length(), stop));
    }
    return -1;
  }
}
