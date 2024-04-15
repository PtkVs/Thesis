package unipassau.thesis.vehicledatadissemination.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StringToJsonConverter {

    public static void main(String[] args) {
        // Your normal string data
        String normalString = "Hello, world!";

        // Convert the normal string to JSON
        String jsonString = convertToJson(normalString);

        // Print the JSON string
        System.out.println("JSON String: " + jsonString);
    }


    public static String convertToJson(String normalString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Create a JSON object with your string as a value
            return objectMapper.writeValueAsString(normalString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


/*
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
            connection.setRequestProperty("Content-Type", "application/json");

            // Convert the normal string to JSON
            String jsonString = convertToJson(policyContent);

            // Print the JSON string
            System.out.println("JSON String: " + jsonString);


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












/******************************************************************* JSON
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
            connection.setRequestProperty("Content-Type", "application/json");

            // Convert the normal string to JSON
           String jsonData = convertToJson(policyContent);




            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonData);
                writer.flush();
            }


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













 */

/********************************* XML
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

    public boolean updateRemotePDPServer(byte[] policyContent) {
         loadProperties();

        try {


    // Construct the URL for the updatePDPConfig endpoint
    URL url = new URL(pdpServerUrl + "authorize/xml");

    // Open a connection to the URL
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // Set the request method to POST
    connection.setRequestMethod("POST");

    // Enable input/output streams
    connection.setDoOutput(true);

    // Set the content type to XML
    connection.setRequestProperty("Content-Type", "application/xml");

    // Remove double quotes from the beginning and end of the string
    System.out.println(policyContent);
    if (policyContent.startsWith(" \" ") && policyContent.endsWith(" \" ")) {
    policyContent = policyContent.substring(1, policyContent.length() - 1);
    }

    // Write the XML content to the output stream
    try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
    writer.write(policyContent);
    writer.flush();
    }



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




    /* For XML data
    // Construct the URL for the updatePDPConfig endpoint
    URL url = new URL(pdpServerUrl + "authorize/xml");

    // Open a connection to the URL
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // Set the request method to POST
            connection.setRequestMethod("POST");

    // Enable input/output streams
            connection.setDoOutput(true);

    // Set the content type to XML
            connection.setRequestProperty("Content-Type", "application/xml");

    Document document = convertStringToXML(policyContent);

    String xmlString = serializeXML(document);

    // Write the policy content to the output stream
            try (OutputStream os = connection.getOutputStream()) {
        os.write(xmlString.getBytes());


     public String convertToJson(String normalString) {
        // Remove double quotes from the beginning and end of the string
        normalString = normalString.substring(1, normalString.length() - 1);

        // Encapsulate the string within curly braces
        return "{" + normalString + "}";
    }

     */







/*
  // Convert the normal string to JSON
            String jsonData = "{\n" +
                    "   \"Policy\": {\n" +
                    "      \"Target\": {\n" +
                    "         \"AnyOf\": {\n" +
                    "            \"AllOf\": [\n" +
                    "               {\n" +
                    "                  \"Match\": [\n" +
                    "                     {\n" +
                    "                        \"AttributeValue\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"__text\": \"bob\"\n" +
                    "                        },\n" +
                    "                        \"AttributeDesignator\": {\n" +
                    "                           \"_Category\": \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\n" +
                    "                           \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:subject:subject-id\",\n" +
                    "                           \"_MustBePresent\": \"true\",\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "                        },\n" +
                    "                        \"_MatchId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "                     },\n" +
                    "                     {\n" +
                    "                        \"AttributeValue\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"__text\": \"POST\"\n" +
                    "                        },\n" +
                    "                        \"AttributeDesignator\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:action:action-id\",\n" +
                    "                           \"_Category\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\n" +
                    "                           \"_MustBePresent\": \"true\"\n" +
                    "                        },\n" +
                    "                        \"_MatchId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "                     }\n" +
                    "                  ]\n" +
                    "               },\n" +
                    "               {\n" +
                    "                  \"Match\": [\n" +
                    "                     {\n" +
                    "                        \"AttributeValue\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"__text\": \"bob\"\n" +
                    "                        },\n" +
                    "                        \"AttributeDesignator\": {\n" +
                    "                           \"_Category\": \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\n" +
                    "                           \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:subject:subject-id\",\n" +
                    "                           \"_MustBePresent\": \"true\",\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                    "                        },\n" +
                    "                        \"_MatchId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "                     },\n" +
                    "                     {\n" +
                    "                        \"AttributeValue\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"__text\": \"POST\"\n" +
                    "                        },\n" +
                    "                        \"AttributeDesignator\": {\n" +
                    "                           \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                           \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:action:action-id\",\n" +
                    "                           \"_Category\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\n" +
                    "                           \"_MustBePresent\": \"true\"\n" +
                    "                        },\n" +
                    "                        \"_MatchId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "                     }\n" +
                    "                  ]\n" +
                    "               }\n" +
                    "            ]\n" +
                    "         }\n" +
                    "      },\n" +
                    "      \"Rule\": [\n" +
                    "         {\n" +
                    "            \"Condition\": {\n" +
                    "               \"Apply\": {\n" +
                    "                  \"AttributeValue\": {\n" +
                    "                     \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                     \"__text\": \"/authorize\"\n" +
                    "                  },\n" +
                    "                  \"Apply\": {\n" +
                    "                     \"AttributeDesignator\": {\n" +
                    "                        \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                        \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                    "                        \"_Category\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                    "                        \"_MustBePresent\": \"true\"\n" +
                    "                     },\n" +
                    "                     \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\"\n" +
                    "                  },\n" +
                    "                  \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            \"_Effect\": \"Permit\",\n" +
                    "            \"_RuleId\": \"AllowReEncryption\"\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"Condition\": {\n" +
                    "               \"Apply\": {\n" +
                    "                  \"AttributeValue\": {\n" +
                    "                     \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                     \"__text\": \"skip\"\n" +
                    "                  },\n" +
                    "                  \"Apply\": {\n" +
                    "                     \"AttributeDesignator\": {\n" +
                    "                        \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                        \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                    "                        \"_Category\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                    "                        \"_MustBePresent\": \"true\"\n" +
                    "                     },\n" +
                    "                     \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\"\n" +
                    "                  },\n" +
                    "                  \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            \"_Effect\": \"Permit\",\n" +
                    "            \"_RuleId\": \"1\"\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"Condition\": {\n" +
                    "               \"Apply\": {\n" +
                    "                  \"AttributeValue\": {\n" +
                    "                     \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                     \"__text\": \"/benchmark/authorize\"\n" +
                    "                  },\n" +
                    "                  \"Apply\": {\n" +
                    "                     \"AttributeDesignator\": {\n" +
                    "                        \"_DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                    "                        \"_AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                    "                        \"_Category\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                    "                        \"_MustBePresent\": \"true\"\n" +
                    "                     },\n" +
                    "                     \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\"\n" +
                    "                  },\n" +
                    "                  \"_FunctionId\": \"urn:oasis:names:tc:xacml:1.0:function:string-equal\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            \"_Effect\": \"Permit\",\n" +
                    "            \"_RuleId\": \"AllowBenchmark\"\n" +
                    "         }\n" +
                    "      ],\n" +
                    "      \"_xmlns\": \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\",\n" +
                    "      \"_Version\": \"1.0\",\n" +
                    "      \"_PolicyId\": \"DataReEncryption\",\n" +
                    "      \"_RuleCombiningAlgId\": \"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\"\n" +
                    "   }\n" +
                    "}";

 */



