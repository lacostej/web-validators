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

package org.coffeebreaks.validators.nu;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.coffeebreaks.validators.ValidationRequest;
import org.coffeebreaks.validators.ValidationResult;
import org.coffeebreaks.validators.Validator;
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
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:13 PM
 */
public class NuValidator implements Validator {
  private String baseUrl;

  public NuValidator(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * http://wiki.whatwg.org/wiki/Validator.nu_JSON_Output
   */
  static class NuValidatorJSonOutput {
    List<Message> messages;
    String url;
    Source source;
    //ParseTree parseTree;

    public List<Message> getMessages() {
      return messages;
    }
    public String getUrl() {
      return url;
    }
    public Source getSource() {
      return source;
    }
    public int getErrorCount() {
      return countMessageWithType("error");
    }
    private int countMessageWithType(String type) {
      return countMessageWithTypeAndSubtype(type, null);
    }
    private int countMessageWithTypeAndSubtype(String type, String subtype) {
      int errorCount = 0;
      for(Message message : messages){
        if (type.equals(message.getType())) {
          if (subtype == null || subtype.equals(message.getSubType()) ) {
            errorCount++;
          }
        }
      }
      return errorCount;
    }
    public boolean isResultIndeterminate() {
      return countMessageWithType("non-document-error") > 0;
    }
    public int getWarningCount() {
      return countMessageWithTypeAndSubtype("info", "warning");
    }
    /*public ParseTree getParseTree() {
      return parseTree;
    }*/
    static class Message {
      String type;
      String subType;
      String message;
      String extract;
      String offset;
      String url;
      String firstLine;
      String firstColumn;
      String lastLine;
      String lastColumn;

      public String getType() {
        return type;
      }
      public String getSubType() {
        return subType;
      }
      public String getMessage() {
        return message;
      }
      public String getExtract() {
        return extract;
      }
      public String getOffset() {
        return offset;
      }
      public String getUrl() {
        return url;
      }
      public String getFirstLine() {
        return firstLine;
      }
      public String getFirstColumn() {
        return firstColumn;
      }
      public String getLastLine() {
        return lastLine;
      }
      public String getLastColumn() {
        return lastColumn;
      }
    }
    static class Source {
      String code;
      String type;
      String encoding;

      public String getCode() {
        return code;
      }
      public String getType() {
        return type;
      }
      public String getEncoding() {
        return encoding;
      }
    }
    /*
    static class ParseTree {

    }*/
  }

  public ValidationResult validateContent(String content, ValidationRequest request) throws IOException {
    return validateContent(StringUtil.stringToInputStream(content, "UTF-8"), request);
  }

  public ValidationResult validateUri(String uri, ValidationRequest request) throws IOException {
    return validateUri(new URL(uri), request);
  }

  public ValidationResult validateUri(URI uri, ValidationRequest request) throws IOException {
    return validateUri(uri.toURL(), request);
  }

  ValidationResult validateUri(URL url, ValidationRequest request) throws IOException {
    String parser = request.getValue("parser", null);
    HttpRequestBase method;
    List<NameValuePair> qParams = new ArrayList<NameValuePair>();
    qParams.add(new BasicNameValuePair("out", "json"));
    if (parser != null) {
      qParams.add(new BasicNameValuePair("parser", parser));
    }
    qParams.add(new BasicNameValuePair("doc", url.toString()));

    try{
      URI uri = new URI(baseUrl);
      URI uri2 = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(qParams, "UTF-8"), null);
      method = new HttpGet(uri2);
      return validate(method);
    } catch(URISyntaxException e){
      throw new IllegalArgumentException("invalid uri. Check your baseUrl " + baseUrl, e);
    }
  }

  public ValidationResult validateContent(InputStream inputStream, ValidationRequest request) throws IOException {
    String parser = request.getValue("parser", null);
    HttpRequestBase method;
    HttpPost httpPost = new HttpPost(baseUrl + "?out=json&parser=" + parser);
    httpPost.addHeader("Content-Type", "text/html");
    InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, -1);
    httpPost.setEntity(inputStreamEntity);
    method = httpPost;
    return validate(method);
  }

  private ValidationResult validate(HttpRequestBase method) throws IOException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpResponse response = httpclient.execute(method);
    HttpEntity entity = response.getEntity();
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.SC_OK) {
      throw new IllegalStateException("Unexpected HTTP status code: " + statusCode + ". Implementation error ?");
    }
    if (entity == null) {
      throw new IllegalStateException("No entity but HTTP status code: " + statusCode + ". Server side error ?");
    }
    InputStream entityContentInputStream = entity.getContent();
    StringWriter output = new StringWriter();
    IOUtils.copy(entityContentInputStream, output, "UTF-8");
    final String json = output.toString();
    final NuValidatorJSonOutput jsonObject = parseJSonObject(json);

    return new ValidationResult() {
      public boolean isResultIndeterminate() {
        return jsonObject.isResultIndeterminate();
      }
      public int getErrorCount() {
        return jsonObject.getErrorCount();
      }
      public int getWarningCount() {
        return jsonObject.getWarningCount();
      }
      public String getResponseContent() {
        return json;
      }
    };
  }

  static NuValidatorJSonOutput parseJSonObject(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, NuValidatorJSonOutput.class);
  }
}
