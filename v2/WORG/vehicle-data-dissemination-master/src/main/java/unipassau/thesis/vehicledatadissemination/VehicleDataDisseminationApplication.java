package unipassau.thesis.vehicledatadissemination;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import unipassau.thesis.vehicledatadissemination.model.ProxyReEncryptionModel;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.File;


@SpringBootApplication



public class VehicleDataDisseminationApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory
			.getLogger(VehicleDataDisseminationApplication.class);

	@Value("${app.cryptoFolder}")
	private String cryptoFolder;
	@Value("${app.policyFolder}")
	private String policyFolder;
	@Value("${pre.schemeName}")
	private String schemeName;
	@Value("${pre.plainTextModulus}")
	private int plainTextModulus;
	@Value("${pre.ringSize}")
	private int ringSize;
	@Value("${pre.securityLevel}")
	private ProxyReEncryptionModel.SecurityLevel securityLevel;
	@Value("${user.dir}/tmp/")
	private String tmpFolder;
	@Value("${app.reEncKeysFolder}")
	private String reEncKeysFolder;


	public static void main(String[] args) {
		SpringApplication.run(VehicleDataDisseminationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		File temporaryFolder= new File(tmpFolder);
		temporaryFolder.mkdir();
		FileUtils.cleanDirectory(temporaryFolder);
		System.out.println("HhhhhhhhhhheeeeEEELLllllllllllllllllllllllllloooLOOOOOOOOOOOOOO");
		// temporaryFolder.mkdir(); unable to create tmp folder
		ProxyReEncryptionModel pre = new ProxyReEncryptionModel(schemeName, plainTextModulus, ringSize, securityLevel);


		LOG.info("EXECUTING : Generating crypto context");
		OpenPRE.INSTANCE.cryptoContextGen(pre.getSchemeName(),
				cryptoFolder,
				"cryptocontext",
				256,
				pre.getRingSize(),
				pre.getSecurityLevel().toString());
		LOG.info("EXECUTING : Generating key pairs for Alice, Bob and Carlos");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "alice");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "bob");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "carlos");
		LOG.info("EXECUTING : Generating re encryption keys from Alice to Bob");
		OpenPRE.INSTANCE.reKeyGen(cryptoFolder + "alice-private-key",
				cryptoFolder + "bob-public-key",
				reEncKeysFolder + "alice2bob" );
		LOG.info("EXECUTING : Generating re encryption keys from Alice to Carlos");
		OpenPRE.INSTANCE.reKeyGen(cryptoFolder + "carlos-private-key",
				cryptoFolder + "carlos-public-key",
				reEncKeysFolder + "alice2carlos" );
	}

}

/*
1.@SpringBootApplication:
-Annotation indicating that this class is the main application class of a Spring Boot application.

2.CommandLineRunner Implementation:
-The run method is executed after the Spring application context is initialized. It contains the core logic of the application.

3.Temporary Folder Setup:
-Creates a temporary folder and cleans its contents using FileUtils.cleanDirectory.

4.ProxyReEncryptionModel Instantiation:
-Creates an instance of the ProxyReEncryptionModel with configuration properties.

5.Crypto Context Generation:
-Invokes OpenPRE.INSTANCE.cryptoContextGen to generate the crypto context for the Proxy Re-Encryption (PRE) scheme.

6.Key Pair Generation:
-Invokes OpenPRE.INSTANCE.keysGen to generate key pairs for Alice, Bob, and Carlos.

7.Re-Encryption Key Generation:
-Invokes OpenPRE.INSTANCE.reKeyGen to generate re-encryption keys from Alice to Bob and from Alice to Carlos.

**Summary:
The VehicleDataDisseminationApplication class is the main entry point of the Vehicle Data Dissemination application. This class is
annotated with @SpringBootApplication, indicating that it is a Spring Boot application. Additionally, it implements CommandLineRunner,
which means that the run method will be executed after the Spring application context is initialized.
The VehicleDataDisseminationApplication class is responsible for initializing the Spring Boot application, setting up necessary
 configurations, and generating cryptographic components (crypto context, key pairs, and re-encryption keys) using the OpenPRE library.
 */