package unipassau.thesis.vehicledatadissemination.services;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
public class ProxyReEncryptionService {
    @Autowired
    org.springframework.core.env.Environment env;

    public byte[] reEncrypt(byte[] data, Principal principal){
        OkHttpClient httpClient = new OkHttpClient();
        Request locationRequest = new Request.Builder()
                .url(env.getProperty("pre.location") + "re-encrypt")
                .post(RequestBody.create(data))
                .addHeader("subjectID", principal.getName())
                .build();



        try (Response response = httpClient.newCall(locationRequest).execute()) {
            byte[] result = response.body().bytes();

            return result;

        } catch (IOException e) {
            throw new NullPointerException();
        }
    }

}

/*
1.Constructor and Autowired Fields:
-The class is annotated with @Service, indicating that it's a Spring service component.
-It has a field annotated with @Autowired, which injects the Spring environment for property access.

2.reEncrypt Method:
-This method takes two parameters: data (the data to be re-encrypted) and principal (user information).
-It uses the OkHttp library to make an HTTP POST request to a specified URL (pre.location + "re-encrypt").
-The data is sent as the request body.
-The subjectID header is set with the name of the principal, indicating the subject of the re-encryption.
-The method then reads the response body as bytes and returns the result.

3.Exception Handling:
-If an IOException occurs during the HTTP request, the method throws a NullPointerException.

**Summary**
The ProxyReEncryptionService class encapsulates the functionality of proxy re-encrypting data. It communicates with an external
service (presumably related to proxy re-encryption) using HTTP POST requests. The re-encryption is performed based on the provided
data and the principal information of the user initiating the re-encryption. The service is designed to be used in a larger system
where proxy re-encryption is part of the overall data processing flow.

 */
