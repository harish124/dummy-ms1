package com.pqstation.dummyms1.security;

import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.SslContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import java.util.List;

@Configuration
public class NettySSLConfig {

    @Autowired
    private SSLContext sslContext;

    @Bean
    public NettyServerCustomizer sslNettyCustomizer() {
        return httpServer -> httpServer.secure(sslContextSpec -> {
            sslContextSpec.sslContext(new DelegatingSslContext(sslContext));
        });
    }


    static class DelegatingSslContext extends io.netty.handler.ssl.SslContext {
        private final SSLContext delegate;

        DelegatingSslContext(SSLContext delegate) {
            if (delegate == null) {
                throw new IllegalArgumentException("Provided SSLContext is null!");
            }
            this.delegate = delegate;
        }

        @Override
        public boolean isClient() {
            return false;
        }

        @Override
        public List<String> cipherSuites() {
            return List.of();
        }

        @Override
        public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
            return null;
        }

        @Override
        public SSLEngine newEngine(io.netty.buffer.ByteBufAllocator alloc) {
            SSLEngine sslEngine = delegate.createSSLEngine();
            sslEngine.setUseClientMode(false); // for server mode
            return sslEngine;
        }

        @Override
        public SSLEngine newEngine(io.netty.buffer.ByteBufAllocator alloc, String peerHost, int peerPort) {
            SSLEngine engine = delegate.createSSLEngine(peerHost, peerPort);
            engine.setUseClientMode(false); // for server mode
            return engine;
        }

        @Override
        public SSLSessionContext sessionContext() {
            return null;
        }
    }
}

