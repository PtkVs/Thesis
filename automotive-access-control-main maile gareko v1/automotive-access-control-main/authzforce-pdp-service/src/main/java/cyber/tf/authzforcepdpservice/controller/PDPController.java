package cyber.tf.authzforcepdpservice.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyber.tf.authzforcepdpservice.service.PDPService;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
public class PDPController {

    private final Logger logger = LoggerFactory.getLogger(PDPController.class);

    @Autowired
    private PDPService pdpService;


    @RequestMapping(value = "/authorize",
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

    @RequestMapping(value = "/authorize",
            method = RequestMethod.POST,
            consumes = {"application/json", "application/xacml+json"},
            produces = {"application/json", "application/xacml+json"}
    )
    public String evaluateJSON(@RequestBody String request) throws IOException {
        JSONObject json = new JSONObject(new JSONTokener(request));
        logger.info("Evaluating XACXML JSON request.");
        JSONObject res = pdpService.getJSONAdapter().evaluate(json);
        logger.info("Finished evaluating XACXML JSON request.");
        //return res.toString();


        String jsonString = res.toString();
        try {

            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON string into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Access and print values from the JsonNode
            String decision = jsonNode.get("Response").get(0).get("Decision").asText();

            System.out.println("Decision: " + decision);

            if(decision.equals("Permit")) {
            	System.out.println("ReEncryption in progress");

                System.out.println("Sending JSON to ReEncryption Service");

                // run curl command in terminal
                // curl command to send json to server and not use postman
                // curl -X POST -H "Content-Type: application/json" -d @payload1.json http://localhost:8888/authorize

                //need to work more to make it work for next server and make it dynamic
                String[] command = {"curl", "-X", "POST", "-H", "Content-Type: application/json", "-d", "@payload1_subjectID.json", "http://localhost:8080/authorize"};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                int exitVal = process.waitFor();
                System.out.println("ReEncryption Service exited with code " + exitVal);

            	// Save the JSON object to a file
                //String jsonString = res.toString();
                Files.write(Paths.get("output.json"), jsonString.getBytes());
                System.out.println("JSON file created: " + jsonString);
            }
            else {
            	System.out.println("Unable to ReEncrypt: Permission" + decision);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }





        /*// Save the JSON object to a file
        String jsonString = res.toString();
        Files.write(Paths.get("output.json"), jsonString.getBytes());
        System.out.println("JSON file created: " + jsonString);
        */
        return jsonString;
    }
}
