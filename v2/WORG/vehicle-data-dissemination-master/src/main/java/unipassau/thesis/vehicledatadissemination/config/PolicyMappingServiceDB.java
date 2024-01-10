package unipassau.thesis.vehicledatadissemination.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import unipassau.thesis.vehicledatadissemination.util.Encoder;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;
import unipassau.thesis.vehicledatadissemination.model.MappingModelDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PolicyMappingServiceDB {

    private MappingRepoDB maprepo;

    @Autowired
    public PolicyMappingServiceDB(MappingRepoDB maprepo){
        this.maprepo = maprepo;
    }

    public void saveMappings(String hashValue, String policyName) {
        MappingModelDB mapping = new MappingModelDB();
        mapping.setHashValue(hashValue);
        mapping.setPolicyName(policyName);

        maprepo.save(mapping);
        System.out.println("Saved to DB");
    }

    public void printAllMappings() {
        List<MappingModelDB> mappings = maprepo.findAll();
        System.out.println("All mappings in the database:");
        for (MappingModelDB mapping : mappings) {
            System.out.println("HashValue: " + mapping.getHashValue() + ", PolicyName: " + mapping.getPolicyName());
        }
    }
}







