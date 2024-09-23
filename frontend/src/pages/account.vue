<template>
    <div>
      <h1>Obserwowani zawodnicy</h1>
      <div v-if="players.length > 0" class="players-container">
        <PlayerCard v-for="player in players" :key="player.id" :player="player" />
      </div>
      <div v-else class="no-players-message">
        <p>Nie obserwujesz żadnych zawodników. Zaobserwuj ich w zakładkach <i>Ranking WTA</i> lub <i>Ranking ATP</i></p>
      </div>
    </div>
  </template>
  
  <script>
  import PlayerCard from '../components/PlayerCard.vue';

  import '../css/account.css'
  
  export default {
    name: "AccountView",
    components: { PlayerCard },
    data() {
      return {
        players: []
      };
    },
    mounted() {
      this.fetchFollowedPlayers();
    },
    methods: {
      async fetchFollowedPlayers() {
        try {
            const response = await fetch('http://localhost:8080/account', {
            method: 'GET',
            credentials: 'include',
            });
          if (response.ok) {
            this.players = await response.json();
          } else {
            console.error('Błąd podczas ładowania zawodników');
          }
        } catch (error) {
          console.error('Błąd:', error);
        }
      }
    }
  };
  </script>
  