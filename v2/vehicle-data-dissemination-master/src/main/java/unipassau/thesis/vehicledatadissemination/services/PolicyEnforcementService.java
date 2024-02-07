package unipassau.thesis.vehicledatadissemination.services;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import unipassau.thesis.vehicledatadissemination.pep.EmbeddedPdpInterceptor;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.security.Principal;
import java.util.HashMap;

@Service
public class PolicyEnforcementService {
    Logger LOG = LoggerFactory.getLogger(PolicyEnforcementService.class);

    @Autowired
    private HashMap<String, String> policyMap;

    @Autowired
    org.springframework.core.env.Environment env;

    private PdpEngineConfiguration pdpEngineConfiguration;

    public boolean authorize(Principal principal, String requestURI, String action) throws Exception {
        // Set the pdp engine configuration with the PDP config file
        pdpEngineConfiguration = PdpEngineConfiguration.getInstance(env.getProperty("pdp.config.path"));
        // Deploy an Embedded PDP engine
        final BasePdpEngine pdp = new BasePdpEngine(pdpEngineConfiguration);
        // Pass the pdp engine to a pdpInterceptor to create an evaluate an XACML request
        final EmbeddedPdpInterceptor pdpInterceptor = new EmbeddedPdpInterceptor(pdp);
        // evaluate the XACML request against the policy
        boolean res = pdpInterceptor.authorize(principal, requestURI, action);

        return res;
    }


    private void setPolicy(File file, String policy) throws UnsupportedEncodingException {

        byte[] data = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = new byte[(int) file.length()];
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = new String(data, "UTF-8");
        String tag = "<policyLocation>";
        String closetag = "</policyLocation>";
        StringBuffer sbf = new StringBuffer(input);
        sbf.delete(input.indexOf(tag) + tag.length() , input.indexOf(closetag));
        input= sbf.toString();
        String newXML = input.substring(0, input.indexOf(tag) + tag.length()) + "policies/" + policy + input.substring(input.indexOf(tag) + tag.length(), input.length());
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(newXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("PDP config file updated with policy : " + policy);
    }

    public void setPdpConfigFile(byte[] hash){
        try {
            String policy = policyMap.get(Encoder.bytesToHex(hash));

            File inputFile = new File(env.getProperty("pdp.config.path"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            XPath xPath = XPathFactory.newInstance().newXPath();
            Node policyLocationNode = (Node) xPath.compile("/pdp/policyProvider/policyLocation").evaluate(doc, XPathConstants.NODE);

            boolean checkForPolicy = (policyLocationNode.getTextContent().equals("policies/"+policy));
            if (!checkForPolicy){
                LOG.info("Updating PDP config file with policy : " + policy);
                setPolicy(inputFile, policy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/*
1.Constructor and Autowired Fields:
-The class is annotated with @Service, indicating that it's a Spring service component.
-It has fields annotated with @Autowired, such as policyMap and env, indicating dependency injection.

2.authorize Method:
-This method takes a Principal (user), a request URI, and an action as parameters and performs an authorization check.
-It uses an embedded PDP engine to evaluate the XACML request created by the EmbeddedPdpInterceptor.
-The method returns true if the decision is PERMIT, indicating that the user is authorized.

3.setPolicy Method:
-This method is a helper method used to update the PDP configuration file with a specific policy.

4.setPdpConfigFile Method:
-This method is responsible for updating the PDP configuration file based on the hash of a policy.
-It checks if the current PDP configuration already includes the specified policy. If not, it updates the configuration.

5.Logging:
-The class uses SLF4J for logging, and it logs information about updating the PDP configuration file and the XACML authorization decision.

**Summary:
The PolicyEnforcementService class is a Spring service responsible for enforcing access control policies using an embedded PDP engine.
It can authorize users based on XACML policies, and it includes functionality to update the PDP configuration file dynamically based on
policy changes.
 */