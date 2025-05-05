//package com.pqstation.dummyms1.security;
//
//import io.netty.handler.ssl.SslContext;
//import lombok.AllArgsConstructor;
//import org.apache.coyote.http11.AbstractHttp11Protocol;
//
//import org.apache.tomcat.util.net.SSLHostConfig;
//import org.apache.tomcat.util.net.SSLHostConfigCertificate;
//import org.apache.tomcat.util.net.SSLUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.SSLContext;
//import java.security.NoSuchAlgorithmException;
//
//@Configuration
//public class WebServerSSLConfig {
//
//    @Autowired
//    private SSLContext sslContext;
//
//    @Bean
//    public ServletWebServerFactory servletContainer() throws NoSuchAlgorithmException {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//
//        factory.addConnectorCustomizers(connector -> {
//            // Enable SSL and set the necessary attributes
//            connector.setScheme("https");
//            connector.setSecure(true);
//            connector.setPort(8443);
//
//
//            AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol<?>) connector.getProtocolHandler();
//            protocol.setSSLEnabled(true);
//
//            SSLHostConfig sslHostConfig = new SSLHostConfig();
//            SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.UNDEFINED);
//
//
//            certificate.setSslContext((org.apache.tomcat.util.net.SSLContext) sslContext);
//            sslHostConfig.addCertificate(certificate);
//            protocol.addSslHostConfig(sslHostConfig);
//        });
//
//        return factory;
//    }
//}
//
