<template>

    <h1>Ranking WTA</h1>

    <!-- Sortowanie i filtrowanie -->
    <div class="controls">
      <label for="sortOrder">Sortuj według pozycji: </label>
      <select v-model="sortOrder" id="sortOrder">
        <option value="asc">Rosnąco</option>
        <option value="desc">Malejąco</option>
      </select>

      <label for="playerSearch">Szukaj gracza: </label>
      <input type="text" v-model="searchQuery" id="playerSearch" placeholder="Wpisz imię lub nazwisko" />

      <label for="countryFilter">Filtruj wg kraju: </label>
      <select v-model="selectedCountry" id="countryFilter">
        <option value="">Wszystkie kraje</option>
        <option v-for="country in availableCountries" :key="country" :value="country">{{ country }}</option>
      </select>

      <label for="playerLimit">Liczba graczy do wyświetlenia: </label>
      <input type="number" v-model.number="playerLimit" id="playerLimit" min="1" max="500" />

    </div>

    <div class="ranking-list-legend">
      <p class="ranking">Miejsce</p>
      <p class="name">Imię i nazwisko</p>
      <p class="country">Kraj</p>
      <p class="points">Punkty</p>
      <p class="follow">Kliknij, by zaobserwować</p>
    </div>

    <div class="ranking-list">
      <RankingCard v-for="player in filteredPlayers" :key="player.playerID" :player="player" />
    </div>

</template>

<script>
import axios from 'axios';
import RankingCard from '../components/RankingCard.vue';

import '../css/ranking.css'

export default {
  data() {
    return {
      players: [],
      sortOrder: 'asc',
      playerLimit: 50,
      selectedCountry: '',     // Filtr krajów, domyślnie wszystkie kraje
      availableCountries: [],   // Lista dostępnych krajów do filtrowania
      searchQuery: ''
    };
  },
  mounted() {
    axios.get('http://localhost:8080/getAllWTAPlayers', {
      withCredentials: true
    })
      .then(response => {
        this.players = response.data;
        this.extractCountries(); // Wyciągnij dostępne kraje z danych graczy
      });
  },
  computed: {
    filteredPlayers() {
      let filteredPlayers = [...this.players];

      // Filtrowanie według kraju
      if (this.selectedCountry) {
        filteredPlayers = filteredPlayers.filter(player => player.country === this.selectedCountry);
      }

      // Filtrowanie po nazwisku
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filteredPlayers = filteredPlayers.filter(player => 
          player.name.toLowerCase().includes(query)
        );
      }

      // Sortowanie po pozycji w rankingu
      filteredPlayers.sort((a, b) => {
        return this.sortOrder === 'asc' ? a.ranking - b.ranking : b.ranking - a.ranking;
      });
      
      // Zwracanie ograniczonej liczby graczy
      return filteredPlayers.slice(0, this.playerLimit);
    }
  },
  methods: {
    extractCountries() {
      // Wyciągnięcie unikalnych krajów z danych graczy
      this.availableCountries = [...new Set(this.players.map(player => player.country))].sort();
    }
  },
  components: {
    RankingCard
  },
  name: 'WTARanking'
};
</script>
