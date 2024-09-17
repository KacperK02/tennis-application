<template>
  <div>
    <h1>Ranking ATP</h1>
    <div class="ranking-list">
      <RankingCard v-for="player in players" :key="player.id" :player="player" />
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import RankingCard from '../components/RankingCard.vue';

import '../css/ranking.css'

export default {
  data() {
    return {
      players: []
    };
  },
  mounted() {
    axios.get('http://localhost:8080/getAllATPPlayers', {
      withCredentials: true
    })
      .then(response => {
        this.players = response.data;
      });
  },
  components: {
    RankingCard
  },
  name: 'ATPRanking'
};
</script>
