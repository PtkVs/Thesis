package unipassau.thesis.vehicledatadissemination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import unipassau.thesis.vehicledatadissemination.benchmark.Benchmark;
import unipassau.thesis.vehicledatadissemination.benchmark.BenchmarkResult;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/benchmark")
public class BenchmarkController {
    Logger logger = LoggerFactory.getLogger(BenchmarkController.class);
    @Autowired
    org.springframework.core.env.Environment env;
    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;



    @RequestMapping(value = "/authorize",
            method = RequestMethod.POST,
            produces = {"text/csv"}
    )
    public ResponseEntity authorize(InputStream dataStream) throws Exception {

        long start = System.nanoTime();
        // Read the binary file contained in the body of the request
        byte[] stickyDocument = dataStream.readAllBytes();
        // Seperate the hash value and the ciphertext from the input file
        Map<String, byte[]> stickyDocumentMap =  DataHandler.read(stickyDocument);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();

        Principal principal = request.getUserPrincipal();
        long end = System.nanoTime();

        int runs = Integer.parseInt(env.getProperty("benchmark.runs"));
        int warmupRuns = Integer.parseInt(env.getProperty("benchmark.warmup.runs"));
        logger.info(String.valueOf(runs));
        logger.info(String.valueOf(warmupRuns));


        logger.info("Warm-Up for benchmarking ");

        for (int i = 0; i < warmupRuns; i++) {

            // Retrieve the policy to enforce from the hash and set the pdp config file
            policyEnforcementService.setPdpConfigFile(stickyDocumentMap.get("hash"));
            // Create XACML request for the PDP and get access control decision.
            boolean res = policyEnforcementService.authorize
                    (principal, request.getRequestURI(), request.getMethod());
            byte[] data = proxyReEncryptionService.reEncrypt
                     (stickyDocumentMap.get("data"), principal);
        }

        Benchmark benchmark = new Benchmark();

        logger.info("Benchmarking ");

        for (int i = 0; i < runs; i++) {
            long before = System.nanoTime();


            // Retrieve the policy to enforce from the hash and set the pdp config file
            policyEnforcementService.setPdpConfigFile(stickyDocumentMap.get("hash"));
            // Create XACML request for the PDP and get access control decision.
            boolean res = policyEnforcementService.authorize
                    (principal, request.getRequestURI(), request.getMethod());

            byte[] data = proxyReEncryptionService.reEncrypt
                    (stickyDocumentMap.get("data"), principal);
            long after = System.nanoTime();

            benchmark.addResult(new BenchmarkResult(i, after - before + end - start));
        }

        logger.info("Finished benchmarking");

        return ResponseEntity.ok().body(benchmark.toCSV());
    }


}

/*

1.Annotations:
-The class is annotated with @Controller, indicating that it serves the role of a controller in a Spring MVC application.
-The @RequestMapping("/benchmark") annotation specifies that this controller handles requests with the base path /benchmark.

2.Logger and Autowired Fields:
-The class includes a logger (from SLF4J) and several autowired fields.
-Environment env: Autowired for accessing application properties.
-PolicyEnforcementService policyEnforcementService: Autowired for interacting with a policy enforcement service.
-ProxyReEncryptionService proxyReEncryptionService: Autowired for handling proxy re-encryption.

3.authorize Method:
-This method is mapped to the endpoint /benchmark/authorize with the HTTP method POST and produces content type "text/csv".
-It takes an InputStream parameter (dataStream) as the request body, which is likely used for benchmarking purposes.

4.Benchmarking Logic:
-The method performs a series of operations related to benchmarking:
    -Reads the binary file contained in the body of the request (stickyDocument) and separates the hash value and ciphertext.
    -Retrieves the Principal and HttpServletRequest from the request.
    -Performs warm-up runs and actual benchmarking runs using a loop.
    -Within each run, it interacts with services (PolicyEnforcementService and ProxyReEncryptionService) and measures the time taken.
    -The benchmark results are collected and stored using a Benchmark object.

5.Benchmark Object (Benchmark):
-The Benchmark object seems to be responsible for collecting and formatting benchmark results.
-It includes a method toCSV() that converts the benchmark results to a CSV format.

6.Logging:
-The class logs various messages using the logger, including information about the number of runs and warm-up runs.

**Summary**
This controller is designed for benchmarking a specific scenario related to vehicle data dissemination. It interacts with services
to enforce policies and perform proxy re-encryption while measuring the time taken for these operations. The benchmark results are
then collected and formatted into a CSV format for further analysis or reporting. The specific details of the benchmarking scenario
would depend on the implementation of the autowired services (PolicyEnforcementService and ProxyReEncryptionService).
*/
