<template>
  <div id="app">
    <header>
      NaKorcie.pl
    </header>

    <nav>
      <router-link to="/">Strona Główna</router-link>
      <router-link to="/WTARanking">Ranking WTA</router-link>
      <router-link to="/ATPRanking">Ranking ATP</router-link>
      <router-link to="/live">Mecze LIVE</router-link>
      <router-link to="/account">Twoje Konto</router-link>
    </nav>
    <router-view></router-view>

    <footer>
      © 2024 NaKorcie.pl
    </footer>

  </div>
</template>


<script>

import './css/App.css'

export default {
  name: 'App'
}

</script>

