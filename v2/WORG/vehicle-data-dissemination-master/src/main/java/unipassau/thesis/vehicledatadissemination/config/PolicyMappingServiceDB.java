package unipassau.thesis.vehicledatadissemination.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import unipassau.thesis.vehicledatadissemination.util.Encoder;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;
import unipassau.thesis.vehicledatadissemination.demo.MappingPolicyDB;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
@Service

public class PolicyMappingServiceDB {


    private MappingRepoDB maprepo;



    //can change the variable name "maprepo" to somthing else if confusing.
    @Autowired
    public PolicyMappingServiceDB(MappingRepoDB maprepo){
        this.maprepo = maprepo;
    }


    public void loadAndSavePolicies() {
        try {
            HashMap<String, String> policyMap = loadPolicies();
            saveMappings(policyMap);
        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
        }
    }

    private HashMap<String, String> loadPolicies() throws FileNotFoundException {
        String POLICY_STORE_PATH = "/home/sick/Documents/GitHub/Thesis/v2/WORG/vehicle-data-dissemination-master/policies";
        HashMap<String, String> policyMap = new HashMap<>();
        File directoryPath = new File(POLICY_STORE_PATH);
        File filesList[] = directoryPath.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                policyMap.put(Encoder.bytesToHex(Encoder.xmlToHash(file.getAbsolutePath())), file.getName());
            }
        }
        return policyMap;
    }

    public void saveMappings(HashMap<String, String> policyMap) {
        for (Map.Entry<String, String> entry : policyMap.entrySet()) {
            String hashValue = entry.getKey();
            String policyName = entry.getValue();

            MappingPolicyDB mapping = new MappingPolicyDB();
            mapping.setHashValue(hashValue);
            mapping.setPolicyName(policyName);

            maprepo.save(mapping);
        }
    }
}






