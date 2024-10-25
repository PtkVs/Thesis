package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class CPUNoEncAlice {

    private static final Logger LOG = LoggerFactory.getLogger(CPUNoEncAlice.class);

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

        int numberOfRuns = 100;  // Number of times to repeat the process
        long totalLatency = 0;  // To store total latency for all runs

        LOG.info("Starting CPU and memory usage benchmark for Alice (processing without saving).");

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Run " + (run + 1) + " of " + numberOfRuns);

            long runStartTime = System.nanoTime();  // Start time for this run

            // Measure memory usage before processing
            logMemoryUsage("Before processing");

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    long recordStartTime = System.nanoTime();  // Start time for this record

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
                    res.put("temperature", record[8]);

                    LOG.info("Processed record: " + res.toString());

                    count++;
                    processedCount++;

                    long recordEndTime = System.nanoTime();  // End time for this record
                    long recordLatency = (recordEndTime - recordStartTime) / 1_000_000;  // Convert to milliseconds

                    LOG.info("Latency for record " + count + ": " + recordLatency + " milliseconds");
                    totalLatency += recordLatency;  // Accumulate total latency

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

            long runEndTime = System.nanoTime();  // End time for this run
            long runLatency = (runEndTime - runStartTime) / 1_000_000;  // Convert to milliseconds

            LOG.info("Total latency for run " + (run + 1) + ": " + runLatency + " milliseconds");
        }

        // Calculate and log the average latency per run
        long averageLatency = totalLatency / numberOfRuns;
        LOG.info("Total latency for all runs: " + totalLatency + " milliseconds");
        LOG.info("Average latency per run: " + averageLatency + " milliseconds");
    }

    // Method to log memory usage
    private static void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        LOG.info(phase + " - Heap memory used: " + (heapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        LOG.info(phase + " - Non-heap memory used: " + (nonHeapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        LOG.info(phase + " - Total memory used: " +
                ((heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed()) / 1024 / 1024) + " MB");
    }
}
