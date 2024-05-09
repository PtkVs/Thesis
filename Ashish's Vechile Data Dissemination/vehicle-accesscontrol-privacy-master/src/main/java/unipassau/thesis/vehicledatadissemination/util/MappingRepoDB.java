package unipassau.thesis.vehicledatadissemination.util;


import org.springframework.stereotype.Repository;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository

public interface MappingRepoDB extends JpaRepository<MappingPolicyDB, Long> {
    MappingPolicyDB findByHashValue(String hashValue);
    MappingPolicyDB findByPolicyName(String policyFilename);
}

