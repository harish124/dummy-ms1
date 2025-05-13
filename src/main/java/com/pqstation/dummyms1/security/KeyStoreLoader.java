package com.pqstation.dummyms1.security;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

@Configuration
public class KeyStoreLoader {

    @Value("${keystore.path}")
    private Resource keystoreResource;

    @Value("${keystore.password}")
    private String keystorePassword;

    @Value("${truststore.path}")
    private String truststorePath;

    @Value("${truststore.password}")
    private String truststorePassword;

    @Value("${keystore.key-password}")
    private String keyPassword;

    @Value("${keystore.alias}")
    private String keyAlias;

    private PrivateKey privateKey;
    private X509Certificate certificate;

    /**
     * @param keyStoreType - JKS / PKCS12
     * @param provider     -e.g BC
     * @param tlsProtocol  -e.g TLSv1.3
     * @return
     * @throws Exception
     */
//    @Bean
    public SSLContext loadKeystore(final String keyStoreType,
                                   final String provider,
                                   final String tlsProtocol) throws Exception {

        System.out.println("Reached Security class");
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());

        KeyStore keyStore = KeyStore.getInstance(keyStoreType, provider); // Or "BCFKS" if you're using that
//        KeyStore keyStore = KeyStore.getInstance("JKS"); // Or "BCFKS" if you're using that
        try (InputStream is = keystoreResource.getInputStream()) {
            keyStore.load(is, keystorePassword.toCharArray());
            System.out.println("keystore loaded successfully");
        }

        // Initialize KeyManagerFactory for the Keystore
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        System.out.println("Number of key managers: " + keyManagers.length);
        for (KeyManager km : keyManagers) {
            System.out.println("KeyManager: " + km.getClass().getName());
        }

        TrustManagerFactory trustManagerFactory = loadTrustStore();

        // Create the SSLContext
        var sslContext = SSLContext.getInstance(tlsProtocol);  // or TLSv1.2
        if (trustManagerFactory != null) {
            sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
        } else {
            sslContext.init(keyManagers, null, null);
        }


        this.privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
        this.certificate = (X509Certificate) keyStore.getCertificate(keyAlias);

        System.out.println("Loaded key: " + privateKey.getAlgorithm());
        System.out.println("Certificate: " + certificate);

        return sslContext;
    }

    private TrustManagerFactory loadTrustStore() {
        try {
            // Load Truststore (you can also configure it programmatically if you need to)
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream trustStoreStream = new FileInputStream(truststorePath)) {
                trustStore.load(trustStoreStream, truststorePassword.toCharArray());
            }

            // Initialize TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            return trustManagerFactory;
        } catch (Exception e) {
            System.out.println("Error while loading truststore = " + e.getMessage());
            return null;
        }

    }
}
