<template>
  <div v-if="player" class="player-info">
    <!-- Sekcja z danymi zawodnika -->
    <div class="player-details">
      <h1>{{ player.name }}</h1>
      <img :src="getPlayerPhotoUrl(player.teamid)" alt="Zdjęcie zawodnika" />
      <p><strong>Kraj:</strong> {{ player.country }}</p>
      <p><strong>Ranking:</strong> {{ player.ranking }}</p>
      <p><strong>Punkty:</strong> {{ player.points }}</p>
      <button @click="stopFollow">Przestań obserwować</button>
    </div>

    <div class="match-table-container">
      <h2>Ostatni mecz</h2>
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
      <button @click="checkMatchStats">Sprawdź statystyki</button>
    </div>
  </div>
  <div v-else>
    <p>Ładowanie danych zawodnika...</p>
  </div>

  <div style="margin-top: 40px;"></div>

  <div class="player-info">
  <div class="last-tournaments-container">
      <h2>Wyniki w ostatnich turniejach</h2>
      <table v-if="lastTournaments" class="tournaments-table">
        <thead>
          <tr>
            <th>Nazwa turnieju</th>
            <th>Ranga</th>
            <th>Runda</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(tournament, index) in lastTournaments" :key="index">
            <td>{{ tournament.name }}</td>
            <td>{{ tournament.rank }}</td>
            <td>{{ tournament.round }}</td>
          </tr>
        </tbody>
      </table>
  </div>

  <div class="match-table-container">
    <h2>Następny mecz</h2>
    <table v-if="matches.length > 0 && matches[1]" class="match-table">
      <tr>
          <td><b>Turniej</b></td>
          <td>{{ matches[1].nameOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Ranga</b></td>
          <td>{{ matches[1].rankOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Nawierzchnia</b></td>
          <td>{{ matches[1].surface }}</td>
        </tr>
        <tr>
          <td><b>Runda</b></td>
          <td>{{ matches[1].round }}</td>
        </tr>
        <tr>
          <td><b>Status</b></td>
          <td>{{ matches[1].status }}</td>
        </tr>
        <tr>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>{{ matches[1].firstPlayerInfo[0] + " (" + matches[1].firstPlayerInfo[2] + "), " + matches[1].firstPlayerInfo[1] }}</td>
        </tr>
        <tr>
          <td></td>
          <td>VS</td>
        </tr>
        <tr>
          <td></td>
          <td>{{ matches[1].secondPlayerInfo[0] + " (" + matches[1].secondPlayerInfo[2] + "), " + matches[1].secondPlayerInfo[1] }}</td>
        </tr>
    </table>
    <p v-else>Niezaplanowany</p>
  </div>
</div>

</template>

  
<script>
import '../css/playerDetails.css'

export default {
  props: {
    id: String
  },
  data() {
    return {
      player: null,
      matches: [],
      lastTournaments: null
    };
  },
  mounted() {
    this.fetchPlayerDetails();
    this.fetchMatches();
    this.fetchLastTournaments();
  },
  methods: {
    getPlayerPhotoUrl(teamid) {
      try {
        return require(`@/assets/playerPhotos/${teamid}.png`);
      } catch (error) {
        return require('@/assets/playerPhotos/placeholder.png');
      }
    },
    async fetchPlayerDetails() {
      try {
        const response = await fetch(`http://localhost:8080/player/${this.id}`, {
          method: 'GET',
          credentials: 'include',
        });
        if (response.ok) {
          this.player = await response.json();
        } else {
          console.error('Błąd podczas pobierania danych zawodnika.');
        }
      } catch (error) {
        console.error('Błąd:', error);
      }
    },
    async fetchMatches() {
      try {
        const response = await fetch(`http://localhost:8080/player/${this.id}/getMatches`, {
          method: 'GET',
          credentials: 'include',
        });
        if (response.ok) {
          this.matches = await response.json();
        } else {
          console.error('Błąd podczas pobierania danych ostatniego meczu.');
        }
      } catch (error) {
        console.error('Błąd:', error);
      }
    },
    async fetchLastTournaments() {
      try {
        const response = await fetch(`http://localhost:8080/player/${this.id}/lastTournaments`, {
          method: 'GET',
          credentials: 'include',
        });
        if (response.ok) {
          this.lastTournaments = await response.json();
        } else {
          console.error('Błąd podczas pobierania danych ostatniego meczu.');
        }
      } catch (error) {
        console.error('Błąd:', error);
      }
    },
    stopFollow() {
      fetch(`http://localhost:8080/follow/deleteFollow/${this.player.playerID}`, {
        method: 'DELETE',
        credentials: 'include'
      })
      .then(response => {
        if (response.ok) {
          this.$router.push('/account');
        } else {
          console.error('Błąd podczas usuwania zawodnika z obserwowanych.');
        }
      })
      .catch(error => {
        console.error('Błąd:', error);
      });
    },
    checkMatchStats() {
      localStorage.setItem('matches', JSON.stringify(this.matches));
      localStorage.setItem('player', JSON.stringify(this.player));
      this.$router.push(`/match/${this.matches[0].id}`);
    }
  }
};
</script>

  