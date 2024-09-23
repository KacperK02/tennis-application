<template>
  <div>
    <h2>Witaj w serwisie NaKorcie.pl!</h2>

    <p>Najlepszy serwis dla polskich fanów tenisa! Obserwuj swoich ulubionych zawodników i nie przegap żadnych informacji o ich meczach! Załóż konto już dziś!</p>

    <div v-if="loginError" class="error-message">
      {{ loginError }}
    </div>

    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>

    <div v-if="registrationSuccess" class="success-message">
      {{ registrationSuccess }}
    </div>

    <!-- Sprawdzenie, czy użytkownik jest zalogowany -->
    <div v-if="!isLoggedIn">
      <h2>Logowanie</h2>
      <form @submit.prevent="login">
        <div>
          <label>Email:</label>
          <input v-model="email" type="email" required />
        </div>
        <div>
          <label>Hasło:</label>
          <input v-model="password" type="password" required />
        </div>
        <div>
          <button @click.prevent="goToRegister">Zarejestruj się</button>
          <button type="submit">Zaloguj się</button>
        </div>
      </form>
    </div>

    <!-- Przyciski, jeśli użytkownik jest zalogowany -->
    <div v-else>
      <p>Jesteś zalogowany.</p>
      <button @click="logout">Wyloguj się</button>
    </div>

  </div>

  <img src="@/assets/logo.png" alt="Logo" class="logo" />
  
</template>

<script>
import '../css/index.css'

export default {
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
      loginError: this.$route.query.loginError || '',
      isLoggedIn: false,
    };
  },
  async created() {
    this.registrationSuccess = this.$route.query.registration_success || '';
    await this.checkLoginStatus();
  },
  watch: {
    // Nasłuchiwanie na zmiany w query params
    '$route.query'(newQuery) {
      // Gdy query params się zmienią, aktualizujemy loginError
      this.loginError = newQuery.loginError || '';
    }
  },
  methods: {
    async checkLoginStatus() {
      try {
        const response = await fetch('http://localhost:8080/checkSession', {
          method: 'GET',
          credentials: 'include',
        });
        const result = await response.json();
        this.isLoggedIn = result.loggedIn;
        
      } catch (error) {
        console.error('Błąd podczas sprawdzania sesji:', error);
      }
    },
    async login() {
      try {
        const response = await fetch('http://localhost:8080/login', {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: this.email, password: this.password })
        });
        const result = await response.json();
        if (result.status === 'success' && result.redirectUrl) {
          window.location.href = result.redirectUrl;
        } else {
          this.errorMessage = result.error;
        }
      } catch (error) {
        this.errorMessage = 'An error occurred. Please try again.';
      }
    },
    async logout() {
      try {
        const response = await fetch('http://localhost:8080/logout', {
          method: 'POST',
          credentials: 'include',
        });
        if (response.ok) {
          this.isLoggedIn = false;
          window.location.reload();
        } else {
          this.errorMessage = 'Błąd podczas wylogowywania.';
        }
      } catch (error) {
        this.errorMessage = 'Błąd podczas wylogowywania.';
      }
    },
    goToRegister() {
      this.$router.push('/register'); // Przekierowanie na stronę rejestracji
    }
  },
  name: "IndexView"
};
</script>
