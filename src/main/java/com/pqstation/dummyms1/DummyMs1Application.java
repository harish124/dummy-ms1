package com.pqstation.dummyms1;

import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.security.*;

@SpringBootApplication
public class DummyMs1Application {
	static {
		// 1. Add Bouncy Castle as a security provider
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) {
		SpringApplication.run(DummyMs1Application.class, args);
		String s = " Hello MS1 here";
		System.out.println("s = " + s);

		loadKeystore();

		// 2. List all registered providers (to verify BC is added)
		System.out.println("=== Registered JCA Providers ===");
		for (Provider provider : Security.getProviders()) {
			System.out.println(provider.getName() + " - " + provider.getInfo());
		}

		// 3. Use SHA3-512 which is supported by Bouncy Castle
		System.out.println("\n=== Hash with SHA3-512 (via BC) ===");
        MessageDigest digest = null;
		Signature signature;
        try {
			digest = MessageDigest.getInstance("SHA3-512", "BC");

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        String input = "hello-jca-with-bc";
		byte[] hash = digest.digest(input.getBytes());

		System.out.println("Input: " + input);
		System.out.println("SHA3-512 Hash: " + bytesToHex(hash));

//		callDilithium();

//		getAlgosByBc();
	}

	private static void loadKeystore() {

	}

	private static void getAlgosByBc() {
		Provider bc = Security.getProvider("BC");
		System.out.println("Reached");
		System.out.println("No. of algos in BC = " + (long) bc.entrySet().size());
		bc.forEach((key, value) -> System.out.println("algoName from BC = " + key));
	}


	private static void callDilithium() {
		// Initialize the KeyPairGenerator with the SPHINCS+ algorithm
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("Dilithium2", "BC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        // Generate the SPHINCS+ key pair (public + private key)
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// Retrieve the public and private keys
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		System.out.println("Dilithium Public Key: " + publicKey);
		System.out.println("Dilithium Private Key: " + privateKey);

		String s = "My Custom msg";

	}

	// Helper method to convert byte array to hex string
	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}


}
