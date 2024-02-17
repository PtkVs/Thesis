package unipassau.thesis.vehicledatadissemination.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

@Configuration
public class PolicyConfiguration {

    private final Logger logger = LoggerFactory.getLogger(PolicyConfiguration.class);

    // Set the location of the stored policies
    private final String POLICY_STORE_PATH = "policies";

    private final DatabaseConfiguration policyMapping;

    @Autowired
    public PolicyConfiguration(DatabaseConfiguration policyMapping) {
        this.policyMapping = policyMapping;
    }

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

           //Note this is different to original code
            String hashValue = Encoder.bytesToHex(Encoder.xmlToHash(file.getAbsolutePath()));
            String policyName = file.getName();
            
            //Store policy in the database using the service
            policyMapping.saveMappings(hashValue, policyName);

            policyMap.put(hashValue, policyName);
        }
        logger.info(policyMap.toString());
        logger.info("Loaded policies configuration.");


        // Print all mappings which was saved in the db just checking ko lagi matra yeha PolicyConfiguratin ma rakheko nothing else
        policyMapping.printAllMappings();

        return policyMap;
    }
}
