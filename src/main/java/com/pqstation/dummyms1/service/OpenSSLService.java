package com.pqstation.dummyms1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class OpenSSLService {

    @Value("${my.server.crt}")
    private String serverCert;

    @Value("${my.server.key}")
    private String serverKey;

    public Flux<String> startOpenSSLServer() {
        String[] command = {
                "openssl", "s_server",
                "-key", serverKey,
                "-cert", serverCert,
                "-groups", "X25519MLKEM768"
        };

        return Flux.<String>create(sink -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                // This loop runs in background thread managed by boundedElastic
                while ((line = reader.readLine()) != null) {
                    sink.next(line);
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    sink.error(new RuntimeException("OpenSSL server exited with code: " + exitCode));
                } else {
                    System.out.println("Connection Established");
                    sink.complete();
                }

            } catch (Exception e) {
                sink.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic()); // Run on non-blocking scheduler
    }
}

