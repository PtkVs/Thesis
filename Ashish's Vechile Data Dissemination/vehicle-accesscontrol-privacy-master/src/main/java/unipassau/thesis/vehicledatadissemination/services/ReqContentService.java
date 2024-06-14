package unipassau.thesis.vehicledatadissemination.services;

import org.springframework.beans.factory.annotation.Autowired;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReqContentService {

    @Autowired
    private MappingRepoDB maprepo;

    public String fetchPolicyContent(String hashValue) throws IOException {

        // Fetch metadata from the database
        MappingPolicyDB mapping = maprepo.findByHashValue(hashValue);
        if (mapping != null) {
            // Read the file content from the path
            String filePath = mapping.getPolicyReqPath();
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
        return null;
    }
}
