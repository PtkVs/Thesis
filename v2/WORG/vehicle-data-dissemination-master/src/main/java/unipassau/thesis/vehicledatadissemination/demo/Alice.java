package unipassau.thesis.vehicledatadissemination.demo;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Alice {

    private static Logger LOG = LoggerFactory
            .getLogger(Alice.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";
    public static String gpsUrl = "http://localhost:8081/";

    public static int count = 0;
    public static JSONObject res = new JSONObject();
    public static String randomPlaintext;


    public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                Request locationRequest = new Request.Builder()
                        .url(gpsUrl)
                        .get()
                        .build();

                try (Response response = httpClient.newCall(locationRequest).execute()) {
                    res = new JSONObject(response.body().string());

                } catch (IOException e) {
                    throw new NullPointerException();
                }

                System.out.print(res.toString(4) + " ");
                // Remove comment if there is no GPS data
                //randomPlaintext= RandomStringUtils.randomAlphanumeric(8192);
                LOG.info("Encrypting Data ...");
                OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);

                LOG.info("Sticking hash of the policy to the data ...");
                DataHandler.writer(policyFolder + args[0], dataFolder + count);

                if (++count >= Integer.parseInt(args[0])) {
                    exec.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    }
}





/* Speed with random data
package unipassau.thesis.vehicledatadissemination.demo;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Alice {

    private static Logger LOG = LoggerFactory.getLogger(Alice.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static void main(String[] args) {
        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger count = new AtomicInteger(0);

        exec.scheduleAtFixedRate(() -> {
            // Dummy speed data for testing
            double speed = 100.5;

            // Include speed data in encryption
            JSONObject dataToEncrypt = new JSONObject();
            dataToEncrypt.put("speed", speed);

            LOG.info("Encrypting Data ...");

            // Modify encryption to include speed data
            OpenPRE.INSTANCE.encrypt(pubKey, dataToEncrypt.toString(), dataFolder + count.get());

            LOG.info("Sticking hash of the policy to the data ...");

            // Assuming the policy hash is relevant to the speed data
            DataHandler.writer(policyFolder + args[1], dataFolder + count.get());

            if (count.incrementAndGet() >= Integer.parseInt(args[0])) {
                exec.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}

*/

/* Speed with GET request from postman
package unipassau.thesis.vehicledatadissemination.demo;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Alice {

    private static Logger LOG = LoggerFactory
            .getLogger(Alice.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static String speedUrl = "http://localhost:8082/speed";

    public static int count = 0;
    public static JSONObject res = new JSONObject();
    public static String randomPlaintext;

    public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                // Retrieve speed data from the body of a GET request
                Request speedRequest = new Request.Builder()
                        .url(speedUrl)
                        .get()
                        .build();

                double speed = 0.0; // Default speed if not available
                try (Response response = httpClient.newCall(speedRequest).execute()) {
                    // Parse speed data from the JSON response body
                    String responseBody = response.body().string();
                    JSONObject speedJson = new JSONObject(responseBody);
                    // Assuming the speed data is stored under the key "speed"
                    if (speedJson.has("speed")) {
                        speed = speedJson.getDouble("speed");
                    }

                } catch (IOException | org.json.JSONException e) {
                    // Handle the case where speed data is not available or not in the expected format
                    e.printStackTrace();
                }

                // Include speed data in encryption
                JSONObject dataToEncrypt = new JSONObject();
                dataToEncrypt.put("speed", speed);

                LOG.info("Encrypting Data ...");

                // Modify encryption to include speed data
                OpenPRE.INSTANCE.encrypt(pubKey, dataToEncrypt.toString(), dataFolder + count);

                LOG.info("Sticking hash of the policy to the data ...");

                // Assuming the policy hash is relevant to the speed data
                DataHandler.writer(policyFolder + args[1], dataFolder + count);

                if (++count >= Integer.parseInt(args[0])) {
                    exec.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

 */



    /* Location is disabled for now.
    public static String gpsUrl = "http://localhost:8081/";

    public static int count=0;
    public static JSONObject res = new JSONObject();
    public static String randomPlaintext;


    public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                Request locationRequest = new Request.Builder()
                        .url(gpsUrl)
                        .get()
                        .build();

                try (Response response = httpClient.newCall(locationRequest).execute()) {
                    res = new JSONObject(response.body().string());

                } catch (IOException e) {
                    throw new NullPointerException();
                }

                System.out.print(res.toString(4) + " ");
                // Remove comment if there is no GPS data
                //randomPlaintext= RandomStringUtils.randomAlphanumeric(8192);
                LOG.info("Encrypting Data ...");
                OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);

                LOG.info("Sticking hash of the policy to the data ...");
                DataHandler.writer(policyFolder + args[1], dataFolder + count);

                if (++count>= Integer.parseInt(args[0]) ) {
                    exec.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    } */


/*
1.Dependencies:
-The class relies on external libraries, including OkHttp, Apache Commons Lang, and custom utility classes (DataHandler, OpenPRE, Encoder).

2.Static Fields:
-Various static fields define file paths (cryptoFolder, dataFolder, pubKey, policyFolder) and the GPS data source URL (gpsUrl).

3.Logger:
-The class uses SLF4J for logging, with a static logger (LOG).

4.Scheduled Execution:
-The class uses a ScheduledExecutorService to periodically execute a task defined by the Runnable interface.

5.Data Retrieval:
-The Runnable task sends a GET request to the specified GPS data source URL (gpsUrl) using OkHttp.
-The received JSON response is stored in the res field.

6.Encryption:
-The data from the GPS response is encrypted using the OpenPRE.INSTANCE.encrypt method and stored in the dataFolder with an incremental
 filename.

7.Policy Hashing:
-The hash of the policy specified in the command-line arguments (args[1]) is appended to the encrypted data using DataHandler.writer.

8.Termination:
-The process repeats until the specified number of iterations (Integer.parseInt(args[0])) is reached, at which point the
 ScheduledExecutorService is shut down.

**Summary**
The Alice class simulates a data source that periodically retrieves GPS data, encrypts it, appends the hash of a specified policy,
and saves the result to a data folder. The process is scheduled to repeat at fixed intervals. The class appears to be part of a larger
system for experimenting with or demonstrating vehicle data dissemination with encryption and policy enforcement.
*/
