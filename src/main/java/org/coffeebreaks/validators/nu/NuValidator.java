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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:13 PM
 */
public class NuValidator {
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
      int errorCount = 0;
      for(Message message : messages){
        if (type.equals(message.getType())) {
          errorCount++;
        }
      }
      return errorCount;
    }
    public boolean isResultIndeterminate() {
      return countMessageWithType("non-document-error") > 0;
    }
    /*public ParseTree getParseTree() {
      return parseTree;
    }*/
    static class Message {
      String type;
      String subtype;
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
      public String getSubtype() {
        return subtype;
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

  public ValidationResult validateContent(InputStream inputStream, String parser) throws IOException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(baseUrl + "?out=json&parser=" + parser);
    httpPost.addHeader("Content-Type", "text/html");
    InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, -1);
    httpPost.setEntity(inputStreamEntity);
    HttpResponse response = httpclient.execute(httpPost);
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
      public String getJSonOutput() {
        return json;
      }
    };
  }

  static NuValidatorJSonOutput parseJSonObject(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, NuValidatorJSonOutput.class);
  }
}
