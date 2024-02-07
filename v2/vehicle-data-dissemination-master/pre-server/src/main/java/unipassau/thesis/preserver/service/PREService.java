package unipassau.thesis.preserver.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipassau.thesis.preserver.util.OpenPRE;

import java.io.*;
import java.util.HashMap;

@Service
public class PREService {

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    private HashMap<String, String> reEncryptionKeysMap;

    public byte[] reEncryptionService(byte[] encryptedData, String subjectID) throws IOException {

        String tempFileName = RandomStringUtils.randomAlphanumeric(12);
        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(env.getProperty("app.tmpFolder") + tempFileName, true)))){

            out.write(encryptedData);

        } catch (IOException e) {
            e.printStackTrace();
        }

        OpenPRE.INSTANCE.reEncrypt(env.getProperty("app.tmpFolder") + tempFileName,
                env.getProperty("app.reEncKeysFolder") + reEncryptionKeysMap.get(subjectID),
                env.getProperty("app.tmpFolder") + tempFileName + "-re");
        FileInputStream read = new FileInputStream(new File(env.getProperty("app.tmpFolder") + tempFileName + "-re"));

        return read.readAllBytes();
    }
}
