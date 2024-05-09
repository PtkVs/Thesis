package unipassau.thesis.vehicledatadissemination.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;

import java.util.List;

@Component
public class DatabaseConfiguration {

    @Autowired
    private MappingRepoDB maprepo;

    //@Autowired
    // public DatabaseConfiguration(MappingRepoDB maprepo){
    //     this.maprepo = maprepo;
    //}

    public void saveMappings(String hashValue, String policyName) {
        MappingPolicyDB mapping = new MappingPolicyDB();
        mapping.setHashValue(hashValue);
        mapping.setPolicyName(policyName);

        maprepo.save(mapping);
        System.out.println("Saved to DB");
    }

    public void printAllMappings() {
        List<MappingPolicyDB> mappings = maprepo.findAll();
        System.out.println("All mappings in the database:");
        for (MappingPolicyDB mapping : mappings) {
            System.out.println("HashValue: " + mapping.getHashValue() + ", PolicyName: " + mapping.getPolicyName());
        }
    }

    public boolean authenticate(String hashValue) {

        //object created so that getter methods of the MappingPolicyDB classes can be invoked, check its availability and compare for authentication
       MappingPolicyDB mapped = maprepo.findByHashValue(hashValue);
       if(mapped !=null && mapped.getHashValue().equals(hashValue)){

           // Hash value match a record in the database
           return true;
       }  else {
           // No match found in the database
           return false;
       }



    }
}






