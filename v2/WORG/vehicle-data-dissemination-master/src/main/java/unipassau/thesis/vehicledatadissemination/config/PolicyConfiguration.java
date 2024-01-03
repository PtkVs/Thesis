package unipassau.thesis.vehicledatadissemination.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

@Configuration


public class PolicyConfiguration {

    private final Logger logger = LoggerFactory.getLogger(PolicyConfiguration.class);

    // Set the location of the stored policies
    private final String POLICY_STORE_PATH = "/home/sick/Documents/GitHub/Thesis/v2/WORG/vehicle-data-dissemination-master/policies";

    // Loading policies into a HashMap that maps policies to their hash value
    @Bean
    public HashMap<String, String> policyMap() throws FileNotFoundException {
        logger.info("Loading policies configuration.");
        HashMap<String, String> policyMap = new HashMap<>() ;
        File directoryPath = new File(POLICY_STORE_PATH);
        File filesList[] = directoryPath.listFiles();
        // Load hash value of each policy into a HashMap
        for(File file : filesList) {
            logger.info("Loading policy : "+ file.getName() );
            policyMap.put(Encoder.bytesToHex(Encoder.xmlToHash(file.getAbsolutePath()))
                    , file.getName());
        }
        logger.info(policyMap.toString());
        logger.info("Loaded policies configuration.");

        return policyMap;
    }


}

/*
1.@Configuration Annotation:
-This annotation indicates that the class contains configuration methods that should be processed by the Spring container during the
  application context initialization.

2. Logger Initialization:
-The class initializes a logger using the SLF4J (LoggerFactory.getLogger) framework. This logger is used to output log messages throughout
 the class.

3. POLICY_STORE_PATH Constant:
-This constant represents the file path where policies are stored. In this case, it's set to a specific directory path on the file system.

4. @Bean Annotation:
-The policyMap method is annotated with @Bean, indicating that it will produce a bean to be managed by the Spring container.

5.Loading Policies into a HashMap:
-The method loads policies from the specified directory (POLICY_STORE_PATH) into a HashMap. Each policy is represented by its hash
 value (computed using the Encoder.xmlToHash method) mapped to its file name.
-The Encoder.bytesToHex method is likely used to convert the byte array hash value into a hexadecimal string for better readability.

6.Logging Information:
-The class logs informational messages using the logger. It logs when it starts loading policies, the names of the policies being loaded,
 the resulting policy map, and a message indicating that policies have been successfully loaded.

7. Return Value:
-The method returns the HashMap<String, String> containing the mapping of policy hash values to their file names.

**Summary**
 This configuration class is responsible for loading policies from a specified directory, computing their hash values, and creating a
 HashMap where each policy's hash value is mapped to its file name. The resulting HashMap is then made available as a bean in the Spring
 application context. This could be useful for other parts of the application that need to access or reference these policies based on
 their hash values.

*/
