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

        Principal principal = request.getUserPrincipal();

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
