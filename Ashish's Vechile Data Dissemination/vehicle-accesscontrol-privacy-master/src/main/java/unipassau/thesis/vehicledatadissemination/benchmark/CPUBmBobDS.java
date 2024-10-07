package unipassau.thesis.vehicledatadissemination.benchmark;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CPUBmBobDS {
    private static Logger LOG = LoggerFactory.getLogger(CPUBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";

    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    public static byte[] data = null;
    public static String res = "";

    // Memory management bean to track memory usage
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    // Variables to accumulate total memory usage
    private static long totalHeapMemoryUsed = 0;
    private static long totalNonHeapMemoryUsed = 0;

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
        // Build client with authentication information
        return new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
    }

    public static void main(String[] args) {
        // Getting user input for the range
        Scanner scanner = new Scanner(System.in);
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        long totalLatency = 0;  // Variable to accumulate total latency

        while (true) {
            System.out.print("Please Enter the Start of the Encrypted Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Encrypted Count Range: ");
            int endCount = scanner.nextInt();

            // Check if the user wants to exit (0 0 input)
            if (startCount == 0 && endCount == 0) {
                System.out.println("Exiting program...");
                break;
            }

            // Loop through the count range
            for (int count = startCount; count <= endCount; count++) {
                byte[] stickyDocument = null;
                try {
                    // Reading just the hash
                    FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"));
                    stickyDocument = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                // Measure CPU and memory usage before re-encryption
                logMemoryUsage("Before re-encryption");

                // Measure latency for re-encryption
                long reEncryptionStartTime = System.nanoTime();  // Start time for re-encryption request
                Request reEncryptionRequest = new Request.Builder()
                        .url(serverUrl + "authorize?count=" + count)
                        .post(RequestBody.create(stickyDocument))
                        .build();

                try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                    long reEncryptionEndTime = System.nanoTime();  // End time for re-encryption request
                    long reEncryptionLatency = (reEncryptionEndTime - reEncryptionStartTime) / 1_000_000;  // Convert to milliseconds
                    LOG.info("Re-encryption latency for count " + count + ": " + reEncryptionLatency + " milliseconds");

                    data = response.body().bytes();
                    Files.write(Path.of(tmpFolder + count), data);

                    // Measure CPU and memory usage after re-encryption
                    logMemoryUsage("After re-encryption");

                    // Measure latency for decryption
                    long decryptionStartTime = System.nanoTime();  // Start time before decryption
                    res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                    long decryptionEndTime = System.nanoTime();  // End time after decryption
                    long decryptionLatency = (decryptionEndTime - decryptionStartTime) / 1_000_000;  // Convert to milliseconds
                    LOG.info("Decryption latency for count " + count + ": " + decryptionLatency + " milliseconds");

                    // Add both latencies to total latency
                    long totalCountLatency = reEncryptionLatency + decryptionLatency;
                    totalLatency += totalCountLatency;

                    LOG.info("Decrypted response for count " + count + " is: " + res);

                    JSONObject jsonObject = new JSONObject(res);
                    System.out.println("Decrypted JSON response for count " + count + ": ");
                    System.out.println(jsonObject.toString(4));  // Pretty-print JSON

                    // Measure CPU and memory usage after decryption
                    logMemoryUsage("After decryption");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println();  // Add spacing between outputs for different counts
            }

            // After each loop, log the total latency and total memory used for all counts processed so far
            LOG.info("Total latency for all decrypted counts so far: " + totalLatency + " milliseconds");

            // Log total memory usage
            LOG.info("Total heap memory used for all decrypted counts so far: " + (totalHeapMemoryUsed / 1024 / 1024) + " MB");
            LOG.info("Total non-heap memory used for all decrypted counts so far: " + (totalNonHeapMemoryUsed / 1024 / 1024) + " MB");
            LOG.info("Total memory used for all decrypted counts so far: " + ((totalHeapMemoryUsed + totalNonHeapMemoryUsed) / 1024 / 1024) + " MB");
        }
    }

    // Method to log memory usage and accumulate total memory used
    private static void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapUsed = heapMemoryUsage.getUsed();
        long nonHeapUsed = nonHeapMemoryUsage.getUsed();

        // Accumulate total memory used
        totalHeapMemoryUsed += heapUsed;
        totalNonHeapMemoryUsed += nonHeapUsed;

        LOG.info(phase + " - Heap memory used: " + (heapUsed / 1024 / 1024) + " MB");
        LOG.info(phase + " - Non-heap memory used: " + (nonHeapUsed / 1024 / 1024) + " MB");
        LOG.info(phase + " - Total memory used: " + ((heapUsed + nonHeapUsed) / 1024 / 1024) + " MB");
    }
}
