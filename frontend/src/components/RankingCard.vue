<template>
    <div class="card">
      <p>{{ player.ranking }}.</p>
      <p>{{ player.name }}</p>
      <p>{{ player.country }}</p>
      <p>{{ player.tournamentsPlayed }}</p>
      <p>{{ player.points }}</p>
      <button @click="followPlayer">{{ isFollowing ? 'Obserwujesz' : 'Obserwuj' }}</button>
    </div>
</template>
  
<script>
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
  
<style scoped>
  .card {
    border: 1px solid #ccc;
    padding: 10px;
    margin: 10px;
    border-radius: 8px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 50px;
    margin-bottom: 20px;
    display: flex;
    gap: 20px;
  }

  .card p {
    width: 15%;
  }

  button {
  margin: 5px 0;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

</style>
  