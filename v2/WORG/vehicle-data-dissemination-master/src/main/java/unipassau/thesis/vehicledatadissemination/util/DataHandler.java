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
