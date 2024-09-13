<template>
    <div class="card">
      <p>{{ player.ranking }}.</p>
      <p class="name">{{ player.name }}</p>
      <p>{{ player.country }}</p>
      <p>{{ player.tournamentsPlayed }}</p>
      <p>{{ player.points }}</p>
      <button @click="followPlayer">{{ isFollowing ? 'Obserwujesz' : 'Obserwuj' }}</button>
    </div>
</template>
  
<script>
  import '../css/rankingCard.css'

  export default {
    props: {
      player: Object
    },
    data() {
    return {
      isFollowing: false // Flaga czy gracz jest obserwowany
    };
  },
  methods: {
    followPlayer() {
      this.isFollowing = !this.isFollowing; // Przełącz stan obserwowania
    }
  },
    name: 'RankingCard'
  };
</script>

  