package com.example.application.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BackendService {

    public String fetchSlowly() {
        try {
            return fetchSlowlyAsync().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<String> fetchSlowlyAsync() {
        return CompletableFuture.supplyAsync(() -> {
            sleepFor(Duration.ofSeconds(3));
            return "I'm a response from a slow operation!";
        });
    }

    private void sleepFor(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
