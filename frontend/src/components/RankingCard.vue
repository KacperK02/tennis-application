<template>
  <div class="card">
    <p class="ranking">{{ player.ranking }}.</p>
    <p class="name">{{ player.name }}</p>
    <p class="country">{{ player.country }}</p>
    <p class="points">{{ player.points }}</p>

    <!-- Dynamiczny przycisk Obserwuj / Obserwujesz -->
    <button v-if="!isFollowing" @click="handleFollow">Obserwuj</button>
    <p v-else class="isFollowing">Obserwujesz</p>
  </div>
</template>

<script>
import axios from 'axios';

import '../css/rankingCard.css'

export default {
  props: {
    player: Object
  },
  data() {
    return {
      isFollowing: false // Flaga, czy gracz jest obserwowany
    };
  },
  mounted() {
    this.checkIfFollowing(); // Sprawdź, czy użytkownik obserwuje zawodnika
  },
  methods: {
    checkIfFollowing() {
      axios.get('http://localhost:8080/follow/isFollowing/' + this.player.playerID, {
        withCredentials: true
      })
        .then(response => {
          this.isFollowing = response.data; // Ustaw stan obserwowania
        })
        .catch(error => {
          console.error('Błąd podczas sprawdzania obserwowania:', error);
        });
    },
    handleFollow() {
      // Sprawdź, czy użytkownik jest zalogowany
      axios.get('http://localhost:8080/checkSession', {
        withCredentials: true
      })
      .then(response => {
        if (response.data.loggedIn) {
          this.followPlayer();
        } else {
          alert("Musisz być zalogowany, aby obserwować gracza.")
        }
      })
      .catch(error => {
        console.error('Błąd podczas sprawdzania sesji:', error);
      });
    },
    followPlayer() {
      axios.post('http://localhost:8080/follow/followPlayer', 
      JSON.stringify(this.player.playerID),{
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      }
        )
        .then(() => {
          this.isFollowing = true; // Ustaw stan na "obserwowany"
        })
        .catch(error => {
          console.error('Błąd podczas obserwowania zawodnika:', error);
        });
    }
  },
  name: 'RankingCard'
};
</script>
