package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getAllWTAPlayers")
    public ResponseEntity<List<Player>> getWTAPlayers(){
        List <Player> players = playerService.getAllWTAPlayers(); // pobierz z bazy danych wszystkie tenisistki
        players.sort(Comparator.comparingInt(Player::getRanking)); // sortowanie po pozycji w rankingu

        //CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic();
        //return ResponseEntity.ok().cacheControl(cacheControl).body(players);

        LocalDateTime now = LocalDateTime.now();
        CacheControl cacheControl;

        // Definiujemy okno aktualizacji: Poniedziałek od 00:00 do 14:00 (możesz dostosować godzinę)
        boolean isUpdateWindow = now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() < 14;

        if (isUpdateWindow) {
            // Poniedziałek rano: nie ufamy cache'owi w ciemno.
            // Zmuszamy przeglądarkę do zapytania serwera i użycia ETaga.
            cacheControl = CacheControl.noCache();
        } else {
            // Reszta tygodnia: ranking na pewno się nie zmieni.
            // Obliczamy czas (w sekundach) do następnego poniedziałku o 00:00.
            LocalDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);

            long secondsUntilNextMonday = Duration.between(now, nextMonday).getSeconds();

            // Mówimy przeglądarce: "Nie pytaj mnie o nic przez tyle sekund"
            cacheControl = CacheControl.maxAge(secondsUntilNextMonday, TimeUnit.SECONDS).cachePublic();
        }

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(players);
    }


    @GetMapping("/getAllATPPlayers")
    public ResponseEntity<List<Player>> getATPPlayers() {
        List<Player> players = playerService.getAllATPPlayers(); // pobierz z bazy danych wszystkich tenisistów
        players.sort(Comparator.comparingInt(Player::getRanking)); // sortowanie po pozycji w rankingu
        //CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic();
        //return ResponseEntity.ok().cacheControl(cacheControl).body(players);

        LocalDateTime now = LocalDateTime.now();
        CacheControl cacheControl;

        // Definiujemy okno aktualizacji: Poniedziałek od 00:00 do 14:00 (możesz dostosować godzinę)
        boolean isUpdateWindow = now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() < 14;

        if (isUpdateWindow) {
            // Poniedziałek rano: nie ufamy cache'owi w ciemno.
            // Zmuszamy przeglądarkę do zapytania serwera i użycia ETaga.
            cacheControl = CacheControl.noCache();
        } else {
            // Reszta tygodnia: ranking na pewno się nie zmieni.
            // Obliczamy czas (w sekundach) do następnego poniedziałku o 00:00.
            LocalDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);

            long secondsUntilNextMonday = Duration.between(now, nextMonday).getSeconds();

            // Mówimy przeglądarce: "Nie pytaj mnie o nic przez tyle sekund"
            cacheControl = CacheControl.maxAge(secondsUntilNextMonday, TimeUnit.SECONDS).cachePublic();
        }

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(players);
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

    // Ustawiamy produces na typ obrazka, np. image/png lub image/jpeg
    @GetMapping(value = "/player/photo/{teamID}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPlayerPhoto(@PathVariable String teamID) {

        APIConnection apiConnection = new APIConnection();

        byte[] imageBytes = apiConnection.getPlayerPhoto(teamID);

        CacheControl cacheControl = CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic();

        return ResponseEntity.ok().cacheControl(cacheControl).body(imageBytes);
    }
}
