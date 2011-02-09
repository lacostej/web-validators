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

package org.coffeebreaks.validators.jsonlint;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/7/11 8:13 PM
 */
public class JsonlintValidator implements Validator {
  private String baseUrl = "http://www.jsonlint.com/ajax/validate";

  public JsonlintValidator() {
  }

  /**
   * {"result":"Valid JSON","responseCode":0,"prettyJSON":"[\n    {\n        \"kind\": \"Listing\",\n        \"data\": {\n            \"modhash\": \"\",\n            \"children\": [\n                {\n                    \"kind\": \"t3\",\n                    \"data\": {\n                        \"domain\": \"jsonlint.com\",\n                        \"media_embed\": {\n                            \n                        },\n                        \"levenshtein\": null,\n                        \"subreddit\": \"programming\",\n                        \"selftext_html\": null,\n                        \"selftext\": \"\",\n                        \"likes\": null,\n                        \"saved\": false,\n                        \"id\": \"9szpc\",\n                        \"clicked\": false,\n                        \"author\": \"umbrae\",\n                        \"media\": null,\n                        \"score\": 0,\n                        \"over_18\": false,\n                        \"hidden\": false,\n                        \"thumbnail\": \"\",\n                        \"subreddit_id\": \"t5_2fwo\",\n                        \"downs\": 4,\n                        \"is_self\": false,\n                        \"permalink\": \"\/r\/programming\/comments\/9szpc\/jsonlint_a_handy_json_validator_and_reformatter\/\",\n                        \"name\": \"t3_9szpc\",\n                        \"created\": 1255281251.0,\n                        \"url\": \"http:\/\/www.jsonlint.com\",\n                        \"title\": \"JSONLint - A Handy JSON Validator and Reformatter\",\n                        \"created_utc\": 1255281251.0,\n                        \"num_comments\": 0,\n                        \"ups\": 3\n                    }\n                }\n            ],\n            \"after\": null,\n            \"before\": null\n        }\n    },\n    {\n        \"kind\": \"Listing\",\n        \"data\": {\n            \"modhash\": \"\",\n            \"children\": [\n                \n            ],\n            \"after\": null,\n            \"before\": null\n        }\n    }\n]"}
   */
  static class JsonlintValidatorJSonOutput {
    String result;
    int responseCode;
    String prettyJSON;

    public int getErrorCount() {
      return isValid() ? 0: 1;
    }
    public boolean isResultIndeterminate() {
      return false;
    }
    public int getWarningCount() {
      return 0;
    }
    public boolean isValid() {
      return responseCode == 0;
    }
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
    return validate(url.toString(), request);
  }

  public ValidationResult validateContent(InputStream inputStream, ValidationRequest request) throws IOException {
    return validate(StringUtil.readIntoString(inputStream, "UTF-8"), request);
  }

  private ValidationResult validate(String contentOrUrl, ValidationRequest request) throws IOException {
    HttpPost httpPost = new HttpPost(baseUrl);
    List<NameValuePair> formParams = new ArrayList<NameValuePair>();
    formParams.add(new BasicNameValuePair("reformat", "no")); // yes or compress
    formParams.add(new BasicNameValuePair("json", contentOrUrl));
    UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
    httpPost.setEntity(requestEntity);

    HttpClient httpclient = new DefaultHttpClient();
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
    final JsonlintValidatorJSonOutput jsonObject = parseJSonObject(json);

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

  static JsonlintValidatorJSonOutput parseJSonObject(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, JsonlintValidatorJSonOutput.class);
  }
}
