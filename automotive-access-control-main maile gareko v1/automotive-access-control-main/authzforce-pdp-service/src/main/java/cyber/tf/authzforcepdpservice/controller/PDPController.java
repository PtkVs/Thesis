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


    @RequestMapping(value = "/authorize/json",
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

        return res.toString();
    }

     //
    //}
}


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

