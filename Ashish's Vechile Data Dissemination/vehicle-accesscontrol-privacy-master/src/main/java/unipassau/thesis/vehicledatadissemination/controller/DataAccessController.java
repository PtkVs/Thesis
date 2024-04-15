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

import unipassau.thesis.vehicledatadissemination.config.PolicyConfiguration;
import unipassau.thesis.vehicledatadissemination.config.PolicyFile;
import unipassau.thesis.vehicledatadissemination.services.PDPService;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;


import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DataAccessController {

    public static String dataFolder = System.getProperty("user.dir")+"/data/";
    public static int count=8; //need to make it dynamic

    private final String POLICY_STORE_PATH = "policies";

    Logger LOG = LoggerFactory.getLogger(DataAccessController.class);



    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @Autowired
    private PDPService pdpService;

   @Autowired
   private PolicyConfiguration policyConfiguration;



    @RequestMapping(method = RequestMethod.POST, value = "/authorize")
    public ResponseEntity<byte[]> authorize(InputStream dataStream) throws Exception {   //bob le request garyo with necessary crediantials along with binary data which is encrypted data(stickyDocument) aba teslai evaluate ko lagi processing

        // Read the binary file contained in the body of the request
        byte[] onlyHash = dataStream.readAllBytes();

        // Seperate the hash value and the ciphertext from the input file
        Map<String, byte[]> stickyDocumentMap =  DataHandler.readOnlyHash(onlyHash);  // Datahandler class lai call garera binary data ko hash ra data lai sperate hune kam garyo and returnd hash and data





        // Get the attributes of the user from the context handler
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())  //attributes like bob ko username and password  etc haru liyo
                .getRequest();

        //Principal class le match garyo like above attributes request ma ayeko  for authentication and authorization
        Principal principal = request.getUserPrincipal();

        //Get the attributes of the user from the request
        String uri = request.getRequestURI();
        String method = request.getMethod();





        //Conversion of byte[] to string
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFileName = policyConfiguration.policyMap().get(hashValue);

        if(policyFileName != null) {

            //Retreive the XML policy file
           File policyXmlFile = new File((POLICY_STORE_PATH + File.separator + policyFileName));
           PolicyFile policyFile = new PolicyFile(policyFileName, policyXmlFile);

            //Read the content of the policy file
            String policyContent = new String(Files.readAllBytes(policyXmlFile.toPath()), StandardCharsets.UTF_8);


            //Read the content of the policy file
          //  byte[] policyContent = Files.readAllBytes(policyXmlFile.toPath());





            // Retrieve the policy to enforce from the hash and set the pdp config file
          //  policyEnforcementService.setPdpConfigFile(stickyDocumentMap.get("hash"));  //PolicyEnforcementService class ko setpdpConfigFile method call vayo jasle pdp engine set garna according to used polices help garxa and hash compare garinxa given by bob and the actual policy

            // Create XACML request for the PDP and get access control decision.
            //  boolean res = policyEnforcementService.authorize(principal, request.getRequestURI(), request.getMethod());   //pdp setup vayo now it will authenticate the values sent in the request with the xml policy parameters



            boolean res = pdpService.updateRemotePDPServer(policyContent);

            if (res) {
                // Permit decision

                byte[] onlyData = null;
                try {
                    FileInputStream read = new FileInputStream(new File(dataFolder + count));

                    onlyData = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);
                return new ResponseEntity<>(proxyReEncryptionService.reEncrypt
                        (data.get("data"), principal), HttpStatus.OK);


            } else {
                // Deny decision
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } else {
            // Policy not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }
}
