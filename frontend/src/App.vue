<template>
  <div id="app">
    <nav>
      <router-link to="/">Home</router-link>
      <router-link to="/WTARanking">Ranking WTA</router-link>
      <router-link to="/ATPRanking">Ranking ATP</router-link>
      <router-link to="/account">Twoje Konto</router-link>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

nav a {
  font-size: 18px;
  padding: 0 15px;
  color: #42b983;
  text-decoration: none;
}

nav a.router-link-active {
  font-weight: bold;
}
</style>
