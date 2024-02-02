package unipassau.thesis.vehicledatadissemination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;


import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.security.Principal;
import java.util.Map;

@RestController
public class DataAccessController {

    Logger LOG = LoggerFactory.getLogger(DataAccessController.class);

    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @RequestMapping(method = RequestMethod.POST, value = "/authorize")
    public ResponseEntity<byte[]> authorize(InputStream dataStream) throws Exception {
        // Read the binary file contained in the body of the request
        byte[] stickyDocument = dataStream.readAllBytes();
        // Seperate the hash value and the ciphertext from the input file
        Map<String, byte[]> stickyDocumentMap =  DataHandler.read(stickyDocument);

        // Get the attributes of the user from the context handler
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();

        Principal principal = request.getUserPrincipal(); //yeha yo principal empty xaaa note

        // Retrieve the policy to enforce from the hash and set the pdp config file
        policyEnforcementService.setPdpConfigFile(stickyDocumentMap.get("hash"));
        // Create XACML request for the PDP and get access control decision.
        boolean res = policyEnforcementService.authorize
                (principal, request.getRequestURI(), request.getMethod());

        if (res){
            // Permit decision
            return new ResponseEntity<>(proxyReEncryptionService.reEncrypt
                    (stickyDocumentMap.get("data"), principal), HttpStatus.OK);


        }else {
            // Deny decision
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }
}


/*
1.Annotations:
-The class is annotated with @RestController, indicating that it combines @Controller and @ResponseBody. This means that each method
 returns a domain object instead of a view and is directly written into the HTTP response as JSON or XML.

2.Logger and Autowired Fields:
-The class includes a logger (LOG from SLF4J) and two autowired fields.
-PolicyEnforcementService policyEnforcementService: Autowired for interacting with a policy enforcement service.
-ProxyReEncryptionService proxyReEncryptionService: Autowired for handling proxy re-encryption.

3.authorize Method:
-This method is mapped to the endpoint /authorize with the HTTP method POST.
-It takes an InputStream parameter (dataStream) as the request body, which is likely used to provide data for authorization.

4.Authorization Logic:
-The method performs the following steps:
    -Reads the binary file contained in the body of the request (stickyDocument).
    -Separates the hash value and ciphertext from the input file using DataHandler.read.
    -Retrieves the Principal and HttpServletRequest from the request context.
    -Sets the policy enforcement service's PDP configuration file based on the hash value.
    -Creates an XACML request for the PDP and gets an access control decision using policyEnforcementService.authorize.
    -If the decision is to permit (true), it re-encrypts the data using the proxy re-encryption service and returns the result in the HTTP response.
    -If the decision is to deny (false), it returns an HTTP response with status UNAUTHORIZED (401).

5.Logging:
-The class logs various messages using the logger.

**Summary**
This controller handles incoming requests to the /authorize endpoint with a POST method. It reads data from the request, separates
hash values and ciphertext, performs authorization using a policy enforcement service, and either permits or denies access based on
the authorization decision. The result, either re-encrypted data or an unauthorized status, is returned in the HTTP response.
This controller is a crucial component in the data access control flow of the application.
*/