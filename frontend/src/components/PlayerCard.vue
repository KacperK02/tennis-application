<template>
    <div class="player-card">
        <img v-if="photoExists" :src="getPlayerPhotoUrl(player.teamid)" alt="Zdjęcie zawodnika" />
        <!-- Wyświetl placeholder, jeśli zdjęcie nie istnieje -->
        <img v-else :src="require(`@/assets/playerPhotos/placeholder.png`)" alt="Brak zdjęcia" />

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
    data() {
      return {
        photoExists: false,
        photoUrl: ''
      };
    },
    mounted() {
      this.checkPhotoExists();  // Sprawdź, czy zdjęcie istnieje po zamontowaniu komponentu
    },
    methods: {
        getPlayerPhotoUrl(teamid) {
            try {
                return require(`@/assets/playerPhotos/${teamid}.png`);
            } catch (error) {
                return require('@/assets/playerPhotos/placeholder.png');
            }
        },
      async checkPhotoExists() {
        const teamid = this.player.teamid;
        try {
          const response = await fetch(`http://localhost:8080/player/photoExists/${teamid}`);
          const exists = await response.json();
          if (exists) {
            this.photoExists = true;
            this.photoUrl = `src/assets/playerPhotos/${teamid}.png`;
          } else {
            await this.fetchPhotoFromAPI();  // Pobierz zdjęcie z API, jeśli nie istnieje
          }
        } catch (error) {
          console.error('Błąd podczas sprawdzania zdjęcia:', error);
        }
      },
      async fetchPhotoFromAPI() {
        try {
          const response = await fetch(`http://localhost:8080/player/fetchPhoto/${this.player.teamid}`, {
            method: 'POST'
          });
          if (response.ok) {
            this.photoExists = true;
            this.photoUrl = `src/assets/playerPhotos/${this.player.teamid}.png`;
          }
        } catch (error) {
          console.error('Błąd podczas pobierania zdjęcia z API:', error);
        }
      },
      checkDetails() {
        this.$router.push(`/player/${this.player.playerID}`);
      }
    }
  };
  </script>
  