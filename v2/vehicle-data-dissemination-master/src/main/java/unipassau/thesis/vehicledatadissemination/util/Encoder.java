package unipassau.thesis.vehicledatadissemination.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {

    public static final String hashAlgorithm = "SHA3-256";

    public static byte[] xmlToHash(String filePath){

        MessageDigest digest = null;
        byte[] encodedhash = null;
        try {
            digest = MessageDigest.getInstance(hashAlgorithm);
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            encodedhash = digest.digest(data);
            return encodedhash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedhash;
    }
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }



}

/*
1.xmlToHash Method:
-Takes a file path (filePath) as a parameter.
-Reads the content of the file specified by the file path.
-Computes the hash (using SHA3-256) of the file content.
-Returns the computed hash.

2.bytesToHex Method:
-Takes a byte array (hash) as a parameter.
-Converts the byte array to a hexadecimal string representation.

3.Hexadecimal Conversion Utility Methods:
 -toDigit: Converts a hexadecimal character to its decimal value.
 -hexToByte: Converts a two-character hexadecimal string to a byte.
 -decodeHexString: Converts a hexadecimal string to a byte array.

**Summary**
The Encoder class provides methods for encoding and decoding data. It includes functionality for hashing file content, converting byte
arrays to hexadecimal strings, and converting hexadecimal strings back to byte arrays. These encoding and decoding operations are commonly
 used in cryptographic and data manipulation tasks.
 */
