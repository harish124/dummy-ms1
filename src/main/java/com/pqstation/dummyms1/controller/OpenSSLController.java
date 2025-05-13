package com.pqstation.dummyms1.controller;

import com.pqstation.dummyms1.service.OpenSSLService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;

@RestController
@RequiredArgsConstructor
public class OpenSSLController {

    @Value("${my.server.crt}")
    private String serverCert;

    @Value("${my.server.key}")
    private String serverKey;

    private final OpenSSLService openSSLService;


    @GetMapping("/start-openssl-service")
    public Flux<String> startServer() {
        return openSSLService.startOpenSSLServer();
    }

    @GetMapping("/start-openssl-server")
    public Mono<String> startOpenSSLServer() {
        return Mono.fromCallable(() -> {
                    // Define the OpenSSL server command
                    String[] command = {
                            "openssl", "s_server",
                            "-key", serverKey,
                            "-cert", serverCert,
                            "-groups", "X25519MLKEM768"
                    };
//                    command = new String[]{
//                            "openssl", "version"
//                    };

                    // Execute the OpenSSL server command
                    return executeOpenSSLCommand(command);
                })
                .doOnNext(data->{
                    System.out.println("Got a connection");
                })
                .subscribeOn(Schedulers.boundedElastic()) // Run the command on a separate thread pool
                .doOnTerminate(() -> System.out.println("OpenSSL server has finished execution."));
    }

    private String executeOpenSSLCommand(String[] command) {
        StringBuilder output = new StringBuilder();
        try {
            var processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            // Provide the password to OpenSSL via stdin
//            OutputStream os = process.getOutputStream();
//            String password = "changeit";  // replace with the actual password
//            os.write((password + "\n").getBytes());
//            os.flush();
//            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("OpenSSL server failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error occurred while executing OpenSSL server: " + e.getMessage();
        }
        return output.toString();
    }
}
