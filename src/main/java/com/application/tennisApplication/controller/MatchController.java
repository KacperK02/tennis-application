package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.cache.ResearchMetricsCollector;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.service.MatchService;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@CrossOrigin
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @Value("${app.cache.strategy:http}")
    private String cacheStrategy;

    private ResearchMetricsCollector metricsCollector;

    public MatchController(ResearchMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping("/getLiveMatches")
    public ResponseEntity<List<Match>> getLiveMatches() throws JsonProcessingException {
        APIConnection apiConnection = new APIConnection();
        String response = apiConnection.getLiveMatches(); // pobieranie wyników aktualnie trwających meczów z RapidAPI
        List<Match> liveMatches = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode node = jsonNode.path("events");

        for (int i = 0; i < node.size(); i++) {
            liveMatches.add(matchService.getMatchInfo(node.path(i))); // wyodrębnianie z odpowiedzi od RapidAPI informacji o poszczególnych meczach
        }

        return ResponseEntity.ok(liveMatches);
    }

    @GetMapping("/getMatchStats/{id}")
    public ResponseEntity<List<HashMap<String, String>>> getMatchStats(@PathVariable int id) throws JsonProcessingException {

        metricsCollector.registerRequest(); // do testów JMetter

        if ("server".equalsIgnoreCase(cacheStrategy)) {
            // ==========================================
            // STRATEGIA 1: CAFFEINE (Server-side)
            // ==========================================

            // Pobieramy gotowe, sparsowane obiekty z pamięci RAM serwera
            List<HashMap<String, String>> stats = matchService.getMatchStatsCached(id);

            // Zmuszamy przeglądarkę do zapytania serwera (na potrzeby testów wydajnościowych)
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore().mustRevalidate())
                    .body(stats);

        } else {
            // ==========================================
            // STRATEGIA 2: HTTP CACHE (Browser-side)
            // ==========================================

            // Wykonujemy pełne zapytanie do API i parsowanie JSON-a za każdym razem,
            // gdy przeglądarka nie ma tego w swoim cache
            List<HashMap<String, String>> stats = matchService.getMatchStatsUncached(id);

            CacheControl cacheControl = CacheControl.maxAge(365, TimeUnit.DAYS).cachePrivate();

            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(stats);
        }
    }

    @GetMapping("/exportMetrics")
    public ResponseEntity<String> exportMetrics(@RequestParam(defaultValue = "Badanie") String testName) {
        String result = metricsCollector.exportMetricsToFile(testName);
        return ResponseEntity.ok(result);
    }
}
