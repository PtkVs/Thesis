package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BobDS {
    private static Logger LOG = LoggerFactory
            .getLogger(Bob.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";

    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";


    public static int count;

    public static byte[] data = null;
    public static String res = "";


    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    public static void main(String[] args) {
        //Getting user input for count
        Scanner scanner = new Scanner(System.in);

        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the count: ");
            count = scanner.nextInt();

           // ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            //exec.scheduleAtFixedRate(new Runnable() {
              //  @Override
                //public void run() {

                    byte[] stickyDocument = null;
                    try {
                        // FileInputStream read = new FileInputStream(new File(dataFolder + count ));
                        FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"));  //reading just hash
                        stickyDocument = read.readAllBytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

/*?count= Query Parameter
?count= is part of the query string that starts with a ?.
Query parameters are used to send additional data to the server. In this case, count is the name of the parameter, and its value is appended to it.
For example, if count is 5, then this part of the URL will be ?count=5.
Query parameters are added after the endpoint path and are separated by & if there are multiple parameters. Since there is only one parameter here, it starts with ?.*/
                    Request reEncryptionRequest = new Request.Builder()
                            .url(serverUrl + "authorize?count=" + count)
                            .post(RequestBody.create(stickyDocument))
                            .build();

                    try (Response response = httpClient.newCall(reEncryptionRequest).execute()) { //yeha bata jump to DataAccesscontroller
                        data = response.body().bytes();
                        Files.write(Path.of(tmpFolder + count), data);
                        res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                        LOG.info("Decrypted response is:" + res);

                        JSONObject jsonObject = new JSONObject(res);
                        System.out.print(jsonObject.toString(4) + " ");

                    } catch (IOException e) {
                        //Change -commented below line because we were getting an error
                        //throw new NullPointerException();
                        e.printStackTrace();
                    }

                   // if (++count >= Integer.parseInt(args[0])) {
                    //    exec.shutdown();
                        System.out.println();
                    }
                }
            }//, 0, 1, TimeUnit.SECONDS);



