<template>

<div class="player-info">
  <div v-if="player && enemy" class="player-details">
    <h1>{{ matches[0].firstPlayerInfo[3] == player.teamid ? player.name : enemy.name }}</h1>
    <img :src="getPlayerPhotoUrl(matches[0].firstPlayerInfo[3] == player.teamid ? player.teamid : enemy.teamid)" alt="Zdjęcie zawodnika" />
    <p><strong>Kraj:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? player.country : enemy.country }}</p>
    <p><strong>Ranking:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? player.ranking : enemy.ranking }}</p>
    <p><strong>Punkty:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? player.points : enemy.points }}</p>
    <button v-if="matches[0].firstPlayerInfo[3] == player.teamid" @click="goBack">Wróć do profilu zawodnika</button>
  </div>

  <div v-if="player && !enemy" class="player-details">
      <h1>{{ player.name }}</h1>
      <img :src="getPlayerPhotoUrl(player.teamid)" alt="Zdjęcie zawodnika" />
      <p><strong>Kraj:</strong> {{ player.country }}</p>
      <p><strong>Ranking:</strong> {{ player.ranking }}</p>
      <p><strong>Punkty:</strong> {{ player.points }}</p>
      <button @click="goBack">Wróć do profilu zawodnika</button>
    </div>

    <div class="match-table-container">
      <table v-if="matches.length > 0 && matches[0]" class="match-table">
        <tr>
          <td><b>Turniej</b></td>
          <td>{{ matches[0].nameOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Ranga</b></td>
          <td>{{ matches[0].rankOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Nawierzchnia</b></td>
          <td>{{ matches[0].surface }}</td>
        </tr>
        <tr>
          <td><b>Runda</b></td>
          <td>{{ matches[0].round }}</td>
        </tr>
        <tr>
          <td><b>Status</b></td>
          <td>{{ matches[0].status }}</td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td>Set 1</td>
          <td>Set 2</td>
          <td v-if="matches[0].firstPlayerScore[2] != 0 && matches[0].secondPlayerScore[2] != 0">Set 3</td>
          <td v-if="matches[0].firstPlayerScore[3] != 0 && matches[0].secondPlayerScore[3] != 0">Set 4</td>
          <td v-if="matches[0].firstPlayerScore[4] != 0 && matches[0].secondPlayerScore[4] != 0">Set 5</td>
          <td v-if="matches[0].gamePoints">Punkty</td>
        </tr>
        <tr :class="{ 'winner': matches[0].winner === 1 }" style="height: 50px;">
          <td><img v-if="matches[0].service && matches[0].service == 1" src="@/assets/ball.jpg" alt="serwis" style="width: 25px; height: 25px;"></td>
          <td>{{ matches[0].firstPlayerInfo[0] + " (" + matches[0].firstPlayerInfo[2] + "), " + matches[0].firstPlayerInfo[1] }}</td>
          <td>{{ matches[0].firstPlayerScore[0] }}</td>
          <td>{{ matches[0].firstPlayerScore[1] }}</td>
          <td v-if="matches[0].firstPlayerScore[2] != 0 && matches[0].secondPlayerScore[2] != 0">{{ matches[0].firstPlayerScore[2] }}</td>
          <td v-if="matches[0].firstPlayerScore[3] != 0 && matches[0].secondPlayerScore[3] != 0">{{ matches[0].firstPlayerScore[3] }}</td>
          <td v-if="matches[0].firstPlayerScore[4] != 0 && matches[0].secondPlayerScore[4] != 0">{{ matches[0].firstPlayerScore[4] }}</td>
          <td v-if="matches[0].gamePoints">{{ matches[0].gamePoints[0] }}</td>
        </tr>
        <tr :class="{ 'winner': matches[0].winner === 2 }" style="height: 50px;">
          <td><img v-if="matches[0].service && matches[0].service == 2" src="@/assets/ball.jpg" alt="serwis" style="width: 25px; height: 25px;"></td>
          <td>{{ matches[0].secondPlayerInfo[0] + " (" + matches[0].secondPlayerInfo[2] + "), " + matches[0].secondPlayerInfo[1] }}</td>
          <td>{{ matches[0].secondPlayerScore[0] }}</td>
          <td>{{ matches[0].secondPlayerScore[1] }}</td>
          <td v-if="matches[0].firstPlayerScore[2] != 0 && matches[0].secondPlayerScore[2] != 0">{{ matches[0].secondPlayerScore[2] }}</td>
          <td v-if="matches[0].firstPlayerScore[3] != 0 && matches[0].secondPlayerScore[3] != 0">{{ matches[0].secondPlayerScore[3] }}</td>
          <td v-if="matches[0].firstPlayerScore[4] != 0 && matches[0].secondPlayerScore[4] != 0">{{ matches[0].secondPlayerScore[4] }}</td>
          <td v-if="matches[0].gamePoints">{{ matches[0].gamePoints[1] }}</td>
        </tr>
      </table>
    </div>

    <div v-if="player && enemy" class="player-details">
      <h1>{{ matches[0].firstPlayerInfo[3] == player.teamid ? enemy.name : player.name }}</h1>
      <img :src="getPlayerPhotoUrl(matches[0].firstPlayerInfo[3] == player.teamid ? enemy.teamid : player.teamid)" alt="Zdjęcie zawodnika" />
      <p><strong>Kraj:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? enemy.country : player.country }}</p>
      <p><strong>Ranking:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? enemy.ranking : player.ranking }}</p>
      <p><strong>Punkty:</strong> {{ matches[0].firstPlayerInfo[3] == player.teamid ? enemy.points : player.points }}</p>
      <button v-if="matches[0].firstPlayerInfo[3] == enemy.teamid" @click="goBack">Wróć do profilu zawodnika</button>
  </div>
</div>

    <div class="match-stats-container">
      <h1>Statystyki meczu</h1>
      <table v-if="matchStats.length === 2" class="match-stats-table">
        <thead>
          <tr>
            <th>{{ matches[0].firstPlayerInfo[0] }}</th>
            <th>Statystyka</th>
            <th>{{ matches[0].secondPlayerInfo[0] }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ matchStats[0].aces }}</td>
            <td>Asy</td>
            <td>{{ matchStats[1].aces }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].doubleFaults }}</td>
            <td>Podwójne błędy</td>
            <td>{{ matchStats[1].doubleFaults }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].firstService }}</td>
            <td>Trafiony 1-szy serwis</td>
            <td>{{ matchStats[1].firstService }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].firstServicePointsWon }}</td>
            <td>Punkty wygrane po 1-szym serwisie</td>
            <td>{{ matchStats[1].firstServicePointsWon }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].secondServicePointsWon }}</td>
            <td>Punkty wygrane po 2-gim serwisie</td>
            <td>{{ matchStats[1].secondServicePointsWon }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].forhendWinners }}</td>
            <td>Winnery (forhend)</td>
            <td>{{ matchStats[1].forhendWinners }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].bekhendWinners }}</td>
            <td>Winnery (bekhend)</td>
            <td>{{ matchStats[1].bekhendWinners }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].forhendUnforcedErrors }}</td>
            <td>Niewymuszone błędy (forhend)</td>
            <td>{{ matchStats[1].forhendUnforcedErrors }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].bekhendUnforcedErrors }}</td>
            <td>Niewymuszone błędy (bekhend)</td>
            <td>{{ matchStats[1].bekhendUnforcedErrors }}</td>
          </tr>
          <tr>
            <td>{{ matchStats[0].breakPointsConverted }}</td>
            <td>Wykorzystane break pointy</td>
            <td>{{ matchStats[1].breakPointsConverted }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else>Ładowanie statystyk meczu...</p>
    </div>
  </template>
  
  <script>
  import "../css/matchStats.css"

  export default {
    data() {
      return {
        matchStats: [],
        matches: [],
        player: null,
        enemy: null
      };
    },
    async created() {
      const matchId = this.$route.params.id;
      this.matches = JSON.parse(localStorage.getItem('matches'));
      this.player = JSON.parse(localStorage.getItem('player'));

      await this.fetchMatchStats(matchId);
      await this.fetchEnemyPlayer();
      await this.checkPhotoExists();
    },
    methods: {
      goBack() {
        this.$router.go(-1);  // Przejście do poprzedniej strony
      },
      async fetchMatchStats(matchId) {
        try {
            const response = await fetch(`http://localhost:8080/getMatchStats/${matchId}`, {
            method: 'GET',
        });
        if (response.ok) {
          this.matchStats = await response.json();
        } else {
          console.error('Błąd podczas pobierania statystyk.');
        }
        } catch (error) {
          console.error("Błąd podczas pobierania statystyk:", error);
        }
      },
      getPlayerPhotoUrl(teamid) {
      try {
        return require(`@/assets/playerPhotos/${teamid}.png`);
      } catch (error) {
        return require('@/assets/playerPhotos/placeholder.png');
      }
    },
    async fetchEnemyPlayer() {
      let response = null;
      try {
        if (this.matches[0].firstPlayerInfo[3] == this.player.teamid) {
          response = await fetch(`http://localhost:8080/getPlayer/${this.matches[0].secondPlayerInfo[3]}`, {
          method: 'GET',
        });
        }
        else {
          response = await fetch(`http://localhost:8080/getPlayer/${this.matches[0].firstPlayerInfo[3]}`, {
          method: 'GET',
        });
        }
        
        if (response.ok) {
          this.enemy = await response.json();
        } else {
          console.error('Błąd podczas pobierania danych zawodnika.');
        }
      } catch (error) {
        console.error('Błąd:', error);
      }
    },
    async checkPhotoExists() {
        if (this.enemy && this.enemy.teamid) {
          try {
          const response = await fetch(`http://localhost:8080/player/photoExists/${this.enemy.teamid}`);
          const exists = await response.json();
          if (exists) {
            this.photoExists = true;
            this.photoUrl = `src/assets/playerPhotos/${this.enemy.teamid}.png`;
          } else {
            await this.fetchPhotoFromAPI();  // Pobierz zdjęcie z API, jeśli nie istnieje
          }
        } catch (error) {
          console.error('Błąd podczas sprawdzania zdjęcia:', error);
        }
        }
      },
      async fetchPhotoFromAPI() {
        if (this.enemy && this.enemy.teamid) {
          try {
          const response = await fetch(`http://localhost:8080/player/fetchPhoto/${this.enemy.teamid}`, {
            method: 'POST'
          });
          if (response.ok) {
            this.photoExists = true;
            this.photoUrl = `src/assets/playerPhotos/${this.enemy.teamid}.png`;
          }
        } catch (error) {
          console.error('Błąd podczas pobierania zdjęcia z API:', error);
        }
        }
      }
    }
  };
  </script>