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
  
  export default {
    data() {
      return {
        players: []
      };
    },
    mounted() {
      axios.get('http://localhost:8080/getAllATPPlayers')
        .then(response => {
          this.players = response.data;
        });
    },
    components: {
      RankingCard // Dodaj komponent do rejestru komponentów
    },
    name: 'ATPRanking'
  };
  </script>
  
  <style scoped>
  .ranking-list {
    display: block;
    width: 100%;
    max-width: 1280px;
    margin: 0 auto;
    padding: 20px;
  }
  </style>
  