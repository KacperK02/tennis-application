package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @Value("${app.cache.strategy:http}")
    private String cacheStrategy;

    @GetMapping("/getAllWTAPlayers")
    public ResponseEntity<List<Player>> getWTAPlayers(){
        if ("server".equalsIgnoreCase(cacheStrategy)) {
            // ==========================================
            // STRATEGIA 1: CAFFEINE (Server-side)
            // ==========================================

            // Pobieramy gotową, posortowaną listę prosto z pamięci RAM serwera (Caffeine)
            List<Player> cachedPlayers = playerService.getSortedWTAPlayersCached();

            // Zmuszamy przeglądarkę, by nie cache'owała, żeby testy Caffeine były wiarygodne
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore().mustRevalidate())
                    .body(cachedPlayers);

        } else {
            // ==========================================
            // STRATEGIA 2: HTTP CACHE (Browser-side)
            // ==========================================

            // Pobieramy standardowo z bazy (lub zwykłego serwisu) i sortujemy "w locie"
            List<Player> players = playerService.getAllWTAPlayers();
            players.sort(Comparator.comparingInt(Player::getRanking));

            LocalDateTime now = LocalDateTime.now();
            CacheControl cacheControl;

            boolean isUpdateWindow = now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() < 14;

            if (isUpdateWindow) {
                cacheControl = CacheControl.noCache();
            } else {
                LocalDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                        .withHour(0).withMinute(0).withSecond(0);

                long secondsUntilNextMonday = Duration.between(now, nextMonday).getSeconds();
                cacheControl = CacheControl.maxAge(secondsUntilNextMonday, TimeUnit.SECONDS).cachePublic();
            }

            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(players);
        }
    }


    @GetMapping("/getAllATPPlayers")
    public ResponseEntity<List<Player>> getATPPlayers() {
        if ("server".equalsIgnoreCase(cacheStrategy)) {
            // ==========================================
            // STRATEGIA 1: CAFFEINE (Server-side)
            // ==========================================

            // Pobieramy gotową, posortowaną listę prosto z pamięci RAM serwera (Caffeine)
            List<Player> cachedPlayers = playerService.getSortedATPPlayersCached();

            // Zmuszamy przeglądarkę, by nie cache'owała, żeby testy Caffeine były wiarygodne
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore().mustRevalidate())
                    .body(cachedPlayers);

        } else {
            // ==========================================
            // STRATEGIA 2: HTTP CACHE (Browser-side)
            // ==========================================

            // Pobieramy standardowo z bazy (lub zwykłego serwisu) i sortujemy "w locie"
            List<Player> players = playerService.getAllATPPlayers();
            players.sort(Comparator.comparingInt(Player::getRanking));

            LocalDateTime now = LocalDateTime.now();
            CacheControl cacheControl;

            boolean isUpdateWindow = now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() < 14;

            if (isUpdateWindow) {
                cacheControl = CacheControl.noCache();
            } else {
                LocalDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                        .withHour(0).withMinute(0).withSecond(0);

                long secondsUntilNextMonday = Duration.between(now, nextMonday).getSeconds();
                cacheControl = CacheControl.maxAge(secondsUntilNextMonday, TimeUnit.SECONDS).cachePublic();
            }

            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(players);
        }
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable int id) {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getPlayer/{teamid}")
    public ResponseEntity<Player> getPlayerByTeamId(@PathVariable int teamid) {
        Optional<Player> playerOptional = playerService.getPlayerByTeamId(teamid);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/getMatches")
    public ResponseEntity<List<Match>> getPlayerMatches(@PathVariable int id) throws JsonProcessingException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerNearEvent(String.valueOf(player.getTeamid())); // pobieranie danych o meczach z RapidAPI
            List<Match> matches = new ArrayList<>();
            Match lastMatch = playerService.getPlayerMatch(response, "previous"); // informacje o poprzednim meczu
            Match nextMatch = playerService.getPlayerMatch(response, "next"); // informacje o następnym meczu
            matches.add(lastMatch);
            matches.add(nextMatch);
            return ResponseEntity.ok(matches);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/lastTournaments")
    public ResponseEntity<List<Tournament>> getPlayerLastTournaments(@PathVariable int id) throws IOException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerLastTournaments(String.valueOf(player.getTeamid()));
            List <Tournament> tournaments = playerService.getPlayerTournaments(response);
            return ResponseEntity.ok(tournaments);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/player/photo/{teamID}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPlayerPhoto(@PathVariable String teamID) {

        if ("server".equalsIgnoreCase(cacheStrategy)) {
            // ==========================================
            // STRATEGIA 1: CAFFEINE (Server-side)
            // ==========================================
            byte[] imageBytes = playerService.getPlayerPhotoCached(teamID);

            // Zmuszamy przeglądarkę do każdorazowego odpytania serwera,
            // aby zmierzyć rzeczywisty czas odpowiedzi Caffeine w badaniach.
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore().mustRevalidate())
                    .body(imageBytes);

        } else {
            // ==========================================
            // STRATEGIA 2: HTTP CACHE (Browser-side)
            // ==========================================
            byte[] imageBytes = playerService.getPlayerPhotoUncached(teamID);

            // Klasyczny cache po stronie przeglądarki na 365 dni
            CacheControl cacheControl = CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic();

            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(imageBytes);
        }
    }
}
