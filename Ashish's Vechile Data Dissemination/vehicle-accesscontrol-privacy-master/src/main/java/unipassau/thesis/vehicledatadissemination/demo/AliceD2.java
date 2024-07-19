package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AliceD2 {

        private static Logger LOG = LoggerFactory.getLogger(AliceDec.class);

        public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
        public static String dataFolder = System.getProperty("user.dir") + "/data/";
        public static String privateKey = cryptoFolder + "alice-private-key";
        public static int count = 99; // need to make dynamic

        public static void main(String[] args) {
            try {
                LOG.info("Starting decryption for file: " + dataFolder + count);
                String decryptedFilePathStr = dataFolder + count;

                // Log the size of the file being decrypted
                File fileToDecrypt = new File(decryptedFilePathStr);
                LOG.info("Size of file to decrypt: " + fileToDecrypt.length() + " bytes");

                // Perform decryption
                String res = OpenPRE.INSTANCE.decrypt(privateKey, decryptedFilePathStr);
                LOG.info("Decrypted response length: " + res.length());
                LOG.info("Decrypted response content: " + res);

                // Write the decrypted message to a new .txt file
                Path decryptedFilePath = Paths.get(dataFolder, "decrypted_message_" + count + ".txt");
                Files.writeString(decryptedFilePath, res);
                LOG.info("Decrypted message written to file: " + decryptedFilePath);

                // Read the message from the newly created .txt file
                String decryptedMessage = Files.readString(decryptedFilePath);
                JSONObject jsonObject = new JSONObject(decryptedMessage);
                System.out.print(jsonObject.toString(4) + " ");
            } catch (IOException e) {
                LOG.error("IOException occurred", e);
                throw new NullPointerException();
            } catch (Exception e) {
                LOG.error("Exception occurred", e);
                throw new RuntimeException(e);
            }
        }
    }


