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

    <!-- Tabela z danymi o ostatnim meczu -->
    <div class="match-table-container">
      <h2>Ostatni mecz</h2>
      <table v-if="lastMatch" class="match-table">
        <tr>
          <td><b>Turniej</b></td>
          <td>{{ lastMatch.nameOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Ranga</b></td>
          <td>{{ lastMatch.rankOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Runda</b></td>
          <td>{{ lastMatch.round }}</td>
        </tr>
        <tr>
          <td><b>Status</b></td>
          <td>{{ lastMatch.status }}</td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td>Set 1</td>
          <td>Set 2</td>
          <td v-if="lastMatch.firstPlayerScore[2] != 0 && lastMatch.secondPlayerScore[2] != 0">Set 3</td>
          <td v-if="lastMatch.firstPlayerScore[3] != 0 && lastMatch.secondPlayerScore[3] != 0">Set 4</td>
          <td v-if="lastMatch.firstPlayerScore[4] != 0 && lastMatch.secondPlayerScore[4] != 0">Set 5</td>
        </tr>
        <tr :class="{ 'winner': lastMatch.winner === 1 }">
          <td></td>
          <td>{{ lastMatch.firstPlayerInfo[0] + " (" + lastMatch.firstPlayerInfo[2] + ") " + lastMatch.firstPlayerInfo[1] }}</td>
          <td>{{ lastMatch.firstPlayerScore[0] }}</td>
          <td>{{ lastMatch.firstPlayerScore[1] }}</td>
          <td v-if="lastMatch.firstPlayerScore[2] != 0 && lastMatch.secondPlayerScore[2] != 0">{{ lastMatch.firstPlayerScore[2] }}</td>
          <td v-if="lastMatch.firstPlayerScore[3] != 0 && lastMatch.secondPlayerScore[3] != 0">{{ lastMatch.firstPlayerScore[3] }}</td>
          <td v-if="lastMatch.firstPlayerScore[4] != 0 && lastMatch.secondPlayerScore[4] != 0">{{ lastMatch.firstPlayerScore[4] }}</td>
        </tr>
        <tr :class="{ 'winner': lastMatch.winner === 2 }">
          <td></td>
          <td>{{ lastMatch.secondPlayerInfo[0] + " (" + lastMatch.secondPlayerInfo[2] + ") " + lastMatch.secondPlayerInfo[1] }}</td>
          <td>{{ lastMatch.secondPlayerScore[0] }}</td>
          <td>{{ lastMatch.secondPlayerScore[1] }}</td>
          <td v-if="lastMatch.firstPlayerScore[2] != 0 && lastMatch.secondPlayerScore[2] != 0">{{ lastMatch.secondPlayerScore[2] }}</td>
          <td v-if="lastMatch.firstPlayerScore[3] != 0 && lastMatch.secondPlayerScore[3] != 0">{{ lastMatch.secondPlayerScore[3] }}</td>
          <td v-if="lastMatch.firstPlayerScore[4] != 0 && lastMatch.secondPlayerScore[4] != 0">{{ lastMatch.secondPlayerScore[4] }}</td>
        </tr>
      </table>
    </div>
  </div>
  <div v-else>
    <p>Ładowanie danych zawodnika...</p>
  </div>

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
      lastMatch: null,
      lastTournaments: null
    };
  },
  mounted() {
    this.fetchPlayerDetails();
    this.fetchLastMatchDetails();
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
    async fetchLastMatchDetails() {
      try {
        const response = await fetch(`http://localhost:8080/player/${this.id}/lastMatch`, {
          method: 'GET',
          credentials: 'include',
        });
        if (response.ok) {
          this.lastMatch = await response.json();
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
    }
  }
};
</script>

  