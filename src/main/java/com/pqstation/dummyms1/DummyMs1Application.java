package com.pqstation.dummyms1;

import com.pqstation.dummyms1.security.KeyStoreLoader;
import com.wolfssl.provider.jsse.WolfSSLProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLContext;
import java.security.*;

@SpringBootApplication
public class DummyMs1Application {

    static {
        // 1. Add Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SpringApplication.run(DummyMs1Application.class, args);

        sayHello();

        loadWolfSslProvider();

        changeDefaultJSSEProvider();

        listAllRegisteredProviders();

        callSha3512ByBouncyCastle();

//        callDilithium();

//        printAlgosOfferedByProvider("BCJSSE");
    }

    private static void callSha3512ByBouncyCastle() {
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
    }

    private static void listAllRegisteredProviders() {
        System.out.println("=== Registered JCA Providers ===");
        for (Provider provider : Security.getProviders()) {
            System.out.println(provider.getName() + " - " + provider.getInfo());
        }
    }

    private static void sayHello() {
        String s = " Hello MS1 here";
        System.out.println("s = " + s);
    }

    private static void changeDefaultJSSEProvider() throws NoSuchAlgorithmException {
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 1);
        SSLContext context = SSLContext.getDefault();
        System.out.println("SSLContext provider: " + context.getProvider().getName());
    }

    private static void loadWolfSslProvider() {
        Security.addProvider(new WolfSSLProvider());
        printAlgosOfferedByProvider("wolfJSSE");
    }

    private static void printAlgosOfferedByProvider(final String providerName) {
        Provider bc = Security.getProvider(providerName);
        System.out.println("Reached");
        System.out.println("No. of algos in " + providerName + " = " + (long) bc.size());
        bc.forEach((key, value) -> System.out.println("algoName from " + providerName + " = " + key));
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
