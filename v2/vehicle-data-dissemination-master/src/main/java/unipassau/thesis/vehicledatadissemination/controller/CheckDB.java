package unipassau.thesis.vehicledatadissemination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import unipassau.thesis.vehicledatadissemination.model.MappingModelDB;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class CheckDB{

    private final MappingRepoDB mappingRepoDB;

    @Autowired
    public CheckDB(MappingRepoDB mappingRepoDB) {
        this.mappingRepoDB = mappingRepoDB;
    }

    private final Logger logger = LoggerFactory.getLogger(CheckDB.class);
    @GetMapping("/printDB")
    public String printDatabase() {

        Iterable<MappingModelDB> mappings = mappingRepoDB.findAll();

        //loop variable and the collection being iterated(It's like saying, "For each item in the collection mappings, let's call each item mapping during this iteration.")
        for (MappingModelDB mapping : mappings) {
            System.out.println("Printing DB");
           logger.info("Hash Value: {}, Policy Name: {}", mapping.getHashValue(), mapping.getPolicyName());

        }
        return "Successfully printed to console.";
    }
}
