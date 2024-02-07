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
