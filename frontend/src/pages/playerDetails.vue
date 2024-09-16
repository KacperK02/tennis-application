<template>
    <div v-if="player">
      <h1>{{ player.name }}</h1>
      <img :src="getPlayerPhotoUrl(player.teamid)" alt="Zdjęcie zawodnika" />
      <p><strong>Kraj:</strong> {{ player.country }}</p>
      <p><strong>Ranking:</strong> {{ player.ranking }}</p>
      <p><strong>Punkty:</strong> {{ player.points }}</p>
      <button @click="goBack">Przestań obserwować</button>
    </div>
    <div v-else>
      <p>Ładowanie danych zawodnika...</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      id: String // ID zawodnika przekazane jako props
    },
    data() {
      return {
        player: null  // Obiekt, który będzie przechowywał dane zawodnika
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
      goBack() {
        this.$router.go(-1);  // Przekieruj użytkownika z powrotem
      }
    }
  };
  </script>
  