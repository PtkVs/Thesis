package unipassau.thesis.vehicledatadissemination.controller;

import com.fasterxml.jackson.annotation.JsonRawValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class LocationController {
    OkHttpClient httpClient = new OkHttpClient();

    @Autowired
    org.springframework.core.env.Environment env;

    @RequestMapping(value="/vehicle/location", produces="application/json")
    @JsonRawValue
    public String getLocation() {
        Request locationRequest = new Request.Builder()
                .url(env.getProperty("gps.location"))
                .get()
                .build();

        try (Response response = httpClient.newCall(locationRequest).execute()) {
            JSONObject res = new JSONObject(response.body().string());

            return res.toString();

        } catch (IOException e) {
            throw new NullPointerException();
        }
    }
}

/*
1.Annotations:
-The class is annotated with @RestController, indicating that it combines @Controller and @ResponseBody. This means that each method
returns a domain object directly in the HTTP response body.

2.OkHttpClient and Autowired Field:
-The class includes an instance of OkHttpClient and an autowired field.
-OkHttpClient httpClient: An HTTP client for making HTTP requests using the OkHttp library.
-org.springframework.core.env.Environment env: Autowired for accessing application properties.

3.getLocation Method:
-This method is mapped to the endpoint /vehicle/location with the HTTP method GET.
-It produces a response in JSON format (produces="application/json").
-The @JsonRawValue annotation is used to indicate that the returned string should be treated as raw JSON, preventing it from being escaped.

4.HTTP Request to External Service:
-The method constructs an HTTP request using OkHttpClient to fetch the vehicle location information.
-It sends a GET request to the URL specified in the application properties (env.getProperty("gps.location")).
-The response is processed using the OkHttp library, and the body is read into a JSONObject.

5.Exception Handling:
-Any IOException that occurs during the HTTP request is caught, and a NullPointerException is thrown. This exception handling might
 need improvement based on the specific requirements of the application.

6.Return Value:
-The method returns the JSON response as a string.

**Summary**

This controller handles requests to the /vehicle/location endpoint using the HTTP GET method. It makes an external HTTP request to retrieve
vehicle location information from a specified URL, processes the response, and returns the JSON response as a raw string. The class uses
OkHttp for making HTTP requests and the Spring Environment for accessing application properties. This controller is responsible for
exposing an endpoint that provides information about the current location of a vehicle.
 */


