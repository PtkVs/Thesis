
       /*  original code
       //Controller with PDP and PRE response time logged
package unipassau.thesis.vehicledatadissemination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import unipassau.thesis.vehicledatadissemination.config.DatabaseConfiguration;
import unipassau.thesis.vehicledatadissemination.config.PolicyConfiguration;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.services.PDPService;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class BenchmarkController {

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count;

    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkController.class);

    // Static variables to track time and memory metrics
    private static final AtomicLong totalPdpResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPdpLatencyMillis = new AtomicLong(0); // Total PDP latency
    private static final AtomicLong totalPreResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPreLatencyMillis = new AtomicLong(0); // Total PRE latency
    private static final AtomicLong totalRequestProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0); // Total processing latency
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @Autowired
    private PDPService pdpService;

    @Autowired
    private PolicyConfiguration policyConfiguration;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    @RequestMapping(method = RequestMethod.POST, value = "/benchmark")
    public ResponseEntity<byte[]> authorize(InputStream dataStream, @RequestParam("count") int count) throws Exception {
        this.count = count;

        // Record total request start time
        long requestStartTime = System.nanoTime();

        // Read binary file from request
        byte[] onlyHash = dataStream.readAllBytes();

        // Separate hash and data
        Map<String, byte[]> stickyDocumentMap = DataHandler.readOnlyHash(onlyHash);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Principal principal = request.getUserPrincipal();

        // Convert hash to string and get the policy filename
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFilename = policyConfiguration.policyMap().get(hashValue);

        // Measure start-to-processing latency
        long processingStartTime = System.nanoTime();
        long startLatency = (processingStartTime - requestStartTime) / 1_000_000; // Convert to ms
        LOG.info("Start-to-processing latency: {} ms", startLatency);

        if (databaseConfiguration.authenticate(hashValue)) {
            MappingPolicyDB mapping = databaseConfiguration.getMappingByHashValue(hashValue);
            if (mapping != null) {
                String requestFilePath = mapping.getPolicyReqPath();
                String requestContent = new String(Files.readAllBytes(new File(requestFilePath).toPath()), StandardCharsets.UTF_8);

                // Measure Post-to-Processing latency
                long postProcessingStartTime = System.nanoTime();
                long postToProcessingLatency = (postProcessingStartTime - processingStartTime) / 1_000_000;
                LOG.info("Post-to-processing latency: {} ms", postToProcessingLatency);

                // Measure memory usage before PDP
                logMemoryUsage("Before PDP evaluation");

                // Measure Before-PDP latency
                long beforePdpStartTime = System.nanoTime();
                long beforePdpLatency = (beforePdpStartTime - postProcessingStartTime) / 1_000_000;
                LOG.info("Before-PDP latency: {} ms", beforePdpLatency);

                // PDP processing and response time
                long pdpStartTime = System.nanoTime();
                boolean pdpDecision = pdpService.updateRemotePDPServer(requestContent);
                long pdpEndTime = System.nanoTime();
                long pdpResponseTime = (pdpEndTime - pdpStartTime) / 1_000_000;
                long pdpLatency = (pdpStartTime - postProcessingStartTime) / 1_000_000;

                logMemoryUsage("After PDP evaluation");

                // Measure After-PDP latency
                long afterPdpLatency = (pdpStartTime - postProcessingStartTime) / 1_000_000;
                LOG.info("After-PDP latency: {} ms", afterPdpLatency);

                if (pdpDecision) {
                    // Measure Before-PRE latency
                    long beforePreStartTime = System.nanoTime();
                    long beforePreLatency = (beforePreStartTime - pdpEndTime) / 1_000_000;
                    LOG.info("Before-PRE latency: {} ms", beforePreLatency);

                    byte[] onlyData;
                    try (FileInputStream read = new FileInputStream(new File(dataFolder + count))) {
                        onlyData = read.readAllBytes();
                    }

                    // Read data to be re-encrypted
                    Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);

                    // Measure memory usage before PRE
                    logMemoryUsage("Before PRE re-encryption");

                    // PRE processing and response time
                    long preStartTime = System.nanoTime();
                    byte[] reEncryptedData = proxyReEncryptionService.reEncrypt(data.get("data"), principal);
                    long preEndTime = System.nanoTime();
                    long preResponseTime = (preEndTime - preStartTime) / 1_000_000;
                    long preLatency = (preStartTime - beforePreStartTime) / 1_000_000;

                    logMemoryUsage("After PRE re-encryption");

                    // Measure After-PRE latency
                    long afterPreLatency = (System.nanoTime() - preEndTime) / 1_000_000;
                    LOG.info("After-PRE latency: {} ms", afterPreLatency);

                    // Increment request count
                    long currentCount = requestCount.incrementAndGet();

                    // Total end-to-end request time
                    long requestEndTime = System.nanoTime();
                    long totalRequestTime = (requestEndTime - requestStartTime) / 1_000_000;
                     // Accumulate total request time

                    // Calculate Total Processing Latency
                    long finishToProcessingLatency = (requestEndTime - postProcessingStartTime) / 1_000_000;
                    long totalProcessingLatency = startLatency + finishToProcessingLatency; // Add Start-to-End latency


                    updateMetrics(totalProcessingLatency, totalRequestTime, preResponseTime, preLatency, pdpResponseTime, pdpLatency);

                    // Log cumulative metrics
                    logCumulativeMetrics(currentCount);

                    return ResponseEntity.ok(reEncryptedData);
                } else {
                    LOG.warn("Policy decision denied.");
                    return ResponseEntity.status(401).build();
                }
            } else {
                LOG.warn("Mapping not found for hash value.");
                return ResponseEntity.status(404).build();
            }
        }
        return ResponseEntity.status(401).build();
    }


    //Update all the cumulative metrics directly
    private void updateMetrics(long totalProcessingLatency, long totalRequestTime, long preResponseTime, long preLatency, long pdpResponseTime, long pdpLatency){
        totalPdpResponseTimeMillis.addAndGet(pdpResponseTime);
        totalPdpLatencyMillis.addAndGet(pdpLatency);
        totalProcessingLatencyMillis.addAndGet(totalProcessingLatency);
        totalRequestProcessingTimeMillis.addAndGet(totalRequestTime);
        totalPreResponseTimeMillis.addAndGet(preResponseTime);
        totalPreLatencyMillis.addAndGet(preLatency);
    }
    // Method to log cumulative metrics for benchmarking
    private void logCumulativeMetrics(long currentCount) {
        long totalPdpTime = totalPdpResponseTimeMillis.get();
        long totalPreTime = totalPreResponseTimeMillis.get();
        long totalProcessingTime = totalRequestProcessingTimeMillis.get();
        long totalProcessingLatency = totalProcessingLatencyMillis.get();
        long totalMemoryUsed = totalMemoryConsumed.get();
        long totalPdpLatency = totalPdpLatencyMillis.get();
        long totalPreLatency = totalPreLatencyMillis.get();

        LOG.info("Cumulative Metrics ({} requests):", currentCount);
        LOG.info("  - Total PDP response time: {} ms, Average: {} ms", totalPdpTime, totalPdpTime / currentCount);
        LOG.info("  - Total PDP latency: {} ms, Average: {} ms", totalPdpLatency, totalPdpLatency / currentCount);
        LOG.info("  - Total PRE response time: {} ms, Average: {} ms", totalPreTime, totalPreTime / currentCount);
        LOG.info("  - Total PRE latency: {} ms, Average: {} ms", totalPreLatency, totalPreLatency / currentCount);
        LOG.info("  - Total processing time: {} ms, Average: {} ms", totalProcessingTime, totalProcessingTime / currentCount);
        LOG.info("  - Total processing latency: {} ms, Average: {} ms", totalProcessingLatency, totalProcessingLatency / currentCount);
        LOG.info("  - Total memory consumed: {} MB, Average: {} MB", totalMemoryUsed / 1024 / 1024, totalMemoryUsed / 1024 / 1024 / currentCount);
    }

    // Method to log memory usage and update total memory consumed
    private void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);

        LOG.info("{} - Heap memory used: {} MB", phase, heapMemoryUsage.getUsed() / 1024 / 1024);
        LOG.info("{} - Non-heap memory used: {} MB", phase, nonHeapMemoryUsage.getUsed() / 1024 / 1024);
    }
}








        */




/*  yoooooooooooooooooooooooooooooooohooooooooooooooooooooooooooooo
package unipassau.thesis.vehicledatadissemination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import unipassau.thesis.vehicledatadissemination.config.DatabaseConfiguration;
import unipassau.thesis.vehicledatadissemination.config.PolicyConfiguration;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.services.PDPService;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class BenchmarkController {

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count;

    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkController.class);

    // Static variables to track time and memory metrics
    private static final AtomicLong totalPdpResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPdpLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalPreResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPreLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalRequestProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalExecutionTimeMillis = new AtomicLong(0); // Total Execution Time
    private static final AtomicLong totalExecutionTimeLatencyMillis = new AtomicLong(0); // Total Execution Latency
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @Autowired
    private PDPService pdpService;

    @Autowired
    private PolicyConfiguration policyConfiguration;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    @RequestMapping(method = RequestMethod.POST, value = "/benchmark")
    public ResponseEntity<byte[]> authorize(InputStream dataStream, @RequestParam("count") int count) throws Exception {
        this.count = count;

        // Record the total request start time
        long requestStartTime = System.nanoTime();

        // Read binary file from request
        byte[] onlyHash = dataStream.readAllBytes();

        // Separate hash and data
        Map<String, byte[]> stickyDocumentMap = DataHandler.readOnlyHash(onlyHash);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Principal principal = request.getUserPrincipal();

        // Convert hash to string and get the policy filename
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFilename = policyConfiguration.policyMap().get(hashValue);

        long processingStartTime = System.nanoTime();
        long startLatency = (processingStartTime - requestStartTime) / 1_000_000; // Convert to ms
        LOG.info("Start-to-processing latency: {} ms", startLatency);

        if (databaseConfiguration.authenticate(hashValue)) {
            MappingPolicyDB mapping = databaseConfiguration.getMappingByHashValue(hashValue);
            if (mapping != null) {
                String requestFilePath = mapping.getPolicyReqPath();
                String requestContent = new String(Files.readAllBytes(new File(requestFilePath).toPath()), StandardCharsets.UTF_8);

                long postProcessingStartTime = System.nanoTime();
                long postToProcessingLatency = (postProcessingStartTime - processingStartTime) / 1_000_000;
                LOG.info("Post-to-processing latency: {} ms", postToProcessingLatency);

                logMemoryUsage("Before PDP evaluation");

                // PDP processing
                long pdpStartTime = System.nanoTime();
                boolean pdpDecision = pdpService.updateRemotePDPServer(requestContent);
                long pdpEndTime = System.nanoTime();
                long pdpResponseTime = (pdpEndTime - pdpStartTime) / 1_000_000;
                long pdpLatency = (pdpStartTime - postProcessingStartTime) / 1_000_000;

                logMemoryUsage("After PDP evaluation");

                if (pdpDecision) {
                    logMemoryUsage("Before PRE re-encryption");

                    // PRE processing
                    long preStartTime = System.nanoTime();
                    byte[] onlyData;
                    try (FileInputStream read = new FileInputStream(new File(dataFolder + count))) {
                        onlyData = read.readAllBytes();
                    }

                    Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);
                    byte[] reEncryptedData = proxyReEncryptionService.reEncrypt(data.get("data"), principal);
                    long preEndTime = System.nanoTime();
                    long preResponseTime = (preEndTime - preStartTime) / 1_000_000;
                    long preLatency = (preStartTime - pdpEndTime) / 1_000_000;

                    logMemoryUsage("After PRE re-encryption");

                    long requestEndTime = System.nanoTime();
                    long totalRequestTime = (requestEndTime - requestStartTime) / 1_000_000;

                    // Update cumulative metrics
                    updateMetrics(
                            postToProcessingLatency, totalRequestTime,
                            preResponseTime, preLatency,
                            pdpResponseTime, pdpLatency
                    );

                    // Track total execution separately
                    totalExecutionTimeMillis.addAndGet(totalRequestTime); // Actual execution
                    totalExecutionTimeLatencyMillis.addAndGet(startLatency + totalRequestTime); // Includes delay

                    // Increment request count
                    long currentCount = requestCount.incrementAndGet();

                    if (currentCount % 10 == 0) { // Periodic cumulative logging
                        logCumulativeMetrics();
                        logExecutionMetrics();
                    }

                    return ResponseEntity.ok(reEncryptedData);
                } else {
                    LOG.warn("Policy decision denied.");
                    return ResponseEntity.status(401).build();
                }
            } else {
                LOG.warn("Mapping not found for hash value.");
                return ResponseEntity.status(404).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    private void updateMetrics(long totalProcessingLatency, long totalRequestTime, long preResponseTime, long preLatency, long pdpResponseTime, long pdpLatency) {
        totalProcessingLatencyMillis.addAndGet(totalProcessingLatency);
        totalRequestProcessingTimeMillis.addAndGet(totalRequestTime);
        totalPreResponseTimeMillis.addAndGet(preResponseTime);
        totalPreLatencyMillis.addAndGet(preLatency);
        totalPdpResponseTimeMillis.addAndGet(pdpResponseTime);
        totalPdpLatencyMillis.addAndGet(pdpLatency);
    }

    private void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapMemoryUsedMB = heapMemoryUsage.getUsed() / (1024 * 1024);
        long nonHeapMemoryUsedMB = nonHeapMemoryUsage.getUsed() / (1024 * 1024);

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);

        LOG.info("{} - Memory usage (Heap: {} MB, Non-Heap: {} MB)", phase, heapMemoryUsedMB, nonHeapMemoryUsedMB);
    }

    private void logCumulativeMetrics() {
        long currentCount = requestCount.get();
        LOG.info("Cumulative Metrics After {} Requests:", currentCount);
        LOG.info("  - Total PDP Response Time: {} ms (Avg: {} ms)", totalPdpResponseTimeMillis.get(), totalPdpResponseTimeMillis.get() / currentCount);
        LOG.info("  - Total PDP Latency: {} ms (Avg: {} ms)", totalPdpLatencyMillis.get(), totalPdpLatencyMillis.get() / currentCount);
        LOG.info("  - Total PRE Response Time: {} ms (Avg: {} ms)", totalPreResponseTimeMillis.get(), totalPreResponseTimeMillis.get() / currentCount);
        LOG.info("  - Total PRE Latency: {} ms (Avg: {} ms)", totalPreLatencyMillis.get(), totalPreLatencyMillis.get() / currentCount);
        LOG.info("  - Total Processing Latency: {} ms (Avg: {} ms)", totalProcessingLatencyMillis.get(), totalProcessingLatencyMillis.get() / currentCount);
        LOG.info("  - Total Processing Time: {} ms (Avg: {} ms)", totalRequestProcessingTimeMillis.get(), totalRequestProcessingTimeMillis.get() / currentCount);
    }

    private void logExecutionMetrics() {
        LOG.info("Execution Metrics:");
        LOG.info("  - Total Execution Time: {} ms", totalExecutionTimeMillis.get());
        LOG.info("  - Total Execution Latency: {} ms", totalExecutionTimeLatencyMillis.get());
    }
}

 */