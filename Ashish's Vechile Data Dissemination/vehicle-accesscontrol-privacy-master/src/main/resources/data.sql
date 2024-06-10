CREATE  TABLE Mapping_Policy_DB(policyName VARCHAR(255) PRIMARY KEY ,
                                hashValue VARCHAR(255),
                                policyRequest VARCHAR(4096));

INSERT INTO Mapping_Policy_DB (policyName, hashValue, policyRequest) VALUES (
                                                                                '77.xml',  '922249bf4ce33f13fa7f493318550c9d14eb1224b496d0198e55dbd2cf0b0b17', '"{\n" +
                                                                           "  \"Request\": {\n" +
                                                                           "    \"Category\": [\n" +
                                                                           "      {\n" +
                                                                           "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\n" +
                                                                           "        \"Attribute\": [\n" +
                                                                           "          {\n" +
                                                                           "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:action:action-id\",\n" +
                                                                           "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                                                                           "            \"Value\": \"GET\"\n" +
                                                                           "          }\n" +
                                                                           "        ]\n" +
                                                                           "      },\n" +
                                                                           "      {\n" +
                                                                           "        \"CategoryId\": \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\n" +
                                                                           "        \"Attribute\": [\n" +
                                                                           "          {\n" +
                                                                           "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:subject:subject-id\",\n" +
                                                                           "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                                                                           "            \"Value\": \"carsentinel\"\n" +
                                                                           "          }\n" +
                                                                           "        ]\n" +
                                                                           "      },\n" +
                                                                           "      {\n" +
                                                                           "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                                                                           "        \"Attribute\": [\n" +
                                                                           "          {\n" +
                                                                           "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                                                                           "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                                                                           "            \"Value\": \"/vehicle/camera\"\n" +
                                                                           "          }\n" +
                                                                           "        ]\n" +
                                                                           "      },\n" +
                                                                           "      {\n" +
                                                                           "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                                                                           "        \"Attribute\": [\n" +
                                                                           "          {\n" +
                                                                           "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                                                                           "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                                                                           "            \"Value\": \"/vehicle/microphone\"\n" +
                                                                           "          }\n" +
                                                                           "        ]\n" +
                                                                           "      },\n" +
                                                                           "      {\n" +
                                                                           "        \"CategoryId\": \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\n" +
                                                                           "        \"Attribute\": [\n" +
                                                                           "          {\n" +
                                                                           "            \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n" +
                                                                           "            \"DataType\": \"http://www.w3.org/2001/XMLSchema#string\",\n" +
                                                                           "            \"Value\": \"/vehicle/proximitySensor\"\n" +
                                                                           "          }\n" +
                                                                           "        ]\n" +
                                                                           "      }\n" +
                                                                           "    ]\n" +
                                                                           "  }\n" +
                                                                           "}\n"'
                                                                            );