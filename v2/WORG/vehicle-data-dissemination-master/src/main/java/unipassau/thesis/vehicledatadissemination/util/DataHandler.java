package unipassau.thesis.vehicledatadissemination.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    public static void writer(String xmlPolicyPath, String ciphertext){
        byte[] ct = null;
        try {
            FileInputStream read = new FileInputStream(new File(ciphertext));
            ct = read.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(ciphertext)))){
            out.write(Encoder.xmlToHash(xmlPolicyPath));
            System.out.println("hash is :  " + Encoder.bytesToHex(Encoder.xmlToHash(xmlPolicyPath)));
            out.write(ct);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, byte[]> read(byte[] stickydata) throws NoSuchAlgorithmException {
        Map<String, byte[]> stickydocument = new HashMap<>();
        byte[] hash = Arrays.copyOfRange(stickydata, 0,  MessageDigest.getInstance(Encoder.hashAlgorithm).getDigestLength());
        byte[] data = Arrays.copyOfRange(stickydata,  MessageDigest.getInstance(Encoder.hashAlgorithm).getDigestLength(), stickydata.length);
        stickydocument.put("hash", hash);
        stickydocument.put("data", data);
        return stickydocument;


    }
}

/*
1.writer Method:
-Takes two parameters: xmlPolicyPath (path to an XML policy file) and ciphertext (path to a ciphertext file).
-Reads the content of the ciphertext file into a byte array (ct).
-Writes the hash of the XML policy file and the ciphertext data into the same ciphertext file.
-Uses Encoder.xmlToHash to obtain the hash of the XML policy file.

2.read Method:
-Takes a byte array stickydata as a parameter.
-Extracts the hash and data from the stickydata byte array.
-Returns a Map containing the hash and data with corresponding keys ("hash" and "data").

**Summary**
The DataHandler class facilitates writing and reading data in a specific format. It is designed to handle a scenario where data
consists of a hash (presumably representing a policy) followed by actual data, and it provides methods to perform these operations.
The methods in this class are likely used in the broader context of the application to manipulate and process data in a specific way.
 */