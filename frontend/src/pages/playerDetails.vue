<template>
    <div v-if="player">
      <h1>{{ player.name }}</h1>
      <img :src="getPlayerPhotoUrl(player.teamid)" alt="Zdjęcie zawodnika" />
      <p><strong>Kraj:</strong> {{ player.country }}</p>
      <p><strong>Ranking:</strong> {{ player.ranking }}</p>
      <p><strong>Punkty:</strong> {{ player.points }}</p>
      <button @click="stopFollow">Przestań obserwować</button>
    </div>
    <div v-else>
      <p>Ładowanie danych zawodnika...</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      id: String
    },
    data() {
      return {
        player: null
      };
    },
    mounted() {
      this.fetchPlayerDetails();  // Pobierz dane zawodnika po zamontowaniu komponentu
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
  