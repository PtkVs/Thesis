// With TLC logged
package cyber.tf.authzforcepdpservice.controller;

import cyber.tf.authzforcepdpservice.service.PDPService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PDPController {

    private final Logger logger = LoggerFactory.getLogger(PDPController.class);

    @Autowired
    private PDPService pdpService;

    // Static variables to track metrics
    private static final AtomicLong totalPDPEvaluationTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPDPEvaluationLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalExecutionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalExecutionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    @RequestMapping(value = "/application/json",
            method = RequestMethod.POST,
            consumes = {"application/json", "application/xacml+json"},
            produces = {"application/json", "application/xacml+json"}
    )
    public String evaluateJSON(@RequestBody String request) throws IOException {
        JSONObject json = new JSONObject(new JSONTokener(request));
        logger.info("Evaluating XACML JSON request.");

        long requestReceivedTime = System.nanoTime(); // Time when request is received
        logMemoryUsage("Before PDP evaluation (JSON)");

        // PDP evaluation
        long evaluationStartTime = System.nanoTime();
        JSONObject res = pdpService.getJSONAdapter().evaluate(json);
        long evaluationEndTime = System.nanoTime();

        // Measure time spent inside PDP
        long pdpEvaluationTime = (evaluationEndTime - evaluationStartTime) / 1_000_000;
        totalPDPEvaluationTimeMillis.addAndGet(pdpEvaluationTime);

        // Calculate PDP evaluation latency
        long pdpEvaluationLatency = pdpEvaluationTime; // latency for PDP evaluation
        totalPDPEvaluationLatencyMillis.addAndGet(pdpEvaluationLatency);
        logger.info("PDP evaluation time (JSON): {} ms", pdpEvaluationTime);
        logger.info("PDP evaluation latency (JSON): {} ms", pdpEvaluationLatency);

        // Measure processing time latency
        long processingTime = (evaluationEndTime - requestReceivedTime) / 1_000_000;
        long processingTimeLatency = processingTime - pdpEvaluationTime;
        totalProcessingTimeLatencyMillis.addAndGet(processingTimeLatency);

        logger.info("Processing time for this JSON request: {} ms", processingTime);
        logger.info("Processing time latency (excluding PDP evaluation): {} ms", processingTimeLatency);

        logMemoryUsage("After PDP evaluation (JSON)");

        // Post-evaluation processing
        String decision = extractDecision(res);
        long postEvaluationEndTime = System.nanoTime();

        // Calculate total execution time
        long totalExecutionTime = (postEvaluationEndTime - requestReceivedTime) / 1_000_000; // Overall time
        totalExecutionTimeMillis.addAndGet(totalExecutionTime);

        // Calculate total execution latency
        long executionLatency = totalExecutionTime - processingTime;
        totalExecutionLatencyMillis.addAndGet(executionLatency);

        logger.info("Total execution time for this JSON request: {} ms", totalExecutionTime);
        logger.info("Total execution latency (including overhead): {} ms", executionLatency);
        logger.info("Decision: {}", decision);

        // Update all metrics
        updateMetrics(totalExecutionTime, pdpEvaluationLatency, processingTimeLatency, executionLatency);

        logCumulativeMetrics();

        return decision;
    }

    private void updateMetrics(long totalExecutionTime, long pdpEvaluationLatency, long processingTimeLatency, long executionLatency) {
        // Update all the cumulative metrics directly
        totalProcessingTimeMillis.addAndGet(totalExecutionTime);
        totalPDPEvaluationLatencyMillis.addAndGet(pdpEvaluationLatency);
        totalProcessingTimeLatencyMillis.addAndGet(processingTimeLatency);
        totalExecutionTimeMillis.addAndGet(totalExecutionTime);
        totalExecutionLatencyMillis.addAndGet(executionLatency);
        requestCount.incrementAndGet();
    }

    private void logCumulativeMetrics() {
        long currentRequestCount = requestCount.get();

        // Get the cumulative totals
        long totalProcessingTime = totalProcessingTimeMillis.get();
        long totalProcessingLatency = totalProcessingTimeLatencyMillis.get();

        long totalExecutionTime = totalExecutionTimeMillis.get();
        long totalExecutionLatency = totalExecutionLatencyMillis.get();

        long totalPdpEvaluationTime = totalPDPEvaluationTimeMillis.get();
        long totalPdpEvalLatency = totalExecutionLatency - totalProcessingLatency;

        long totalHeapMemoryUsedCumulative = totalHeapMemoryUsed.get();
        long totalNonHeapMemoryUsedCumulative = totalNonHeapMemoryUsed.get();
        long totalMemoryUsedCumulative = totalMemoryConsumed.get();

        // Log the cumulative metrics with averages
        logger.info("Cumulative Metrics ({} requests):", currentRequestCount);
        logger.info("  - Total Processing Time: {} ms, Average: {} ms",
                totalProcessingTime, totalProcessingTime / currentRequestCount);
        logger.info("  - Total Processing Time Latency (excluding PDP evaluation): {} ms, Average: {} ms",
                totalProcessingLatency, totalProcessingLatency / currentRequestCount);

        logger.info("  - Total PDP Evaluation Time: {} ms, Average: {} ms",
                totalPdpEvaluationTime, totalPdpEvaluationTime / currentRequestCount);
        logger.info("  - Total PDP Evaluation Latency(Only PDP): {} ms, Average: {} ms",
                totalPdpEvalLatency, totalPdpEvalLatency / currentRequestCount);

        logger.info("  - Total Execution Time: {} ms, Average: {} ms",
                totalExecutionTime, totalExecutionTime / currentRequestCount);
        logger.info("  - Total Execution Latency: {} ms, Average: {} ms",
                totalExecutionLatency, totalExecutionLatency / currentRequestCount);

        // Log total memory consumed with accurate heap and non-heap values
        logger.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average: {}",
                totalMemoryUsedCumulative / 1024 / 1024,
                totalHeapMemoryUsedCumulative / 1024 / 1024,
                totalNonHeapMemoryUsedCumulative / 1024 / 1024,
                totalMemoryUsedCumulative/currentRequestCount/1024/1024 );
    }

    private void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapMemoryUsed = heapMemoryUsage.getUsed();
        long nonHeapMemoryUsed = nonHeapMemoryUsage.getUsed();
        long totalMemoryUsed = heapMemoryUsed + nonHeapMemoryUsed;

        // Update the cumulative memory counters for heap and non-heap memory separately
        totalHeapMemoryUsed.addAndGet(heapMemoryUsed);
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsed);
        totalMemoryConsumed.addAndGet(totalMemoryUsed);

        logger.info("{} - Heap memory used: {} MB, Non-heap memory used: {} MB, Total memory used: {} MB",
                phase, heapMemoryUsed / 1024 / 1024, nonHeapMemoryUsed / 1024 / 1024, totalMemoryUsed / 1024 / 1024);
    }

    private String extractDecision(JSONObject res) {
        try {
            if (res.has("Response")) {
                Object responseObject = res.get("Response");
                JSONObject responseJson = responseObject instanceof JSONArray ?
                        ((JSONArray) responseObject).getJSONObject(0) : (JSONObject) responseObject;

                return responseJson.optString("Decision", "Not Found");
            }
        } catch (Exception e) {
            logger.error("Error extracting decision from JSON response: {}", e.getMessage());
        }
        return "Error";
    }
}




/*  //Without TLC logged
package cyber.tf.authzforcepdpservice.controller;
import cyber.tf.authzforcepdpservice.service.PDPService;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


@RestController
public class PDPController {

    private final Logger logger = LoggerFactory.getLogger(PDPController.class);

    @Autowired
    private PDPService pdpService;


    @RequestMapping(value = "/authorize/xml",
            method = RequestMethod.POST,
            consumes = {"application/xml", "application/xacml+xml"},
            produces = {"application/xml", "application/xacml+xml"}
    )
    public Response evaluateXML(@RequestBody String requestString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Request.class);
        Request request = (Request) context.createUnmarshaller().unmarshal(new StringReader(requestString));

        logger.info("Evaluating XACXML XML request.");
        Response res = pdpService.getXMLAdapter().evaluate(request);
        logger.info("Finished evaluating XACXML XML request.");
        return res;
    }


    @RequestMapping(value = "/application/json",
            method = RequestMethod.POST,
            consumes = {"application/json", "application/xacml+json"},
            produces = {"application/json", "application/xacml+json"}
    )

    //public ResponseEntity<String> evaluateJSON(@RequestBody String request) throws IOException

    public String evaluateJSON(@RequestBody String request) throws IOException {
        JSONObject json = new JSONObject(new JSONTokener(request));

        logger.info("Evaluating XACXML JSON request.");
        JSONObject res = pdpService.getJSONAdapter().evaluate(json);
        logger.info("Finished evaluating XACXML JSON request.");

        String decision = "Unknown";

        if (res.has("Response")) {
            Object responseObject = res.get("Response");
            if (responseObject instanceof JSONObject) {
                JSONObject responseObj = (JSONObject) responseObject;
                if (responseObj.has("Decision")) {
                    decision = responseObj.getString("Decision");
                } else {
                    logger.error("Invalid response: 'Decision' field is missing.");
                }
            } else if (responseObject instanceof JSONArray) {
                JSONArray responseArray = (JSONArray) responseObject;
                // Handle the case where 'Response' is an array
                if (responseArray.length() > 0) {
                    JSONObject firstResponse = responseArray.getJSONObject(0);
                    if (firstResponse.has("Decision")) {
                        decision = firstResponse.getString("Decision");
                    } else {
                        logger.error("Invalid response: 'Decision' field is missing in the first response object of the array.");
                    }
                } else {
                    logger.error("Invalid response: 'Response' array is empty.");
                }
            } else {
                logger.error("Invalid response: 'Response' field is not a JSON object or array.");
            }
        } else {
            logger.error("Invalid response: 'Response' field is missing.");
        }

        logger.info("Decision: {}", decision);

        System.out.println();

      //  return res.toString();
        return decision;
    }
}

*/


   /* @RequestMapping(value = "/updatePDPConfig", method = RequestMethod.POST)
    public ResponseEntity<String> updatePDPConfig(@RequestBody byte[] hash) {
        try {
            pdpService.setPdpConfigFile(hash);
            return new ResponseEntity<>("PDP configuration updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating PDP configuration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
*/
