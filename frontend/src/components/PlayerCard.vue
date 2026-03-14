<template>
    <div class="player-card">
        <img :src="`http://localhost:8080/player/photo/${player.teamid}`" alt="Zdjęcie zawodnika" />

        <p>Imię i nazwisko: {{ player.name }}</p>
        <p>Kraj: {{ player.country }}</p>
        
        <p>
            Miejsce w rankingu 
            <span v-if="player.playerID < 501">WTA</span>
            <span v-else>ATP</span>: {{ player.ranking }}
        </p>
  
        <button @click="checkDetails">Szczegóły</button>
    </div>
  </template>
  
  <script>
  import '../css/playerCard.css'

  export default {
    props: {
      player: Object
    },
    methods: {
      checkDetails() {
        this.$router.push(`/player/${this.player.playerID}`);
      }
    }
  };
  </script>
  