package unipassau.thesis.vehicledatadissemination.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import unipassau.thesis.vehicledatadissemination.config.PolicyFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Properties;

@Service
public class PDPService {

    private String pdpServerUrl;

    @Autowired
    private org.springframework.core.env.Environment env;

    public boolean updateRemotePDPServer(String policyContent) {
        loadProperties();

        try {

            // Construct the URL for the updatePDPConfig endpoint
            URL url = new URL(pdpServerUrl + "authorize/json");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Set the content type to XML
         //   connection.setRequestProperty("Content-Type", "application/json");

            // Set the content type to XML
            connection.setRequestProperty("Content-Type", "application/json");

            // Convert the normal string to JSON
            String jsonData = "{\n" +
                    "      \"Request\": {\n" +
                    "    \"Category\": [\n" +
                    "{\n" +
                    "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\n" +
                    "        \"Attribute\": [\n" +
                    "          {\n" +
                    "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:action:action-id\",\n" +
                    "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "            \"Value\": \"POST\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"CategoryId\": \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\n" +
                    "        \"Attribute\": [\n" +
                    "          {\n" +
                    "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:subject:subject-id\",\n" +
                    "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "            \"Value\": \"pratik\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                    "        \"Attribute\": [\n" +
                    "          {\n" +
                    "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                    "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "            \"Value\": \"/authorize\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}\n";




            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonData);
                writer.flush();
            }


         //   try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
           //     writer.write(jsonData);
             //   writer.flush();
            //}


            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the request was successful (HTTP status code 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("PDP config updated successfully");
            } else {
                System.out.println("Failed to update PDP config. Response Code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private String loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                pdpServerUrl = properties.getProperty("pdp.location");

            } else {
                System.out.println("Sorry, unable to Connect to PDP server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdpServerUrl;
    }


    private Document convertStringToXML(String policyContent) throws ParserConfigurationException, IOException, SAXException {
        // Create a DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the policyContent string to create a Document
        Document document = builder.parse(new InputSource(new ByteArrayInputStream(policyContent.getBytes())));

        return document;
    }

    private String serializeXML(Document document) throws TransformerException {
        // Create a Transformer
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // Serialize the XML document to a string
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.getBuffer().toString();
    }


    public String convertToJson(String normalString) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("key", normalString);

        try {
            return objectMapper.writeValueAsString(objectNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}