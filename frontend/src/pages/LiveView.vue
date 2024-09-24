<template>
    <h1>Wyniki na żywo</h1>
    <div class="live-matches">
        <div class="match-table-container" v-for="(match, index) in matches" :key="index">
        <table class="match-table">
        <tr>
          <td><b>Turniej</b></td>
          <td>{{ match.nameOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Ranga</b></td>
          <td>{{ match.rankOfTournament }}</td>
        </tr>
        <tr>
          <td><b>Nawierzchnia</b></td>
          <td>{{ match.surface }}</td>
        </tr>
        <tr>
          <td><b>Runda</b></td>
          <td>{{ match.round }}</td>
        </tr>
        <tr>
          <td><b>Status</b></td>
          <td>{{ match.status }}</td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td>Set 1</td>
          <td>Set 2</td>
          <td v-if="match.firstPlayerScore[2] != 0 && match.secondPlayerScore[2] != 0">Set 3</td>
          <td v-if="match.firstPlayerScore[3] != 0 && match.secondPlayerScore[3] != 0">Set 4</td>
          <td v-if="match.firstPlayerScore[4] != 0 && match.secondPlayerScore[4] != 0">Set 5</td>
          <td>Punkty</td>
        </tr>
        <tr :class="{ 'winner': match.winner === 1 }" style="height: 50px;">
          <td><img v-if="match.service == 1" src="@/assets/ball.jpg" alt="serwis" style="width: 25px; height: 25px;"></td>
          <td>{{ match.firstPlayerInfo[0] + " (" + match.firstPlayerInfo[2] + "), " + match.firstPlayerInfo[1] }}</td>
          <td>{{ match.firstPlayerScore[0] }}</td>
          <td>{{ match.firstPlayerScore[1] }}</td>
          <td v-if="match.firstPlayerScore[2] != 0 && match.secondPlayerScore[2] != 0">{{ match.firstPlayerScore[2] }}</td>
          <td v-if="match.firstPlayerScore[3] != 0 && match.secondPlayerScore[3] != 0">{{ match.firstPlayerScore[3] }}</td>
          <td v-if="match.firstPlayerScore[4] != 0 && match.secondPlayerScore[4] != 0">{{ match.firstPlayerScore[4] }}</td>
          <td>{{ match.gamePoints[0] }}</td>
        </tr>
        <tr :class="{ 'winner': match.winner === 2 }" style="height: 50px;">
          <td><img v-if="match.service == 2" src="@/assets/ball.jpg" alt="serwis" style="width: 25px; height: 25px;"></td>
          <td>{{ match.secondPlayerInfo[0] + " (" + match.secondPlayerInfo[2] + "), " + match.secondPlayerInfo[1] }}</td>
          <td>{{ match.secondPlayerScore[0] }}</td>
          <td>{{ match.secondPlayerScore[1] }}</td>
          <td v-if="match.firstPlayerScore[2] != 0 && match.secondPlayerScore[2] != 0">{{ match.secondPlayerScore[2] }}</td>
          <td v-if="match.firstPlayerScore[3] != 0 && match.secondPlayerScore[3] != 0">{{ match.secondPlayerScore[3] }}</td>
          <td v-if="match.firstPlayerScore[4] != 0 && match.secondPlayerScore[4] != 0">{{ match.secondPlayerScore[4] }}</td>
          <td>{{ match.gamePoints[1] }}</td>
        </tr>
      </table>
        </div>
    </div>
</template>

<script>
import "../css/liveMatches.css"

export default {
  data() {
    return {
      matches: [],
    };
  },
  mounted() {
    fetch('http://localhost:8080/getLiveMatches')
      .then(response => response.json())
      .then(data => {
        this.matches = data;
      })
      .catch(error => console.error('Błąd:', error));
  },
};
</script>