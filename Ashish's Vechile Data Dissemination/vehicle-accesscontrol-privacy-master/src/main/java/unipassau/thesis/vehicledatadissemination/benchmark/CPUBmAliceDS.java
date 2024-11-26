package unipassau.thesis.vehicledatadissemination.benchmark;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class CPUBmAliceDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmAliceDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-1000.csv";

    public static int count = 0;

    // Memory management bean to track memory usage
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    public static void main(String[] args) {

        if (args.length == 0) {
            LOG.error("No argument provided for the number of records to process. Exiting.");
            System.exit(1);
        }

        int recordsToProcess;
        try {
            recordsToProcess = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            LOG.error("Invalid number format for the argument. Exiting.", e);
            System.exit(1);
            return;
        }

        int numberOfRuns = 10; // Number of times to repeat the process
        long totalEncryptionLatency = 0; // To store total encryption latency for all runs
        long totalPolicyHashLatency = 0; // To store total policy hash latency for all runs

        LOG.info("Starting CPU and memory usage benchmark for Alice with " + numberOfRuns + " runs.");

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Run " + (run + 1) + " of " + numberOfRuns);

            long runStartTime = System.nanoTime(); // Start time for this run

            // Measure memory usage before processing
            logMemoryUsage("Before processing");

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    long recordStartTime = System.nanoTime(); // Start time for this record

                    // Create JSON object from the record (simulating data processing)
                    JSONObject res = new JSONObject();
                    res.put("header", record[0]);
                    res.put("timestamp", record[1]);
                    res.put("antennaAltitudeUnit", record[2]);
                    res.put("antennaAltitude", record[3]);
                    res.put("usedSatellites", record[4]);
                    res.put("quality", record[5]);
                    res.put("longitude", record[6]);
                    res.put("latitude", record[7]);

                    // Measure CPU and memory usage before encryption
                    logMemoryUsage("Before encryption");

                    // Measure encryption latency
                    LOG.info("Encrypting Data ...");
                    long encryptionStartTime = System.nanoTime();
                    long fileSizeInBytes = res.toString().getBytes().length; // Data size in bytes
                    OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);
                    long encryptionEndTime = System.nanoTime();

                    long encryptionLatency = (encryptionEndTime - encryptionStartTime) / 1_000_000; // Convert to milliseconds
                    totalEncryptionLatency += encryptionLatency;
                    LOG.info("Encryption latency for record " + count + ": " + encryptionLatency + " milliseconds");

                    // Calculate speed in kbps
                    long encryptionTimeInSeconds = encryptionLatency / 1000;
                    double dataSizeInKilobits = (fileSizeInBytes * 8) / 1000.0;
                    double speedInKbps = encryptionTimeInSeconds > 0 ? dataSizeInKilobits / encryptionTimeInSeconds : 0;
                    LOG.info("Data encryption speed for record " + count + ": " + speedInKbps + " kbps");

                    // Measure CPU and memory usage after encryption
                    logMemoryUsage("After encryption");

                    // Measure policy hash latency
                    LOG.info("Sticking hash of the policy to the data ...");
                    long policyHashStartTime = System.nanoTime();
                    DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);
                    long policyHashEndTime = System.nanoTime();

                    long policyHashLatency = (policyHashEndTime - policyHashStartTime) / 1_000_000; // Convert to milliseconds
                    totalPolicyHashLatency += policyHashLatency;
                    LOG.info("Policy hash latency for record " + count + ": " + policyHashLatency + " milliseconds");

                    // Measure CPU and memory usage after policy hashing
                    logMemoryUsage("After policy hashing");

                    count++;
                    processedCount++;

                    if (processedCount >= recordsToProcess) {
                        break;
                    }
                }

                LOG.info("Total records processed in this run: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during benchmark", e);
            }

            // Measure memory usage after processing
            logMemoryUsage("After processing");

            long runEndTime = System.nanoTime(); // End time for this run
            long runLatency = (runEndTime - runStartTime) / 1_000_000; // Convert to milliseconds

            LOG.info("Total latency for run " + (run + 1) + ": " + runLatency + " milliseconds");
        }

        // Calculate and log average latencies across all runs
        long averageEncryptionLatency = totalEncryptionLatency / (numberOfRuns * recordsToProcess);
        long averagePolicyHashLatency = totalPolicyHashLatency / (numberOfRuns * recordsToProcess);

        LOG.info("Total encryption latency across all runs: " + totalEncryptionLatency + " milliseconds");
        LOG.info("Average encryption latency per record: " + averageEncryptionLatency + " milliseconds");
        LOG.info("Total policy hash latency across all runs: " + totalPolicyHashLatency + " milliseconds");
        LOG.info("Average policy hash latency per record: " + averagePolicyHashLatency + " milliseconds");
        LOG.info("Total Number of Runs:" + numberOfRuns);
    }

    // Method to log memory usage
    private static void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        LOG.info(phase + " - Heap memory used: " + (heapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        LOG.info(phase + " - Non-heap memory used: " + (nonHeapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        LOG.info(phase + " - Total memory used: " + ((heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed()) / 1024 / 1024) + " MB");

    }
}
