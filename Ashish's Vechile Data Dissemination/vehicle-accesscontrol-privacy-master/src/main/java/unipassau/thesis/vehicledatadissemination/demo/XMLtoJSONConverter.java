package unipassau.thesis.vehicledatadissemination.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLtoJSONConverter {

    public static String convertXMLtoJSON(String xmlString) {
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Policy xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" Version=\"1.0\" PolicyId=\"DataReEncryption\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\">\n" +
                "    <Target>\n" +
                "        <AnyOf>\n" +
                "            <AllOf>\n" +
                "                <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">bob</AttributeValue>\n" +
                "                    <AttributeDesignator Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" MustBePresent=\"true\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" +
                "                </Match>\n" +
                "                <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">POST</AttributeValue>\n" +
                "                    <AttributeDesignator DataType=\"http://www.w3.org/2001/XMLSchema#string\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" MustBePresent=\"true\"/>\n" +
                "                </Match>\n" +
                "            </AllOf>\n" +
                "            <AllOf>\n" +
                "                <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">bob</AttributeValue>\n" +
                "                    <AttributeDesignator Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" MustBePresent=\"true\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" +
                "                </Match>\n" +
                "                <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">POST</AttributeValue>\n" +
                "                    <AttributeDesignator DataType=\"http://www.w3.org/2001/XMLSchema#string\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" MustBePresent=\"true\"/>\n" +
                "                </Match>\n" +
                "            </AllOf>\n" +
                "        </AnyOf>\n" +
                "    </Target>\n" +
                "    <Rule Effect=\"Permit\" RuleId=\"AllowReEncryption\">\n" +
                "        <Target/>\n" +
                "        <Condition>\n" +
                "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">/authorize</AttributeValue>\n" +
                "                <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
                "                    <AttributeDesignator DataType=\"http://www.w3.org/2001/XMLSchema#string\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"true\"/>\n" +
                "                </Apply>\n" +
                "            </Apply>\n" +
                "        </Condition>\n" +
                "    </Rule>\n" +
                "    <Rule Effect=\"Permit\" RuleId=\"1\">\n" +
                "        <Target/>\n" +
                "        <Condition>\n" +
                "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">skip</AttributeValue>\n" +
                "                <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
                "                    <AttributeDesignator DataType=\"http://www.w3.org/2001/XMLSchema#string\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"true\"/>\n" +
                "                </Apply>\n" +
                "            </Apply>\n" +
                "        </Condition>\n" +
                "    </Rule>\n" +
                "    <Rule Effect=\"Permit\" RuleId=\"AllowBenchmark\">\n" +
                "        <Target/>\n" +
                "        <Condition>\n" +
                "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                "                <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">/benchmark/authorize</AttributeValue>\n" +
                "                <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
                "                    <AttributeDesignator DataType=\"http://www.w3.org/2001/XMLSchema#string\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"true\"/>\n" +
                "                </Apply>\n" +
                "            </Apply>\n" +
                "        </Condition>\n" +
                "    </Rule>\n" +
                "</Policy>";

        String jsonData = convertXMLtoJSON(xmlData);
        System.out.println(jsonData);
    }
}
