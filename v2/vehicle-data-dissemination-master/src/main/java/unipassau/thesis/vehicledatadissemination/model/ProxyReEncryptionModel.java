package unipassau.thesis.vehicledatadissemination.model;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import unipassau.thesis.vehicledatadissemination.VehicleDataDisseminationApplication;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.File;

public class ProxyReEncryptionModel {

    public enum SecurityLevel{
        SECURITY_LEVEL_128,
        SECURITY_LEVEL_192,
        SECURITY_LEVEL_256;
    }

    private String schemeName;
    private int plainTextModulus;
    private int ringSize;
    private SecurityLevel securityLevel;

    public ProxyReEncryptionModel(String schemeName, int plainTextModulus, int ringSize, SecurityLevel securityLevel) {
        this.schemeName = schemeName;
        this.plainTextModulus = plainTextModulus;
        this.ringSize = ringSize;
        this.securityLevel = securityLevel;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public int getPlainTextModulus() {
        return plainTextModulus;
    }

    public void setPlainTextModulus(int plainTextModulus) {
        this.plainTextModulus = plainTextModulus;
    }

    public int getRingSize() {
        return ringSize;
    }

    public void setRingSize(int ringSize) {
        this.ringSize = ringSize;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }


}

/*
1.Enums:
The class declares an enum SecurityLevel representing different security levels (128, 192, 256 bits).

2.Fields:
-The class has several private fields:
        -schemeName: Represents the name of a cryptographic scheme.
        -plainTextModulus: Represents the modulus for plaintext in the cryptographic scheme.
        -ringSize: Represents the ring size in the cryptographic scheme.
        -securityLevel: Represents the security level, chosen from the SecurityLevel enum.

3.Constructor:
-The class has a parameterized constructor that allows initializing the fields when creating an instance of the class.

4.Getter and Setter Methods:
-The class provides getter and setter methods for each field, allowing access and modification of the object's state.

**Summary**:
The ProxyReEncryptionModel class is a data model representing the parameters related to proxy re-encryption, including the cryptographic
scheme name, plaintext modulus, ring size, and security level. It encapsulates these parameters within a Java object, making it easier to
manage and pass around in the codebase. This type of model class is common in software systems to organize and structure data in a
meaningful way.

*/
