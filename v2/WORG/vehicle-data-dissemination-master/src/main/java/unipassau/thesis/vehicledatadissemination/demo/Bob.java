package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bob {
    private static Logger LOG = LoggerFactory
            .getLogger(Bob.class);

    public static String cryptoFolder = System.getProperty("user.dir")+"/crypto/";
    public static String dataFolder = System.getProperty("user.dir")+"/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir")+"/tmp/";

    public static String serverUrl = "http://localhost:8080/";


    public static int count=0;

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
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                byte[] stickyDocument = null;
                try {
                    FileInputStream read = new FileInputStream(new File(dataFolder + count));
                    stickyDocument = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Request reEncryptionRequest = new Request.Builder()
                        .url(serverUrl + "authorize")
                        .post(RequestBody.create(stickyDocument))
                        .build();

                try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                    data = response.body().bytes();
                    Files.write(Path.of(tmpFolder + count), data);
                    res = OpenPRE.INSTANCE.decrypt(privateKey,tmpFolder + count );
                    JSONObject jsonObject = new JSONObject(res);
                    System.out.print(jsonObject.toString(4) + " ");

                } catch (IOException e) {
                    throw new NullPointerException();
                }

                if (++count>= Integer.parseInt(args[0]) ) {
                    exec.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    }
}
