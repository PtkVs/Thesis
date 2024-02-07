package unipassau.thesis.vehicledatadissemination.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface OpenPRE extends Library {
    // Load the C++ OpenPRE library from the system
    OpenPRE INSTANCE = (OpenPRE)
            Native.load((Platform.isLinux() ? "crypto" : "c"  ),
                    OpenPRE.class);
    // Generate the crypto context for our PRE scheme
    void cryptoContextGen(String schemeName,
                          String cryptoFolder,
                          String filename,
                          int plaintextModulus,
                          int ringDimension,
                          String securityLevel);
    // Generate the key pairs
    void keysGen(String cryptoContext, String destinationPath);
    // Encrypt the data
    void  encrypt(String publickey, String plaintext, String destinationPath);
    // Decrypt the data
    String decrypt(String secretKey, String ciphertext);
    // Generate the re-encryption keys
    void reKeyGen(String secretKey, String publicKey, String destinationPath);
    // Re-encrypt the data
    void reEncrypt(String ciphertext, String reEncryptionKey, String destinationPath);
}

/*
1.INSTANCE Field:
-Represents an instance of the OpenPRE interface, allowing access to the native library functions.

2.cryptoContextGen Method:
-Generates the crypto context for the Proxy Re-Encryption (PRE) scheme.
-Parameters include scheme name, folder paths, filename, plaintext modulus, ring dimension, and security level.

3.keysGen Method:
-Generates key pairs for encryption and decryption.
-Parameters include the crypto context and the destination path for storing the keys.

4.encrypt Method:
-Encrypts data using the specified public key and stores the result in the destination path.

5.decrypt Method:
-Decrypts the given ciphertext using the provided secret key.
-Returns the decrypted plaintext.

6.reKeyGen Method:
-Generates re-encryption keys based on the secret key and public key.
-Stores the generated keys in the specified destination path.

7.reEncrypt Method:
-Re-encrypts data using the provided ciphertext and re-encryption key.
-Stores the re-encrypted data in the specified destination path.

**Summary**
The OpenPRE interface acts as a bridge between Java and a native library, providing Java methods that correspond to functions in the
native library responsible for implementing the Proxy Re-Encryption (PRE) scheme. This allows Java code to leverage the functionalities
of the native library for cryptographic operations.
 */