package com.application.tennisApplication.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ResearchMetricsCollector {

    @Value("${app.cache.algorithm:caffeine}")
    private String currentAlgorithm;

    // Bezpieczne liczniki dla wielowątkowości
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    // Metody do inkrementacji
    public void registerRequest() {
        totalRequests.incrementAndGet();
    }

    public void registerMiss() {
        cacheMisses.incrementAndGet();
    }

    // Zapis do pliku CSV i reset liczników
    public String exportMetricsToFile(String testName) {
        int total = totalRequests.get();
        int misses = cacheMisses.get();
        int hits = total - misses;

        // Zabezpieczenie przed dzieleniem przez zero
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100.0;

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = "wyniki_" + testName + "_" + timestamp + ".csv";

        // Tworzymy linię nagłówka (jeśli plik nie istnieje) i linię z danymi
        String header = "Algorytm;Test;Zapytań Total;Cache Hits;Cache Misses;Hit Rate (%)\n";
        String dataLine = String.format("%s;%s;%d;%d;%d;%.2f\n",
                currentAlgorithm.toUpperCase(), testName, total, hits, misses, hitRate);

        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.writeString(path, header, StandardOpenOption.CREATE);
            }
            Files.writeString(path, dataLine, StandardOpenOption.APPEND);

            // RESET LICZNIKÓW na poczet kolejnego testu!
            totalRequests.set(0);
            cacheMisses.set(0);

            return "Zapisano wyniki do pliku: " + path.toAbsolutePath();
        } catch (IOException e) {
            return "Błąd podczas zapisu pliku: " + e.getMessage();
        }
    }
}