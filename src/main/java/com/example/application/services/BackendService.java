package com.example.application.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Month;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BackendService {

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

    public record SalesData(
            String productName,
            Map<String, Integer> salesPerMonth,
            Map<String, String> trendPerMonth) {}

    public CompletableFuture<List<SalesData>> generateRandomSalesData() {
        return CompletableFuture.supplyAsync(() -> {
            sleepFor(Duration.ofSeconds(5));
            String[] products = {"Product A", "Product B", "Product C", "Product D", "Product E"};
            List<SalesData> salesDataList = new ArrayList<>();
            Random random = new Random();

            for (String product : products) {
                Map<String, Integer> salesPerMonth = new HashMap<>();
                Map<String, String> trendPerMonth = new HashMap<>();
                int previousSales = random.nextInt(1000);

                for (Month month : Month.values()) {
                    int sales = random.nextInt(1000);
                    salesPerMonth.put(month.name(), sales);

                    if (previousSales > 0) {
                        double changePercentage = Math.abs((sales - previousSales) / (double) previousSales) * 100;
                        String trend = changePercentage < 10 ? "stable" : sales > previousSales ? "rising" : "falling";
                        trendPerMonth.put(month.name(), trend);
                    } else {
                        trendPerMonth.put(month.name(), "stable");
                    }
                    previousSales = sales;
                }

                salesDataList.add(new SalesData(product, salesPerMonth, trendPerMonth));
            }

            return salesDataList;
        });
    }

}
