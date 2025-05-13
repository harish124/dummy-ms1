//package com.pqstation.dummyms1.service;
//
//import com.pqstation.dummyms1.security.KeyStoreLoader;
//import com.wolfssl.provider.jsse.WolfSSLProvider;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.net.ssl.SSLContext;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.Security;
//
//@Service
//@RequiredArgsConstructor
//public class WolfSSLService {
//    private final KeyStoreLoader keyStoreLoader;
//
//    @PostConstruct
//    public void call() {
//        init();
//    }
//
//    public void init() {
//        try {
//            Security.addProvider(new WolfSSLProvider());
//            var sslContext = keyStoreLoader.loadKeystore("PKCS12", "wolfJSSE", "TLSv1.3");
//
//            for (String suite : sslContext.getSupportedSSLParameters().getCipherSuites()) {
//                System.out.println("WolfSSL Ciphers supported: " + suite);
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
