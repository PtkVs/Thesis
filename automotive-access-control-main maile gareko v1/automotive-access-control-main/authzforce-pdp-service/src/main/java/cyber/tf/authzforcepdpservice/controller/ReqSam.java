package cyber.tf.authzforcepdpservice.controller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;

public class ReqSam {

    public static void main(String[] args) throws JAXBException {




        String a = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Request ReturnPolicyIdList=\"true\" CombinedDecision=\"true\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
                "    <RequestDefaults>\n" +
                "        <!-- Example Request Defaults Data -->\n" +
                "        <XPathVersion>1.0</XPathVersion>\n" +
                "    </RequestDefaults>\n" +
                "    <Attributes>\n" +
                "        <!-- First Attributes -->\n" +
                "        <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
                "            <!-- Example Attribute 1 -->\n" +
                "            <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" IncludeInResult=\"true\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">example-resource-id-1</AttributeValue>\n" +
                "            </Attribute>\n" +
                "            <!-- Example Attribute 2 -->\n" +
                "            <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:scope\" IncludeInResult=\"false\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">example-scope-1</AttributeValue>\n" +
                "            </Attribute>\n" +
                "        </Attributes>\n" +
                "        <!-- Second Attributes -->\n" +
                "        <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">\n" +
                "            <!-- Example Attribute 1 -->\n" +
                "            <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" IncludeInResult=\"true\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">example-action-id-1</AttributeValue>\n" +
                "            </Attribute>\n" +
                "            <!-- Example Attribute 2 -->\n" +
                "            <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:scope\" IncludeInResult=\"false\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">example-scope-2</AttributeValue>\n" +
                "            </Attribute>\n" +
                "        </Attributes>\n" +
                "    </Attributes>\n" +
                "    <MultiRequests>\n" +
                "        <!-- Example MultiRequests Data -->\n" +
                "        <RequestReference>\n" +
                "            <AttributesReference ReferenceId=\"example-attributes-reference-id\"/>\n" +
                "        </RequestReference>\n" +
                "    </MultiRequests>\n" +
                "</Request>\n";

        JAXBContext context = JAXBContext.newInstance(RequestSample.class);
        RequestSample req = (RequestSample) context.createUnmarshaller().unmarshal(new StringReader(a));

        System.out.println(req.attributes);


    }
}
