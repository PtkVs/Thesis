package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AliceT {

    private static Logger LOG = LoggerFactory.getLogger(AliceT.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String privateKey = cryptoFolder + "alice-private-key"; // Add private key for decryption
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static int count = 99;

    public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            String dataToEncrypt = "omgshit"; // Use fixed string for encryption

            LOG.info("Encrypting Data ...");
            OpenPRE.INSTANCE.encrypt(pubKey, dataToEncrypt, dataFolder + count);
            LOG.info("Sticking hash of the policy to the data ...");

            DataHandler.writer(policyFolder + count + ".xml", dataFolder + count, count);

            if (++count >= Integer.parseInt(args[0])) {
                exec.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
